package edu.umbc.ebiquity.mithril.ui.activities;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.data.model.Policy;
import edu.umbc.ebiquity.mithril.data.model.components.AppData;
import edu.umbc.ebiquity.mithril.data.model.rules.Action;
import edu.umbc.ebiquity.mithril.data.model.rules.PolicyRule;
import edu.umbc.ebiquity.mithril.data.model.rules.Violation;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticActivity;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticLocation;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticNearActor;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticTime;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticUserContext;
import edu.umbc.ebiquity.mithril.simulations.DataGenerator;
import edu.umbc.ebiquity.mithril.ui.fragments.rulechangeactivityfragments.RuleChangeFragment;
import edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions.ContextImplementationMissingException;
import edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions.SemanticInconsistencyException;
import edu.umbc.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

public class RuleChangeActivity extends AppCompatActivity implements RuleChangeFragment.OnListFragmentInteractionListener {
    private Violation currentViolation;
    private FloatingActionButton fab;
    private boolean ruleAdded;
    private Button saveBtn;
    private SQLiteDatabase mithrilDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_change);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_rule_change);
        setSupportActionBar(toolbar);

        mithrilDB = MithrilDBHelper.getHelper(this).getWritableDatabase();
        currentViolation = getIntent().getParcelableExtra("rule");
        ruleAdded = false;

        saveBtn = (Button) findViewById(R.id.ruleSaveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<PolicyRule> policyRules = MithrilDBHelper.getHelper(v.getContext()).findAllPoliciesById(mithrilDB, currentViolation.getPolicyId());
                for(PolicyRule policyRule : policyRules) {
                    policyRule.setEnabled(true);
                    MithrilDBHelper.getHelper(v.getContext()).updatePolicyRule(mithrilDB, policyRule);
                }
                finish();
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab_rule_edit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogContextType();
                loadRuleChangeFragment();
                ruleAdded = true;
            }
        });

        loadRuleChangeFragment();
    }

    @Override
    public void onBackPressed() {
        if(ruleAdded)
            PermissionHelper.toast(this, "Please click on save to go back...");
    }

    private void showDialogContextType() {
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice);
        final String[] listOfContextTypesFromTheOntology = MithrilAC.getContextArrayTypes();
        for (int index = 0; index < listOfContextTypesFromTheOntology.length; index++)
            arrayAdapter.add(listOfContextTypesFromTheOntology[index]);

        final SQLiteDatabase mithrilDB = MithrilDBHelper.getHelper(this).getWritableDatabase();
        final String appPkgName = MithrilDBHelper.getHelper(this).findAppById(mithrilDB, currentViolation.getAppId()).getPackageName();
        List<PolicyRule> policyRules = MithrilDBHelper.getHelper(this).findAllPoliciesForAppWhenPerformingOp(mithrilDB, appPkgName, currentViolation.getOprId());
        final Action currentAction = policyRules.get(0).getAction();
        for (PolicyRule policyRule : policyRules) {
            Pair<String, String> contextPiece = MithrilDBHelper.getHelper(this).findContextByID(mithrilDB, policyRule.getCtxId());
            //You cannot be at two locations at the same time
            if(contextPiece.first.equals(MithrilAC.getPrefKeyContextTypeLocation()))
                arrayAdapter.remove(MithrilAC.getPrefKeyContextTypeLocation());
            if(contextPiece.first.equals(MithrilAC.getPrefKeyContextTypeTemporal()))
                arrayAdapter.remove(MithrilAC.getPrefKeyContextTypeTemporal());
//            if(contextPiece.first.equals(MithrilAC.getPrefKeyContextTypePresence()))
//                arrayAdapter.remove(MithrilAC.getPrefKeyContextTypePresence());
//            if(contextPiece.first.equals(MithrilAC.getPrefKeyContextTypeActivity()))
//                arrayAdapter.remove(MithrilAC.getPrefKeyContextTypeActivity());
        }
        final AppData app = MithrilDBHelper.getHelper(this).findAppByAppPkgName(mithrilDB, appPkgName);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.view_list);
        builder.setTitle("What rule context are you adding?");
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String semanticLocationType = arrayAdapter.getItem(which);
                try {
                    String semanticLocationLabel = getAContext(semanticLocationType);
                    PolicyRule policyRule = DataGenerator.createPolicyRule(
                            currentViolation.getPolicyId(),
                            appPkgName,
                            app.getAppName(), // the name returned is not correct we have find the method that fixes that
                            currentViolation.getOprId(), // Manifest.permission.ACCESS_FINE_LOCATION,
                            semanticLocationLabel,
                            semanticLocationType,
                            currentAction,
                            mithrilDB, builder.getContext());
                    try {
                        MithrilDBHelper.getHelper(builder.getContext()).addPolicyRule(mithrilDB, policyRule);
                    } catch (SQLiteConstraintException e) {
                        Log.e(MithrilAC.getDebugTag(), "Error inserting policy for " + policyRule.getAppStr() + "policy inserted already", e);
                    }
                } catch (NullPointerException e) {
                    PermissionHelper.toast(builder.getContext(), "No context of this type is currently known. Perhaps you should create one through menu option \"Manage context info\"");
                } catch (SemanticInconsistencyException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.show();
    }

    private String getAContext(String type) {
        SharedPreferences sharedPrefs = getSharedPreferences(MithrilAC.getSharedPreferencesName(), Context.MODE_PRIVATE);
        Gson retrieveDataGson = new Gson();
        String retrieveDataJson;
        Map<String, ?> allPrefs;
        allPrefs = sharedPrefs.getAll();
        for (Map.Entry<String, ?> aPref : allPrefs.entrySet()) {
            if (aPref.getKey().startsWith(type)) {
                retrieveDataJson = sharedPrefs.getString(aPref.getKey(), "");
                if (type.equals(MithrilAC.getPrefKeyContextTypeLocation())){
                    SemanticLocation semanticLocation = retrieveDataGson.fromJson(retrieveDataJson, SemanticLocation.class);
                    return semanticLocation.getLabel();
                } else if(type.equals(MithrilAC.getPrefKeyContextTypeTemporal())){
                    SemanticTime semanticTime = retrieveDataGson.fromJson(retrieveDataJson, SemanticTime.class);
                    return semanticTime.getLabel();
                } else if(type.equals(MithrilAC.getPrefKeyContextTypePresence())){
                    SemanticNearActor semanticNearActor = retrieveDataGson.fromJson(retrieveDataJson, SemanticNearActor.class);
                    return semanticNearActor.getLabel();
                } else if(type.equals(MithrilAC.getPrefKeyContextTypeActivity())) {
                    SemanticActivity semanticActivity = retrieveDataGson.fromJson(retrieveDataJson, SemanticActivity.class);
                    return semanticActivity.getLabel();
                }
            }
        }
        throw new NullPointerException("No context of this type is currently known. " +
                "Perhaps you should create one through menu option \"Manage context info\"");
    }

    private void loadNewRuleFragment() {

    }

    private void loadRuleChangeFragment() {
        Bundle data = new Bundle();
        data.putParcelable("rule", currentViolation);

        RuleChangeFragment aRuleChangeFragment = new RuleChangeFragment();
        aRuleChangeFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_rule_change, aRuleChangeFragment)
                .commit();
    }

    @Override
    public void onListFragmentInteraction(SemanticUserContext semanticUserContext, String newContextLabel) {
        try {
            String contextType = semanticUserContext.getType();
            String oldContextLabel = semanticUserContext.getLabel();
            long ctxtId = MithrilDBHelper.getHelper(this).findContextIdByLabelAndType(mithrilDB, newContextLabel, contextType);
            List<PolicyRule> policyRules = MithrilDBHelper.getHelper(this).findAllPoliciesByIdIncludeEnabled(mithrilDB, currentViolation.getPolicyId());
            for(PolicyRule policyRule : policyRules) {
                if(policyRule.getCtxStr().equals(oldContextLabel)) {
                    policyRule.setCtxId(ctxtId);
                    policyRule.setCtxStr(newContextLabel);
                    MithrilDBHelper.getHelper(this).updatePolicyRule(mithrilDB, policyRule);
                }
            }
        } catch (ContextImplementationMissingException e) {
            e.printStackTrace();
        }
    }
}