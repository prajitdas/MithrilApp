package edu.umbc.cs.ebiquity.mithril.util.specialtasks.context;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import edu.umbc.cs.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

/**
 * Created by prajit on 12/1/16.
 */

public class UserLocation {
    Context context;
    // Acquire a reference to the system Location Manager
    LocationManager locationManager;
    // Define a listener that responds to location updates
    LocationListener locationListener;

    public UserLocation(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                makeUseOfNewLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // Register the listener with the Location Manager to receive location updates
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException securityException) {
            PermissionHelper.requestAllNecessaryPermissions(context);
        }
    }

    private void makeUseOfNewLocation(Location location) {
    }
}