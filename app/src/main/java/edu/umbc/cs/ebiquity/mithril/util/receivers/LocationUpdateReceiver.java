package edu.umbc.cs.ebiquity.mithril.util.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.location.LocationResult;

public class LocationUpdateReceiver extends BroadcastReceiver {
    private LocationResult mLocationResult;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (LocationResult.hasResult(intent)) {
            this.mLocationResult = LocationResult.extractResult(intent);
//            Log.i(MithrilApplication.getDebugTag(), "Location Received: " + this.mLocationResult.toString());
        }
    }
}