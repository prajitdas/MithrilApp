package edu.umbc.cs.ebiquity.mithril.util.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class AppLaunchDetectionIntentService extends Service {
    public AppLaunchDetectionIntentService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
