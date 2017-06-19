package edu.umbc.ebiquity.mithril.util.specialtasks.detect.runningapps;

/*
 * Created by prajit on 11/20/16.
 */

import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.Log;
import android.util.Pair;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import edu.umbc.ebiquity.mithril.MithrilAC;

/*
 * Taken from:
 * https://gist.github.com/plateaukao/011fa857d1919f2bbfdc
 */
public class AppLaunchDetector {
    public Pair<String, Integer> getForegroundApp(Context context) {
//        if (!PermissionHelper.needsUsageStatsPermission(context))
//            return null;

        String currentPackageName = null;

        long time = System.currentTimeMillis();
        try {
            UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Service.USAGE_STATS_SERVICE);

        /*
         * Technique from: https://github.com/ricvalerio/foregroundappchecker
         * On Nexus 5x it detects foreground launcher events after the app event!!
         */
            if (MithrilAC.getDeviceName().equals("LGE Nexus 5")) {
                UsageEvents usageEvents = usageStatsManager.queryEvents(time - 1000 * 3600, time);
                SortedMap<Long, UsageEvents.Event> runningTasks = new TreeMap<>();
                if (usageEvents != null) {
                /*
                 * getNextEvent
                 * Added in API level 21
                 * boolean getNextEvent (UsageEvents.Event eventOut)
                 * Retrieve the next UsageEvents.Event from the collection and put the resulting data into eventOut.
                 * Parameters: eventOut	UsageEvents.Event: The UsageEvents.Event object that will receive the next event data.
                 * Returns: boolean	true if an event was available, false if there are no more events.
                 */
                    UsageEvents.Event event = new UsageEvents.Event();
                    while (usageEvents.hasNextEvent()) {
                        usageEvents.getNextEvent(event);
                        runningTasks.put(event.getTimeStamp(), event);
                    }
                    if (runningTasks.isEmpty()) {
//                    Log.d(MithrilAC.getDebugTag(), "tasks are empty");
                        return null;
                    }
                    if (runningTasks.get(runningTasks.lastKey()).getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                        currentPackageName = runningTasks.get(runningTasks.lastKey()).getPackageName();
                    }
                }
            } else {
            /*
             * Previous technique did not work on Nexus 5(returns null on nexus 5 with Android 6.0.1 cm13.0), worked on Nexus 5X
             */
                List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 3600, time);
                if (stats != null) {
                    SortedMap<Long, UsageStats> runningTasks = new TreeMap<>();
                    for (UsageStats usageStats : stats) {
                        runningTasks.put(usageStats.getLastTimeUsed(), usageStats);
                    }
                    if (runningTasks.isEmpty()) {
//                    Log.d(MithrilAC.getDebugTag(), "tasks are empty");
                        return null;
                    }
                    currentPackageName = runningTasks.get(runningTasks.lastKey()).getPackageName();
                }
            }

            if (currentPackageName != null &&
                    !currentPackageName.equals(MithrilAC.getLauncherName(context)) &&
                    !currentPackageName.equals(context.getPackageName()))
//                    currentPackageName = null;
//                else {
//                    try {
//                        ViolationDetector.detectViolation(context, currentPackageName, getOp(currentPackageName), location);
//                    } catch (SemanticInconsistencyException cwaException) {
                        //Something is wrong!!!! We have a Closed World Assumption we cannot have deny rules...
                    //                Log.e(MithrilAC.getDebugTag(), "Serious error! DB contains deny rules. This violates our CWA");
//                    }
                return new Pair<>(currentPackageName, getOp(currentPackageName));
//                }
//            }
        } catch (SecurityException e) {
            Log.d(MithrilAC.getDebugTag(), "Probably a security exception because we don't have the right permissions " + e.getMessage());
        }
        return null;
    }

    private int getOp(String currentPackageName) {
        return 1;
    }
}