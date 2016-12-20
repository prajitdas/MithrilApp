package edu.umbc.cs.ebiquity.mithril.ui.fragments;

import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Map;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.R;
import edu.umbc.cs.ebiquity.mithril.util.services.GeofenceTransitionsIntentService;
import edu.umbc.cs.ebiquity.mithril.util.specialtasks.errors.GeofenceErrorMessages;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Prajit Kumar Das on 6/11/2016.
 */

public class PrefsFragment extends PreferenceFragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status> {

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;
    /**
     * The list of geofences used in this sample.
     */
    protected ArrayList<Geofence> mGeofenceList;
    /**
     * Used to keep track of whether geofences were added.
     */
    private boolean mGeofencesAdded;

    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;

    /**
     * Used to persist application state about whether geofences were added.
     */
    private SharedPreferences sharedPrefs;

    private SwitchPreference mSwitchPrefEnableLocationEnabled;
    private EditTextPreference mEditTextPrefHomeLocation;
    private EditTextPreference mEditTextPrefWorkLocation;

    private SwitchPreference mSwitchPrefEnableTemporalEnabled;
    private EditTextPreference mEditTextPrefWorkHours;
    private EditTextPreference mEditTextPrefDNDHours;

    private SwitchPreference mSwitchPrefEnablePresenceInfoEnabled;
    private EditTextPreference mEditTextPrefPresenceInfoSupervisor;
    private EditTextPreference mEditTextPrefPresenceInfoColleague;

    private Context context;

