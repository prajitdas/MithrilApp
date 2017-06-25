package edu.umbc.ebiquity.mithril.util.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Pair;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.data.model.rules.Resource;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticActivity;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticLocation;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticNearActor;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticTime;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticUserContext;
import edu.umbc.ebiquity.mithril.ui.activities.CoreActivity;
import edu.umbc.ebiquity.mithril.ui.activities.InstanceCreationActivity;
import edu.umbc.ebiquity.mithril.util.specialtasks.detect.policyconflicts.ViolationDetector;
import edu.umbc.ebiquity.mithril.util.specialtasks.detect.runningapps.AppLaunchDetector;
import edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions.AddressKeyMissingError;
import edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions.SemanticInconsistencyException;
import edu.umbc.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

public class AppLaunchDetectorService extends Service implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener {
    private SQLiteDatabase mithrilDB;
    private AppLaunchDetector appLaunchDetector;
    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;
    private Context context;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;
    private GoogleApiClient mGoogleApiClient;
    private GoogleApiClient mGooglePlacesApiClient;
    private Location mCurrentLocation;
    private Place mCurrentPlace;
    private List<String> currentPlaceNames = new ArrayList<>();
    private Pair<String, List<Resource>> pkgOpPair;
    private boolean servicesAvailable;
    private boolean mInProgress;
    private boolean mPlacesInProcgress;
    private List<SemanticUserContext> semanticUserContextList = new ArrayList<>();
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
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        context = this;
        mInProgress = false;
        mPlacesInProcgress = false;
        servicesAvailable = servicesConnected();
        sharedPrefs = getSharedPreferences(MithrilAC.getSharedPreferencesName(), Context.MODE_PRIVATE);
        editor = getSharedPreferences(MithrilAC.getSharedPreferencesName(), Context.MODE_PRIVATE).edit();
        // Set defaults, then update using values stored in the Bundle.
        mAddressRequested = false;
        mAddressResultReceiver = new AddressResultReceiver(new Handler(), this);
        initDB(context);
        /* Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        setUpLocationClientIfNeeded();
        try {
            appLaunchDetector = new AppLaunchDetector(this);
            if (mTimer != null) {
                mTimer.cancel();
            } else {// recreate new
                mTimer = new Timer();
            }
        } catch (NullPointerException e) {
            Log.d(MithrilAC.getDebugTag(), "Check if we have the right permissions, we probably could not instantiate the detector");
        }
    }

    private void initDB(Context context) {
        // Let's get the DB instances loaded too
        mithrilDB = MithrilDBHelper.getHelper(context).getWritableDatabase();
    }

    @Override
    public void onDestroy() {
        mInProgress = false;
        mPlacesInProcgress = false;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (!servicesAvailable || mGoogleApiClient.isConnected() || mInProgress)
            return START_STICKY;

        setUpLocationClientIfNeeded();
        if (!mGoogleApiClient.isConnected() || !mGoogleApiClient.isConnecting() && !mInProgress) {
            mInProgress = true;
            mGoogleApiClient.connect();
        }

        if (!mGooglePlacesApiClient.isConnected() || !mGooglePlacesApiClient.isConnecting() && !mPlacesInProcgress) {
            mPlacesInProcgress = true;
            mGooglePlacesApiClient.connect();
        }

        mTimer.scheduleAtFixedRate(new LaunchedAppDetectTimerTask(), 0, MithrilAC.getLaunchDetectInterval());

        return START_STICKY;
    }

    private boolean servicesConnected() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result))
                PermissionHelper.toast(this, "Could not connect to the Google API services");
            return false;
        }
        return true;
    }

    private void setUpLocationClientIfNeeded() {
        if (mGoogleApiClient == null)
            buildGoogleApiClient();
        if (mGooglePlacesApiClient == null)
            buildGooglePlacesApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(AppLaunchDetectorService.this)
                .addOnConnectionFailedListener(AppLaunchDetectorService.this)
                .build();
    }

    protected synchronized void buildGooglePlacesApiClient() {
        mGooglePlacesApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (mGoogleApiClient.isConnected())
            requestLastLocation();
        if (mGooglePlacesApiClient.isConnected())
            guessCurrentPlace();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(MithrilAC.getDebugTag(), "Disconnected. Please re-connect.");
        mInProgress = false;
        mGoogleApiClient = null;
        mGooglePlacesApiClient = null;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(MithrilAC.getDebugTag(), "Connection failed");
        mInProgress = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }

    private List<SemanticUserContext> getSemanticContexts() {
        //We are always at some location... where are we now? Also we are only in one place at a time
        if (mGoogleApiClient.isConnected()) {
            semanticUserContextList.add(getSemanticLocation(mCurrentLocation));

            //Do we know the semantic temporal contexts?
            for (SemanticTime semanticTime : getSemanticTimes())
                semanticUserContextList.add(semanticTime);

            //Do we detect any presence?
            for (SemanticNearActor semanticNearActor : getSemanticNearActors())
                semanticUserContextList.add(semanticNearActor);

            //Do we know of any activity significant to the user?
            for (SemanticActivity semanticActivity : getSemanticActivities())
                semanticUserContextList.add(semanticActivity);

            return semanticUserContextList;
        }
        return new ArrayList<>();
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startSearchAddressIntentService(Location location) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        mAddressRequested = true;
        intent.putExtra(MithrilAC.getAddressRequestedExtra(), mAddressRequested);
        intent.putExtra(MithrilAC.getCurrAddressKey(), "curr_sem_loc");

        // Pass the result receiver as an extra to the service.
        intent.putExtra(MithrilAC.getAppReceiver(), mAddressResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(MithrilAC.getLocationDataExtra(), location);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    private List<SemanticActivity> getSemanticActivities() {
        return new ArrayList<>();
    }

    private List<SemanticNearActor> getSemanticNearActors() {
        return new ArrayList<>();
    }

    private List<SemanticTime> getSemanticTimes() {
        List<SemanticTime> semanticTimes = new ArrayList<>();

        return semanticTimes;
    }

    private void requestLastLocation() {
        try {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Log.d(MithrilAC.getDebugTag(), "Location found: "
                    + String.valueOf(mCurrentLocation.getLatitude())
                    + String.valueOf(mCurrentLocation.getLongitude()));
        } catch (SecurityException e) {
            Log.d(MithrilAC.getDebugTag(), e.getMessage());
        }
    }

    private SemanticLocation getSemanticLocation(Location currentLocation) {
        SemanticLocation semanticLocation = null;
        Gson retrieveDataGson = new Gson();
        String retrieveDataJson;
//        float shortestDistanceToKnownLocation = Float.MAX_VALUE;
        Map<String, ?> allPrefs;
        try {
            allPrefs = sharedPrefs.getAll();
            for (Map.Entry<String, ?> aPref : allPrefs.entrySet()) {
                if (aPref.getKey().startsWith(MithrilAC.getPrefKeyContextTypeLocation())) {
                    retrieveDataJson = sharedPrefs.getString(aPref.getKey(), "");
                    SemanticLocation knownSemanticLocation = retrieveDataGson.fromJson(retrieveDataJson, SemanticLocation.class);
                    /**
                     * We are parsing all known locations and we know the current location's distance to them.
                     * Let's determine if we are at a certain known location and at what is that location.
                     */
                    int level = Integer.MAX_VALUE;
                    for (SemanticLocation currSemLoc : currentSemanticLocations.values()) {
                        if (knownSemanticLocation.compareTo(currSemLoc) == 0) {
                            if (level > knownSemanticLocation.getLevel()) {
                                level = knownSemanticLocation.getLevel();
                                semanticLocation = knownSemanticLocation;
                            }
                        }
                        Log.d(MithrilAC.getDebugTag(), "Did not match but at least we got a location"+currSemLoc.getLabel()+currSemLoc.getName());
                    }
                    if(semanticLocation != null)
                        Log.d(MithrilAC.getDebugTag(), "Eureka we got a match to a location" + knownSemanticLocation.getName() + knownSemanticLocation.getLabel());
                    else
                        Log.d(MithrilAC.getDebugTag(), "still null");
//                    float distanceTo = knownSemanticLocation.getLocation().distanceTo(currentLocation);
//                    if (distanceTo < MithrilAC.getGeofenceRadiusInMeters() && shortestDistanceToKnownLocation > distanceTo) {
//                        shortestDistanceToKnownLocation = distanceTo;
//                        semanticLocation = knownSemanticLocation;
//                        Log.d(MithrilAC.getDebugTag(), "Passed location found: "
//                                + String.valueOf(currentLocation.getLatitude())
//                                + String.valueOf(currentLocation.getLongitude())
//                                + String.valueOf(distanceTo)
//                                + aPref.getKey()
//                        );
//                    }
                }
            }
        } catch (NullPointerException e) {
            Log.d(MithrilAC.getDebugTag(), "Prefs empty somehow?!");
        } catch (Exception e) {
            Log.d(MithrilAC.getDebugTag(), "came here");
        }
        if (semanticLocation == null)
            semanticLocation = handleUnknownLocation(currentLocation);
        Log.d(MithrilAC.getDebugTag(), "This is a test for location: " + semanticLocation.getLabel());
        return semanticLocation;
    }

    private SemanticLocation handleUnknownLocation(Location currentLocation) {
        Log.d(MithrilAC.getDebugTag(), "We are at a new location: " + mCurrentPlace.getAddress());
        Gson contextDataStoreGson = new Gson();
        SemanticLocation semanticLocation = new SemanticLocation(
                MithrilAC.getPrefKeyContextInstanceUnknown() + Long.toString(System.currentTimeMillis()),
                currentLocation,
                mCurrentPlace.getName().toString(),
                mCurrentPlace.getId(),
                mCurrentPlace.getPlaceTypes(),
                0
        );
        // Send notification and log the transition details.
        sendNotification(semanticLocation);
        addContext(
                MithrilAC.getPrefKeyContextTypeLocation(),
                mCurrentPlace.getName().toString() + String.valueOf(System.currentTimeMillis()),
                contextDataStoreGson.toJson(semanticLocation)
        );
        return semanticLocation;
    }

    private void guessCurrentPlace() {
        Log.d(MithrilAC.getDebugTag(), "in get current place");
        PendingResult<PlaceLikelihoodBuffer> result;
        try {
            result = Places.PlaceDetectionApi
                    .getCurrentPlace(mGooglePlacesApiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                    float mostLikelihood = Float.MIN_VALUE;
                    for (PlaceLikelihood placeLikelihood : likelyPlaces)
                        if (placeLikelihood.getLikelihood() > mostLikelihood) {
                            mCurrentPlace = placeLikelihood.getPlace();
                            Log.d(MithrilAC.getDebugTag(),
                                    "Place found: " +
                                            placeLikelihood.getPlace().getAddress() +
                                            " with likelihood: " +
                                            placeLikelihood.getLikelihood());
                        }
//                    likelyPlaces.release();
                }
            });
        } catch (SecurityException e) {
            Log.e(MithrilAC.getDebugTag(), "security exception happened");
        }
    }

    private void addContextToDB(String contextType, String contextLabel) {
        MithrilDBHelper.getHelper(this).addContext(mithrilDB, contextType, contextLabel, true);
    }

    private void addContext(String contextType, String contextLabel, String serializedJsonContext) {
        editor.putString(contextType + contextLabel, serializedJsonContext);
        editor.apply();
        addContextToDB(contextType, contextLabel);
    }

    private class LaunchedAppDetectTimerTask extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    pkgOpPair = appLaunchDetector.getForegroundApp(context);
                    if (pkgOpPair != null) {
                        if (sharedPrefs.contains(MithrilAC.getPrefKeyLastRunningApp())) {
                            if (!sharedPrefs.getString(MithrilAC.getPrefKeyLastRunningApp(), "").equals(pkgOpPair.first)) {
                                //last running app is not same as currently running one
                                //detect violation, if any
                                //no need to change sharedprefs
                                editor.putString(MithrilAC.getPrefKeyLastRunningApp(), pkgOpPair.first);
                                editor.apply();
                                Log.d(MithrilAC.getDebugTag(), pkgOpPair.first);

                                requestLastLocation();
                                startSearchAddressIntentService(mCurrentLocation);
                                /**
                                 * Once we receive the result of the address search, we can detect violation
                                 */
                            } else {
                                //currently running app is same as previously detected app
                                //nothing to do
                            }
                        } else {
                            //there's no known last running app
                            //add to sharedprefs currently running app and detect violation, if any
                            editor.putString(MithrilAC.getPrefKeyLastRunningApp(), pkgOpPair.first);
                            editor.apply();
                            Log.d(MithrilAC.getDebugTag(), pkgOpPair.first);

                            requestLastLocation();
                            startSearchAddressIntentService(mCurrentLocation);
                        }
                    } else {
                        //null! nothing to do
                    }
                }
            });
        }
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    private void sendNotification(SemanticLocation semanticLocation) {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(this, InstanceCreationActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(CoreActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setSmallIcon(R.drawable.map_marker)
                // In a real app, you may want to use a library like Volley to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.map_marker))
                .setColor(Color.RED)
                .setContentTitle(getString(R.string.we_are_at_a_new_location) + semanticLocation.getName())
                .setContentText(getString(R.string.is_this_location_important))
                .setContentIntent(notificationPendingIntent)
                .addAction(R.drawable.content_save_all, "Save", notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }

    private void addContextLogToDB(String label, String startOrEnd) {
        MithrilDBHelper.getHelper(this).addContextLog(
                mithrilDB,
                MithrilDBHelper.getHelper(this).findContextIdByLabelAndType(
                        mithrilDB,
                        label,
                        MithrilAC.getPrefKeyContextTypeLocation()),
                startOrEnd);
    }

    private Map<String, SemanticLocation> currentSemanticLocations = new HashMap<>();

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
                SemanticLocation tempSemanticLocation = new SemanticLocation(key, mCurrentLocation, 0);
                tempSemanticLocation.setName(mCurrentPlace.getName().toString());
                tempSemanticLocation.setPlaceId(mCurrentPlace.getId());
                tempSemanticLocation.setPlaceTypes(mCurrentPlace.getPlaceTypes());
                tempSemanticLocation.setAddress(mAddressOutput);
                currentSemanticLocations.put(key, tempSemanticLocation);

                Address address = tempSemanticLocation.getAddress();
                Location location = tempSemanticLocation.getLocation();
                String placeId = tempSemanticLocation.getPlaceId();
                List<Integer> placeTypes = tempSemanticLocation.getPlaceTypes();

                currentSemanticLocations.put(key+"_Street", new SemanticLocation(location, address,
                        key+"_Street",
                        false, address.getThoroughfare(), placeId, placeTypes, false, 1));
                currentSemanticLocations.put(key+"_City", new SemanticLocation(location, address,
                        key+"_City",
                        false, address.getLocality(), placeId, placeTypes, false, 2));
                currentSemanticLocations.put(key+"_State", new SemanticLocation(location, address,
                        key+"_State",
                        false, address.getAdminArea(), placeId, placeTypes, false, 3));
                currentSemanticLocations.put(key+"_Country", new SemanticLocation(location, address,
                        key+"_Country",
                        false, address.getCountryName(), placeId, placeTypes, false, 4));

                try {
                    ViolationDetector.detectViolation(
                            context,
                            pkgOpPair.first,
                            pkgOpPair.second,
                            getSemanticContexts());
                } catch (SemanticInconsistencyException e) {
                    Log.e(MithrilAC.getDebugTag(), e.getMessage());
                }
                try {
                    ViolationDetector.detectViolation(
                            context,
                            pkgOpPair.first,
                            pkgOpPair.second,
                            getSemanticContexts());
                } catch (SemanticInconsistencyException e) {
                    Log.e(MithrilAC.getDebugTag(), e.getMessage());
                }
            }
            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;
        }
    }
}