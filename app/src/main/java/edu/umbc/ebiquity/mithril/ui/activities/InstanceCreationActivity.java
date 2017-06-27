package edu.umbc.ebiquity.mithril.ui.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticActivity;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticLocation;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticNearActor;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticTime;
import edu.umbc.ebiquity.mithril.ui.fragments.instancecreationactivityfragments.SemanticActivityFragment;
import edu.umbc.ebiquity.mithril.ui.fragments.instancecreationactivityfragments.SemanticLocationFragment;
import edu.umbc.ebiquity.mithril.ui.fragments.instancecreationactivityfragments.SemanticNearActorFragment;
import edu.umbc.ebiquity.mithril.ui.fragments.instancecreationactivityfragments.SemanticTimeFragment;
import edu.umbc.ebiquity.mithril.util.services.FetchAddressIntentService;
import edu.umbc.ebiquity.mithril.util.specialtasks.contextinstances.DayOfWeek;
import edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions.AddressKeyMissingError;
import edu.umbc.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

@TargetApi(Build.VERSION_CODES.N)
public class InstanceCreationActivity extends AppCompatActivity
        implements SemanticTimeFragment.OnListFragmentInteractionListener,
        SemanticLocationFragment.OnListFragmentInteractionListener,
        SemanticNearActorFragment.OnListFragmentInteractionListener,
        SemanticActivityFragment.OnListFragmentInteractionListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String FRAGMENT_LOCATION = "location";
    private static final String FRAGMENT_PRESENCE = "presence";
    private static final String FRAGMENT_TEMPORAL = "temporal";
    private static final String FRAGMENT_ACTIVITY = "activity";
    private static String currentFragment = new String();
    private static Gson contextDataStoreGson = new Gson();
    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE_HOME = 1;
    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE_WORK = 2;
    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE_MORE = 3;
    private final int TIME_REQUEST_CODE_WORK = 4;
    private final int TIME_REQUEST_CODE_DND = 5;
    private final int TIME_REQUEST_CODE_MORE = 6;
    private final int GEOFENCE_REQUEST_CODE = 7;
    private final int TIMEFENCE_REQUEST_CODE = 8;
    /**
     * Provides the entry point to Google Play services: Geo fence
     */
    protected GoogleApiClient mGoogleApiClient;
    private SQLiteDatabase mithrilDB;
    private BottomNavigationView bottomNavigationView;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private String activityBaseTitle;
    private Button mOtherCtxtBtn;
    private Button mFirstMajorCtxtBtn;
    private Button mSecondMajorCtxtBtn;
    private FloatingActionButton mSaveFAB;
    private boolean isThereLocationContextToSave = false;
    private boolean isThereTemporalContextToSave = false;
    private boolean isTherePresenceContextToSave = false;
    private boolean isThereActivityContextToSave = false;
    private Map<String, SemanticLocation> semanticLocations = new HashMap<>();
    private Map<String, SemanticNearActor> semanticNearActors = new HashMap<>();
    private Map<String, SemanticTime> semanticTimes = new HashMap<>();
    private Map<String, SemanticActivity> semanticActivities = new HashMap<>();
    /**
     * Tracks whether the user has requested an address. Becomes true when the user requests an
     * address and false when the address (or an error message) is delivered.
     * The user requests an address by pressing the Fetch Address button. This may happen
     * before GoogleApiClient connects. This activity uses this boolean to keep track of the
     * user's intent. If the value is true, the activity tries to fetch the address as soon as
     * GoogleApiClient connects.
     */
    private boolean mAddressRequested;
    private AddressResultReceiver mAddressResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testInitInstancesCreateAndLaunchNextActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);

        initData();
        initViews();
        if (semanticTimes.size() == 0)
            createSemanticTimes();
        mGoogleApiClient.connect();
    }

    private void initData() {
        mithrilDB = MithrilDBHelper.getHelper(this).getWritableDatabase();
        Gson retrieveDataGson = new Gson();
        String retrieveDataJson;
        Map<String, ?> allPrefs;
        try {
            allPrefs = sharedPreferences.getAll();
            for (Map.Entry<String, ?> aPref : allPrefs.entrySet()) {
                if (aPref.getKey().startsWith(MithrilAC.getPrefKeyContextTypeLocation())) {
                    retrieveDataJson = sharedPreferences.getString(aPref.getKey(), "");
                    if (!retrieveDataGson.fromJson(retrieveDataJson, SemanticLocation.class).isEnabled())
                        isThereLocationContextToSave = true;
                    //Filtering out by location keys the main key starts from after the word "Location"
                    semanticLocations.put(aPref.getKey().substring(8), retrieveDataGson.fromJson(retrieveDataJson, SemanticLocation.class));
                } else if (aPref.getKey().startsWith(MithrilAC.getPrefKeyContextTypeTemporal())) {
                    retrieveDataJson = sharedPreferences.getString(aPref.getKey(), "");
                    if (!retrieveDataGson.fromJson(retrieveDataJson, SemanticTime.class).isEnabled())
                        isThereTemporalContextToSave = true;
                    //Filtering out by location keys the main key starts from after the word "Temporal"
                    semanticTimes.put(aPref.getKey().substring(8), retrieveDataGson.fromJson(retrieveDataJson, SemanticTime.class));
                } else if (aPref.getKey().startsWith(MithrilAC.getPrefKeyContextTypePresence())) {
                    retrieveDataJson = sharedPreferences.getString(aPref.getKey(), "");
                    if (!retrieveDataGson.fromJson(retrieveDataJson, SemanticNearActor.class).isEnabled())
                        isTherePresenceContextToSave = true;
                    //Filtering out by location keys the main key starts from after the word "Presence"
                    semanticNearActors.put(aPref.getKey().substring(8), retrieveDataGson.fromJson(retrieveDataJson, SemanticNearActor.class));
                } else if (aPref.getKey().startsWith(MithrilAC.getPrefKeyContextTypeActivity())) {
                    retrieveDataJson = sharedPreferences.getString(aPref.getKey(), "");
                    if (!retrieveDataGson.fromJson(retrieveDataJson, SemanticActivity.class).isEnabled())
                        isThereActivityContextToSave = true;
                    //Filtering out by location keys the main key starts from after the word "Activity"
                    semanticActivities.put(aPref.getKey().substring(8), retrieveDataGson.fromJson(retrieveDataJson, SemanticActivity.class));
                }
            }
        } catch (NullPointerException e) {
            Log.d(MithrilAC.getDebugTag(), "Prefs empty somehow?!");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    private void initViews() {
        editor = getSharedPreferences(MithrilAC.getSharedPreferencesName(), Context.MODE_PRIVATE).edit();
        activityBaseTitle = getResources().getString(R.string.title_activity_instance_creation);

        mAddressResultReceiver = new AddressResultReceiver(new Handler(), this);
        // Set defaults, then update using values stored in the Bundle.
        mAddressRequested = false;

        setContentView(R.layout.activity_instance_creation);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation_menu);

        mOtherCtxtBtn = (Button) findViewById(R.id.otherCtxtBtn);
        mFirstMajorCtxtBtn = (Button) findViewById(R.id.firstCtxtBtn);
        mSecondMajorCtxtBtn = (Button) findViewById(R.id.secondCtxtBtn);
        mSaveFAB = (FloatingActionButton) findViewById(R.id.fab_save_instances);

        handleLocation();
        setOnNavigationListeners();
        setSaveBtnOnClickListener();

        // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient();
    }

    private void setOnNavigationListeners() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
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
                    case R.id.navigation_activity:
                        handleActivity();
                        return true;
                }
                return false;
            }
        });
    }

    private void handleLocation() {
        setTitle(activityBaseTitle + getApplicationContext().getResources().getString(R.string.text_instance_creation_location));

        mOtherCtxtBtn.setVisibility(View.VISIBLE);
        mFirstMajorCtxtBtn.setVisibility(View.VISIBLE);
        mSecondMajorCtxtBtn.setVisibility(View.VISIBLE);

        mOtherCtxtBtn.setText(R.string.pref_other_location_summary);
        mFirstMajorCtxtBtn.setText(R.string.pref_home_location_summary);
        mSecondMajorCtxtBtn.setText(R.string.pref_work_location_summary);

        if (!sharedPreferences.getBoolean(MithrilAC.getPrefKeyLocaInstancesCreated(), false)) {
            PermissionHelper.toast(getApplicationContext(), getApplicationContext().getResources().getString(R.string.tooltip_location), Toast.LENGTH_SHORT);
            editor.putBoolean(MithrilAC.getPrefKeyLocaInstancesCreated(), true);
            editor.apply();
        }

        currentFragment = FRAGMENT_LOCATION;
        refreshVisibleFragment();
        setOnClickListeners();
    }

    private void handleTemporal() {
        setTitle(activityBaseTitle + getApplicationContext().getResources().getString(R.string.text_instance_creation_temporal));

        mOtherCtxtBtn.setVisibility(View.GONE);
        mFirstMajorCtxtBtn.setVisibility(View.GONE);
        mSecondMajorCtxtBtn.setVisibility(View.GONE);

//        mOtherCtxtBtn.setText(R.string.pref_other_hours_context_summary);

        if (!sharedPreferences.getBoolean(MithrilAC.getPrefKeyTimeInstancesCreated(), false)) {
            PermissionHelper.toast(getApplicationContext(), getApplicationContext().getResources().getString(R.string.tooltip_temporal), Toast.LENGTH_SHORT);
            editor.putBoolean(MithrilAC.getPrefKeyTimeInstancesCreated(), true);
            editor.apply();
        }

        currentFragment = FRAGMENT_TEMPORAL;
        refreshVisibleFragment();
        setOnClickListeners();
    }

    private void handlePresence() {
        setTitle(activityBaseTitle + getApplicationContext().getResources().getString(R.string.text_instance_creation_presence_related));

        mOtherCtxtBtn.setVisibility(View.VISIBLE);
        mFirstMajorCtxtBtn.setVisibility(View.VISIBLE);
        mSecondMajorCtxtBtn.setVisibility(View.VISIBLE);

        mOtherCtxtBtn.setText(R.string.pref_presence_info_others_summary);
        mFirstMajorCtxtBtn.setText(R.string.pref_presence_info_supervisor_summary);
        mSecondMajorCtxtBtn.setText(R.string.pref_presence_info_colleague_summary);

        if (!sharedPreferences.getBoolean(MithrilAC.getPrefKeyPresInstancesCreated(), false)) {
            PermissionHelper.toast(getApplicationContext(), getApplicationContext().getResources().getString(R.string.tooltip_presence_related), Toast.LENGTH_SHORT);
            editor.putBoolean(MithrilAC.getPrefKeyTimeInstancesCreated(), true);
            editor.apply();
        }

        currentFragment = FRAGMENT_PRESENCE;
        refreshVisibleFragment();
        setOnClickListeners();
    }

    private void handleActivity() {
        setTitle(activityBaseTitle + getApplicationContext().getResources().getString(R.string.text_instance_creation_activity));

        mOtherCtxtBtn.setVisibility(View.VISIBLE);
        mFirstMajorCtxtBtn.setVisibility(View.VISIBLE);
        mSecondMajorCtxtBtn.setVisibility(View.VISIBLE);

        mOtherCtxtBtn.setText(R.string.pref_other_activity_context_title);
        mFirstMajorCtxtBtn.setText(R.string.pref_personal_activity_context_title);
        mSecondMajorCtxtBtn.setText(R.string.pref_professional_activity_context_title);

        if (!sharedPreferences.getBoolean(MithrilAC.getPrefKeyActiInstancesCreated(), false)) {
            PermissionHelper.toast(getApplicationContext(), getApplicationContext().getResources().getString(R.string.tooltip_activity), Toast.LENGTH_SHORT);
            editor.putBoolean(MithrilAC.getPrefKeyTimeInstancesCreated(), true);
            editor.apply();
        }

        currentFragment = FRAGMENT_ACTIVITY;
        refreshVisibleFragment();
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        if (currentFragment.equals(FRAGMENT_LOCATION)) {
            mOtherCtxtBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAutocompleteActivity(PLACE_AUTOCOMPLETE_REQUEST_CODE_MORE);
                }
            });

            mFirstMajorCtxtBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAutocompleteActivity(PLACE_AUTOCOMPLETE_REQUEST_CODE_HOME);
                }
            });

            mSecondMajorCtxtBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAutocompleteActivity(PLACE_AUTOCOMPLETE_REQUEST_CODE_WORK);
                }
            });
