package edu.umbc.ebiquity.mithril.ui.activities;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticLocation;
import edu.umbc.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticNearActors;
import edu.umbc.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticTime;
import edu.umbc.ebiquity.mithril.ui.fragments.instancecreationactivityfragments.SemanticLocationFragment;
import edu.umbc.ebiquity.mithril.ui.fragments.instancecreationactivityfragments.SemanticNearActorsFragment;
import edu.umbc.ebiquity.mithril.ui.fragments.instancecreationactivityfragments.SemanticTimeFragment;
import edu.umbc.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

public class InstanceCreationActivity extends AppCompatActivity
        implements SemanticTimeFragment.OnListFragmentInteractionListener,
        SemanticLocationFragment.OnListFragmentInteractionListener,
        SemanticNearActorsFragment.OnListFragmentInteractionListener {//,
//        SemanticActivityFragment.OnListFragmentInteractionListener {

    private static final String FRAGMENT_LOCATION = "location";
    private static final String FRAGMENT_PRESENCE = "presence";
    private static final String FRAGMENT_TEMPORAL = "temporal";
//    private static final String FRAGMENT_ACTIVITY = "activity";

    private BottomNavigationView navigation;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private String activityBaseTitle;
    private Button mOtherCtxtBtn;
    private Button mFirstMajorCtxtBtn;
    private Button mSecondMajorCtxtBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
        init();
    }

    private void init() {
        sharedPreferences = getApplicationContext().getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE);
        testInitInstancesCreateAndLaunchNextActivity();
        editor = getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE).edit();
        activityBaseTitle = getApplicationContext().getResources().getString(R.string.title_activity_instance_creation);

        setContentView(R.layout.activity_instance_creation);
        navigation = (BottomNavigationView) findViewById(R.id.navigation_menu);

        mOtherCtxtBtn = (Button) findViewById(R.id.otherCtxtBtn);
        mFirstMajorCtxtBtn = (Button) findViewById(R.id.firstCtxtBtn);
        mSecondMajorCtxtBtn = (Button) findViewById(R.id.secondCtxtBtn);

        handleLocation();
        setOnNavigationListeners();
    }

    private void setOnNavigationListeners() {
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_location:
                        handleLocation();
                        return true;
                    case R.id.navigation_temporal:
                        handleTemporal();
                        return true;
                    case R.id.navigation_presence_related:
                        handlePresence();
                        return true;
//                    case R.id.navigation_activity:
//                        handleActivity();
//                        return true;
                }
                return false;
            }
        });
    }

    private void handleLocation() {
        setTitle(activityBaseTitle + getApplicationContext().getResources().getString(R.string.text_instance_creation_location));

        mOtherCtxtBtn.setText(R.string.pref_other_location_summary);
        mFirstMajorCtxtBtn.setText(R.string.pref_home_location_summary);
        mSecondMajorCtxtBtn.setText(R.string.pref_work_location_summary);

        if (!sharedPreferences.getBoolean(MithrilApplication.getPrefKeyLocationInstance(), false)) {
            PermissionHelper.toast(getApplicationContext(), getApplicationContext().getResources().getString(R.string.tooltip_location), Toast.LENGTH_SHORT);
            editor.putBoolean(MithrilApplication.getPrefKeyLocationInstance(), true);
            editor.apply();
        }
        loadSemanticLocationFragment();
        setOnClickListeners(FRAGMENT_LOCATION);
    }

    private void handleTemporal() {
        setTitle(activityBaseTitle + getApplicationContext().getResources().getString(R.string.text_instance_creation_temporal));

        mOtherCtxtBtn.setText(R.string.pref_other_hours_context_summary);
        mFirstMajorCtxtBtn.setText(R.string.pref_work_hours_context_summary);
        mSecondMajorCtxtBtn.setText(R.string.pref_DND_hours_context_summary);

        if (!sharedPreferences.getBoolean(MithrilApplication.getPrefKeyTemporalInstance(), false)) {
            PermissionHelper.toast(getApplicationContext(), getApplicationContext().getResources().getString(R.string.tooltip_temporal), Toast.LENGTH_SHORT);
            editor.putBoolean(MithrilApplication.getPrefKeyTemporalInstance(), true);
            editor.apply();
        }
        loadSemanticTemporalFragment();
        setOnClickListeners(FRAGMENT_TEMPORAL);
    }

    private void handlePresence() {
        setTitle(activityBaseTitle + getApplicationContext().getResources().getString(R.string.text_instance_creation_presence_related));

        mOtherCtxtBtn.setText(R.string.pref_presence_info_others_summary);
        mFirstMajorCtxtBtn.setText(R.string.pref_presence_info_supervisor_summary);
        mSecondMajorCtxtBtn.setText(R.string.pref_presence_info_colleague_summary);

        if (!sharedPreferences.getBoolean(MithrilApplication.getPrefKeyPresenceInstance(), false)) {
            PermissionHelper.toast(getApplicationContext(), getApplicationContext().getResources().getString(R.string.tooltip_presence_related), Toast.LENGTH_SHORT);
            editor.putBoolean(MithrilApplication.getPrefKeyPresenceInstance(), true);
            editor.apply();
        }
        loadSemanticPresenceFragment();
        setOnClickListeners(FRAGMENT_PRESENCE);
    }

    /*
    private void handleActivity() {
        setTitle(activityBaseTitle+getApplicationContext().getResources().getString(R.string.text_instance_creation_activity));

        mOtherCtxtBtn.setText(R.string.pref_other_location_summary);
        mFirstMajorCtxtBtn.setText(R.string.pref_home_location_summary);
        mSecondMajorCtxtBtn.setText(R.string.pref_work_location_summary);

        if (!sharedPreferences.getBoolean(MithrilApplication.getPrefKeyActivityInstance(), false)) {
            PermissionHelper.toast(getApplicationContext(), getApplicationContext().getResources().getString(R.string.tooltip_activity), Toast.LENGTH_SHORT);
            editor.putBoolean(MithrilApplication.getPrefKeyActivityInstance(), true);
            editor.apply();
        }
        loadSemanticActivityFragment();
        setOnClickListeners(FRAGMENT_ACTIVITY);
    }*/

    private void setOnClickListeners(String fragmentInView) {
        if (fragmentInView.equals(FRAGMENT_LOCATION)) {
            mOtherCtxtBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            mFirstMajorCtxtBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            mSecondMajorCtxtBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        } else if (fragmentInView.equals(FRAGMENT_TEMPORAL)) {
            mOtherCtxtBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            mFirstMajorCtxtBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            mSecondMajorCtxtBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        } else {//if(fragmentInView.equals(FRAGMENT_PRESENCE)){
            mOtherCtxtBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            mFirstMajorCtxtBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            mSecondMajorCtxtBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        } /*else {//if(fragmentInView.equals(FRAGMENT_ACTIVITY)){
            mOtherCtxtBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            mFirstMajorCtxtBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            mSecondMajorCtxtBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }*/
    }
    private void loadSemanticLocationFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_instances, new SemanticLocationFragment())
                .commit();
    }

    private void loadSemanticTemporalFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_instances, new SemanticLocationFragment())
                .commit();
    }

    private void loadSemanticPresenceFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_instances, new SemanticLocationFragment())
                .commit();
    }

    /*
    private void loadSemanticActivityFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_instances, new SemanticLocationFragment())
                .commit();
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_instance_creation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.done_with_instances_settings) {
            startNextActivity(this, CoreActivity.class);
            editor.putBoolean(MithrilApplication.getPrefKeyInitInstancesCreated(), true);
            editor.apply();
        }
        return super.onOptionsItemSelected(item);
    }

    private void testInitInstancesCreateAndLaunchNextActivity() {
        if (sharedPreferences.getBoolean(MithrilApplication.getPrefKeyInitInstancesCreated(), false))
            startNextActivity(this, CoreActivity.class);
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0, 0);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        finish(); // quit app
    }

    private void startNextActivity(Context context, Class activityClass) {
        Intent launchNextActivity = new Intent(context, activityClass);
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(launchNextActivity);
    }

    @Override
    public void onListFragmentInteraction(SemanticTime item) {

    }

    @Override
    public void onListFragmentInteraction(SemanticLocation item) {

    }

//    @Override
//    public void onListFragmentInteraction(SemanticActivity item) {
//
//    }

    @Override
    public void onListFragmentInteraction(SemanticNearActors item) {

    }
}