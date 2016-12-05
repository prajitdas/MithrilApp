package edu.umbc.cs.ebiquity.mithril;

import android.app.Application;

/**
 * Created by Prajit Kumar Das on 5/1/2016.
 */
public class MithrilApplication extends Application {
    public static final int ALL_PERMISSIONS_MITHRIL_REQUEST_CODE = 1;
    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    public static final String PACKAGE_NAME = "edu.umbc.cs.ebiquity.mithril";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

    private static final String PREF_KEY_LOCATION = "location";
    private static final String PERMISSION_PROTECTION_LEVEL_UNKNOWN = "unknown";
    private static final String PERMISSION_PROTECTION_LEVEL_NORMAL = "normal";
    private static final String PERMISSION_PROTECTION_LEVEL_DANGEROUS = "dangerous";
    private static final String PERMISSION_PROTECTION_LEVEL_SIGNATURE = "signature";
    private static final String PERMISSION_PROTECTION_LEVEL_PRIVILEGED = "privileged";
    private static final String PERMISSION_FLAG_COSTS_MONEY = "costs-money";
    private static final String PERMISSION_FLAG_INSTALLED = "installed";
    private static final String PERMISSION_FLAG_NONE = "no-flags";
    private static final String PERMISSION_NO_GROUP = "no-groups";
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    private static final int UPDATE_INTERVAL_IN_SECONDS = 60;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 60;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    private static final String CMD_READ_LOGS_PERMISSION_FOR_APP = "pm grant edu.umbc.cs.ebiquity.mithril android.permission.READ_LOGS";
    private static final String CMD_PACKAGE_USAGE_STATS_PERMISSION_FOR_APP = "pm grant edu.umbc.cs.ebiquity.mithril android.permission.PACKAGE_USAGE_STATS";
    private static final String CMD_ROOT_PRIVILEGE = "su -c";
    private static final String CMD_DETECT_APP_LAUNCH = "logcat -d ActivityManager:I *:S | grep 'LAUNCHER'";
    //    private static final String CMD_DETECT_APP_LAUNCH = "logcat -d ActivityManager:I *:S | grep 'LAUNCHER' | cut -f5 -d'=' | cut -f1 -d'/'";
    private static final String CMD_DETECT_APP_LAUNCH_DATE = "logcat -d ActivityManager:I *:S | grep 'LAUNCHER' | cut -f1 -d' '";
    private static final String CMD_DETECT_APP_LAUNCH_TIME = "logcat -d ActivityManager:I *:S | grep 'LAUNCHER' | cut -f2 -d' '";
    private static final String LOG_INTENT = "android.intent.category.LAUNCHER";
    private static final String BROADCAST_INTENT_COMMAND_APP = "edu.umbc.ebiquity.mithril.command.intent.action.DATA_REQUEST";
    private static final String SHARED_PREFERENCES_NAME = "edu.umbc.cs.ebiquity.mithril.mithrilappmanager";
    private static final String PREF_KEY_HOME_LOC = "homeLocation";
    private static final String PREF_KEY_WORK_LOC = "workLocation";
    private static final String PREF_KEY_START_TIME = "startTime";
    private static final String PREF_KEY_END_TIME = "endTime";
    private static final String PREF_KEY_WORKING_HOURS_ENABLED = "workingHoursEnabled";
    private static final String PREF_KEY_APP_LAUNCH_MONITORING_SERVICE_STATE = "appLaunchMonitoringServiceState";
    private static final String PREF_KEY_LOCATION_UPDATE_SERVICE_STATE = "locationUpdateServiceState";
    private static final String PREF_KEY_ALL_APPS_DISPLAY = "allApps";
    private static final String PREF_KEY_SYSTEM_APPS_DISPLAY = "systemApps";
    private static final String PREF_KEY_USER_APPS_DISPLAY = "userApps";
    private static final String PREF_KEY_APP_DISPLAY_TYPE = "AppDisplayTypeTag";
    private static final String PREF_KEY_APP_PKG_NAME = "AppPkgNameTag";
    private static final String PREF_KEY_APP_COUNT = "AppCount";
    private static final String DATABASE_NAME = "mithril.db";

