package edu.umbc.cs.ebiquity.mithril.util.receivers;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.util.services.AppLaunchDetectorService;
import edu.umbc.cs.ebiquity.mithril.util.services.LocationUpdateService;
import edu.umbc.cs.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

public class StartServicesOnBootReceiver extends BroadcastReceiver {
    private SharedPreferences sharedPref;

    @Override
    public void onReceive(Context context, Intent intent) {
        sharedPref = context.getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE);
        // We have got the user agreement part done! Now we can start services...
        if (sharedPref.getBoolean(MithrilApplication.getPrefKeyUserAgreementCopied(), false)) {
            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                if (PermissionHelper.isExplicitPermissionAcquisitionNecessary()) {
                    if (PermissionHelper.getUsageStatsPermission(context))
                        context.startService(new Intent(context, AppLaunchDetectorService.class));
                    if (PermissionHelper.isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        boolean updatesRequested = false;
                        /*
                        * Get any previous setting for location updates
                        * Gets "false" if an error occurs
                        */
                        if (sharedPref.contains(MithrilApplication.getPrefKeyLocationUpdateServiceState())) {
                            updatesRequested = sharedPref.getBoolean(MithrilApplication.getPrefKeyLocationUpdateServiceState(), false);
                        }
                        if (updatesRequested) {
                            context.startService(new Intent(context, LocationUpdateService.class));
                        }
                    }
                }
            } else {
                Log.e(MithrilApplication.getDebugTag(), "Received unexpected intent " + intent.toString());
            }
        }
    }
}