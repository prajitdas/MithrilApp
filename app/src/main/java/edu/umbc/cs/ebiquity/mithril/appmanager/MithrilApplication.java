package edu.umbc.cs.ebiquity.mithril.appmanager;

import android.app.Application;
import android.hardware.camera2.params.StreamConfigurationMap;

/**
 * Created by PrajitKumar on 5/1/2016.
 */
public class MithrilApplication extends Application{
    private static final String sharedPreferencesName = "edu.umbc.cs.ebiquity.mithril.mithrilappmanager";
    private static final String debugTag = "MithrilAppManagerDebugTag";

    public static String getAllAppsDisplayTag() {
        return allAppsDisplayTag;
    }

    private static final String allAppsDisplayTag = "allApps";

    public static String getSystemAppsDisplayTag() {
        return systemAppsDisplayTag;
    }

    public static String getUserAppsDisplayTag() {
        return userAppsDisplayTag;
    }

    private static final String systemAppsDisplayTag = "systemApps";
    private static final String userAppsDisplayTag = "userApps";

    public static String getAppDisplayTypeTag() {
        return appDisplayTypeTag;
    }

    private static final String appDisplayTypeTag = "AppDisplayTypeTag";

    public static String getSharedPreferenceAppCount() {
        return sharedPreferenceAppCount;
    }

    private static final String sharedPreferenceAppCount = "AppCount";

    public static String getSharedPreferencesName() {
        return sharedPreferencesName;
    }

    public static String getDebugTag() {
        return debugTag;
    }
}
