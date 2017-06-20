package edu.umbc.ebiquity.mithril.util.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticActivity;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticLocation;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticNearActor;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticTime;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticUserContext;
import edu.umbc.ebiquity.mithril.ui.activities.InstanceCreationActivity;
import edu.umbc.ebiquity.mithril.util.specialtasks.detect.policyconflicts.ViolationDetector;
import edu.umbc.ebiquity.mithril.util.specialtasks.detect.runningapps.AppLaunchDetector;
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
    private boolean servicesAvailable;
    private boolean mInProgress;
    private boolean mPlacesInProcgress;

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
        if(mGooglePlacesApiClient == null)
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
        if(mGoogleApiClient.isConnected())
            requestLastLocation();
        if(mGooglePlacesApiClient.isConnected())
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
        List<SemanticUserContext> semanticUserContextList = new ArrayList<>();

        //We are always at some location... where are we now? Also we are only in one place at a time
        if(mGoogleApiClient.isConnected()) {
            requestLastLocation();
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

    private SemanticLocation getSemanticLocation(Location location) {
        SemanticLocation semanticLocation = null;
        Gson retrieveDataGson = new Gson();
        String retrieveDataJson;
        float distanceToKnownLocation = Float.MAX_VALUE;
        Map<String, ?> allPrefs;
        try {
            allPrefs = sharedPrefs.getAll();
            for (Map.Entry<String, ?> aPref : allPrefs.entrySet()) {
                if (aPref.getKey().startsWith(MithrilAC.getPrefKeyContextTypeLocation())) {
                    retrieveDataJson = sharedPrefs.getString(aPref.getKey(), "");
                    SemanticLocation tempSemanticLocation = retrieveDataGson.fromJson(retrieveDataJson, SemanticLocation.class);
                    /**
                     * We are parsing all known locations and we know the current location's distance to them.
                     * Let's determine if we are at a certain known location and at what is that location.
                     */
                    float dist = tempSemanticLocation.getLocation().distanceTo(location);
                    if (dist < 200 && distanceToKnownLocation > dist) {
                        distanceToKnownLocation = dist;
                        semanticLocation = tempSemanticLocation;
                        Log.d(MithrilAC.getDebugTag(), "Passed location found: "
                                + String.valueOf(location.getLatitude())
                                + String.valueOf(location.getLongitude())
                                + String.valueOf(dist)
                                + aPref.getKey()
                        );
                    }
                }
            }
        } catch (NullPointerException e) {
            Log.d(MithrilAC.getDebugTag(), "Prefs empty somehow?!");
        } catch (Exception e) {
            Log.d(MithrilAC.getDebugTag(), "came here");
        }
        if (semanticLocation == null) {
            semanticLocation = new SemanticLocation(
                    MithrilAC.getPrefKeyContextInstanceUnknown() + Long.toString(System.currentTimeMillis()),
                    location);
            if (mGooglePlacesApiClient.isConnected()) {
                Place currentPlace = guessCurrentPlace();
                if (currentPlace != null) {
                    semanticLocation.setName(currentPlace.getName());
                    semanticLocation.setPlaceId(currentPlace.getId());
                    semanticLocation.setPlaceTypes(currentPlace.getPlaceTypes());
                }
            }
            Gson contextDataStoreGson = new Gson();
            addContext(
                    MithrilAC.getPrefKeyContextTypeLocation(),
                    MithrilAC.getPrefKeyContextInstanceUnknown() + String.valueOf(System.currentTimeMillis()),
                    contextDataStoreGson.toJson(semanticLocation)
            );
        }
        Log.d(MithrilAC.getDebugTag(), "This is a test for location: "+semanticLocation.getLabel());
        return semanticLocation;
    }

    private Place guessCurrentPlace() {
        Log.d(MithrilAC.getDebugTag(), "in get current place");
        final Place[] currentPlace = new Place[1];
        PendingResult<PlaceLikelihoodBuffer> result;
        try {
            result = Places.PlaceDetectionApi
                    .getCurrentPlace(mGooglePlacesApiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                    float mostLikelihood = Float.MIN_VALUE;
                    for (PlaceLikelihood placeLikelihood : likelyPlaces)
                        if(placeLikelihood.getLikelihood() > mostLikelihood) {
                            currentPlace[0] = placeLikelihood.getPlace();
                            Log.d(MithrilAC.getDebugTag(), "Place found: " + placeLikelihood.getPlace().getAddress());
                        }
                    likelyPlaces.release();
                }
            });
        } catch (SecurityException e) {
            Log.e(MithrilAC.getDebugTag(), "security exception happened");
        }
        return currentPlace[0];
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
                    Pair<String, Integer> pkgOpPair = appLaunchDetector.getForegroundApp(context);
                    if (pkgOpPair != null) {
                        if (sharedPrefs.contains(MithrilAC.getPrefKeyLastRunningApp())) {
                            if (!sharedPrefs.getString(MithrilAC.getPrefKeyLastRunningApp(), "").equals(pkgOpPair.first)) {
                                //last running app is not same as currently running one
                                //detect violation, if any
                                //no need to change sharedprefs
                                editor.putString(MithrilAC.getPrefKeyLastRunningApp(), pkgOpPair.first);
                                editor.apply();
                                Log.d(MithrilAC.getDebugTag(), pkgOpPair.first);

                                try {
                                    ViolationDetector.detectViolation(
                                            context,
                                            pkgOpPair.first,
                                            pkgOpPair.second,
                                            getSemanticContexts());
                                } catch (SemanticInconsistencyException e) {
                                    Log.e(MithrilAC.getDebugTag(), e.getMessage());
                                }
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
                    } else {
                        //null! nothing to do
                    }
                }
            });
        }
    }
}