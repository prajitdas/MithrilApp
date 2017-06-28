package edu.umbc.ebiquity.mithril.util.specialtasks.detect.runningapps;

import android.app.AppOpsManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.data.model.rules.Resource;
import edu.umbc.ebiquity.mithril.util.specialtasks.appops.AppOpsState;

/*
 * Taken from:
 * https://gist.github.com/plateaukao/011fa857d1919f2bbfdc
 */
public class AppLaunchDetector {
    private UsageStatsManager mUsageStatsManager;
    private Context context;
    private String currentPackageName = null;
    private UsageStats currentUsageStats = null;
    private UsageEvents.Event currentUsageEvent = null;
    private LastTimeUsedComparator mLastTimeUsedComparator = new LastTimeUsedComparator();
    private PackageManager mPackageManager;
//    private SQLiteDatabase mithrilDB;

    public AppLaunchDetector(Context context) {
        this.context = context;
        mUsageStatsManager = (UsageStatsManager) this.context.getSystemService(Service.USAGE_STATS_SERVICE);
        mPackageManager = this.context.getPackageManager();
//        mithrilDB = MithrilDBHelper.getHelper(this.context).getWritableDatabase();
    }

    public Pair<String, List<Resource>> getForegroundApp(Context context) {
        long time = System.currentTimeMillis();
        try {
            /*
             * Technique from: https://github.com/ricvalerio/foregroundappchecker
             * On Nexus 5x it detects foreground launcher events after the app event!!
             *
            if (MithrilAC.getDeviceName().equals("LGE Nexus 5")) {
                UsageEvents usageEvents = mUsageStatsManager.queryEvents(time - 1000 * 3600, time);
                SortedMap<Long, UsageEvents.Event> runningTasks = new TreeMap<>();
                if (usageEvents != null) {
                /*
                 * getNextEvent
                 * Added in API level 21
                 * boolean getNextEvent (UsageEvents.Event eventOut)
                 * Retrieve the next UsageEvents.Event from the collection and put the resulting data into eventOut.
                 * Parameters: eventOut	UsageEvents.Event: The UsageEvents.Event object that will receive the next event data.
                 * Returns: boolean	true if an event was available, false if there are no more events.
                 *
                    UsageEvents.Event event = new UsageEvents.Event();
                    while (usageEvents.hasNextEvent()) {
                        usageEvents.getNextEvent(event);
                        runningTasks.put(event.getTimeStamp(), event);
                    }
                    if (runningTasks.isEmpty()) {
                        Log.d(MithrilAC.getDebugTag(), "tasks are empty");
                        return null;
                    }
                    if (runningTasks.get(runningTasks.lastKey()).getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                        currentUsageEvent = runningTasks.get(runningTasks.lastKey());
                        currentUsageStats = null;
                        currentPackageName = currentUsageEvent.getPackageName();
                    }
                }
            } else {*/
            /*
             * Previous technique did not work on Nexus 5(returns null on nexus 5 with Android 6.0.1 cm13.0), worked on Nexus 5X
             */
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 3600, time);
            if (stats != null) {
                SortedMap<Long, UsageStats> runningTasks = new TreeMap<>();
                for (UsageStats usageStats : stats) {
                    runningTasks.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (runningTasks.isEmpty()) {
                    Log.d(MithrilAC.getDebugTag(), "tasks are empty");
                    return null;
                }
                //This is the trick! We get last key to find out last package running
                currentUsageStats = runningTasks.get(runningTasks.lastKey());
                currentUsageEvent = null;
                currentPackageName = currentUsageStats.getPackageName();

                if (currentPackageName != null &&
                        !isSystemApp(currentPackageName) &&
                        !currentPackageName.equals(MithrilAC.getLauncherName(context)) &&
                        !currentPackageName.equals(context.getPackageName()))
                    return new Pair<>(currentPackageName, getOp());
            }
        } catch (SecurityException e) {
            Log.d(MithrilAC.getDebugTag(), "Probably a security exception because we don't have the right permissions " + e.getMessage());
        }
        return null;
    }

