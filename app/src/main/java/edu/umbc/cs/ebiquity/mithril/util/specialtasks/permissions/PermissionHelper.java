package edu.umbc.cs.ebiquity.mithril.util.specialtasks.permissions;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import edu.umbc.cs.ebiquity.mithril.R;

/**
 * Created by Prajit on 11/21/2016.
 */

public class PermissionHelper {
    public static void quitMithril(Context context) {
        context.startActivity(new Intent(
                Intent.ACTION_DELETE, Uri.parse(
                "package:" + context.getPackageName())));
    }

    public static boolean isExplicitPermissionAcquisitionNecessary() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static int isPermissionGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission);
    }

    public static void requestPermissionIfAllowed(Context context, String permission, int requestCode) {
        // Here, thisActivity is the current activity
        Activity thisActivity = (Activity) context;
        if (ContextCompat.checkSelfPermission(thisActivity, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity, permission)) {
                // Show an explanation to the user *asynchronously* -- don't block this thread waiting for the user's response!
                // After the user sees the explanation, try again to request the permission.
                Toast.makeText(context, "You denied " + permission + " permission. This might disrupt some app functionality!", Toast.LENGTH_SHORT).show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(thisActivity, new String[]{permission}, requestCode);
                // requestCode is an app-defined int constant. The callback method gets the result of the request.
            }
        }
    }

    public static boolean getUsageStatsPermission(final Context context) {
        if (!needsUsageStatsPermission(context)) {
            return true;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(R.string.allow_usage_stats_permission)
                    .setPositiveButton(R.string.dialog_resp_allow, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            requestUsageStatsPermission(context);
                            //This is not working as it kills the current activity and launches the previous one!
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    })
                    .setNegativeButton(R.string.dialog_resp_deny, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(context, "You denied " + Manifest.permission.PACKAGE_USAGE_STATS + " permission. This might disrupt some functionality!", Toast.LENGTH_SHORT).show();
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    });
            // create alert dialog
            AlertDialog alertDialog = builder.create();
            // show it
            alertDialog.show();
        }
        return false;
    }

    private static boolean needsUsageStatsPermission(Context context) {
        return postLollipop() && !hasUsageStatsPermission(context);
    }

    public static boolean postLollipop() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    private static boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void requestUsageStatsPermission(Context context) {
        if (!hasUsageStatsPermission(context))
            context.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
    }

    /**
     * <uses-permission android:name="android.permission.READ_LOGS" />
     * read logs needs the above permission. We are unable to trigger it from inside the app. Something special needs to be done in this case?
     * We need to execute
     * adb shell pm grant edu.umbc.cs.ebiquity.mithril android.permission.READ_LOGS
     * as per the instructions here: http://stackoverflow.com/a/11517421/1816861
     * from terminal and just the shell command from this class as below:
     * pm grant edu.umbc.cs.ebiquity.mithril android.permission.READ_LOGS
     * <p>
     * To test: adb shell dumpsys package edu.umbc.cs.ebiquity.mithril
     *
     public static boolean getReadLogsPermission(Context context) {
     String packageName = context.getPackageName();
     RootAccess rootAccess = new RootAccess(context);
     String[] CMDLINE_GRANTPERMS = {"su", "-c", null};
     if (context.getPackageManager().checkPermission(Manifest.permission.READ_LOGS, packageName) != 0) {
     Log.d(MithrilApplication.getDebugTag(), "we do not have the READ_LOGS permission!");
     if (android.os.Build.VERSION.SDK_INT >= 16) {
     Log.d(MithrilApplication.getDebugTag(), "Working around JellyBeans 'feature'...");
     try {
     CMDLINE_GRANTPERMS[2] = MithrilApplication.getReadLogsPermissionForAppCmd();
     boolean result = RootAccess.runScript(CMDLINE_GRANTPERMS);
     if (!result)
     throw new Exception("failed to become root");
     } catch (Exception e) {
     Log.d(MithrilApplication.getDebugTag(), "exec(): " + e);
     Toast.makeText(context, "Failed to obtain READ_LOGS permission", Toast.LENGTH_LONG).show();
     return false;
     }
     }
     } else
     Log.d(MithrilApplication.getDebugTag(), "we have the READ_LOGS permission already!");
     return true;
     }
     */
}