    private static final String DEBUG_TAG = "MithrilDebugTag";

    /**
     * Context constants
     * TODO Change the constants as per the ontology and ensure that you generate some rules to be modified by the automated script
     * The following constants are made up and not in sync with the ontology
     */
    private static final String CONTEXT_ARRAY_ACTIVITY[] = {"Activity", "Personal_Activity", "Professional_Activity", "Date", "Enterntainment",
            "Exercising", "Sleeping", "Partying", "Playing", "Running"};
    private static final String CONTEXT_ARRAY_LOCATION[] = {"Location", "Place", "Classroom", "Home", "Library", "Movie_Theatre", "Office",
            "Department_Office", "Lab", "Supervisor_Office", "Government_Office", "Post_Office", "Secure_Government_Office"};
    private static final String CONTEXT_ARRAY_PRESENCE_INFO_IDENTITY[] = {"Public", "Professional_Network", "Personal_Network", "Superior",
            "Subordinate", "Colleague", "Family", "Friends", "Private"};
    private static final String CONTEXT_ARRAY_TIME[] = {"Week_Day", "Working_Hours", "Weekend", "Off_Hours"};
    /**
     * Resource constants
     */
    private static final String CONTEXT_ARRAY_RESOURCE_CATEGORY[] = {
            "Default_Resource", "Camera", "Microphone", "Light_Sensor", "Pressure_Sensor", "Relative_Humidity_Sensor", "Temperature_Sensor",
            "Network_Location", "WiFi_Location", "GPS_Location", "Accelerometer", "Gravity_Sensor", "Gyroscope", "Rotation", "Bluetooth",
            "Cellular_Network", "WiFi_Network", "Geomagnetic_Field_Sensor", "Orientation_Sensor", "Proximity_Sensor", "Calendar", "Call_Logs",
            "Contacts", "Email", "Audio_Files", "Non_Media_Files", "Image_Files", "Video_Files", "Messages", "User_Dictionary", "Device_Id"};
    /**
     * Requester constants
     */
    private static final String CONTEXT_ARRAY_REQUESTER_CATEGORY[] = {
            "Default_App_Category", "Books_And_Reference", "Business", "Comics", "Communication", "Education", "Entertainment", "Finance",
            "Health_And_Fitness", "Libraries_And_Demo", "Lifestyle", "App_Wallpaper", "Media_And_Video", "Medical", "Music_And_Audio",
            "News_And_Magazines", "Personalization", "Photography", "Productivity", "Shopping", "Social", "Sports", "Tools", "Transportation",
            "Travel_And_Local", "Weather", "App_Widgets", "Game", "Game_Action", "Game_Adventure", "Game_Arcade", "Game_Board", "Game_Card",
            "Game_Casino", "Game_Casual", "Game_Educational", "Game_Family", "Game_Music", "Game_Puzzle", "Game_Racing", "Game_Role_Playing",
            "Game_Simulation", "Game_Sports", "Game_Strategy", "Game_Trivia", "Game_Word"};
    private static final String CONTEXT_DEFAULT_TIME = "Default Time";
    private static final String CONTEXT_DEFAULT_ACTIVITY = "Default Activity";
    private static final String CONTEXT_DEFAULT_LOCATION = "Default Location";
    private static final String CONTEXT_DEFAULT_IDENTITY = "John Doe";
    private static final String POL_RUL_NAME_SOCIAL_MEDIA_CAMERA_ACCESS_RULE = "Social_Media_Camera_Access_Rule";
    private static final String POL_RUL_DEFAULT_RULE = "Default policy rule name";
    private static final String DEFAULT_DESCRIPTION = "Default description";
    private static final String KEY_POLICY_RULE_ID = "PolicyRuleId";
    private static final String KEY_POLICY_RULE_NAME = "PolicyRuleName";
    private static final String PRESENCE_INFO_HEADER = "Presence Info";
    private static final String REQUESTER_INFO_HEADER = "Requester Info";
    private static final String USER_ACTIVITY_HEADER = "User Activity";
    private static final String USER_IDENTITY_HEADER = "User SemanticIdentity";
    private static final String USER_LOCATION_HEADER = "User Location";
    private static final String USER_TIME_HEADER = "Date and Time";

