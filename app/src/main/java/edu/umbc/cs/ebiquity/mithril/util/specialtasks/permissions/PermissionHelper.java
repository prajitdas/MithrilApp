package edu.umbc.cs.ebiquity.mithril.util.specialtasks.permissions;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.R;

/**
 * Created by Prajit on 11/21/2016.
 */

public class PermissionHelper {
    private static final List<String> permissionsRequired = Arrays.asList(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    );

    public static void quitMithril(Context context) {
        Toast.makeText(context, "You denied permissions I desperately needed, please uninstall me :(", Toast.LENGTH_LONG).show();
        context.startActivity(new Intent(
                Intent.ACTION_DELETE, Uri.parse(
                "package:" + context.getPackageName())));
    }

    public static void quitMithril(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        context.startActivity(new Intent(
                Intent.ACTION_DELETE, Uri.parse(
                "package:" + context.getPackageName())));
    }

    private static List<String> getPermissionsRequired() {
        return permissionsRequired;
    }

    public static List<String> getPermissionsThatCanBeRequested(final Context context) {
        List<String> permissionsThatCanBeRequested = new ArrayList<String>();
        for (String permission : getPermissionsRequired()) {
            if (isPermissionGranted(context, permission) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permission)) {
                    Toast.makeText(context, "You denied " + permission + " permission. This might disrupt some functionality!", Toast.LENGTH_SHORT).show();
                } else {
                    permissionsThatCanBeRequested.add(permission);
                }
            }
        }
        return permissionsThatCanBeRequested;
    }

    public static boolean isAllRequiredPermissionsGranted(Context context) {
        List<String> permissionsThatCanBeRequested = new ArrayList<String>();
        for (String permission : getPermissionsRequired())
            if (isPermissionGranted(context, permission) == PackageManager.PERMISSION_DENIED)
                return false;
        return true;
    }

    public static int isPermissionGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission);
    }

    public static boolean isExplicitPermissionAcquisitionNecessary() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
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
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    })
                    .setNegativeButton(R.string.dialog_resp_deny, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SharedPreferences.Editor editor = context.getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE).edit();
                            editor.putBoolean(MithrilApplication.getPrefKeyUserDeniedUsageStatsPermissions(), true);
                            // editor.apply(); //Apply method is asynchronous.
                            // It does not get stored if we kill the process right after. So only in this place have we used editor.commit();
                            editor.commit();
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
        return false;
    }

    public static boolean needsUsageStatsPermission(Context context) {
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
}