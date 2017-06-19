package edu.umbc.ebiquity.mithril.util.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticActivity;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticLocation;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticNearActor;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticTime;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticUserContext;
import edu.umbc.ebiquity.mithril.util.specialtasks.detect.policyconflicts.ViolationDetector;
import edu.umbc.ebiquity.mithril.util.specialtasks.detect.runningapps.AppLaunchDetector;
import edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions.SemanticInconsistencyException;
import edu.umbc.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

public class AppLaunchDetectorService extends Service implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener {
    private AppLaunchDetector appLaunchDetector;
    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;
    private Context context;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private boolean servicesAvailable;
    private boolean mInProgress;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        context = this;
        mInProgress = false;
        servicesAvailable = servicesConnected();
        sharedPrefs = getSharedPreferences(MithrilAC.getSharedPreferencesName(), Context.MODE_PRIVATE);
        editor = getSharedPreferences(MithrilAC.getSharedPreferencesName(), Context.MODE_PRIVATE).edit();/*
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

    @Override
    public void onDestroy() {
        mInProgress = false;
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
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(AppLaunchDetectorService.this)
                .addOnConnectionFailedListener(AppLaunchDetectorService.this)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLastLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(MithrilAC.getDebugTag(), "Disconnected. Please re-connect.");
        mInProgress = false;
        mGoogleApiClient = null;
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
        } catch (SecurityException e) {
            Log.d(MithrilAC.getDebugTag(), e.getMessage());
        }
    }

    private SemanticLocation getSemanticLocation(Location location) {
        Log.d(MithrilAC.getDebugTag(), "Location found: " + Double.toString(location.getLatitude()) + Double.toString(location.getLongitude()));
        SemanticLocation semanticLocation = null;
        Gson retrieveDataGson = new Gson();
        String retrieveDataJson;
        Map<String, ?> allPrefs;
        try {
            allPrefs = sharedPrefs.getAll();
            for (Map.Entry<String, ?> aPref : allPrefs.entrySet()) {
                if (aPref.getKey().startsWith(MithrilAC.getPrefKeyContextTypeLocation())) {
                    retrieveDataJson = sharedPrefs.getString(aPref.getKey(), "");
                    semanticLocation = retrieveDataGson.fromJson(retrieveDataJson, SemanticLocation.class);
                    if (semanticLocation.isEnabled())
                        Log.d(MithrilAC.getDebugTag(), "Came into the test and found: " + location.toString());
                    if (semanticLocation.getLocation().distanceTo(location) < 1000)
                        return semanticLocation;
                }
            }
        } catch (NullPointerException e) {
            Log.d(MithrilAC.getDebugTag(), "Prefs empty somehow?!");
        } catch (Exception e) {
            Log.d(MithrilAC.getDebugTag(), "came here");
        }
        if (semanticLocation == null)
            semanticLocation = new SemanticLocation(
                    MithrilAC.getPrefKeyContextInstanceUnknown() + Long.toString(System.currentTimeMillis()),
                    location);
        Place currentPlace = null;
        float likelihood = Float.MIN_VALUE;
        for (Map.Entry<Place, Float> placeEntry : guessCurrentPlace().entrySet())
            if (placeEntry.getValue() > likelihood)
                currentPlace = placeEntry.getKey();
        semanticLocation.setPlace(currentPlace);
        return semanticLocation;
    }

    private Map<Place, Float> guessCurrentPlace() {
        final Map<Place, Float> places = new HashMap<>();
        PendingResult<PlaceLikelihoodBuffer> result;
        try {
            result = Places.PlaceDetectionApi
                    .getCurrentPlace(mGoogleApiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                    for (PlaceLikelihood placeLikelihood : likelyPlaces)
                        places.put(placeLikelihood.getPlace(), placeLikelihood.getLikelihood());
                    likelyPlaces.release();
                }
            });
        } catch (SecurityException e) {
            Log.e(MithrilAC.getDebugTag(), "security exception happened");
        }
        return places;
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