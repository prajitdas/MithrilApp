package edu.umbc.cs.ebiquity.mithril.util.services;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.util.receivers.AddressResultReceiver;

/**
 * Getting Location Updates.
 * <p>
 * Demonstrates how to use the Fused Location Provider API to get updates about a device's
 * location. The Fused Location Provider is part of the Google Play services location APIs.
 * <p>
 * For a simpler example that shows the use of Google Play services to fetch the last known location
 * of a device, see
 * https://github.com/googlesamples/android-play-location/tree/master/BasicLocation.
 * <p>
 * This sample uses Google Play services, but it does not require authentication. For a sample that
 * uses Google Play services for authentication, see
 * https://github.com/googlesamples/android-google-accounts/tree/master/QuickStart.
 */
public class LocationUpdateService extends Service implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener {

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;
    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;
    /**
     * The list of geofences used in this sample.
     */
    protected ArrayList<Geofence> mGeofenceList;
    private Context context;
    private IBinder mBinder = new LocalBinder();
    private SharedPreferences sharedPref;
    /**
     * Tracks whether the user has requested an address. Becomes true when the user requests an
     * address and false when the address (or an error message) is delivered.
     * The user requests an address by pressing the Fetch Address button. This may happen
     * before GoogleApiClient connects. This activity uses this boolean to keep track of the
     * user's intent. If the value is true, the activity tries to fetch the address as soon as
     * GoogleApiClient connects.
     */
    private boolean mAddressRequested;
    private AddressResultReceiver mResultReceiver;
    /**
     * Provides the entry point to Google Play services.
     */
    private GoogleApiClient mGoogleApiClient;
    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;
    /**
     * Represents a geographical location.
     */
    private Location mCurrentLocation;
    // Flag that indicates if a request is underway.
    private boolean mInProgress;
    private Boolean servicesAvailable = false;
    /**
     * Used to keep track of whether geofences were added.
     */
//    private boolean mGeofencesAdded;

    /**
     * Used when requesting to add or remove geofences.
     */
//    private PendingIntent mGeofencePendingIntent;

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
        mResultReceiver = new AddressResultReceiver(new Handler(), this);
        // Set defaults, then update using values stored in the Bundle.
        mAddressRequested = false;

        mInProgress = false;
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 1 minute
        mLocationRequest.setInterval(MithrilApplication.getUpdateInterval());
        // Set the fastest update interval to 1 minute
        mLocationRequest.setFastestInterval(MithrilApplication.getFastestInterval());
        // Set the minimum displacement between location updates in meters to 10m.
        mLocationRequest.setSmallestDisplacement(MithrilApplication.getSmallestDisplacement());

        servicesAvailable = servicesConnected();

