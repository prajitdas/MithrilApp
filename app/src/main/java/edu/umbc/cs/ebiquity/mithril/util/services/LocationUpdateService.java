package edu.umbc.cs.ebiquity.mithril.util.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

import edu.umbc.cs.ebiquity.mithril.util.specialtasks.detectrunningapps.Detector;
import edu.umbc.cs.ebiquity.mithril.util.specialtasks.detectrunningapps.LollipopDetector;
import edu.umbc.cs.ebiquity.mithril.util.specialtasks.detectrunningapps.PreLollipopDetector;
import edu.umbc.cs.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

public class LocationUpdateService extends Service {
    private static final long NOTIFY_INTERVAL = 1000; //Do every ten seconds
    private Detector detector;
    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;
    private Context context;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        context = this;
        if (PermissionHelper.postLollipop())
            detector = new LollipopDetector();
        else
            detector = new PreLollipopDetector();
        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {// recreate new
            mTimer = new Timer();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // schedule task
        mTimer.scheduleAtFixedRate(new LocationUpdateService.LaunchedAppDetectTimerTask(), 0, NOTIFY_INTERVAL);
        return super.onStartCommand(intent, flags, startId);
    }

    class LaunchedAppDetectTimerTask extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    detector.getForegroundApp(context);
                }

            });
        }
    }

    private class LocationListener implements android.location.LocationListener {

        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }
}