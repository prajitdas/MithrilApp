package edu.umbc.ebiquity.mithril.util.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.util.specialtasks.detect.runningapps.AppLaunchDetector;

public class AppLaunchDetectorService extends Service {
    private AppLaunchDetector appLaunchDetector;
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
        try {
            appLaunchDetector = new AppLaunchDetector();
            if (mTimer != null) {
                mTimer.cancel();
            } else {// recreate new
                mTimer = new Timer();
            }
        } catch (NullPointerException e) {
            Log.d(MithrilApplication.getDebugTag(), "Check if we have the right permissions, we probably could not instantiate the detector");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // schedule task
        mTimer.scheduleAtFixedRate(new LaunchedAppDetectTimerTask(), 0, MithrilApplication.getLaunchDetectInterval());
        return super.onStartCommand(intent, flags, startId);
    }

    private class LaunchedAppDetectTimerTask extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    appLaunchDetector.getForegroundApp(context);
                }
            });
        }
    }
}