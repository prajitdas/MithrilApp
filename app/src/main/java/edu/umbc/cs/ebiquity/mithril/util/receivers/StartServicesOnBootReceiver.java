package edu.umbc.cs.ebiquity.mithril.util.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import edu.umbc.cs.ebiquity.mithril.util.services.AppLaunchDetectorService;

public class StartServicesOnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            context.startService(new Intent(context, AppLaunchDetectorService.class));
        }
    }
}
