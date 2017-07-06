package edu.umbc.ebiquity.mithril.util.receivers;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import java.util.Calendar;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.ui.activities.CoreActivity;
import edu.umbc.ebiquity.mithril.util.services.AppLaunchDetectorService;
import edu.umbc.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

import static android.content.Context.ALARM_SERVICE;

public class StartServicesOnBootReceiver extends BroadcastReceiver {
    private SharedPreferences sharedPref;

    private void setupAlarm(Context context) {
        if(!isAlarmUp(context)) {
            // Set the alarm to start at approximately 7:00 p.m.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 19);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    new Intent(context,
                            AlarmReceiver.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            // With setInexactRepeating(), you have to use one of the AlarmManager interval
            // constants--in this case, AlarmManager.INTERVAL_DAY.
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    private boolean isAlarmUp(Context context) {
        return PendingIntent.getBroadcast(
                context,
                0,
                new Intent(context,
                        AlarmReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null;
    }

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

                setupAlarm(context);
            }
//            } else {
//                Log.e(MithrilAC.getDebugTag(), "Received unexpected intent " + intent.toString());
//            }
        }
    }
}