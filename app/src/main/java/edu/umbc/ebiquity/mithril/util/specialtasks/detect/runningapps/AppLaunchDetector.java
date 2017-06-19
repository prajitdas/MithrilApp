package edu.umbc.ebiquity.mithril.util.specialtasks.detect.runningapps;

/*
 * Created by prajit on 11/20/16.
 */

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
import edu.umbc.ebiquity.mithril.data.model.rules.AppUsageStats;
import edu.umbc.ebiquity.mithril.data.model.rules.Resource;
import edu.umbc.ebiquity.mithril.util.specialtasks.appops.AppOpsState;

/*
 * Taken from:
 * https://gist.github.com/plateaukao/011fa857d1919f2bbfdc
 */
public class AppLaunchDetector {
    // Constants defining order for display order
    private static final boolean localLOGV = false;
    private UsageStatsManager mUsageStatsManager;
    private Context context;
    //    private final ArrayMap<String, String> mAppLabelMap = new ArrayMap<>();
//    private final ArrayList<AppUsageStats> appUsageStats = new ArrayList<>();
    private String currentPackageName = null;
    private UsageStats currentUsageStats = null;
    private UsageEvents.Event currentUsageEvent = null;
    //    private static final int _DISPLAY_ORDER_USAGE_TIME = 0;
//    private static final int _DISPLAY_ORDER_LAST_TIME_USED = 1;
//    private static final int _DISPLAY_ORDER_APP_NAME = 2;
//    private int mDisplayOrder = _DISPLAY_ORDER_USAGE_TIME;
    private LastTimeUsedComparator mLastTimeUsedComparator = new LastTimeUsedComparator();
    //    private UsageTimeComparator mUsageTimeComparator = new UsageTimeComparator();
//    private AppNameComparator mAppLabelComparator;
    private AppOpsState mState;
    private List<Resource> resources = new ArrayList<>();
    private PackageManager mPackageManager;

    public AppLaunchDetector(Context context) {
        this.context = context;
        mUsageStatsManager = (UsageStatsManager) this.context.getSystemService(Service.USAGE_STATS_SERVICE);
        mState = new AppOpsState(this.context);
        mPackageManager = this.context.getPackageManager();
    }

    public Pair<String, Integer> getForegroundApp(Context context) {
        long time = System.currentTimeMillis();
        try {
            /*
             * Technique from: https://github.com/ricvalerio/foregroundappchecker
             * On Nexus 5x it detects foreground launcher events after the app event!!
             */
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
                        currentUsageEvent = runningTasks.get(runningTasks.lastKey());
                        currentUsageStats = null;
                        currentPackageName = currentUsageEvent.getPackageName();
                    }
                }
            } else {
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
//                    Log.d(MithrilAC.getDebugTag(), "tasks are empty");
                        return null;
                    }
                    //This is the trick! We get last key to find out last package running
                    currentUsageStats = runningTasks.get(runningTasks.lastKey());
                    currentUsageEvent = null;
                    currentPackageName = currentUsageStats.getPackageName();
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
                return new Pair<>(currentPackageName, getOp());
