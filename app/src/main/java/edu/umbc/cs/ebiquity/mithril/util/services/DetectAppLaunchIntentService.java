package edu.umbc.cs.ebiquity.mithril.util.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * http://stackoverflow.com/questions/31451476/what-is-the-difference-between-service-intentservice-in-android
 * https://developer.android.com/guide/components/services.html#ExtendingIntentService
 */
public class DetectAppLaunchIntentService extends IntentService {
    public DetectAppLaunchIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }
}