//        } else if (currentFragment.equals(FRAGMENT_TEMPORAL)) {
//            mOtherCtxtBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    chooseATemporalLabel();
//                }
//            });
        } else if (currentFragment.equals(FRAGMENT_PRESENCE)) {
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
        } else {//if(currentFragment.equals(FRAGMENT_ACTIVITY)){
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
        }
    }

    private void loadSemanticLocationFragment() {
        Bundle data = new Bundle();
        data.putParcelableArrayList(MithrilAC.getPrefKeyListOfLocationInstances(), new ArrayList<>(semanticLocations.values()));

        SemanticLocationFragment semanticLocationFragment = new SemanticLocationFragment();
        semanticLocationFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_instances, semanticLocationFragment)
                .commit();
    }

    private void loadSemanticTemporalFragment() {
        Bundle data = new Bundle();
        data.putParcelableArrayList(MithrilAC.getPrefKeyListOfTemporalInstances(), new ArrayList<>(semanticTimes.values()));

        SemanticTimeFragment semanticTimeFragment = new SemanticTimeFragment();
        semanticTimeFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_instances, semanticTimeFragment)
                .commit();
    }

    private void loadSemanticPresenceFragment() {
        Bundle data = new Bundle();
        data.putParcelableArrayList(MithrilAC.getPrefKeyListOfPresenceInstances(), new ArrayList<>(semanticNearActors.values()));

        SemanticNearActorFragment semanticNearActorFragment = new SemanticNearActorFragment();
        semanticNearActorFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_instances, semanticNearActorFragment)
                .commit();
    }

    private void loadSemanticActivityFragment() {
        Bundle data = new Bundle();
        data.putParcelableArrayList(MithrilAC.getPrefKeyListOfActivityInstances(), new ArrayList<>(semanticActivities.values()));

        SemanticActivityFragment semanticActivityFragment = new SemanticActivityFragment();
        semanticActivityFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_instances, semanticActivityFragment)
                .commit();
    }

    private void createSemanticTimes() {
        List<DayOfWeek> dayOfWeek = new ArrayList<>();
        dayOfWeek.add(DayOfWeek.Saturday);
        dayOfWeek.add(DayOfWeek.Sunday);

        /**
         * Weekend context creation
         */
        SemanticTime semanticTime = new SemanticTime(
                dayOfWeek,
                0,
                0,
                0,
                0,
                MithrilAC.getWeekend(),
                false,
                0
        );
        semanticTimes.put(semanticTime.getLabel(), semanticTime);

        /**********************************************************************************************************************/

        dayOfWeek.add(DayOfWeek.Monday);
        dayOfWeek.add(DayOfWeek.Tuesday);
        dayOfWeek.add(DayOfWeek.Wednesday);
        dayOfWeek.add(DayOfWeek.Thursday);
        dayOfWeek.add(DayOfWeek.Friday);

        /**
         * Anyday of week context creation
         */
        semanticTime = new SemanticTime(
                dayOfWeek,
                0,
                0,
                0,
                0,
                MithrilAC.getAnyday(),
                false,
                0
        );
        semanticTimes.put(semanticTime.getLabel(), semanticTime);

        /**
         * Breakfast hours context
         */
        semanticTime = new SemanticTime(
                dayOfWeek,
                8,
                0,
                8,
                30,
                MithrilAC.getPrefTimeIntervalBreakfastTemporalKey(),
                false,
                0
        );
        semanticTimes.put(semanticTime.getLabel(), semanticTime);

        /**
         * Lunch hours context
         */
        semanticTime = new SemanticTime(
                dayOfWeek,
                12,
                0,
                12,
                30,
                MithrilAC.getPrefTimeIntervalLunchTemporalKey(),
                false,
                0
        );
        semanticTimes.put(semanticTime.getLabel(), semanticTime);

        /**
         * Dinner hours context
         */
        semanticTime = new SemanticTime(
                dayOfWeek,
                19,
                0,
                19,
                30,
                MithrilAC.getPrefTimeIntervalDinnerTemporalKey(),
                false,
                0
        );
        semanticTimes.put(semanticTime.getLabel(), semanticTime);

        /**
         * DND hours context
         */
        semanticTime = new SemanticTime(
                dayOfWeek,
                21,
                0,
                8,
                0,
                MithrilAC.getPrefTimeIntervalDndTemporalKey(),
                false,
                0
        );
        semanticTimes.put(semanticTime.getLabel(), semanticTime);

        /**
         * Family_Time hours context
         */
        semanticTime = new SemanticTime(
                dayOfWeek,
                16,
                0,
                19,
                0,
                MithrilAC.getPrefTimeIntervalFamilyTemporalKey(),
                false,
                0
        );
        semanticTimes.put(semanticTime.getLabel(), semanticTime);

        /**
         * Alone_Time hours context
         */
        semanticTime = new SemanticTime(
                dayOfWeek,
                16,
                0,
                19,
                0,
                MithrilAC.getPrefTimeIntervalFamilyTemporalKey(),
                false,
                0
        );
        semanticTimes.put(semanticTime.getLabel(), semanticTime);

        /**********************************************************************************************************************/

        dayOfWeek.remove(DayOfWeek.Sunday);
        dayOfWeek.remove(DayOfWeek.Saturday);

        /**
         * Weekday context creation
         */
        semanticTime = new SemanticTime(
                dayOfWeek,
                0,
                0,
                0,
                0,
                MithrilAC.getWeekday(),
                false,
                0
        );
        semanticTimes.put(semanticTime.getLabel(), semanticTime);

        /**
         * Work_Morning hours context
         */
        semanticTime = new SemanticTime(
                dayOfWeek,
                8,
                30,
                12,
                0,
                MithrilAC.getPrefTimeIntervalWorkMorningTemporalKey(),
                false,
                0
        );
        semanticTimes.put(semanticTime.getLabel(), semanticTime);

        /**********************************************************************************************************************/

        dayOfWeek.add(DayOfWeek.Saturday);
        dayOfWeek.add(DayOfWeek.Sunday);

        /**********************************************************************************************************************/

        dayOfWeek.remove(DayOfWeek.Sunday);
        dayOfWeek.remove(DayOfWeek.Saturday);

        /**
         * Work_Afternoon hours context
         */
        semanticTime = new SemanticTime(
                dayOfWeek,
                12,
                30,
                16,
                0,
                MithrilAC.getPrefTimeIntervalWorkAfternoonTemporalKey(),
                false,
                0
        );
        semanticTimes.put(semanticTime.getLabel(), semanticTime);

        /**********************************************************************************************************************/

        dayOfWeek.add(DayOfWeek.Saturday);
        dayOfWeek.add(DayOfWeek.Sunday);

        /**
         * Personal hours context
         */
        semanticTime = new SemanticTime(
                dayOfWeek,
                19,
                30,
                21,
                0,
                MithrilAC.getPrefTimeIntervalPersonalTemporalKey(),
                false,
                0
        );
        semanticTimes.put(semanticTime.getLabel(), semanticTime);

        isThereTemporalContextToSave = true;
    }

    private void setSaveBtnOnClickListener() {
        mSaveFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveContext();
            }
        });
    }

    private void saveContext() {
        if (isThereLocationContextToSave)
            setupLocationAwareness();
        if (isThereTemporalContextToSave)
            setupTemporalAwareness();
        if (isTherePresenceContextToSave)
            setupPresenceAwareness();
        if (isThereActivityContextToSave)
            setupActivityAwareness();
    }

    private void setupLocationAwareness() {
        for (Map.Entry<String, SemanticLocation> contextEntry : semanticLocations.entrySet()) {
            contextEntry.getValue().setEnabled(true);
            addContext(contextEntry.getValue().getType(),
                    contextEntry.getKey(),
                    InstanceCreationActivity.contextDataStoreGson.toJson(contextEntry.getValue()),
                    contextEntry.getValue().getLevel());
            semanticLocations.put(contextEntry.getKey(), contextEntry.getValue());
        }
        isThereLocationContextToSave = false;
        setupGeofences();
        refreshVisibleFragment();
    }

    private void setupTemporalAwareness() {
        for (Map.Entry<String, SemanticTime> contextEntry : semanticTimes.entrySet()) {
            contextEntry.getValue().setEnabled(true);
            addContext(contextEntry.getValue().getType(),
                    contextEntry.getKey(),
                    InstanceCreationActivity.contextDataStoreGson.toJson(contextEntry.getValue()),
                    contextEntry.getValue().getLevel());
            semanticTimes.put(contextEntry.getKey(), contextEntry.getValue());
        }
        isThereTemporalContextToSave = false;
        setTemporalFences();
        refreshVisibleFragment();
    }

    private void setupPresenceAwareness() {
        for (Map.Entry<String, SemanticNearActor> contextEntry : semanticNearActors.entrySet()) {
            contextEntry.getValue().setEnabled(true);
            addContext(contextEntry.getValue().getType(),
                    contextEntry.getKey(),
                    InstanceCreationActivity.contextDataStoreGson.toJson(contextEntry.getValue()),
                    contextEntry.getValue().getLevel());
            semanticNearActors.put(contextEntry.getKey(), contextEntry.getValue());
        }
        isTherePresenceContextToSave = false;
        refreshVisibleFragment();
    }

    private void setupActivityAwareness() {
        for (Map.Entry<String, SemanticActivity> contextEntry : semanticActivities.entrySet()) {
            contextEntry.getValue().setEnabled(true);
            addContext(contextEntry.getValue().getType(),
                    contextEntry.getKey(),
                    InstanceCreationActivity.contextDataStoreGson.toJson(contextEntry.getValue()),
                    contextEntry.getValue().getLevel());
            semanticActivities.put(contextEntry.getKey(), contextEntry.getValue());
        }
        isThereActivityContextToSave = false;
        refreshVisibleFragment();
    }

    private void refreshVisibleFragment() {
        //Context pieces have been added; update view
        if (currentFragment.equals(FRAGMENT_LOCATION))
            loadSemanticLocationFragment();
        else if (currentFragment.equals(FRAGMENT_TEMPORAL))
            loadSemanticTemporalFragment();
        else if (currentFragment.equals(FRAGMENT_PRESENCE))
            loadSemanticPresenceFragment();
        else if (currentFragment.equals(FRAGMENT_TEMPORAL))
            loadSemanticActivityFragment();
    }

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
            editor.putBoolean(MithrilAC.getPrefKeyLocaInstancesCreated(), true);
            editor.putBoolean(MithrilAC.getPrefKeyPresInstancesCreated(), true);
            editor.putBoolean(MithrilAC.getPrefKeyActiInstancesCreated(), true);
            editor.putBoolean(MithrilAC.getPrefKeyTimeInstancesCreated(), true);
            editor.apply();
            if (!sharedPreferences.getBoolean(MithrilAC.getPrefKeyPoliciesDownloaded(), false)) {
                startNextActivity(this, DownloadPoliciesActivity.class);
            } else
                startNextActivity(this, CoreActivity.class);
        }
        return super.onOptionsItemSelected(item);
    }

    private void testInitInstancesCreateAndLaunchNextActivity() {
        sharedPreferences = getApplicationContext().getSharedPreferences(MithrilAC.getSharedPreferencesName(), Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(MithrilAC.getPrefKeyLocaInstancesCreated(), false) &&
                sharedPreferences.getBoolean(MithrilAC.getPrefKeyPresInstancesCreated(), false) &&
                sharedPreferences.getBoolean(MithrilAC.getPrefKeyActiInstancesCreated(), false) &&
                sharedPreferences.getBoolean(MithrilAC.getPrefKeyTimeInstancesCreated(), false))
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
        saveContext();
        Intent launchNextActivity = new Intent(context, activityClass);
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(launchNextActivity);
    }

    private void openTemporalDataEntryActivity(int requestCode, String label) {
        List<DayOfWeek> dayOfWeek = new ArrayList<>();
        dayOfWeek.add(DayOfWeek.Sunday);
        dayOfWeek.add(DayOfWeek.Monday);
        dayOfWeek.add(DayOfWeek.Tuesday);
        dayOfWeek.add(DayOfWeek.Wednesday);
        dayOfWeek.add(DayOfWeek.Thursday);
        dayOfWeek.add(DayOfWeek.Friday);
        dayOfWeek.add(DayOfWeek.Saturday);

        SemanticTime semanticTime = new SemanticTime(dayOfWeek, 0, 0, 0, 0, label, false, 0);

        Intent intent = new Intent(this, TemporalDataEntryActivity.class);
        intent.putExtra(MithrilAC.getPrefKeyTemporalLabel(), label);
        intent.putExtra(MithrilAC.getPrefKeyContextTypeTemporal(), semanticTime);
        startActivityForResult(intent, requestCode);
    }

    private void openAutocompleteActivity(int requestCode) {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this);
            startActivityForResult(intent, requestCode);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Log.e(MithrilAC.getDebugTag(), message);
            PermissionHelper.toast(this, message, Toast.LENGTH_SHORT);
        }
    }

    /**
     * A location was selected by the user for their home/work address
     * EDUCATION MOMENT (from http://android-er.blogspot.com/2013/02/convert-between-latlng-and-location.html):
     * //Convert LatLng to Location
     * Location location = new Location("Test");
     * location.setLatitude(point.latitude);
     * location.setLongitude(point.longitude);
     * location.setDetectedAtTime(new Date().getDetectedAtTime()); //Set time as current Date
     * info.setText(location.toString());
     * <p>
     * //Convert Location to LatLng
     * LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
     * Called after the autocomplete activity has finished to return its result.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PLACE_AUTOCOMPLETE_REQUEST_CODE_HOME: {
                if (resultCode == Activity.RESULT_OK) {
                    isThereLocationContextToSave = true;
                    setHomeSemanticLocation(PlaceAutocomplete.getPlace(this, data));
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    Log.e(MithrilAC.getDebugTag(), "Error: Status = " + status.toString());
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // Indicates that the activity closed before a selection was made. For example if the user pressed the back button.
                }
                break;
            }
            case PLACE_AUTOCOMPLETE_REQUEST_CODE_WORK: {
                if (resultCode == Activity.RESULT_OK) {
                    isThereLocationContextToSave = true;
                    setWorkSemanticLocation(PlaceAutocomplete.getPlace(this, data));
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    Log.e(MithrilAC.getDebugTag(), "Error: Status = " + status.toString());
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // Indicates that the activity closed before a selection was made. For example if the user pressed the back button.
                }
                break;
            }
            case PLACE_AUTOCOMPLETE_REQUEST_CODE_MORE: {
                if (resultCode == Activity.RESULT_OK) {
                    isThereLocationContextToSave = true;
                    setMoreSemanticLocation(PlaceAutocomplete.getPlace(this, data));
                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    Log.e(MithrilAC.getDebugTag(), "Error: Status = " + status.toString());
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // Indicates that the activity closed before a selection was made. For example if the user pressed the back button.
                }
                break;
            }
//            case TIME_REQUEST_CODE_WORK: {
//                if (resultCode == Activity.RESULT_OK) {
//                    isThereTemporalContextToSave = true;
//                    setSemanticTemporal(data);
//                } else if (resultCode == Activity.RESULT_CANCELED) {
//                    // Indicates that the activity closed before a selection was made. For example if the user pressed the back button.
//                }
//                break;
//            }
//            case TIME_REQUEST_CODE_DND: {
//                if (resultCode == Activity.RESULT_OK) {
//                    isThereTemporalContextToSave = true;
//                    setSemanticTemporal(data);
//                } else if (resultCode == Activity.RESULT_CANCELED) {
//                    // Indicates that the activity closed before a selection was made. For example if the user pressed the back button.
//                }
//                break;
//            }
            case TIME_REQUEST_CODE_MORE: {
                if (resultCode == Activity.RESULT_OK) {
                    isThereTemporalContextToSave = true;
                    setSemanticTemporal(data);
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // Indicates that the activity closed before a selection was made. For example if the user pressed the back button.
                }
                break;
            }
            case GEOFENCE_REQUEST_CODE: {
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(MithrilAC.getDebugTag(), "Geofences successfully setup! All good...");
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.d(MithrilAC.getDebugTag(), "Are we in trouble?");
                }
                break;
            }
        }
    }

    private void setSemanticTemporal(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            String label = bundle.getString(MithrilAC.getPrefKeyTemporalLabel());
            SemanticTime semanticTime = bundle.getParcelable(MithrilAC.getPrefKeyContextTypeTemporal());
            semanticTimes.put(label, semanticTime);
        }
        bottomNavigationView.setSelectedItemId(R.id.navigation_temporal);
    }

    private void setHomeSemanticLocation(Place place) {
        Location userInputLocation = new Location("placesAPI");
        userInputLocation.setLatitude(place.getLatLng().latitude);
        userInputLocation.setLongitude(place.getLatLng().longitude);

        SemanticLocation semanticLocation = new SemanticLocation(MithrilAC.getPrefHomeLocationKey(), userInputLocation, 0);
        semanticLocation.setName(place.getName().toString());
        Log.d(MithrilAC.getDebugTag(), place.getName().toString());
        semanticLocation.setPlaceId(place.getId());
        semanticLocation.setPlaceTypes(place.getPlaceTypes());

        semanticLocations.put(MithrilAC.getPrefHomeLocationKey(), semanticLocation);
        /**
         * We know the location has changed, let's check the address
         */
        mAddressRequested = true;
        startSearchAddressIntentService(userInputLocation, MithrilAC.getPrefHomeLocationKey());
    }

    private void setWorkSemanticLocation(Place place) {
        Location userInputLocation = new Location("placesAPI");
        userInputLocation.setLatitude(place.getLatLng().latitude);
        userInputLocation.setLongitude(place.getLatLng().longitude);

        SemanticLocation semanticLocation = new SemanticLocation(MithrilAC.getPrefWorkLocationKey(), userInputLocation, 0);
        semanticLocation.setName(place.getName().toString());
        semanticLocation.setPlaceId(place.getId());
        semanticLocation.setPlaceTypes(place.getPlaceTypes());

        semanticLocations.put(MithrilAC.getPrefWorkLocationKey(), semanticLocation);
        /**
         * We know the location has changed, let's check the address
         */
        mAddressRequested = true;
        startSearchAddressIntentService(userInputLocation, MithrilAC.getPrefWorkLocationKey());
    }

    private void setMoreSemanticLocation(final Place place) {
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice);
        final String[] listOfLocationContextPiecesFromTheOntology = MithrilAC.getContextArrayLocation();
        for (int index = 0; index < listOfLocationContextPiecesFromTheOntology.length; index++)
            arrayAdapter.add(listOfLocationContextPiecesFromTheOntology[index]);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.drawable.map_marker);
        dialog.setTitle("What location are you adding?");
        dialog.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String semanticLocationLabel = arrayAdapter.getItem(which);

                Location userInputLocation = new Location("placesAPI");
                userInputLocation.setLatitude(place.getLatLng().latitude);
                userInputLocation.setLongitude(place.getLatLng().longitude);

                SemanticLocation semanticLocation = new SemanticLocation(semanticLocationLabel, userInputLocation, 0);
                semanticLocation.setName(place.getName().toString());
                semanticLocation.setPlaceId(place.getId());
                semanticLocation.setPlaceTypes(place.getPlaceTypes());

                semanticLocations.put(semanticLocationLabel, semanticLocation);
                /**
                 * We know the location has changed, let's check the address
                 */
                mAddressRequested = true;
                startSearchAddressIntentService(userInputLocation, semanticLocationLabel);
            }
        });
        dialog.show();
    }

    private void chooseATemporalLabel() {
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_singlechoice);
        final String[] listOfTimeContextPiecesFromTheOntology = MithrilAC.getContextArrayTime();
        for (int index = 0; index < listOfTimeContextPiecesFromTheOntology.length; index++)
            arrayAdapter.add(listOfTimeContextPiecesFromTheOntology[index]);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.drawable.calendar_clock);
        dialog.setTitle("What time based context are you adding?");
        dialog.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openTemporalDataEntryActivity(TIME_REQUEST_CODE_MORE, arrayAdapter.getItem(which));
            }
        });
        dialog.show();
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startSearchAddressIntentService(Location location, String key) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        intent.putExtra(MithrilAC.getAddressRequestedExtra(), mAddressRequested);
        intent.putExtra(MithrilAC.getCurrAddressKey(), key);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(MithrilAC.getAppReceiver(), mAddressResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(MithrilAC.getLocationDataExtra(), location);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    private void setupGeofences() {
        Intent intent = new Intent(this, SetupGeofencesActivity.class);
        ArrayList<SemanticLocation> tempList = new ArrayList<>(semanticLocations.values());
        intent.putParcelableArrayListExtra(MithrilAC.getPrefKeyGeofenceList(), tempList);
        startActivityForResult(intent, GEOFENCE_REQUEST_CODE);
    }

    private void setTemporalFences() {
        //TODO IMPORTANT Setup the snapshot time fence thingy from:
//        Intent intent = new Intent(this, SetupTimefencesActivity.class);
//        ArrayList<SemanticTime> tempList = new ArrayList<>(semanticTimes.values());
//        intent.putParcelableArrayListExtra(MithrilAC.getPrefKeyTimefenceList(), tempList);
//        startActivityForResult(intent, TIMEFENCE_REQUEST_CODE);
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(MithrilAC.getDebugTag(), "Connected to GoogleApiClient");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(MithrilAC.getDebugTag(), "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        Log.i(MithrilAC.getDebugTag(), "Connection suspended");

        // onConnected() will be called again automatically when the service reconnects
    }

    private void addContextToDB(String contextType, String contextLabel, int level) {
        MithrilDBHelper.getHelper(this).addContext(mithrilDB, contextType, contextLabel, true, level);
    }

    private void addContext(String contextType, String contextLabel, String serializedJsonContext, int level) {
        editor.putString(contextType + contextLabel, serializedJsonContext);
        editor.apply();
        addContextToDB(contextType, contextLabel, level);
    }

    @Override
    public void onListFragmentInteraction(SemanticTime item) {

    }

    @Override
    public void onListFragmentInteraction(SemanticLocation item) {

    }

    @Override
    public void onListFragmentInteraction(SemanticActivity item) {

    }

    @Override
    public void onListFragmentInteraction(SemanticNearActor item) {

    }

    private class AddressResultReceiver extends ResultReceiver {
        private Context context;
        /**
         * The formatted location address.
         */
        private Address mAddressOutput;
        /**
         * Tracks whether the user has requested an address. Becomes true when the user requests an
         * address and false when the address (or an error message) is delivered.
         * The user requests an address by pressing the Fetch Address button. This may happen
         * before GoogleApiClient connects. This activity uses this boolean to keep track of the
         * user's intent. If the value is true, the activity tries to fetch the address as soon as
         * GoogleApiClient connects.
         */
        private boolean mAddressRequested;

        public AddressResultReceiver(Handler handler, Context aContext) {
            super(handler);

            context = aContext;
            // Set defaults, then update using values stored in the Bundle.
            mAddressRequested = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                mAddressOutput = new Address(context.getResources().getConfiguration().getLocales().get(0));
            else
                mAddressOutput = new Address(context.getResources().getConfiguration().locale);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            mAddressRequested = resultData.getBoolean(MithrilAC.getAddressRequestedExtra(), false);
            String key = resultData.getString(MithrilAC.getCurrAddressKey(), null);
            if (key.equals(null))
                throw new AddressKeyMissingError();
            else
                storeAddressInSharedPreferences(key, resultCode, resultData);
        }

        protected void storeAddressInSharedPreferences(String key, int resultCode, Bundle resultData) {
            // Display the address string
            // or an error message sent from the intent service.
            Gson gson = new Gson();
            String json = resultData.getString(MithrilAC.getResultDataKey(), "");
            try {
                mAddressOutput = gson.fromJson(json, Address.class);
            } catch (JsonSyntaxException e) {
                Log.d(MithrilAC.getDebugTag(), e.getMessage());
            }

            Log.d(MithrilAC.getDebugTag(), "Prefs address " + resultData.getString(MithrilAC.getCurrAddressKey()) + mAddressRequested + key + json);
            // Show a toast message if an address was found.
            if (resultCode == MithrilAC.SUCCESS_RESULT) {
                SemanticLocation tempSemanticLocation = null;
                for (Map.Entry<String, SemanticLocation> semanticLocation : semanticLocations.entrySet())
                    if (semanticLocation.getKey().equals(key))
                        tempSemanticLocation = semanticLocation.getValue();
                tempSemanticLocation.setAddress(mAddressOutput);

                Address address = tempSemanticLocation.getAddress();
                Location location = tempSemanticLocation.getLocation();
                String placeId = tempSemanticLocation.getPlaceId();
                List<Integer> placeTypes = tempSemanticLocation.getPlaceTypes();

                semanticLocations.put(key, new SemanticLocation(location, address,
                        key,
                        false, tempSemanticLocation.getName(), placeId, placeTypes, false, 0));
                //Let's go to null island!!!
                location.setLatitude(0);
                location.setLongitude(0);
                semanticLocations.put(key+"_Street", new SemanticLocation(location, address,
                        key+"_Street",
                        false, address.getThoroughfare(), placeId, placeTypes, false, 1));
                semanticLocations.put(key+"_City", new SemanticLocation(location, address,
                        key+"_City",
                        false, address.getLocality(), placeId, placeTypes, false, 2));
                semanticLocations.put(key+"_State", new SemanticLocation(location, address,
                        key+"_State",
                        false, address.getAdminArea(), placeId, placeTypes, false, 3));
                semanticLocations.put(key+"_Country", new SemanticLocation(location, address,
                        key+"_Country",
                        false, address.getCountryName(), placeId, placeTypes, false, 4));

                refreshVisibleFragment();
            }
            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;
        }
    }
}