    private boolean isSystemApp(String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
                return true;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
    }

    private List<Resource> getOp() {
        if (currentUsageEvent == null)
            return checkUsageStats();
        else
            return checkUsageEvent();
    }

    private List<Resource> checkUsageEvent() {
        List<Resource> resources = new ArrayList<>();
        resources.add(new Resource(AppOpsManager.OP_NONE));
        return resources;
    }

    private List<Resource> checkUsageStats() {
        List<Resource> resources = new ArrayList<>();
        try {
            ApplicationInfo appInfo = mPackageManager.getApplicationInfo(currentPackageName, 0);
            AppOpsState appOpsState = new AppOpsState(context);
            List<AppOpsState.AppOpEntry> entries = appOpsState.buildState(
                    AppOpsState.ALL_OPS_TEMPLATE,
                    appInfo.uid,
                    currentPackageName);
            int position = 0;
            for (AppOpsState.AppOpEntry entry : entries) {
                try {
                    AppOpsManager.OpEntry currEntry = entry.getOpEntry(position);
                    String appOpName = AppOpsManager.opToPermission(currEntry.getOp());
                    Log.d(MithrilAC.getDebugTag(), "Found usage of operation: " + appOpName);
                    Resource tempRes = null;
                    if (appOpName != null) {
                        try {
                            PermissionInfo pi = mPackageManager.getPermissionInfo(appOpName, 0);
                            //                            tempRes.setResourceName(pi.packageName);
                            if (pi.group != null) {// && !lastPermGroup.equals(pi.group)) {
                                PermissionGroupInfo pgi = mPackageManager.getPermissionGroupInfo(pi.group, 0);
                                // We care about the resource group because that tells us what was used!
                                if (pgi != null)
                                    tempRes = new Resource(
                                            pi.name, // Resource name
                                            currEntry.getDuration(), // duration
                                            currEntry.getOp(), // application operation
                                            currEntry.getTime(), // time, most probably last time used
                                            entry.getTimeText(context, true).toString(), // text version of when last used
                                            pgi.name, // resource group, in this case we are using the permission's group
                                            MithrilAC.getRiskForPerm(appOpName), // permission's risk level
//                                        MithrilDBHelper.getHelper(context).findRiskLevelByPerm(mithrilDB, appOpName),
                                            currEntry.getMode(), // mode of operation
                                            currEntry.getRejectTime(), // when was this rejected the last time?
                                            currEntry.getAllowedCount(), // how many times has this been allowed?
                                            currEntry.getIgnoredCount() // how many times has this been ignored?
                                    );
                                else
                                    tempRes = new Resource(
                                            pi.name, // Resource name
                                            currEntry.getDuration(), // duration
                                            currEntry.getOp(), // application operation
                                            currEntry.getTime(), // time, most probably last time used
                                            entry.getTimeText(context, true).toString(), // text version of when last used
                                            pgi.name, // resource group, in this case we are using the permission's group
                                            MithrilAC.getRiskForPerm(appOpName), // permission's risk level
//                                        MithrilDBHelper.getHelper(context).findRiskLevelByPerm(mithrilDB, appOpName),
                                            currEntry.getMode(), // mode of operation
                                            currEntry.getRejectTime(), // when was this rejected the last time?
                                            currEntry.getAllowedCount(), // how many times has this been allowed?
                                            currEntry.getIgnoredCount() // how many times has this been ignored?
                                    );
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            Log.e(MithrilAC.getDebugTag(), e.getMessage());
                        }
                    }
                    if (tempRes != null)
                        resources.add(tempRes);
                    position++;
                } catch (IndexOutOfBoundsException e) {
                    Log.e(MithrilAC.getDebugTag(), "Operations were empty!");
                }
            }
            Collections.sort(resources, mLastTimeUsedComparator);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(MithrilAC.getDebugTag(), "This package may be gone" + e.getMessage());
        }
        return resources;
    }

    public static class LastTimeUsedComparator implements Comparator<Resource> {
        @Override
        public final int compare(Resource a, Resource b) {
            return a.compareTo(b);
        }
    }
}