    // Buttons for kicking off the process of adding or removing geofences.
//    private Button mAddGeofencesButton;
//    private Button mRemoveGeofencesButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

//        appOps();
        initViews();
        initData();
        setOnPreferenceChangeListener();
    }

    private void initData() {
        sharedPrefs = getActivity().getSharedPreferences(MithrilApplication.getSharedPreferencesName(), MODE_PRIVATE);

        mSwitchPrefEnableLocationEnabled.setChecked(sharedPrefs.getBoolean(MithrilApplication.getPrefLocationContextEnableKey(), false));
        mEditTextPrefHomeLocation.setSummary(sharedPrefs.getString(MithrilApplication.getPrefHomeLocationKey(), getResources().getString(R.string.pref_home_location_summary)));
        mEditTextPrefWorkLocation.setSummary(sharedPrefs.getString(MithrilApplication.getPrefWorkLocationKey(), getResources().getString(R.string.pref_work_location_summary)));

        mSwitchPrefEnableTemporalEnabled.setChecked(sharedPrefs.getBoolean(MithrilApplication.getPrefTemporalContextEnableKey(), false));
        mEditTextPrefWorkHours.setSummary(sharedPrefs.getString(MithrilApplication.getPrefWorkHoursKey(), getResources().getString(R.string.pref_work_hours_summary)));
        mEditTextPrefDNDHours.setSummary(sharedPrefs.getString(MithrilApplication.getPrefDndHoursKey(), getResources().getString(R.string.pref_DND_hours_summary)));

        mSwitchPrefEnablePresenceInfoEnabled.setChecked(sharedPrefs.getBoolean(MithrilApplication.getPrefPresenceInfoContextEnableKey(), false));
        mEditTextPrefPresenceInfoSupervisor.setSummary(sharedPrefs.getString(MithrilApplication.getPrefPresenceInfoSupervisorKey(), getResources().getString(R.string.pref_presence_info_supervisor_summary)));
        mEditTextPrefPresenceInfoColleague.setSummary(sharedPrefs.getString(MithrilApplication.getPrefPresenceInfoColleagueKey(), getResources().getString(R.string.pref_presence_info_colleague_summary)));
    }

    private void appOps() {
//        PackageInfo mPackageInfo = null;
//        String mPackageName = null;
//        boolean newState = false;
        AppOpsManager mAppOps = (AppOpsManager) getActivity().getSystemService(Context.APP_OPS_SERVICE);
/*
        mAppOps.setMode(AppOpsManager.OP_WRITE_SETTINGS,
                mPackageInfo.applicationInfo.uid, mPackageName, newState
                        ? AppOpsManager.MODE_ALLOWED : AppOpsManager.MODE_ERRORED);
        mCurSysAppOpMode = mAppOps.checkOp(AppOpsManager.OP_SYSTEM_ALERT_WINDOW, uid, pkg);
        mCurToastAppOpMode = mAppOps.checkOp(AppOpsManager.OP_TOAST_WINDOW, uid, pkg);
        mAppOps.setMode(AppOpsManager.OP_SYSTEM_ALERT_WINDOW, uid, pkg, AppOpsManager.MODE_IGNORED);
        mAppOps.setMode(AppOpsManager.OP_TOAST_WINDOW, uid, pkg, AppOpsManager.MODE_IGNORED);
        mAppOps.setMode(AppOpsManager.OP_SYSTEM_ALERT_WINDOW, uid, pkg, mCurSysAppOpMode);
        mAppOps.setMode(AppOpsManager.OP_TOAST_WINDOW, uid, pkg, mCurToastAppOpMode);
        final int switchOp = AppOpsManager.opToSwitch(firstOp.getOp());
        int mode = mAppOps.checkOp(switchOp, entry.getPackageOps().getUid(), entry.getPackageOps().getPackageName());
        mAppOps.setMode(switchOp, entry.getPackageOps().getUid(), entry.getPackageOps().getPackageName(), positionToMode(position));
        sw.setChecked(mAppOps.checkOp(switchOp, entry.getPackageOps()
                .getUid(), entry.getPackageOps().getPackageName()) == AppOpsManager.MODE_ALLOWED);
        sw.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                mAppOps.setMode(switchOp, entry.getPackageOps()
                                .getUid(), entry.getPackageOps()
                                .getPackageName(),
                        isChecked ? AppOpsManager.MODE_ALLOWED
                                : AppOpsManager.MODE_IGNORED);
            }
        });
        List<AppOpsManager.PackageOps> pkgs;
        if (packageName != null) {
            pkgs = mAppOps.getOpsForPackage(uid, packageName, tpl.ops);
        } else {
            pkgs = mAppOps.getPackagesForOps(tpl.ops);
        }
         */
    }

    private void setupGeoFences() {
        // Get the UI widgets.
//        mAddGeofencesButton = (Button) findViewById(R.id.add_geofences_button);
//        mRemoveGeofencesButton = (Button) findViewById(R.id.remove_geofences_button);

        // Empty list for storing geofences.
        mGeofenceList = new ArrayList<Geofence>();

        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
        mGeofencePendingIntent = null;

        // Retrieve an instance of the SharedPreferences object.
        sharedPrefs = getActivity().getSharedPreferences(MithrilApplication.getSharedPreferencesName(), MODE_PRIVATE);

        // Get the value of mGeofencesAdded from SharedPreferences. Set to false as a default.
        mGeofencesAdded = sharedPrefs.getBoolean(MithrilApplication.GEOFENCES_ADDED_KEY, false);
        setButtonsEnabledState();

        // Get the geofences used. Geofence data is hard coded in this sample.
        populateGeofenceList();

        // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient();
    }

    private void initViews() {
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        mSwitchPrefEnableLocationEnabled = (SwitchPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefLocationContextEnableKey());
        mEditTextPrefHomeLocation = (EditTextPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefHomeLocationKey());
        mEditTextPrefWorkLocation = (EditTextPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefWorkLocationKey());

        mSwitchPrefEnableTemporalEnabled = (SwitchPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefTemporalContextEnableKey());
        mEditTextPrefWorkHours = (EditTextPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefWorkHoursKey());
        mEditTextPrefDNDHours = (EditTextPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefDndHoursKey());

        mSwitchPrefEnablePresenceInfoEnabled = (SwitchPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefPresenceInfoContextEnableKey());
        mEditTextPrefPresenceInfoSupervisor = (EditTextPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefPresenceInfoSupervisorKey());
        mEditTextPrefPresenceInfoColleague = (EditTextPreference) getPreferenceManager().findPreference(MithrilApplication.getPrefPresenceInfoColleagueKey());

        setEnabledEditTexts();
    }

    private void setEnabledEditTexts() {
        mEditTextPrefHomeLocation.setEnabled(mSwitchPrefEnableLocationEnabled.isChecked());
        mEditTextPrefWorkLocation.setEnabled(mSwitchPrefEnableLocationEnabled.isChecked());

        mEditTextPrefWorkHours.setEnabled(mSwitchPrefEnableTemporalEnabled.isChecked());
        mEditTextPrefDNDHours.setEnabled(mSwitchPrefEnableTemporalEnabled.isChecked());

        mEditTextPrefPresenceInfoColleague.setEnabled(mSwitchPrefEnablePresenceInfoEnabled.isChecked());
        mEditTextPrefPresenceInfoSupervisor.setEnabled(mSwitchPrefEnablePresenceInfoEnabled.isChecked());
    }

    private void setOnPreferenceChangeListener() {
        final SharedPreferences.Editor editor = sharedPrefs.edit();
        mSwitchPrefEnableLocationEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                mSwitchPrefEnableLocationEnabled.setChecked((Boolean) o);

                editor.putBoolean(MithrilApplication.getPrefLocationContextEnableKey(), mSwitchPrefEnableLocationEnabled.isChecked());
                editor.commit();

                mEditTextPrefHomeLocation.setEnabled(mSwitchPrefEnableLocationEnabled.isChecked());
                mEditTextPrefWorkLocation.setEnabled(mSwitchPrefEnableLocationEnabled.isChecked());
                return true;
                /**
                 * When the user enables the location settings, we set up the geo fences but we have to be careful about how we set this up.
                 * We have to be careful because there's a limit on how many geofences we can create per device.
                 * So, we will set this up only when the user sets up some locations
                 */
//                setupGeoFences();
//                return false;
            }
        });

        mEditTextPrefHomeLocation.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                //TODO Add code for searching home location and storing it for now we don't do anything and have hardcoded the options
                String changedValue = (String) o;
                preference.setSummary(changedValue);

                editor.putString(MithrilApplication.getPrefHomeLocationKey(), changedValue);
                editor.commit();

                return true;
            }
        });

        mEditTextPrefWorkLocation.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                //TODO Add code for storing preferences
                String changedValue = (String) o;
                preference.setSummary(changedValue);

                editor.putString(MithrilApplication.getPrefWorkLocationKey(), changedValue);
                editor.commit();

                return false;
            }
        });

        mSwitchPrefEnableTemporalEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                mSwitchPrefEnableTemporalEnabled.setChecked((Boolean) o);

                editor.putBoolean(MithrilApplication.getPrefTemporalContextEnableKey(), mSwitchPrefEnableTemporalEnabled.isChecked());
                editor.commit();

                mEditTextPrefWorkHours.setEnabled(mSwitchPrefEnableTemporalEnabled.isChecked());
                mEditTextPrefDNDHours.setEnabled(mSwitchPrefEnableTemporalEnabled.isChecked());
                return true;
            }
        });

        mEditTextPrefWorkHours.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                //TODO Add code for storing preferences
                String changedValue = (String) o;
                preference.setSummary(changedValue);

                editor.putString(MithrilApplication.getPrefWorkHoursKey(), changedValue);
                editor.commit();

                return false;
            }
        });

        mEditTextPrefDNDHours.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                //TODO Add code for storing preferences
                String changedValue = (String) o;
                preference.setSummary(changedValue);

                editor.putString(MithrilApplication.getPrefDndHoursKey(), changedValue);
                editor.commit();

                return false;
            }
        });

        mSwitchPrefEnablePresenceInfoEnabled.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                mSwitchPrefEnablePresenceInfoEnabled.setChecked((Boolean) o);

                editor.putBoolean(MithrilApplication.getPrefPresenceInfoContextEnableKey(), mSwitchPrefEnablePresenceInfoEnabled.isChecked());
                editor.commit();

                mEditTextPrefPresenceInfoColleague.setEnabled(mSwitchPrefEnablePresenceInfoEnabled.isChecked());
                mEditTextPrefPresenceInfoSupervisor.setEnabled(mSwitchPrefEnablePresenceInfoEnabled.isChecked());
                return true;
            }
        });

        mEditTextPrefPresenceInfoSupervisor.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                //TODO Add code for storing preferences
                String changedValue = (String) o;
                preference.setSummary(changedValue);

                editor.putString(MithrilApplication.getPrefPresenceInfoSupervisorKey(), changedValue);
                editor.commit();

                return false;
            }
        });

        mEditTextPrefPresenceInfoColleague.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                //TODO Add code for storing preferences
                String changedValue = (String) o;
                preference.setSummary(changedValue);

                editor.putString(MithrilApplication.getPrefPresenceInfoColleagueKey(), changedValue);
                editor.commit();

                return false;
            }
        });
    }

    /**************************************START OF GEOFENCE CODE*********************************************************/
    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        mGoogleApiClient.connect();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        mGoogleApiClient.disconnect();