    private static final String TOAST_MESSAGE_RULE_DELETED = " rule was deleted";
    private static final String TOAST_MESSAGE_TRUE_VIOLATION_NOTED = " the \"true violation\" has been noted. User will not be asked again "
            + "about the same violation";
    private static final String TOAST_MESSAGE_DATABASE_NOT_RELOADED = "Data was not reloaded!";

    public static int getAllPermissionsMithrilRequestCode() {
        return ALL_PERMISSIONS_MITHRIL_REQUEST_CODE;
    }

    public static String getPermissionProtectionLevelUnknown() {
        return PERMISSION_PROTECTION_LEVEL_UNKNOWN;
    }

    public static String getPermissionProtectionLevelNormal() {
        return PERMISSION_PROTECTION_LEVEL_NORMAL;
    }

    public static String getPermissionProtectionLevelDangerous() {
        return PERMISSION_PROTECTION_LEVEL_DANGEROUS;
    }

    public static String getPermissionProtectionLevelSignature() {
        return PERMISSION_PROTECTION_LEVEL_SIGNATURE;
    }

    public static String getPermissionProtectionLevelPrivileged() {
        return PERMISSION_PROTECTION_LEVEL_PRIVILEGED;
    }

    public static String getPermissionFlagCostsMoney() {
        return PERMISSION_FLAG_COSTS_MONEY;
    }

    public static String getPermissionFlagInstalled() {
        return PERMISSION_FLAG_INSTALLED;
    }

    public static String getPermissionFlagNone() {
        return PERMISSION_FLAG_NONE;
    }

    public static String getCmdReadLogsPermissionForApp() {
        return CMD_READ_LOGS_PERMISSION_FOR_APP;
    }

    public static String getCmdPackageUsageStatsPermissionForApp() {
        return CMD_PACKAGE_USAGE_STATS_PERMISSION_FOR_APP;
    }

    public static String getCmdRootPrivilege() {
        return CMD_ROOT_PRIVILEGE;
    }

    public static String getCmdDetectAppLaunch() {
        return CMD_DETECT_APP_LAUNCH;
    }

    public static String getCmdDetectAppLaunchDate() {
        return CMD_DETECT_APP_LAUNCH_DATE;
    }

    public static String getCmdDetectAppLaunchTime() {
        return CMD_DETECT_APP_LAUNCH_TIME;
    }

    public static String getBroadcastIntentCommandApp() {
        return BROADCAST_INTENT_COMMAND_APP;
    }

    public static String getSharedPreferencesName() {
        return SHARED_PREFERENCES_NAME;
    }

    public static String getPrefKeyHomeLoc() {
        return PREF_KEY_HOME_LOC;
    }

    public static String getPrefKeyWorkLoc() {
        return PREF_KEY_WORK_LOC;
    }

    public static String getPrefKeyStartTime() {
        return PREF_KEY_START_TIME;
    }

    public static String getPrefKeyEndTime() {
        return PREF_KEY_END_TIME;
    }

    public static String getPrefKeyWorkingHoursEnabled() {
        return PREF_KEY_WORKING_HOURS_ENABLED;
    }

    public static String getPrefKeyAppLaunchMonitoringServiceState() {
        return PREF_KEY_APP_LAUNCH_MONITORING_SERVICE_STATE;
    }

    public static String getPrefKeyLocationUpdateServiceState() {
        return PREF_KEY_LOCATION_UPDATE_SERVICE_STATE;
    }

    public static String getPrefKeyAllAppsDisplay() {
        return PREF_KEY_ALL_APPS_DISPLAY;
    }

