package edu.umbc.cs.ebiquity.mithril.util.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import edu.umbc.cs.ebiquity.mithril.util.specialtasks.detectrunningapps.Detector;
import edu.umbc.cs.ebiquity.mithril.util.specialtasks.detectrunningapps.LollipopDetector;
import edu.umbc.cs.ebiquity.mithril.util.specialtasks.detectrunningapps.PreLollipopDetector;
import edu.umbc.cs.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

public class AppLaunchDetectorService extends Service {
    private static final long NOTIFY_INTERVAL = 1000; //Do every ten seconds
    private Detector detector;
    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new LaunchedAppDetectTimerTask(), 0, NOTIFY_INTERVAL);
        if (PermissionHelper.postLollipop())
            detector = new LollipopDetector();
        else
            detector = new PreLollipopDetector();
    }

    class LaunchedAppDetectTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    // display toast
//                    Toast.makeText(getApplicationContext(), getDateTime(), Toast.LENGTH_SHORT).show();
                    detector.getForegroundApp(getApplicationContext());
                }

            });
        }

        private String getDateTime() {
            // get date time in custom format
            SimpleDateFormat sdf = new SimpleDateFormat("[yyyy/MM/dd - HH:mm:ss]");
            return sdf.format(new Date());
        }
    }
}
    /*
    private Context context;
    private Handler handler;
    private Detector detector;
    private static Runnable runnable = null;

    public AppLaunchDetectorService() {
        super("AppLaunchWorkerThread");
        if (PermissionHelper.postLollipop())
            detector = new LollipopDetector();
        else
            detector = new PreLollipopDetector();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = this;
        Toast.makeText(this, "Service started", Toast.LENGTH_LONG).show();

        handler = new Handler();
        handler.postDelayed(runnable, 1500);

        startRepeatingTask();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        / * IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE * /
        //handler.removeCallbacks(runnable);
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        runnable = new Runnable() {
            public void run() {
                try {
                    Toast.makeText(context, "Service doing stuff", Toast.LENGTH_LONG).show();
                    detector.getForegroundApp(context);
                } finally {
                    handler.postDelayed(runnable, 1000);
                }
            }
        };
//        synchronized (this) {
//            int count = 0;
//            while (count < 1000) {
//                try {
//                    detector.getForegroundApp(this);
//                    if(PermissionHelper.isExplicitPermissionAcquisitionNecessary()) {
//                        PermissionHelper.getReadLogsPermission(this);
//                        ReadLogs.readLogs();
//                    }
//                    wait(1000);
//                    count++;
//                } catch (InterruptedException interruptedException) {
//                    Log.d(MithrilApplication.getDebugTag(), interruptedException.getMessage());
//                }
//            }
//        }
    }

    void startRepeatingTask() {
        runnable.run();
    }

    void stopRepeatingTask() {
        handler.removeCallbacks(runnable);
    }
    */
//}