//                }
//            }
        } catch (SecurityException e) {
            Log.d(MithrilAC.getDebugTag(), "Probably a security exception because we don't have the right permissions " + e.getMessage());
        }
        return null;
    }

    private int getOp() {
        if (currentUsageEvent == null)
            return checkUsageStats();
        else
            return checkUsageEvent();
    }

    private int checkUsageEvent() {
        return AppOpsManager.OP_NONE;
    }

    private int checkUsageStats() {
        try {
            ApplicationInfo appInfo = mPackageManager.getApplicationInfo(currentPackageName, 0);
            String label = appInfo.loadLabel(mPackageManager).toString();
            AppUsageStats tempUsageStat = new AppUsageStats();
            tempUsageStat.setLastTimeUsed(currentUsageStats.getLastTimeUsed());
            tempUsageStat.setTotalTimeInForeground(currentUsageStats.getTotalTimeInForeground());
            tempUsageStat.setPackageName(currentUsageStats.getPackageName());
            tempUsageStat.setLabel(label);
            tempUsageStat.setIcon(appInfo.loadIcon(mPackageManager));

//            List<Resource> tempListOfResource = new ArrayList<>();
//            String lastPermGroup = "";
            List<AppOpsState.AppOpEntry> entries = mState.buildState(
                    AppOpsState.ALL_OPS_TEMPLATE,
                    appInfo.uid,
                    currentPackageName);
            Log.d(MithrilAC.getDebugTag(), "Whoa!" + Integer.toString(entries.size()));
            for (AppOpsState.AppOpEntry entry : entries) {
                Resource tempRes = new Resource();
                AppOpsManager.OpEntry firstOp = entry.getOpEntry(0);
                tempRes.setLastTimeUsed(entry.getTime());
                String perm = AppOpsManager.opToPermission(firstOp.getOp());
                if (perm != null) {
                    try {
                        PermissionInfo pi = mPackageManager.getPermissionInfo(perm, 0);
                        //                            tempRes.setResourceName(pi.packageName);
                        // We care about the resource group because that tells us what was used!
                        tempRes.setResourceName(pi.group);
                        if (pi.group != null) {// && !lastPermGroup.equals(pi.group)) {
//                            lastPermGroup = pi.group;
                            PermissionGroupInfo pgi = mPackageManager.getPermissionGroupInfo(pi.group, 0);
                            if (pgi.icon != 0) {
                                tempRes.setIcon(pgi.loadIcon(mPackageManager));
                            }
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        Log.e(MithrilAC.getDebugTag(), e.getMessage());
                    }
                }
                tempRes.setLabel(entry.getSwitchText(mState).toString());
                tempRes.setRelativeLastTimeUsed(entry.getTimeText(context, true).toString());
                resources.add(tempRes);
            }
//            appUsageStats.add(tempUsageStat);
            // Sort list
//            mAppLabelComparator = new AppNameComparator(mAppLabelMap);
//            sortList(_DISPLAY_ORDER_LAST_TIME_USED);
            sortList();
            tempUsageStat.setResourcesUsed(resources);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(MithrilAC.getDebugTag(), "This package may be gone" + e.getMessage());
        }
        return AppOpsManager.OP_NONE;
    }

//    public void sortList(int sortOrder) {
//        if (mDisplayOrder == sortOrder) {
//            // do nothing
//            return;
//        }
//        mDisplayOrder= sortOrder;
//        sortList();
//    }

    public void sortList() {
//        if (mDisplayOrder == _DISPLAY_ORDER_USAGE_TIME) {
//            if (localLOGV) Log.i(MithrilAC.getDebugTag(), "Sorting by usage time");
//            Collections.sort(resources, mUsageTimeComparator);
//        } else if (mDisplayOrder == _DISPLAY_ORDER_LAST_TIME_USED) {
//            if (localLOGV) Log.i(MithrilAC.getDebugTag(), "Sorting by last time used");
        Collections.sort(resources, mLastTimeUsedComparator);
//        } else if (mDisplayOrder == _DISPLAY_ORDER_APP_NAME) {
//            if (localLOGV) Log.i(MithrilAC.getDebugTag(), "Sorting by application name");
//            Collections.sort(resources, mAppLabelComparator);
//        }
    }

//    public static class AppNameComparator implements Comparator<Resource> {
//        private Map<String, String> mAppLabelList;
//        AppNameComparator(Map<String, String> appList) {
//            mAppLabelList = appList;
//        }
//        @Override
//        public final int compare(Resource a, Resource b) {
//            String alabel = mAppLabelList.get(a.getPackageName());
//            String blabel = mAppLabelList.get(b.getPackageName());
//            return alabel.compareTo(blabel);
//        }
//    }

    public static class LastTimeUsedComparator implements Comparator<Resource> {
        @Override
        public final int compare(Resource a, Resource b) {
            return a.compareTo(b);
            // return by descending order
//            return (int)(b.getLastTimeUsed() - a.getLastTimeUsed());
        }
    }

//    public static class UsageTimeComparator implements Comparator<Resource> {
//        @Override
//        public final int compare(Resource a, Resource b) {
//            return (int)(b.getTotalTimeInForeground() - a.getTotalTimeInForeground());
//        }
//    }
}