    public static String getPrefKeySystemAppsDisplay() {
        return PREF_KEY_SYSTEM_APPS_DISPLAY;
    }

    public static String getPrefKeyUserAppsDisplay() {
        return PREF_KEY_USER_APPS_DISPLAY;
    }

    public static String getPrefKeyAppDisplayType() {
        return PREF_KEY_APP_DISPLAY_TYPE;
    }

    public static String getPrefKeyAppPkgName() {
        return PREF_KEY_APP_PKG_NAME;
    }

    public static String getPrefKeyAppCount() {
        return PREF_KEY_APP_COUNT;
    }

    public static String getDatabaseName() {
        return DATABASE_NAME;
    }

    public static String getDebugTag() {
        return DEBUG_TAG;
    }

    public static String[] getContextArrayActivity() {
        return CONTEXT_ARRAY_ACTIVITY;
    }

    public static String[] getContextArrayLocation() {
        return CONTEXT_ARRAY_LOCATION;
    }

    public static String[] getContextArrayPresenceInfoIdentity() {
        return CONTEXT_ARRAY_PRESENCE_INFO_IDENTITY;
    }

    public static String[] getContextArrayTime() {
        return CONTEXT_ARRAY_TIME;
    }

    public static String[] getContextArrayResourceCategory() {
        return CONTEXT_ARRAY_RESOURCE_CATEGORY;
    }

    public static String[] getContextArrayRequesterCategory() {
        return CONTEXT_ARRAY_REQUESTER_CATEGORY;
    }

    public static String getContextDefaultTime() {
        return CONTEXT_DEFAULT_TIME;
    }

    public static String getContextDefaultActivity() {
        return CONTEXT_DEFAULT_ACTIVITY;
    }

    public static String getContextDefaultLocation() {
        return CONTEXT_DEFAULT_LOCATION;
    }

    public static String getContextDefaultIdentity() {
        return CONTEXT_DEFAULT_IDENTITY;
    }

    public static String getPolRulNameSocialMediaCameraAccessRule() {
        return POL_RUL_NAME_SOCIAL_MEDIA_CAMERA_ACCESS_RULE;
    }

    public static String getPolRulDefaultRule() {
        return POL_RUL_DEFAULT_RULE;
    }

    public static String getDefaultDescription() {
        return DEFAULT_DESCRIPTION;
    }

    public static String getKeyPolicyRuleId() {
        return KEY_POLICY_RULE_ID;
    }

    public static String getKeyPolicyRuleName() {
        return KEY_POLICY_RULE_NAME;
    }

    public static String getPresenceInfoHeader() {
        return PRESENCE_INFO_HEADER;
    }

    public static String getRequesterInfoHeader() {
        return REQUESTER_INFO_HEADER;
    }

    public static String getUserActivityHeader() {
        return USER_ACTIVITY_HEADER;
    }

    public static String getUserIdentityHeader() {
        return USER_IDENTITY_HEADER;
    }

    public static String getUserLocationHeader() {
        return USER_LOCATION_HEADER;
    }

    public static String getUserTimeHeader() {
        return USER_TIME_HEADER;
    }

    public static String getToastMessageRuleDeleted() {
        return TOAST_MESSAGE_RULE_DELETED;
    }

    public static String getToastMessageTrueViolationNoted() {
        return TOAST_MESSAGE_TRUE_VIOLATION_NOTED;
    }

    public static String getToastMessageDatabaseNotReloaded() {
        return TOAST_MESSAGE_DATABASE_NOT_RELOADED;
    }

    public static String getPermissionNoGroup() {
        return PERMISSION_NO_GROUP;
    }

    public static String getLogIntent() {
        return LOG_INTENT;
    }

    public static long getUpdateInterval() {
        return UPDATE_INTERVAL;
    }

    public static long getFastestInterval() {
        return FASTEST_INTERVAL;
    }

    public static String getPrefKeyLocation() {
        return PREF_KEY_LOCATION;
    }
}