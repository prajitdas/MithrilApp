package edu.umbc.ebiquity.mithril.util.receivers;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.util.services.AppLaunchDetectorService;
import edu.umbc.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

public class StartServicesOnBootReceiver extends BroadcastReceiver {
    private SharedPreferences sharedPref;

    @Override
    public void onReceive(Context context, Intent intent) {
        sharedPref = context.getSharedPreferences(MithrilAC.getSharedPreferencesName(), Context.MODE_PRIVATE);
        // We have got the user agreement part done! Now we can start services...
        if (sharedPref.getBoolean(MithrilAC.getPrefKeyUserAgreementCopied(), false)) {
            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                if (PermissionHelper.isExplicitPermissionAcquisitionNecessary()) {
                    if (!PermissionHelper.needsUsageStatsPermission(context))
                        context.startService(new Intent(context, AppLaunchDetectorService.class));
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        boolean updatesRequested = false;
                        /*
                        * Get any previous setting for location updates
                        * Gets "false" if an error occurs
                        */
                        if (sharedPref.contains(MithrilAC.getPrefKeyLocationUpdateServiceState())) {
                            updatesRequested = sharedPref.getBoolean(MithrilAC.getPrefKeyLocationUpdateServiceState(), false);
                        }
//                        if (updatesRequested) {
//                            context.startService(new Intent(context, LocationUpdateService.class));
//                        }
                    }
                }
            }
//            } else {
//                Log.e(MithrilAC.getDebugTag(), "Received unexpected intent " + intent.toString());
//            }
        }
    }
}