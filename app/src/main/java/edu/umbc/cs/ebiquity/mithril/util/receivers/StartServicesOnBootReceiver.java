package edu.umbc.cs.ebiquity.mithril.util.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.util.services.AppLaunchDetectorService;
import edu.umbc.cs.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

public class StartServicesOnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
            if (PermissionHelper.isExplicitPermissionAcquisitionNecessary())
                if (PermissionHelper.getUsageStatsPermisison(context))
                    context.startService(new Intent(context, AppLaunchDetectorService.class));
        Log.d(MithrilApplication.getDebugTag(), "Action: " + intent.getAction());
    }
}