        sharedPref = getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE);
        /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        setUpLocationClientIfNeeded();
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startSearchAddressIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        intent.putExtra(MithrilApplication.ADDRESS_REQUESTED_EXTRA, mAddressRequested);
        intent.putExtra(MithrilApplication.ADDRESS_KEY, MithrilApplication.getPrefKeyCurrentAddress());

        // Pass the result receiver as an extra to the service.
        intent.putExtra(MithrilApplication.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(MithrilApplication.LOCATION_DATA_EXTRA, mCurrentLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    /**
     * Create a new location client, using the enclosing class to
     * handle callbacks.
     */
    protected synchronized void buildGoogleApiClient() {
        this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private boolean servicesConnected() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
//                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (!servicesAvailable || mGoogleApiClient.isConnected() || mInProgress)
            return START_STICKY;

        setUpLocationClientIfNeeded();
        if (!mGoogleApiClient.isConnected() || !mGoogleApiClient.isConnecting() && !mInProgress) {
//            Log.d(MithrilApplication.getDebugTag(), DateFormat.getDateTimeInstance().format(new Date()) + ": Started");
//            appendLog(DateFormat.getDateTimeInstance().format(new Date()) + ": Started", sharedPref.getString(MithrilApplication.getPrefKeyLogFilename(), "sdcard/log.txt"));
            mInProgress = true;
            mGoogleApiClient.connect();
        }

        return START_STICKY;
    }

    private void setUpLocationClientIfNeeded() {
        if (mGoogleApiClient == null)
            buildGoogleApiClient();
    }

    // Define the callback method that receives location updates
    @Override
    public void onLocationChanged(Location location) {
        // Report to the UI that the location was updated
        String msg = Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude());
//        Log.d(MithrilApplication.getDebugTag(), msg);
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
//        Log.d(MithrilApplication.getDebugTag(), DateFormat.getDateTimeInstance().format(new Date()) + ":" + msg);
        appendLog(DateFormat.getDateTimeInstance().format(new Date()) + ":" + msg);//, sharedPref.getString(MithrilApplication.getPrefKeyLocationFilename(), "sdcard/location.txt"));
        mCurrentLocation = location;
        storeInSharedPreferences(MithrilApplication.getPrefKeyLocation(), mCurrentLocation);

        //Store the current location in preferences
        String json = new GsonBuilder().create().toJson(mCurrentLocation, Location.class);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(MithrilApplication.getPrefKeyCurrentLocation(), json);

        /**
         * We know the location has changed, let's check the address
         */
        mAddressRequested = true;
        startSearchAddressIntentService();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public String getTime() {
        SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return mDateFormat.format(new Date());
    }

    public void appendLog(String text) {
        File logFile = new File(getFilesDir(), "log.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(text);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * From http://stackoverflow.com/a/18463758/1816861
     *
     * @param key
     * @param location To Retreive
     *                 Gson gson = new Gson();
     *                 String json = mPrefs.getString("MyObject", "");
     *                 MyObject obj = gson.fromJson(json, MyObject.class);
     */
    public void storeInSharedPreferences(String key, Location location) {
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(location);
        editor.putString(key, json);
        editor.commit();
    }

    @Override
    public void onDestroy() {
        // Turn off the request flag
        this.mInProgress = false;

        if (this.servicesAvailable && this.mGoogleApiClient != null) {
            this.mGoogleApiClient.unregisterConnectionCallbacks(this);
            this.mGoogleApiClient.unregisterConnectionFailedListener(this);
            this.mGoogleApiClient.disconnect();
            // Destroy the current location client
            this.mGoogleApiClient = null;
        }
        // Display the connection status
        // Toast.makeText(this, DateFormat.getDateTimeInstance().format(new Date()) + ":
        // Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();

        super.onDestroy();
    }

    /**
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) throws SecurityException {
//        Log.i(MithrilApplication.getDebugTag(), "Connected to GoogleApiClient");
//
//        // If the initial location was never previously requested, we use
//        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
//        // its value in the Bundle and check for it in onCreate(). We
//        // do not request it again unless the user specifically requests location updates by pressing
//        // the Start Updates button.
//        //
//        // Because we cache the value of the initial location in the Bundle, it means that if the
//        // user launches the activity,
//        // moves to a new location, and then changes the device orientation, the original location
//        // is displayed as the activity is re-created.
//        if (mCurrentLocation == null) {
//            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
//            updateUI();
//        }
//
//        // If the user presses the Start Updates button before GoogleApiClient connects, we set
//        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
//        // the value of mRequestingLocationUpdates and if it is true, we start location updates.
//        if (mRequestingLocationUpdates) {
//            startLocationUpdates();
//        }
        // Request location updates using static settings
//        Intent intent = new Intent(this, LocationUpdateReceiver.class);
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(this.mGoogleApiClient,
                    mLocationRequest, this);
        } catch (SecurityException e) {
            Log.e(MithrilApplication.getDebugTag(), "Get the permissions needed");
        }
//        Log.d(MithrilApplication.getDebugTag(), DateFormat.getDateTimeInstance().format(new Date()) + ": Connected");
        appendLog(DateFormat.getDateTimeInstance().format(new Date()) + ": Connected");//, sharedPref.getString(MithrilApplication.getPrefKeyLogFilename(), "sdcard/log.txt"));
    }

    /**
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        // Turn off the request flag
        mInProgress = false;
        // Destroy the current location client
        mGoogleApiClient = null;
        // Display the connection status
//        Toast.makeText(this, DateFormat.getDateTimeInstance().format(new Date()) + ": Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
//        Log.d(MithrilApplication.getDebugTag(), DateFormat.getDateTimeInstance().format(new Date()) + ": Disconnected. Please re-connect.");
//        appendLog(DateFormat.getDateTimeInstance().format(new Date()) + ": Disconnected", sharedPref.getString(MithrilApplication.getPrefKeyLogFilename(), "sdcard/log.txt"));
    }

    /**
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mInProgress = false;

    /**
     * Google Play services can resolve some errors it detects.
     * If the error has a resolution, try sending an Intent to
     * start a Google Play services activity that can resolve
     * error.
     */
        if (connectionResult.hasResolution()) {
            // If no resolution is available, display an error dialog
        } else {
        }
    }

    public class LocalBinder extends Binder {
        public LocationUpdateService getServerInstance() {
            return LocationUpdateService.this;
        }
    }
}