package edu.umbc.ebiquity.mithril.ui.activities;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.data.model.components.AppData;
import edu.umbc.ebiquity.mithril.data.model.rules.Action;
import edu.umbc.ebiquity.mithril.data.model.rules.PolicyRule;
import edu.umbc.ebiquity.mithril.data.model.rules.Violation;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticLocation;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticTime;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticUserContext;
import edu.umbc.ebiquity.mithril.simulations.DataGenerator;
import edu.umbc.ebiquity.mithril.ui.fragments.rulechangeactivityfragments.RuleChangeFragment;
import edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions.SemanticInconsistencyException;
import edu.umbc.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

public class RuleChangeActivity extends AppCompatActivity implements RuleChangeFragment.OnListFragmentInteractionListener {
    private Violation currentViolation;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_change);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_rule_change);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab_rule_edit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogContextType();
                loadRuleChangeFragment();
            }
        });

        currentViolation = getIntent().getParcelableExtra("rule");
        loadRuleChangeFragment();
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
                    MithrilDBHelper.getHelper(builder.getContext()).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(
                            currentViolation.getPolicyId(),
                            appPkgName,
                            app.getAppName(), // the name returned is not correct we have find the method that fixes that
                            currentViolation.getOprId(), // Manifest.permission.ACCESS_FINE_LOCATION,
                            semanticLocationLabel,
                            semanticLocationType,
                            currentAction,
                            mithrilDB, builder.getContext())
                    );
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
        try {
            allPrefs = sharedPrefs.getAll();
            for (Map.Entry<String, ?> aPref : allPrefs.entrySet()) {
                if (aPref.getKey().startsWith(type)) {
                    retrieveDataJson = sharedPrefs.getString(aPref.getKey(), "");
                    SemanticLocation semanticLocation = retrieveDataGson.fromJson(retrieveDataJson, SemanticLocation.class);
                    return semanticLocation.getLabel();
                }
            }
        } catch (NullPointerException e) {
            Log.d(MithrilAC.getDebugTag(), "Prefs empty somehow?!");
        } catch (Exception e) {
            Log.d(MithrilAC.getDebugTag(), "came here");
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
    public void onListFragmentInteraction(SemanticUserContext item) {

    }
}