package edu.umbc.ebiquity.mithril.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
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
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticActivity;
import edu.umbc.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticLocation;
import edu.umbc.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticNearActor;
import edu.umbc.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticTime;
import edu.umbc.ebiquity.mithril.ui.fragments.instancecreationactivityfragments.SemanticActivityFragment;
import edu.umbc.ebiquity.mithril.ui.fragments.instancecreationactivityfragments.SemanticLocationFragment;
import edu.umbc.ebiquity.mithril.ui.fragments.instancecreationactivityfragments.SemanticNearActorFragment;
import edu.umbc.ebiquity.mithril.ui.fragments.instancecreationactivityfragments.SemanticTimeFragment;
import edu.umbc.ebiquity.mithril.util.services.FetchAddressIntentService;
import edu.umbc.ebiquity.mithril.util.services.GeofenceTransitionsIntentService;
import edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions.AddressKeyMissingError;
import edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions.GeofenceErrorMessages;
import edu.umbc.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

public class InstanceCreationActivity extends AppCompatActivity
        implements SemanticTimeFragment.OnListFragmentInteractionListener,
        SemanticLocationFragment.OnListFragmentInteractionListener,
        SemanticNearActorFragment.OnListFragmentInteractionListener,
        SemanticActivityFragment.OnListFragmentInteractionListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status> {

    private static final String FRAGMENT_LOCATION = "location";
    private static final String FRAGMENT_PRESENCE = "presence";
    private static final String FRAGMENT_TEMPORAL = "temporal";
    private static final String FRAGMENT_ACTIVITY = "activity";
    private static String currentFragment = new String();
    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE_HOME = 1;
    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE_WORK = 2;
    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE_MORE = 3;
    /**
     * Provides the entry point to Google Play services: Geo fence
     */
    protected GoogleApiClient mGoogleApiClient;
    /**
     * The list of geofences used in this sample.
     */
    protected ArrayList<Geofence> mGeofenceList = new ArrayList<>();
    private BottomNavigationView navigation;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private String activityBaseTitle;
    private Button mOtherCtxtBtn;
    private Button mFirstMajorCtxtBtn;
    private Button mSecondMajorCtxtBtn;
    private FloatingActionButton mSaveFAB;
    private boolean isThereSomethingToSave = false;
    private List<SemanticLocation> semanticLocations = new ArrayList<>();
    private List<SemanticNearActor> semanticNearActors = new ArrayList<>();
    private List<SemanticTime> semanticTimes = new ArrayList<>();
    private List<SemanticActivity> semanticActivities = new ArrayList<>();
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
    /**
     * Used to keep track of whether geofences were added.
     */
    private boolean mGeofencesAdded;
    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;

    /**
     * Helper method to format information about a place nicely.
     */
    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e(MithrilApplication.getDebugTag(), res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testInitInstancesCreateAndLaunchNextActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);

        init();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        editor.putBoolean(MithrilApplication.getPrefKeyLocationInstance(), false);
        editor.putBoolean(MithrilApplication.getPrefKeyTemporalInstance(), false);
        editor.putBoolean(MithrilApplication.getPrefKeyPresenceInstance(), false);
        editor.putBoolean(MithrilApplication.getPrefKeyActivityInstance(), false);
        editor.apply();
    }

    private void init() {
        editor = getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE).edit();
        activityBaseTitle = getApplicationContext().getResources().getString(R.string.title_activity_instance_creation);

        mAddressResultReceiver = new AddressResultReceiver(new Handler(), this);
        // Set defaults, then update using values stored in the Bundle.
        mAddressRequested = false;

        setContentView(R.layout.activity_instance_creation);
        navigation = (BottomNavigationView) findViewById(R.id.navigation_menu);

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

        mOtherCtxtBtn.setText(R.string.pref_other_location_summary);
        mFirstMajorCtxtBtn.setText(R.string.pref_home_location_summary);
        mSecondMajorCtxtBtn.setText(R.string.pref_work_location_summary);

        if (!sharedPreferences.getBoolean(MithrilApplication.getPrefKeyLocationInstance(), false)) {
            PermissionHelper.toast(getApplicationContext(), getApplicationContext().getResources().getString(R.string.tooltip_location), Toast.LENGTH_SHORT);
            editor.putBoolean(MithrilApplication.getPrefKeyLocationInstance(), true);
            editor.apply();
        }
        currentFragment = FRAGMENT_LOCATION;
        loadSemanticLocationFragment();
        setOnClickListeners();
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
        currentFragment = FRAGMENT_TEMPORAL;
        loadSemanticTemporalFragment();
        setOnClickListeners();
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
        currentFragment = FRAGMENT_PRESENCE;
        loadSemanticPresenceFragment();
        setOnClickListeners();
    }

    private void handleActivity() {
        setTitle(activityBaseTitle+getApplicationContext().getResources().getString(R.string.text_instance_creation_activity));

        mOtherCtxtBtn.setText(R.string.pref_other_activity_context_title);
        mFirstMajorCtxtBtn.setText(R.string.pref_personal_activity_context_title);
        mSecondMajorCtxtBtn.setText(R.string.pref_professional_activity_context_title);

        if (!sharedPreferences.getBoolean(MithrilApplication.getPrefKeyActivityInstance(), false)) {
            PermissionHelper.toast(getApplicationContext(), getApplicationContext().getResources().getString(R.string.tooltip_activity), Toast.LENGTH_SHORT);
            editor.putBoolean(MithrilApplication.getPrefKeyActivityInstance(), true);
            editor.apply();
        }
        currentFragment = FRAGMENT_ACTIVITY;
        loadSemanticActivityFragment();
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
        } else if (currentFragment.equals(FRAGMENT_TEMPORAL)) {
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
        data.putParcelableList(MithrilApplication.getPrefKeyLocationInstances(), semanticLocations);

        SemanticLocationFragment semanticLocationFragment = new SemanticLocationFragment();
        semanticLocationFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_instances, semanticLocationFragment)
                .commit();
    }

    private void loadSemanticTemporalFragment() {
        Bundle data = new Bundle();
        data.putParcelableList(MithrilApplication.getPrefKeyTemporalInstances(), semanticTimes);

        SemanticTimeFragment semanticTimeFragment = new SemanticTimeFragment();
        semanticTimeFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_instances, new SemanticTimeFragment())
                .commit();
    }

    private void loadSemanticPresenceFragment() {
        Bundle data = new Bundle();
        data.putParcelableList(MithrilApplication.getPrefKeyTemporalInstances(), semanticNearActors);

        SemanticNearActorFragment semanticNearActorFragment = new SemanticNearActorFragment();
        semanticNearActorFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_instances, new SemanticNearActorFragment())
                .commit();
    }

    private void loadSemanticActivityFragment() {
        Bundle data = new Bundle();
        data.putParcelableList(MithrilApplication.getPrefKeyTemporalInstances(), semanticActivities);

        SemanticActivityFragment semanticActivityFragment = new SemanticActivityFragment();
        semanticActivityFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_instances, new SemanticActivityFragment())
                .commit();
    }

    private void setSaveBtnOnClickListener() {
        mSaveFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isThereSomethingToSave) {
                    if (currentFragment.equals(FRAGMENT_LOCATION))
                        setGeoFences();
                    else if (currentFragment.equals(FRAGMENT_TEMPORAL))
                        setTemporalAlarms();
                    else if (currentFragment.equals(FRAGMENT_TEMPORAL))
                        setNearby();
                    else
                        setActivityDetection();
                }
            }
        });
    }

    private void setGeoFences() {

    }

    private void setTemporalAlarms() {

    }

    private void setNearby() {

    }

    private void setActivityDetection() {

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
            startNextActivity(this, CoreActivity.class);
            editor.putBoolean(MithrilApplication.getPrefKeyInitInstancesCreated(), true);
            editor.apply();
        }
        return super.onOptionsItemSelected(item);
    }

    private void testInitInstancesCreateAndLaunchNextActivity() {
        sharedPreferences = getApplicationContext().getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE);
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

    @Override
    public void onListFragmentInteraction(SemanticActivity item) {

    }

    @Override
    public void onListFragmentInteraction(SemanticNearActor item) {

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

            Log.e(MithrilApplication.getDebugTag(), message);
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
     * location.setTime(new Date().getTime()); //Set time as current Date
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
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_HOME) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                editor.putString(MithrilApplication.getPrefHomeLocationKey(), place.getAddress().toString());
                editor.apply();

                Location userInputLocation = new Location("placesAPI");
                userInputLocation.setLatitude(place.getLatLng().latitude);
                userInputLocation.setLongitude(place.getLatLng().longitude);

                semanticLocations.add(new SemanticLocation(MithrilApplication.getPrefHomeLocationKey(), userInputLocation));

                /**
                 * We know the location has changed, let's check the address
                 */
                mAddressRequested = true;
                startSearchAddressIntentService(userInputLocation, MithrilApplication.getPrefHomeLocationKey());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e(MithrilApplication.getDebugTag(), "Error: Status = " + status.toString());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if the user pressed the back button.
            }
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_WORK) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);

                editor.putString(MithrilApplication.getPrefWorkLocationKey(), place.getAddress().toString());
                editor.apply();

                Location userInputLocation = new Location("placesAPI");
                userInputLocation.setLatitude(place.getLatLng().latitude);
                userInputLocation.setLongitude(place.getLatLng().longitude);

                semanticLocations.add(new SemanticLocation(MithrilApplication.getPrefWorkLocationKey(), userInputLocation));

                /**
                 * We know the location has changed, let's check the address
                 */
                mAddressRequested = true;
                startSearchAddressIntentService(userInputLocation, MithrilApplication.getPrefWorkLocationKey());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e(MithrilApplication.getDebugTag(), "Error: Status = " + status.toString());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if the user pressed the back button.
            }
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_MORE) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                getOtherSemanticLocationLabel(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e(MithrilApplication.getDebugTag(), "Error: Status = " + status.toString());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Indicates that the activity closed before a selection was made. For example if the user pressed the back button.
            }
        }
    }

    private void getOtherSemanticLocationLabel(final Place place) {
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(InstanceCreationActivity.this, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("Grocery Store");
        arrayAdapter.add("Pub");
        arrayAdapter.add("Restaurant");
        arrayAdapter.add("Movie");
        arrayAdapter.add("Mall");
        arrayAdapter.add("Gym");

        AlertDialog.Builder dialog = new AlertDialog.Builder(InstanceCreationActivity.this);
        dialog.setIcon(R.drawable.map_marker);
        dialog.setTitle("Select a location label:");
        dialog.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String semanticLocationLabel = arrayAdapter.getItem(which);

                editor.putString(semanticLocationLabel, place.getAddress().toString());
                editor.apply();

                Location userInputLocation = new Location("placesAPI");
                userInputLocation.setLatitude(place.getLatLng().latitude);
                userInputLocation.setLongitude(place.getLatLng().longitude);

                semanticLocations.add(new SemanticLocation(semanticLocationLabel, userInputLocation));

                /**
                 * We know the location has changed, let's check the address
                 */
                mAddressRequested = true;

                startSearchAddressIntentService(userInputLocation, semanticLocationLabel);
            }
        });
        dialog.show();
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startSearchAddressIntentService(Location location, String key) {
        //Locations have been added; update view
        loadSemanticLocationFragment();

        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        intent.putExtra(MithrilApplication.ADDRESS_REQUESTED_EXTRA, mAddressRequested);
        intent.putExtra(MithrilApplication.ADDRESS_KEY, key);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(MithrilApplication.RECEIVER, mAddressResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(MithrilApplication.LOCATION_DATA_EXTRA, location);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
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
        Log.i(MithrilApplication.getDebugTag(), "Connected to GoogleApiClient");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(MithrilApplication.getDebugTag(), "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        Log.i(MithrilApplication.getDebugTag(), "Connection suspended");

        // onConnected() will be called again automatically when the service reconnects
    }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    /**
     * Adds geofences, which sets alerts to be notified when the device enters or exits one of the
     * specified geofences. Handles the success or failure results returned by addGeofences().
     */
    public void addGeofences() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    /**
     * Removes geofences, which stops further notifications when the device enters or exits
     * previously registered geofences.
     */
    public void removeGeofences() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            // Remove geofences.
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    // This is the same pending intent that was used in addGeofences().
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e(MithrilApplication.getDebugTag(), "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    /**
     * Runs when the result of calling addGeofences() and removeGeofences() becomes available.
     * Either method can complete successfully or with an error.
     * <p>
     * Since this activity implements the {@link ResultCallback} interface, we are required to
     * define this method.
     *
     * @param status The Status returned through a PendingIntent when addGeofences() or
     *               removeGeofences() get called.
     */
    public void onResult(Status status) {
        if (status.isSuccess()) {
            // Update state and save in shared preferences.
            mGeofencesAdded = !mGeofencesAdded;
//            SharedPreferences.Editor editor = sharedPrefs.edit();
//            editor.putBoolean(MithrilApplication.GEOFENCES_ADDED_KEY, mGeofencesAdded);
//            editor.apply();

            // Update the UI. Adding geofences enables the Remove Geofences button, and removing
            // geofences enables the Add Geofences button.
//            setButtonsEnabledState();

            Toast.makeText(
                    this,
                    getString(mGeofencesAdded ? R.string.geofences_added :
                            R.string.geofences_removed),
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
            Log.e(MithrilApplication.getDebugTag(), errorMessage);
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * This sample hard codes geofence data. A real app might dynamically create geofences based on
     * the user's location.
     */
    public void populateGeofenceList(String semanticIdentifier, double latitude, double longitude) {
//        for (Map.Entry<String, LatLng> entry : MithrilApplication.BALTIMORE_COUNTY_LANDMARKS.entrySet()) {

        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(semanticIdentifier)

                // Set the circular region of this geofence.
                .setCircularRegion(
                        latitude,
                        longitude,
                        MithrilApplication.GEOFENCE_RADIUS_IN_METERS
                )

                // Set the expiration duration of the geofence. This geofence gets automatically
                // removed after this period of time.
                .setExpirationDuration(Geofence.NEVER_EXPIRE)

                // Set the transition types of interest. Alerts are only generated for these
                // transition. We track entry and exit transitions in this sample.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)

                // Create the geofence.
                .build());
//        }
    }

    class AddressResultReceiver extends ResultReceiver {
        private Context context;
        private SharedPreferences sharedPref;
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
            sharedPref = context.getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            mAddressRequested = resultData.getBoolean(MithrilApplication.ADDRESS_REQUESTED_EXTRA, false);
            String key = resultData.getString(MithrilApplication.ADDRESS_KEY, null);
            if (key.equals(null))
                throw new AddressKeyMissingError();
            else
                storeAddressInDB(key, resultCode, resultData);
        }

        protected void storeAddressInDB(String key, int resultCode, Bundle resultData) {
            // Display the address string
            // or an error message sent from the intent service.
            Gson gson = new Gson();
            String json = resultData.getString(MithrilApplication.RESULT_DATA_KEY, "");
            try {
                mAddressOutput = gson.fromJson(json, Address.class);
            } catch (JsonSyntaxException e) {
                Log.d(MithrilApplication.getDebugTag(), e.getMessage());
            }

            Log.d(MithrilApplication.getDebugTag(), "Prefs address " + resultData.getString(MithrilApplication.ADDRESS_KEY) + mAddressRequested + key + json);
            // Show a toast message if an address was found.
            if (resultCode == MithrilApplication.SUCCESS_RESULT) {
//                Log.d(MithrilApplication.getDebugMithrilApplication.getDebugTag()(), getString(R.string.address_found) + ":" + mAddressOutput);
//                PermissionHelper.toast(context, getString(R.string.address_found) + ":" + mAddressOutput);
                storeInSharedPreferences(resultData.getString(
                        key),
                        mAddressOutput);
            }
            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;
        }

        /**
         * From http://stackoverflow.com/a/18463758/1816861
         *
         * @param key
         * @param address To Retreive
         *                Gson gson = new Gson();
         *                String json = mPrefs.getString("MyObject", "");
         *                MyObject obj = gson.fromJson(json, MyObject.class);
         */
        public void storeInSharedPreferences(String key, Address address) {
            SharedPreferences.Editor editor = context.getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE).edit();
            Gson gson = new Gson();
            String json = gson.toJson(address);
            editor.putString(key, json);
            editor.apply();
        }
    }
}