//    }

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
    public void addGeofencesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(context, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
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
    public void removeGeofencesButtonHandler(View view) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(context, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
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
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean(MithrilApplication.GEOFENCES_ADDED_KEY, mGeofencesAdded);
            editor.apply();

            // Update the UI. Adding geofences enables the Remove Geofences button, and removing
            // geofences enables the Add Geofences button.
            setButtonsEnabledState();

            Toast.makeText(
                    context,
                    getString(mGeofencesAdded ? R.string.geofences_added :
                            R.string.geofences_removed),
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(context,
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
        Intent intent = new Intent(getActivity(), GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * This sample hard codes geofence data. A real app might dynamically create geofences based on
     * the user's location.
     */
    public void populateGeofenceList() {
        for (Map.Entry<String, LatLng> entry : MithrilApplication.BALTIMORE_COUNTY_LANDMARKS.entrySet()) {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.getKey())

                    // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            MithrilApplication.GEOFENCE_RADIUS_IN_METERS
                    )

                    // Set the expiration duration of the geofence. This geofence gets automatically
                    // removed after this period of time.
                    .setExpirationDuration(MithrilApplication.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                    // Set the transition types of interest. Alerts are only generated for these
                    // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)

                    // Create the geofence.
                    .build());
        }
    }

    /**
     * Ensures that only one button is enabled at any time. The Add Geofences button is enabled
     * if the user hasn't yet added geofences. The Remove Geofences button is enabled if the
     * user has added geofences.
     */
    private void setButtonsEnabledState() {
        if (mGeofencesAdded) {
//            mAddGeofencesButton.setEnabled(false);
//            mRemoveGeofencesButton.setEnabled(true);
        } else {
//            mAddGeofencesButton.setEnabled(true);
//            mRemoveGeofencesButton.setEnabled(false);
        }
    }
    /**************************************END OF GEOFENCE CODE*********************************************************/
}