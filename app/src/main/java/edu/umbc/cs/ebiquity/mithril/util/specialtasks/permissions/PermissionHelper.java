package edu.umbc.cs.ebiquity.mithril.util.specialtasks.permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;

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
}