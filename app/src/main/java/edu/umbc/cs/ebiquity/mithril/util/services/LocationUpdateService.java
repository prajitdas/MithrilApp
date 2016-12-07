package edu.umbc.cs.ebiquity.mithril.util.services;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.R;
import edu.umbc.cs.ebiquity.mithril.util.receivers.LocationUpdateReceiver;
import edu.umbc.cs.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

public class LocationUpdateService extends Service implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener {

    /**
     * The formatted location address.
     */
    protected String mAddressOutput;
    /**
     * Tracks whether the user has requested an address. Becomes true when the user requests an
     * address and false when the address (or an error message) is delivered.
     * The user requests an address by pressing the Fetch Address button. This may happen
     * before GoogleApiClient connects. This activity uses this boolean to keep track of the
     * user's intent. If the value is true, the activity tries to fetch the address as soon as
     * GoogleApiClient connects.
     */
    protected boolean mAddressRequested;
    private Context context;
    private Location mLastLocation;
    private AddressResultReceiver mResultReceiver;
    private IBinder mBinder = new LocalBinder();
    private SharedPreferences sharedPref;
    private GoogleApiClient mGoogleApiClient;
    private PowerManager.WakeLock mWakeLock;
    private LocationRequest mLocationRequest;
    // Flag that indicates if a request is underway.
    private boolean mInProgress;
    private Boolean servicesAvailable = false;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
        mResultReceiver = new AddressResultReceiver(new Handler());
        // Set defaults, then update using values stored in the Bundle.
        mAddressRequested = false;
        mAddressOutput = "";

        mInProgress = false;
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(MithrilApplication.getUpdateInterval());
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(MithrilApplication.getFastestInterval());

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
    protected void startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(MithrilApplication.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(MithrilApplication.LOCATION_DATA_EXTRA, mLastLocation);

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
                Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        PowerManager mgr = (PowerManager) getSystemService(Context.POWER_SERVICE);

        /**
         * WakeLock is reference counted so we don't want to create multiple WakeLocks. So do a check before initializing and acquiring.
         * This will fix the "java.lang.Exception: WakeLock finalized while still held: MyWakeLock" error that you may find.
         */
        if (this.mWakeLock == null) { //Added this
            this.mWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MithrilWakeLock");
        }

        if (!this.mWakeLock.isHeld()) { //Added this
            this.mWakeLock.acquire();
        }

        if (!servicesAvailable || mGoogleApiClient.isConnected() || mInProgress)
            return START_STICKY;

        setUpLocationClientIfNeeded();
        if (!mGoogleApiClient.isConnected() || !mGoogleApiClient.isConnecting() && !mInProgress) {
            Log.d(MithrilApplication.getDebugTag(), DateFormat.getDateTimeInstance().format(new Date()) + ": Started");
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
        Log.d(MithrilApplication.getDebugTag(), msg);
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.d(MithrilApplication.getDebugTag(), DateFormat.getDateTimeInstance().format(new Date()) + ":" + msg);
//        appendLog(DateFormat.getDateTimeInstance().format(new Date()) + ":" + msg, sharedPref.getString(MithrilApplication.getPrefKeyLocationFilename(), "sdcard/location.txt"));
        mLastLocation = location;
        storeInSharedPreferences(MithrilApplication.getPrefKeyLocation(), mLastLocation);
        /**
         * We know the location has changed, let's check the address
         */
        startIntentService();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public String getTime() {
        SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return mDateFormat.format(new Date());
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

        if (this.mWakeLock != null) {
            this.mWakeLock.release();
            this.mWakeLock = null;
        }

        super.onDestroy();
    }

    /**
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
        // Request location updates using static settings
        Intent intent = new Intent(this, LocationUpdateReceiver.class);
        if (PermissionHelper.isExplicitPermissionAcquisitionNecessary()) {
            if (PermissionHelper.isPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                try {
                    LocationServices.FusedLocationApi.requestLocationUpdates(this.mGoogleApiClient,
                            mLocationRequest, this);
                } catch (SecurityException securityException) {
                    PermissionHelper.requestAllNecessaryPermissions(this);
                }
            }
        }
        Log.d(MithrilApplication.getDebugTag(), DateFormat.getDateTimeInstance().format(new Date()) + ": Connected");
//        appendLog(DateFormat.getDateTimeInstance().format(new Date()) + ": Connected", sharedPref.getString(MithrilApplication.getPrefKeyLogFilename(), "sdcard/log.txt"));
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
        Log.d(MithrilApplication.getDebugTag(), DateFormat.getDateTimeInstance().format(new Date()) + ": Disconnected. Please re-connect.");
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

    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            mAddressOutput = resultData.getString(MithrilApplication.RESULT_DATA_KEY);
//            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == MithrilApplication.SUCCESS_RESULT) {
                Log.d(MithrilApplication.getDebugTag(), getString(R.string.address_found));
                Toast.makeText(context, getString(R.string.address_found), Toast.LENGTH_LONG).show();
            }
            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;
        }
    }
}