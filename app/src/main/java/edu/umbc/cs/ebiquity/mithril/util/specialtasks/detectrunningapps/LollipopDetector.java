package edu.umbc.cs.ebiquity.mithril.util.specialtasks.detectrunningapps;

/**
 * Created by prajit on 11/20/16.
 */

import android.annotation.TargetApi;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.SemanticUserContext;
import edu.umbc.cs.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

/**
 * Taken from:
 * https://gist.github.com/plateaukao/011fa857d1919f2bbfdc
 */
public class LollipopDetector implements Detector {
    private MithrilDBHelper mithrilDBHelper;
    private SQLiteDatabase mithrilDB;
    private String currentPackageName;

    /**
     * Returns the consumer friendly device name
     */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

//        String phrase = "";
        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
//                phrase += Character.toUpperCase(c);
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
//            phrase += c;
            phrase.append(c);
        }

        return phrase.toString();
    }

    private static String getLauncherName(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public String getForegroundApp(final Context context) {
        if (!PermissionHelper.getUsageStatsPermisison(context))
            return null;

        SharedPreferences sharedPref = context.getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE);
        mithrilDBHelper = new MithrilDBHelper(context);
        mithrilDB = mithrilDBHelper.getWritableDatabase();
        currentPackageName = null;
        long time = System.currentTimeMillis();
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Service.USAGE_STATS_SERVICE);

        /**
         * Technique from: https://github.com/ricvalerio/foregroundappchecker
         * On Nexus 5x it detects foreground launcher events after the app event!!
         */
        if (LollipopDetector.getDeviceName().equals("LGE Nexus 5")) {
            UsageEvents usageEvents = usageStatsManager.queryEvents(time - 1000 * 3600, time);
            SortedMap<Long, UsageEvents.Event> runningTasks = new TreeMap<Long, UsageEvents.Event>();
            if (usageEvents != null) {
                /**
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
                    Log.d(MithrilApplication.getDebugTag(), "tasks are empty");
                    return null;
                }
                if (runningTasks.get(runningTasks.lastKey()).getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    currentPackageName = runningTasks.get(runningTasks.lastKey()).getPackageName();
                }
            }
        } else {
            /**
             * Previous technique did not work on Nexus 5(returns null on nexus 5 with Android 6.0.1 cm13.0), worked on Nexus 5X
             */
            List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 3600, time);
            if (stats != null) {
                SortedMap<Long, UsageStats> runningTasks = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : stats) {
                    runningTasks.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (runningTasks.isEmpty()) {
                    Log.d(MithrilApplication.getDebugTag(), "tasks are empty");
                    return null;
                }
                currentPackageName = runningTasks.get(runningTasks.lastKey()).getPackageName();
            }
        }

        if (currentPackageName.equals(getLauncherName(context)) ||
                sharedPref.getString(MithrilApplication.getAppPkgNameTag(), "").equals(currentPackageName))
            return null;

        if (mithrilDBHelper.findAppTypeByAppPkgName(mithrilDB, currentPackageName).equals(MithrilApplication.getUserAppsDisplayTag())) {
            Toast.makeText(context, "Mithril detects user app launch: " + currentPackageName, Toast.LENGTH_SHORT).show();
            Log.d(MithrilApplication.getDebugTag(), "Mithril detects user app launch: " + currentPackageName);
            getCurrentSemanticUserContext();
            /**
             * TODO if this is a violation, then insert the information into the violation table.
             * TODO if it is not a violation, then what do we do? **DECIDE**
             */
        }
        mithrilDB.close();

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(MithrilApplication.getAppPkgNameTag(), currentPackageName);
        editor.commit();

        return currentPackageName;
    }

    private SemanticUserContext getCurrentSemanticUserContext() {
        SemanticUserContext semanticUserContext = new SemanticUserContext();
        return semanticUserContext;
    }
}