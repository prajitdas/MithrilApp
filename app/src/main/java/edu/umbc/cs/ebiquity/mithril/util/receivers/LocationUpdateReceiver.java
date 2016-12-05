package edu.umbc.cs.ebiquity.mithril.util.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.LocationResult;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;

public class LocationUpdateReceiver extends BroadcastReceiver {
    private LocationResult mLocationResult;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Need to check and grab the Intent's extras like so
        if (LocationResult.hasResult(intent)) {
            this.mLocationResult = LocationResult.extractResult(intent);
            Log.i(MithrilApplication.getDebugTag(), "Location Received: " + this.mLocationResult.toString());
        }
    }
}