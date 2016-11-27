package edu.umbc.cs.ebiquity.mithril.util.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.util.specialtasks.detectrunningapps.Detector;
import edu.umbc.cs.ebiquity.mithril.util.specialtasks.detectrunningapps.LollipopDetector;
import edu.umbc.cs.ebiquity.mithril.util.specialtasks.detectrunningapps.PreLollipopDetector;
import edu.umbc.cs.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

public class AppLaunchDetectorService extends IntentService {
    private Detector detector;

    public AppLaunchDetectorService() {
        super("AppLaunchWorkerThread");
        if (PermissionHelper.postLollipop())
            detector = new LollipopDetector();
        else
            detector = new PreLollipopDetector();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service started", Toast.LENGTH_LONG).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        synchronized (this) {
            int count = 0;
            while (count < 1000) {
                try {
                    detector.getForegroundApp(this);
//                    ReadLogs.readLogs();
                    wait(1000);
                    count++;
                } catch (InterruptedException interruptedException) {
                    Log.d(MithrilApplication.getDebugTag(), interruptedException.getMessage());
                }
            }
        }
    }
}