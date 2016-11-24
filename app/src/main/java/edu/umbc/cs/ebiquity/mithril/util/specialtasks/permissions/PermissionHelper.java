package edu.umbc.cs.ebiquity.mithril.util.specialtasks.permissions;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.util.specialtasks.root.RootAccess;

/**
 * Created by Prajit on 11/21/2016.
 */

public class PermissionHelper {
    private static final List<String> permissionsRequired = Arrays.asList(
            Manifest.permission.READ_LOGS,
            Manifest.permission.NFC,
            Manifest.permission.PACKAGE_USAGE_STATS
    );
    private static int countOfPermissionsToRequest = 0;

    public static int getPermissionsRequiredCount() {
        return permissionsRequired.size();
    }

    private static List<String> getPermissionsRequired() {
        return permissionsRequired;
    }

    public static boolean isExplicitPermissionAcquisitionNecessary() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    private static int isPermissionGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission);
    }

    private static List<String> getPermissionsThatCanBeRequested(final Context context) {
        List<String> permissionsThatCanBeRequested = new ArrayList<String>();
        for (String permission : getPermissionsRequired()) {
            if (isPermissionGranted(context, permission) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    Toast.makeText(context, "You denied the permission: " + permission + ". This might disrupt some functionality!", Toast.LENGTH_SHORT).show();
                } else {
                    // No explanation needed, we can request the permission.
                    permissionsThatCanBeRequested.add(permission);
                    // getPermissionInt returns an app-defined int constant.
                    // The callback method gets the result of the request.
                }
            } //else {
//                MithrilApplication.addToPermissionsGranted(permission);
//            }
        }
        return permissionsThatCanBeRequested;
    }

    public static void requestAllPermissions(Context context) {
        List<String> permissionsThatCanBeRequested = getPermissionsThatCanBeRequested(context);
        String[] permissionStrings = new String[permissionsThatCanBeRequested.size()];
        int permIdx = 0;
        StringBuffer stringBuffer = new StringBuffer();
        for (String permission : permissionsThatCanBeRequested) {
            permissionStrings[permIdx++] = permission;
            stringBuffer.append(permission);
        }
//        Log.d(MithrilApplication.getDebugTag(), "granted: "+MithrilApplication.getPermissionsGranted());
//        Log.d(MithrilApplication.getDebugTag(), "strings: "+stringBuffer.toString());
        ActivityCompat.requestPermissions((Activity) context,
                permissionStrings,
                MithrilApplication.CONST_ALL_PERMISSIONS_MITHRIL_REQUEST_CODE);
    }

    public static int getCountOfPermissionsToRequest() {
        return countOfPermissionsToRequest;
    }

    public static void setCountOfPermissionsToRequest(int countOfPermissionsToRequest) {
        PermissionHelper.countOfPermissionsToRequest = countOfPermissionsToRequest;
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
     */
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
                    boolean result = rootAccess.runScript(CMDLINE_GRANTPERMS);
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

    public static boolean getUsageStatsPermisison(Context context) {
        String packageName = context.getPackageName();
        RootAccess rootAccess = new RootAccess(context);
        String[] CMDLINE_GRANTPERMS = {"su", "-c", null};
        if (context.getPackageManager().checkPermission(Manifest.permission.PACKAGE_USAGE_STATS, packageName) != 0) {
            Log.d(MithrilApplication.getDebugTag(), "we do not have the PACKAGE_USAGE_STATS permission!");
            /**
             * Alternative method of obtaining permission from user:
             * context.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
             */
            if (android.os.Build.VERSION.SDK_INT >= 16) {
                Log.d(MithrilApplication.getDebugTag(), "Working around JellyBeans 'feature'...");
                try {
                    CMDLINE_GRANTPERMS[2] = MithrilApplication.getPackageUsageStatsPermissionForAppCmd();
                    boolean result = rootAccess.runScript(CMDLINE_GRANTPERMS);
                    if (!result)
                        throw new Exception("failed to become root");
                } catch (Exception e) {
                    Log.d(MithrilApplication.getDebugTag(), "exec(): " + e);
                    Toast.makeText(context, "Failed to obtain PACKAGE_USAGE_STATS permission", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        } else
            Log.d(MithrilApplication.getDebugTag(), "we have the PACKAGE_USAGE_STATS permission already!");
        return true;
    }

    /**
     * This isn't working :(
     *
     * @param context
     * @return
     */
    public static boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        if (mode == AppOpsManager.MODE_ERRORED) {
            context.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            return false;
        } else
            return true;
    }
}