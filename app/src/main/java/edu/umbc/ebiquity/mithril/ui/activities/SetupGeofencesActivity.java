package edu.umbc.ebiquity.mithril.ui.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticLocation;
import edu.umbc.ebiquity.mithril.util.services.GeofenceTransitionsIntentService;
import edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions.GeofenceErrorMessages;
import edu.umbc.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

public class SetupGeofencesActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status> {
    private static final int SETUPGEOFENCESCOMPLETE = 0;
    /**
     * Provides the entry point to Google Play services: Geo fence
     */
    protected GoogleApiClient mGoogleApiClient;
    protected List<Geofence> mGeofenceList;
    private ArrayList<SemanticLocation> semanticLocations;
    private SharedPreferences sharedPrefs;
    /**
     * The list of geofences used in this sample.
     */
    private Handler handler;
    /**
     * Used to keep track of whether geofences were added.
     */
    private boolean mGeofencesAdded;
    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_geofences);

        handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                if (message.what == SETUPGEOFENCESCOMPLETE) {
                    if (mGeofencesAdded) {
                        Intent resultIntent = new Intent();
                        resultIntent.putParcelableArrayListExtra(MithrilAC.getPrefKeyGeofenceList(), semanticLocations);
                        setResult(Activity.RESULT_OK, resultIntent);
                        finish();
                    } else {
                        failed();
                    }
                }
            }
        };

        makeFullScreen();
        initData();
        addGeofences();
        letsPauseABit();
    }

    @Override
    public void onBackPressed() {
        PermissionHelper.toast(this, "Let me finish please...");
    }

    private void failed() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    private void makeFullScreen() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void initData() {
        semanticLocations = getIntent().getParcelableArrayListExtra(MithrilAC.getPrefKeyGeofenceList());
        if (semanticLocations.size() > 0) {
            sharedPrefs = getSharedPreferences(MithrilAC.getSharedPreferencesName(), MODE_PRIVATE);
            /********************************************* Geofence related stuff **************************************************/
            // Empty list for storing geofences.
            mGeofenceList = new ArrayList<>();

            // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.
            mGeofencePendingIntent = null;

            // Get the value of mGeofencesAdded from SharedPreferences. Set to false as a default.
            mGeofencesAdded = sharedPrefs.getBoolean(MithrilAC.getGeofencesAddedKey(), false);

            // Kick off the request to build GoogleApiClient.
            buildGoogleApiClient();

            for (SemanticLocation semanticLocation : semanticLocations) {
                Log.d(MithrilAC.getDebugTag(), "To setup geofences: "
                        + semanticLocation.getLabel()
                        + ", "
                        + semanticLocation.getType()
                        + ", "
                        + semanticLocation.getAddress());
                if (!semanticLocation.isGeofenced())
                    populateGeofenceList(
                            semanticLocation.getLabel(),
                            semanticLocation.getLocation().getLatitude(),
                            semanticLocation.getLocation().getLongitude());
            }
        } else {
            failed();
        }
    }

    private void letsPauseABit() {
        // Start lengthy operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                handler.sendEmptyMessageDelayed(SETUPGEOFENCESCOMPLETE, MithrilAC.getMillisecondsPerSecond() * 5);
            }
        }).start();
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

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
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
            Log.d(MithrilAC.getDebugTag(), getString(R.string.not_connected));
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
            Log.d(MithrilAC.getDebugTag(), getString(R.string.not_connected));
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
        Log.e(MithrilAC.getDebugTag(), "Invalid location permission. " +
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
//            mGeofencesAdded = !mGeofencesAdded;
            mGeofencesAdded = true;
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean(MithrilAC.getGeofencesAddedKey(), mGeofencesAdded);
            editor.apply();
            updateSemanticLocations();

            // Update the UI. Adding geofences enables the Remove Geofences button, and removing
            // geofences enables the Add Geofences button.
//            setButtonsEnabledState();

            Log.d(MithrilAC.getDebugTag(), getString(mGeofencesAdded ? R.string.geofences_added : R.string.geofences_removed));
            PermissionHelper.toast(this,
                    getString(mGeofencesAdded ? R.string.geofences_added : R.string.geofences_removed));
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
            Log.e(MithrilAC.getDebugTag(), errorMessage);
        }
    }

    private void updateSemanticLocations() {
        List<SemanticLocation> tempList = new ArrayList<>();
        for (SemanticLocation semanticLocation : semanticLocations) {
            semanticLocation.setGeofenced(true);
            tempList.add(semanticLocation);
        }
        semanticLocations.clear();
        semanticLocations.addAll(tempList);
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
        Log.d(MithrilAC.getDebugTag(), "Label is:" + semanticIdentifier);
        mGeofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(semanticIdentifier)

                // Set the circular region of this geofence.
                .setCircularRegion(
                        latitude,
                        longitude,
                        MithrilAC.getGeofenceRadiusInMeters()
                )

                // Set the expiration duration of the geofence. This geofence gets automatically
                // removed after this period of detectedAtTime.
                .setExpirationDuration(Geofence.NEVER_EXPIRE)

                // Set the transition types of interest. Alerts are only generated for these
                // transition. We track entry and exit transitions in this sample.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)

                // Create the geofence.
                .build());
    }
}