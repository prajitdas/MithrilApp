package edu.umbc.cs.ebiquity.mithril.util.specialtasks.detect.runningapps;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * Created by prajit on 11/26/16.
 */

public class PreLollipopDetector implements Detector {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public String getForegroundApp(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Service.ACTIVITY_SERVICE);
        ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
        String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();
        PackageManager pm = context.getPackageManager();
        PackageInfo foregroundAppPackageInfo = null;
        try {
            foregroundAppPackageInfo = pm.getPackageInfo(foregroundTaskPackageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String foregroundApp = null;
        if (foregroundAppPackageInfo != null)
            foregroundApp = foregroundAppPackageInfo.applicationInfo.packageName;

        return foregroundApp;
    }
}
