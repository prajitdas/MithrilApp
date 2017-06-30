package edu.umbc.ebiquity.mithril;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Pair;

import java.sql.Timestamp;

/**
 * Created by Prajit Kumar Das on 5/1/2016.
 */
public class MithrilAC extends Application {
    /**
     * Public stuff! Make them private if you can...
     */
    public static final int ALL_PERMISSIONS_MITHRIL_REQUEST_CODE = 1;
    public static final int USAGE_STATS_PERMISSION_REQUEST_CODE = 2;
    public static final int WRITE_SETTINGS_PERMISSION_REQUEST_CODE = 3;
    public static final int SYSTEM_ALERT_WINDOW_PERMISSION_REQUEST_CODE = 4;

    public static final int SUCCESS_RESULT = 3;
    public static final int FAILURE_RESULT = 4;

    public static final String MITHRIL_APP_PACKAGE_NAME = "edu.umbc.ebiquity.mithril";

    public static final String MITHRIL_BYE_BYE_MESSAGE = "Bye! Thanks for helping with our survey...";
    private static final String APP_RECEIVER = MITHRIL_APP_PACKAGE_NAME + ".APP_RECEIVER";
    private static final String RESULT_DATA_KEY = MITHRIL_APP_PACKAGE_NAME + ".RESULT_DATA_KEY";
    private static final String ADDRESS_REQUESTED_EXTRA = "ADDRESS_REQUESTED_EXTRA";
    private static final String PLACE_REQUESTED_EXTRA = "placeRequested";
    private static final String LOCATION_DATA_EXTRA = MITHRIL_APP_PACKAGE_NAME + ".LOCATION_DATA_EXTRA";
    private static final String GEOFENCES_ADDED_KEY = MITHRIL_APP_PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";
    private static final String CURR_ADDRESS_KEY = "curr_address_key";
    private static final String NO_FLAGS = "no-flags";
    private static final String NORMAL_PROTECTION_LEVEL = "normal";
    private static final float RADIUS_OF_200_METERS = 200; // 200 meters
    private static final String CALENDAR_PERMISSION_GROUP_DESC = "Calendar permission group";
    public static final Pair<String, String> CALENDAR_PERMISSION_GROUP = new Pair<>("android.permission-group.CALENDAR", CALENDAR_PERMISSION_GROUP_DESC);
    private static final String CAMERA_PERMISSION_GROUP_DESC = "Camera permission group";
    public static final Pair<String, String> CAMERA_PERMISSION_GROUP = new Pair<>("android.permission-group.CAMERA", CAMERA_PERMISSION_GROUP_DESC);
    private static final String CONTACTS_PERMISSION_GROUP_DESC = "Contacts permission group";
    public static final Pair<String, String> CONTACTS_PERMISSION_GROUP = new Pair<>("android.permission-group.CONTACTS", CONTACTS_PERMISSION_GROUP_DESC);
    private static final String LOCATION_PERMISSION_GROUP_DESC = "Location permission group";
    public static final Pair<String, String> LOCATION_PERMISSION_GROUP = new Pair<>("android.permission-group.LOCATION", LOCATION_PERMISSION_GROUP_DESC);
    private static final String MICROPHONE_PERMISSION_GROUP_DESC = "Microphone permission group";
    public static final Pair<String, String> MICROPHONE_PERMISSION_GROUP = new Pair<>("android.permission-group.MICROPHONE", MICROPHONE_PERMISSION_GROUP_DESC);
    private static final String PHONE_PERMISSION_GROUP_DESC = "Phone permission group";
    public static final Pair<String, String> PHONE_PERMISSION_GROUP = new Pair<>("android.permission-group.PHONE", PHONE_PERMISSION_GROUP_DESC);
    private static final String SENSORS_PERMISSION_GROUP_DESC = "Sensors permission group";
    public static final Pair<String, String> SENSORS_PERMISSION_GROUP = new Pair<>("android.permission-group.SENSORS", SENSORS_PERMISSION_GROUP_DESC);
    private static final String SMS_PERMISSION_GROUP_DESC = "SMS permission group";
    public static final Pair<String, String> SMS_PERMISSION_GROUP = new Pair<>("android.permission-group.SMS", SMS_PERMISSION_GROUP_DESC);
    private static final String STORAGE_PERMISSION_GROUP_DESC = "Storage permission group";
    public static final Pair<String, String> STORAGE_PERMISSION_GROUP = new Pair<>("android.permission-group.STORAGE", STORAGE_PERMISSION_GROUP_DESC);
    private static final String SYSTEM_TOOLS_PERMISSION_GROUP_DESC = "System tools permission group";
    public static final Pair<String, String> SYSTEM_TOOLS_PERMISSION_GROUP = new Pair<>("android.permission-group.SYSTEM_TOOLS", SYSTEM_TOOLS_PERMISSION_GROUP_DESC);
    private static final String CAR_INFORMATION_PERMISSION_GROUP_DESC = "Car information permission group";
    public static final Pair<String, String> CAR_INFORMATION_PERMISSION_GROUP = new Pair<>("com.google.android.gms.permission.CAR_INFORMATION", CAR_INFORMATION_PERMISSION_GROUP_DESC);
    private static final String NO_PERMISSION_GROUP_DESC = "No permission group";
    public static final Pair<String, String> NO_PERMISSION_GROUP = new Pair<>("no-groups", NO_PERMISSION_GROUP_DESC);
    /**
     * Private variables start
     */
    private static final String FLIER_PDF_FILE_NAME = "irbconsent.pdf";
    private static final String PERMISSION_PROTECTION_LEVEL_UNKNOWN = "unknown";
    private static final String PERMISSION_PROTECTION_LEVEL_NORMAL = "normal";
    private static final String PERMISSION_PROTECTION_LEVEL_DANGEROUS = "dangerous";
    private static final String PERMISSION_PROTECTION_LEVEL_SIGNATURE = "signature";
    private static final String PERMISSION_PROTECTION_LEVEL_PRIVILEGED = "privileged";
    private static final String PERMISSION_FLAG_COSTS_MONEY = "costs-money";
    private static final String PERMISSION_FLAG_INSTALLED = "installed";
    private static final String PERMISSION_FLAG_NONE = "no-flags";
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Default time slot of 1 hour in milliseconds
    private static final long DEFAULT_TIME_SLOT = MILLISECONDS_PER_SECOND * 60 * 60;
    // Update frequency for app launch detection in seconds
    private static final int LAUNCH_DETECTION_INTERVAL_IN_SECONDS = 1;
    // Update frequency in milliseconds
    private static final long LAUNCH_DETECT_INTERVAL = MILLISECONDS_PER_SECOND * LAUNCH_DETECTION_INTERVAL_IN_SECONDS;
    // Update frequency in seconds
    private static final int UPDATE_INTERVAL_IN_SECONDS = 10;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    // Set the minimum displacement between location updates in meters. By default this is 0.
    private static final float SMALLEST_DISPLACEMENT = new Float(10.0);
    private static final String CMD_GRANT_READ_LOGS_PERMISSION_FOR_APP = "pm grant " + MITHRIL_APP_PACKAGE_NAME + " android.permission.READ_LOGS";
    private static final String CMD_REVOKE_READ_LOGS_PERMISSION_FOR_APP = "pm revoke " + MITHRIL_APP_PACKAGE_NAME + " android.permission.READ_LOGS";
    private static final String CMD_GRANT_PACKAGE_USAGE_STATS_PERMISSION_FOR_APP = "pm grant " + MITHRIL_APP_PACKAGE_NAME + " android.permission.PACKAGE_USAGE_STATS";
    private static final String CMD_REVOKE_PACKAGE_USAGE_STATS_PERMISSION_FOR_APP = "pm revoke " + MITHRIL_APP_PACKAGE_NAME + " android.permission.PACKAGE_USAGE_STATS";
    private static final String CMD_GRANT_UPDATE_APP_OPS_STATS = "pm grant " + MITHRIL_APP_PACKAGE_NAME + " android.permission.UPDATE_APP_OPS_STATS";
    private static final String CMD_REVOKE_UPDATE_APP_OPS_STATS = "pm revoke " + MITHRIL_APP_PACKAGE_NAME + " android.permission.UPDATE_APP_OPS_STATS";
    private static final String CMD_GRANT_WRITE_SECURE_SETTINGS = "pm grant " + MITHRIL_APP_PACKAGE_NAME + " android.permission.WRITE_SECURE_SETTINGS";
    private static final String CMD_REVOKE_WRITE_SECURE_SETTINGS = "pm revoke " + MITHRIL_APP_PACKAGE_NAME + " android.permission.WRITE_SECURE_SETTINGS";
    private static final String CMD_GRANT_GET_APP_OPS_STATS = "pm grant " + MITHRIL_APP_PACKAGE_NAME + " android.permission.GET_APP_OPS_STATS";
    private static final String CMD_REVOKE_GET_APP_OPS_STATS = "pm revoke " + MITHRIL_APP_PACKAGE_NAME + " android.permission.GET_APP_OPS_STATS";
    private static final String CMD_GRANT_GET_USAGE_STATS = "pm revoke " + MITHRIL_APP_PACKAGE_NAME + " android.permission.PACKAGE_USAGE_STATS";
    private static final String CMD_REVOKE_GET_USAGE_STATS = "pm revoke " + MITHRIL_APP_PACKAGE_NAME + " android.permission.PACKAGE_USAGE_STATS";
    private static final String CMD_GRANT_REAL_GET_TASKS = "pm grant " + MITHRIL_APP_PACKAGE_NAME + " android.permission.REAL_GET_TASKS";
    private static final String CMD_REVOKE_REAL_GET_TASKS = "pm revoke " + MITHRIL_APP_PACKAGE_NAME + " android.permission.REAL_GET_TASKS";
    private static final String CMD_GRANT_MANAGE_APP_OPS_RESTRICTIONS = "pm grant " + MITHRIL_APP_PACKAGE_NAME + " android.permission.MANAGE_APP_OPS_RESTRICTIONS";
    private static final String CMD_REVOKE_MANAGE_APP_OPS_RESTRICTIONS = "pm revoke " + MITHRIL_APP_PACKAGE_NAME + " android.permission.MANAGE_APP_OPS_RESTRICTIONS";
    private static final String CMD_ROOT_PRIVILEGE = "su -c";
    private static final String CMD_DETECT_APP_LAUNCH = "logcat -d ActivityManager:I *:S | grep 'LAUNCHER'";
    //    private static final String CMD_DETECT_APP_LAUNCH = "logcat -d ActivityManager:I *:S | grep 'LAUNCHER' | cut -f5 -d'=' | cut -f1 -d'/'";
    private static final String CMD_DETECT_APP_LAUNCH_DATE = "logcat -d ActivityManager:I *:S | grep 'LAUNCHER' | cut -f1 -d' '";
    private static final String CMD_DETECT_APP_LAUNCH_TIME = "logcat -d ActivityManager:I *:S | grep 'LAUNCHER' | cut -f2 -d' '";
    private static final String LOG_INTENT = "android.intent.category.LAUNCHER";
    private static final String BROADCAST_INTENT_COMMAND_APP = "edu.umbc.ebiquity.mithril.command.intent.action.DATA_REQUEST";
    private static final String SHARED_PREFERENCES_NAME = MITHRIL_APP_PACKAGE_NAME + ".sharedprefs";
    private static final String BACK_PRESSED_USER_AGREEMENT_SCREEN = "backPressed";
    //Preference keys
    private static final String PREF_KEY_POLICIES_DOWNLOADED = "policiesDownloaded";
    private static final String PREF_KEY_LAST_RUNNING_APP = "lastRunningApp";
    private static final String PREF_KEY_CONTEXT_TYPE_LOCATION = "Location";
    private static final String PREF_KEY_CONTEXT_TYPE_ACTIVITY = "Activity";
    private static final String PREF_KEY_CONTEXT_TYPE_TEMPORAL = "Temporal";
    private static final String PREF_KEY_CONTEXT_TYPE_PRESENCE = "Presence";
    private static final String PREF_START_KEY = "startOfContext";
    private static final String PREF_END_KEY = "endOfContext";
    private static final String PREF_HOME_LOCATION_KEY = "Home";
    private static final String PREF_WORK_LOCATION_KEY = "Work";
    private static final String PREF_KEY_TEMPORAL_LABEL = "temporalLabel";
    private static final String PREF_BREAKFAST_TEMPORAL_KEY = "Breakfast"; //0800 - 0830
    private static final String PREF_WORK_MORNING_TEMPORAL_KEY = "Work_Morning"; //0830 - 1200 only Monday - Friday
    private static final String PREF_LUNCH_TEMPORAL_KEY = "Lunch"; //1200 - 1230
    private static final String PREF_WORK_AFTERNOON_TEMPORAL_KEY = "Work_Afternoon"; //1230 - 1600 only Monday - Friday
    private static final String PREF_FAMILY_TEMPORAL_KEY = "Family_Time"; //1600 - 1900
    private static final String PREF_DINNER_TEMPORAL_KEY = "Dinner"; //1900 - 1930
    private static final String PREF_ALONE_TEMPORAL_KEY = "Alone_Time"; //1930 - 2100
    private static final String PREF_DND_TEMPORAL_KEY = "DND"; //2100 - 0800
    private static final String PREF_MONDAY_TEMPORAL_KEY = "Monday";
    private static final String PREF_TUESDAY_TEMPORAL_KEY = "Tuesday";
    private static final String PREF_WEDNESDAY_TEMPORAL_KEY = "Wednesday";
    private static final String PREF_THURSDAY_TEMPORAL_KEY = "Thursday";
    private static final String PREF_FRIDAY_TEMPORAL_KEY = "Friday";
    private static final String PREF_SATURDAY_TEMPORAL_KEY = "Saturday";
    private static final String PREF_SUNDAY_TEMPORAL_KEY = "Sunday";
    private static final String PREF_WEEKDAY_TEMPORAL_KEY = "Weekday";
    private static final String PREF_ANYDAYTIME_TEMPORAL_KEY = "Everyday";
    private static final String PREF_WEEKEND_TEMPORAL_KEY = "Weekend";
    private static final String PREF_PERSONAL_TEMPORAL_KEY = "Personal_Time";
    private static final String PREF_PROFESSIONAL_TEMPORAL_KEY = "Professional_Time";
    private static final String PREF_ANYTIME_TEMPORAL_KEY = "Anytime";
    private static final String NEVER_REPEATS = "never repeats";
    private static final String NOT_YET = "Not yet";
    private static final String PREF_KEY_LOCATION_INSTANCES_HAVE_BEEN_SET = "locationInstancesHaveBeenSet";
    private static final String PREF_KEY_ACTIVITY_INSTANCES_HAVE_BEEN_SET = "activityInstancesHaveBeenSet";
    private static final String PREF_KEY_PRESENCE_INSTANCES_HAVE_BEEN_SET = "presenceInstancesHaveBeenSet";
    private static final String PREF_KEY_TEMPORAL_INSTANCES_HAVE_BEEN_SET = "temporalInstancesHaveBeenSet";
    private static final String PREF_KEY_LIST_OF_LOCATION_INSTANCES = "listOfLocationInstances";
    private static final String PREF_KEY_LIST_OF_ACTIVITY_INSTANCES = "listOfActivityInstances";
    private static final String PREF_KEY_LIST_OF_PRESENCE_INSTANCES = "listOfPresenceInstances";
    private static final String PREF_KEY_LIST_OF_TEMPORAL_INSTANCES = "listOfTemporalInstances";
    private static final String PREF_KEY_LOCA_INSTANCES_CREATED = "locaInstancesCreated";
    private static final String PREF_KEY_TIME_INSTANCES_CREATED = "timeInstancesCreated";
    private static final String PREF_KEY_PRES_INSTANCES_CREATED = "presInstancesCreated";
    private static final String PREF_KEY_ACTI_INSTANCES_CREATED = "actiInstancesCreated";
    private static final String PREF_KEY_USER_DENIED_USAGE_STATS_PERMISSIONS = "userDeniedUsageStatsPermissions";
    private static final String PREF_KEY_USER_DENIED_PERMISSIONS = "userDeniedPermissions";
    //End of preferences.xml
    private static final String PREF_KEY_SHOULD_SHOW_AGREEMENT_SNACKBAR = "shouldShowAgreementSnackbar";
    private static final String PREF_KEY_APP_LAUNCH_MONITORING_SERVICE_STATE = "appLaunchMonitoringServiceState";
    private static final String PREF_KEY_LOCATION_UPDATE_SERVICE_STATE = "locationUpdateServiceState";
    private static final String PREF_KEY_ALL_APPS_DISPLAY = "allApps";
    private static final String PREF_KEY_SYSTEM_APPS_DISPLAY = "systemApps";
    private static final String PREF_KEY_USER_APPS_DISPLAY = "userApps";
    private static final String PREF_KEY_APP_DISPLAY_TYPE = "AppDisplayTypeTag";
    private static final String PREF_KEY_APP_PKG_NAME = "AppPkgNameTag";
    //    private static final String PREF_TIME_INSTANT_SUNRISE_TEMPORAL_KEY = "Sunrise"; //Sunrise in a locale
//    private static final String PREF_TIME_INSTANT_SUNSET_TEMPORAL_KEY = "Sunset"; // Sunset in a locale
//    private static final String PREF_MORNING_TEMPORAL_KEY = "Morning"; //0800 - 1200
//    private static final String PREF_AFTERNOON_TEMPORAL_KEY = "Afternoon"; //1200 - 1600
//    private static final String PREF_EVENING_TEMPORAL_KEY = "Evening"; //1600 - 2100
//    private static final String PREF_DINNER_TEMPORAL_KEY = "Dinner"; //1900 - 1930
//    private static final String PREF_NIGHT_TEMPORAL_KEY = "Night"; //2100 - 0800
//    private static final String PREF_HOLIDAY_TEMPORAL_KEY = "Holiday"; //Official holiday
    private static final String PREF_KEY_PERM_GROUP_NAME = "PermGroupNameTag";
    private static final String PREF_KEY_PERM_GROUP_LABEL = "PermGroupLabelTag";
    private static final String PREF_KEY_APP_COUNT = "AppCount";
    private static final String PREF_KEY_USER_CONSENT = "UserConsent";
    private static final String PREF_KEY_USER_AGREEMENT_PAGE_NUMBER = "UserAgreementPageNumber";
    private static final String PREF_KEY_USER_AGREEMENT_COPIED = "UserAgreementCopied";
    private static final String PREF_KEY_USER_CONTINUE_CLICKED = "ContinueButtonClicked";
    private static final String PREF_KEY_GEOFENCE_LIST = "geofenceList";
    private static final String PREF_KEY_TIMEFENCE_LIST = "timefenceList";
    private static final String DATABASE_NAME = "mithril.db";
    private static final String DEBUG_TAG = "MithrilDebugTag";
    /**
     * Context constants
     * TODO Change the constants as per the ontology and ensure that you generate some rules to be modified by the automated script
     * The following constants are made up and not in sync with the ontology
     */
    private static final String CONTEXT_ARRAY_ACTIVITY[] = {"Activity", "Personal_Activity", "Professional_Activity", "Date", "Entertainment",
            "Exercising", "Sleeping", "Partying", "Playing", "Running"};
    private static final String CONTEXT_ARRAY_LOCATION[] = {"Location", "Place", "Classroom", "Home", "Library", "Movie_Theatre", "Office",
            "Department_Office", "Lab", "Supervisor_Office", "Government_Office", "Post_Office", "Secure_Government_Office"};
    private static final String CONTEXT_ARRAY_PRESENCE_INFO_IDENTITY[] = {"Public", "Professional_Network", "Personal_Network", "Superior",
            "Subordinate", "Colleague", "Family", "Friends", "Private"};
    private static final String CONTEXT_ARRAY_TIME[] = {"Weekday", "Weekend", "Off_Hours", "Lunch", "Break", "Family_Time"};
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
    private static final String CONTEXT_DEFAULT_WORK_LOCATION = "Default Work Location";
    private static final String CONTEXT_DEFAULT_IDENTITY = "John Doe";
    private static final String CONTEXT_DEFAULT_RELATIONSHIP = "Boss";
    private static final String POL_RUL_NAME_SOCIAL_MEDIA_CAMERA_ACCESS_RULE = "Social_Media_Camera_Access_Rule";
    //End of preference keys
    private static final String POL_RUL_DEFAULT_RULE = "Default deny policy";
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
    private static final String INSERT_STATEMENT_GOOGLE_PERMISSIONS = "INSERT INTO permissions (name, protectionlvl, permgrp, flags) VALUES\n" +
            "('android.permission.READ_CONTACTS', 'dangerous', 'android.permission-group.CONTACTS', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.WRITE_CONTACTS', 'dangerous', 'android.permission-group.CONTACTS', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_CALENDAR', 'dangerous', 'android.permission-group.CALENDAR', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.WRITE_CALENDAR', 'dangerous', 'android.permission-group.CALENDAR', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SEND_SMS', 'dangerous', 'android.permission-group.SMS', 'costsMoney'),\n" +
            "('android.permission.RECEIVE_SMS', 'dangerous', 'android.permission-group.SMS', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_SMS', 'dangerous', 'android.permission-group.SMS', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.RECEIVE_WAP_PUSH', 'dangerous', 'android.permission-group.SMS', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.RECEIVE_MMS', 'dangerous', 'android.permission-group.SMS', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_CELL_BROADCASTS', 'dangerous', 'android.permission-group.SMS', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_EXTERNAL_STORAGE', 'dangerous', 'android.permission-group.STORAGE', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.WRITE_EXTERNAL_STORAGE', 'dangerous', 'android.permission-group.STORAGE', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_FINE_LOCATION', 'dangerous', 'android.permission-group.LOCATION', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_COARSE_LOCATION', 'dangerous', 'android.permission-group.LOCATION', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_PHONE_STATE', 'dangerous', 'android.permission-group.PHONE', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CALL_PHONE', 'dangerous', 'android.permission-group.PHONE', 'costsMoney'),\n" +
            "('android.permission.ACCESS_IMS_CALL_SERVICE', 'signature|privileged', 'android.permission-group.PHONE', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_CALL_LOG', 'dangerous', 'android.permission-group.PHONE', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.WRITE_CALL_LOG', 'dangerous', 'android.permission-group.PHONE', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('com.android.voicemail.permission.ADD_VOICEMAIL', 'dangerous', 'android.permission-group.PHONE', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.USE_SIP', 'dangerous', 'android.permission-group.PHONE', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.PROCESS_OUTGOING_CALLS', 'dangerous', 'android.permission-group.PHONE', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.RECORD_AUDIO', 'dangerous', 'android.permission-group.MICROPHONE', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_UCE_PRESENCE_SERVICE', 'signatureOrSystem', 'android.permission-group.PHONE', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_UCE_OPTIONS_SERVICE', 'signatureOrSystem', 'android.permission-group.PHONE', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CAMERA', 'dangerous', 'android.permission-group.CAMERA', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BODY_SENSORS', 'dangerous', 'android.permission-group.SENSORS', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.USE_FINGERPRINT', 'normal', 'android.permission-group.SENSORS', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_PROFILE', 'normal', '" + NO_PERMISSION_GROUP.first + "', 'removed'),\n" +
            "('android.permission.WRITE_PROFILE', 'normal', '" + NO_PERMISSION_GROUP.first + "', 'removed'),\n" +
            "('android.permission.READ_SOCIAL_STREAM', 'normal', '" + NO_PERMISSION_GROUP.first + "', 'removed'),\n" +
            "('android.permission.WRITE_SOCIAL_STREAM', 'normal', '" + NO_PERMISSION_GROUP.first + "', 'removed'),\n" +
            "('android.permission.READ_USER_DICTIONARY', 'normal', '" + NO_PERMISSION_GROUP.first + "', 'removed'),\n" +
            "('android.permission.WRITE_USER_DICTIONARY', 'normal', '" + NO_PERMISSION_GROUP.first + "', 'removed'),\n" +
            "('android.permission.WRITE_SMS', 'normal', '" + NO_PERMISSION_GROUP.first + "', 'removed'),\n" +
            "('com.android.browser.permission.READ_HISTORY_BOOKMARKS', 'normal', '" + NO_PERMISSION_GROUP.first + "', 'removed'),\n" +
            "('com.android.browser.permission.WRITE_HISTORY_BOOKMARKS', 'normal', '" + NO_PERMISSION_GROUP.first + "', 'removed'),\n" +
            "('android.permission.AUTHENTICATE_ACCOUNTS', 'normal', '" + NO_PERMISSION_GROUP.first + "', 'removed'),\n" +
            "('android.permission.MANAGE_ACCOUNTS', 'normal', '" + NO_PERMISSION_GROUP.first + "', 'removed'),\n" +
            "('android.permission.USE_CREDENTIALS', 'normal', '" + NO_PERMISSION_GROUP.first + "', 'removed'),\n" +
            "('android.permission.SUBSCRIBED_FEEDS_READ', 'normal', '" + NO_PERMISSION_GROUP.first + "', 'removed'),\n" +
            "('android.permission.SUBSCRIBED_FEEDS_WRITE', 'normal', '" + NO_PERMISSION_GROUP.first + "', 'removed'),\n" +
            "('android.permission.FLASHLIGHT', 'normal', '" + NO_PERMISSION_GROUP.first + "', 'removed'),\n" +
            "('android.permission.SEND_RESPOND_VIA_MESSAGE', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SEND_SMS_NO_CONFIRMATION', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CARRIER_FILTER_SMS', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.RECEIVE_EMERGENCY_BROADCAST', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.RECEIVE_BLUETOOTH_MAP', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_DIRECTORY_SEARCH', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MODIFY_CELL_BROADCASTS', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('com.android.alarm.permission.SET_ALARM', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('com.android.voicemail.permission.WRITE_VOICEMAIL', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('com.android.voicemail.permission.READ_VOICEMAIL', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_LOCATION_EXTRA_COMMANDS', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.INSTALL_LOCATION_PROVIDER', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.HDMI_CEC', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.LOCATION_HARDWARE', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_MOCK_LOCATION', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.INTERNET', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_NETWORK_STATE', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_WIFI_STATE', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CHANGE_WIFI_STATE', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_WIFI_CREDENTIAL', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.TETHER_PRIVILEGED', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.RECEIVE_WIFI_CREDENTIAL_CHANGE', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.OVERRIDE_WIFI_CONFIG', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_WIMAX_STATE', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CHANGE_WIMAX_STATE', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SCORE_NETWORKS', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BLUETOOTH', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BLUETOOTH_ADMIN', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BLUETOOTH_PRIVILEGED', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BLUETOOTH_MAP', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BLUETOOTH_STACK', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.NFC', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CONNECTIVITY_INTERNAL', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CONNECTIVITY_USE_RESTRICTED_NETWORKS', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.PACKET_KEEPALIVE_OFFLOAD', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.RECEIVE_DATA_ACTIVITY_CHANGE', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.LOOP_RADIO', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.NFC_HANDOVER_STATUS', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GET_ACCOUNTS', 'dangerous', 'android.permission-group.CONTACTS', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCOUNT_MANAGER', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CHANGE_WIFI_MULTICAST_STATE', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.VIBRATE', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.WAKE_LOCK', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.TRANSMIT_IR', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MODIFY_AUDIO_SETTINGS', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_USB', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_MTP', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.HARDWARE_TEST', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_FM_RADIO', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.NET_ADMIN', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.REMOTE_AUDIO_PLAYBACK', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.TV_INPUT_HARDWARE', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CAPTURE_TV_INPUT', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.DVB_DEVICE', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_OEM_UNLOCK_STATE', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.OEM_UNLOCK_STATE', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_PDB_STATE', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.NOTIFY_PENDING_SYSTEM_UPDATE', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CAMERA_DISABLE_TRANSMIT_LED', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CAMERA_SEND_SYSTEM_EVENTS', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MODIFY_PHONE_STATE', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_PRECISE_PHONE_STATE', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_PRIVILEGED_PHONE_STATE', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.REGISTER_SIM_SUBSCRIPTION', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.REGISTER_CALL_PROVIDER', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.REGISTER_CONNECTION_MANAGER', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_INCALL_SERVICE', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_SCREENING_SERVICE', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_CONNECTION_SERVICE', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_TELECOM_CONNECTION_SERVICE', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CONTROL_INCALL_EXPERIENCE', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.RECEIVE_STK_COMMANDS', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.WRITE_MEDIA_STORAGE', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_DOCUMENTS', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CACHE_CONTENT', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.DISABLE_KEYGUARD', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GET_TASKS', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.REAL_GET_TASKS', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.START_TASKS_FROM_RECENTS', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.INTERACT_ACROSS_USERS', 'signature|privileged|development', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.INTERACT_ACROSS_USERS_FULL', 'signature|installer', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_USERS', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CREATE_USERS', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GET_DETAILED_TASKS', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.REORDER_TASKS', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.REMOVE_TASKS', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_ACTIVITY_STACKS', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.START_ANY_ACTIVITY', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.RESTART_PACKAGES', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.KILL_BACKGROUND_PROCESSES', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GET_PROCESS_STATE_AND_OOM_SCORE', 'signature|privileged|development', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GET_PACKAGE_IMPORTANCE', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GET_INTENT_SENDER_INTENT', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SYSTEM_ALERT_WINDOW', 'signature|preinstalled|appop|pre23|development', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_WALLPAPER', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_WALLPAPER_HINTS', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_TIME', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_TIME_ZONE', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.EXPAND_STATUS_BAR', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('com.android.launcher.permission.INSTALL_SHORTCUT', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('com.android.launcher.permission.UNINSTALL_SHORTCUT', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_SYNC_SETTINGS', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.WRITE_SYNC_SETTINGS', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_SYNC_STATS', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_SCREEN_COMPATIBILITY', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CHANGE_CONFIGURATION', 'signature|privileged|development', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.WRITE_SETTINGS', 'signature|preinstalled|appop|pre23', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.WRITE_GSERVICES', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.FORCE_STOP_PACKAGES', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.RETRIEVE_WINDOW_CONTENT', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_ANIMATION_SCALE', 'signature|privileged|development', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.PERSISTENT_ACTIVITY', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GET_PACKAGE_SIZE', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_PREFERRED_APPLICATIONS', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.RECEIVE_BOOT_COMPLETED', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BROADCAST_STICKY', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MOUNT_UNMOUNT_FILESYSTEMS', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MOUNT_FORMAT_FILESYSTEMS', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.STORAGE_INTERNAL', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ASEC_ACCESS', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ASEC_CREATE', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ASEC_DESTROY', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ASEC_MOUNT_UNMOUNT', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ASEC_RENAME', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.WRITE_APN_SETTINGS', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CHANGE_NETWORK_STATE', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CLEAR_APP_CACHE', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ALLOW_ANY_CODEC_FOR_PLAYBACK', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_CA_CERTIFICATES', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.RECOVERY', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_JOB_SERVICE', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.UPDATE_CONFIG', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.RESET_SHORTCUT_MANAGER_THROTTLING', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.WRITE_SECURE_SETTINGS', 'signature|privileged|development', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.DUMP', 'signature|privileged|development', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_LOGS', 'signature|privileged|development', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_DEBUG_APP', 'signature|privileged|development', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_PROCESS_LIMIT', 'signature|privileged|development', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_ALWAYS_FINISH', 'signature|privileged|development', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SIGNAL_PERSISTENT_PROCESSES', 'signature|privileged|development', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GET_ACCOUNTS_PRIVILEGED', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GET_PASSWORD', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.DIAGNOSTIC', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.STATUS_BAR', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.STATUS_BAR_SERVICE', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_QUICK_SETTINGS_TILE', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.FORCE_BACK', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.UPDATE_DEVICE_STATS', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GET_APP_OPS_STATS', 'signature|privileged|development', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.UPDATE_APP_OPS_STATS', 'signature|privileged|installer', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_APP_OPS_RESTRICTIONS', 'signature|installer', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.INTERNAL_SYSTEM_WINDOW', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_APP_TOKENS', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.REGISTER_WINDOW_MANAGER_LISTENERS', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.FREEZE_SCREEN', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.INJECT_EVENTS', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.FILTER_EVENTS', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.RETRIEVE_WINDOW_TOKEN', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.FRAME_STATS', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.TEMPORARY_ENABLE_ACCESSIBILITY', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_ACTIVITY_WATCHER', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SHUTDOWN', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.STOP_APP_SWITCHES', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GET_TOP_ACTIVITY_INFO', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_INPUT_STATE', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_INPUT_METHOD', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_MIDI_DEVICE_SERVICE', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_ACCESSIBILITY_SERVICE', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_PRINT_SERVICE', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_PRINT_RECOMMENDATION_SERVICE', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_NFC_SERVICE', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_PRINT_SPOOLER_SERVICE', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_RUNTIME_PERMISSION_PRESENTER_SERVICE', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_TEXT_SERVICE', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_VPN_SERVICE', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_WALLPAPER', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_VOICE_INTERACTION', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_VOICE_KEYPHRASES', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_REMOTE_DISPLAY', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_TV_INPUT', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_TV_REMOTE_SERVICE', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.TV_VIRTUAL_REMOTE_CONTROLLER', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MODIFY_PARENTAL_CONTROLS', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_ROUTE_PROVIDER', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_DEVICE_ADMIN', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_DEVICE_ADMINS', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_ORIENTATION', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_POINTER_SPEED', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_INPUT_CALIBRATION', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_KEYBOARD_LAYOUT', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.TABLET_MODE', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.REQUEST_INSTALL_PACKAGES', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.INSTALL_PACKAGES', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CLEAR_APP_USER_DATA', 'signature|installer', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GET_APP_GRANTED_URI_PERMISSIONS', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CLEAR_APP_GRANTED_URI_PERMISSIONS', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.DELETE_CACHE_FILES', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.DELETE_PACKAGES', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MOVE_PACKAGE', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CHANGE_COMPONENT_ENABLED_STATE', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GRANT_RUNTIME_PERMISSIONS', 'signature|installer|verifier', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.INSTALL_GRANT_RUNTIME_PERMISSIONS', 'signature|installer|verifier', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.REVOKE_RUNTIME_PERMISSIONS', 'signature|installer|verifier', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.OBSERVE_GRANT_REVOKE_PERMISSIONS', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_SURFACE_FLINGER', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_FRAME_BUFFER', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_INPUT_FLINGER', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CONFIGURE_WIFI_DISPLAY', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CONTROL_WIFI_DISPLAY', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CONFIGURE_DISPLAY_COLOR_MODE', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CONTROL_VPN', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CAPTURE_AUDIO_OUTPUT', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CAPTURE_AUDIO_HOTWORD', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MODIFY_AUDIO_ROUTING', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CAPTURE_VIDEO_OUTPUT', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CAPTURE_SECURE_VIDEO_OUTPUT', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MEDIA_CONTENT_CONTROL', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BRICK', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.REBOOT', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.DEVICE_POWER', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.USER_ACTIVITY', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.NET_TUNNELING', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.FACTORY_TEST', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BROADCAST_PACKAGE_REMOVED', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BROADCAST_SMS', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BROADCAST_WAP_PUSH', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BROADCAST_NETWORK_PRIVILEGED', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MASTER_CLEAR', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CALL_PRIVILEGED', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.PERFORM_CDMA_PROVISIONING', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.PERFORM_SIM_ACTIVATION', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CONTROL_LOCATION_UPDATES', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_CHECKIN_PROPERTIES', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.PACKAGE_USAGE_STATS', 'signature|privileged|development|appop', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CHANGE_APP_IDLE_STATE', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CHANGE_DEVICE_IDLE_TEMP_WHITELIST', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BATTERY_STATS', 'signature|privileged|development', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BACKUP', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CONFIRM_FULL_BACKUP', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_REMOTEVIEWS', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_APPWIDGET', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_KEYGUARD_APPWIDGET', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MODIFY_APPWIDGET_BIND_PERMISSIONS', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CHANGE_BACKGROUND_DATA_SETTING', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GLOBAL_SEARCH', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GLOBAL_SEARCH_CONTROL', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_SEARCH_INDEXABLES', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_WALLPAPER_COMPONENT', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_DREAM_STATE', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.WRITE_DREAM_STATE', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_CACHE_FILESYSTEM', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.COPY_PROTECTED_DATA', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CRYPT_KEEPER', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_NETWORK_USAGE_HISTORY', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_NETWORK_POLICY', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MODIFY_NETWORK_ACCOUNTING', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.intent.category.MASTER_CLEAR.permission.C2D_MESSAGE', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.PACKAGE_VERIFICATION_AGENT', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_PACKAGE_VERIFIER', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.INTENT_FILTER_VERIFICATION_AGENT', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_INTENT_FILTER_VERIFIER', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SERIAL_PORT', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_CONTENT_PROVIDERS_EXTERNALLY', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.UPDATE_LOCK', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_NOTIFICATIONS', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_NOTIFICATION_POLICY', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_NOTIFICATIONS', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_KEYGUARD_SECURE_STORAGE', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_FINGERPRINT', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.RESET_FINGERPRINT_LOCKOUT', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CONTROL_KEYGUARD', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.TRUST_LISTENER', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.PROVIDE_TRUST_AGENT', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.LAUNCH_TRUST_AGENT_SETTINGS', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_TRUST_AGENT', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_NOTIFICATION_LISTENER_SERVICE', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_NOTIFICATION_RANKER_SERVICE', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_CHOOSER_TARGET_SERVICE', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_CONDITION_PROVIDER_SERVICE', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_DREAM_SERVICE', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.INVOKE_CARRIER_SETUP', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_NETWORK_CONDITIONS', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_DRM_CERTIFICATES', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_MEDIA_PROJECTION', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_INSTALL_SESSIONS', 'normal', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.REMOVE_DRM_CERTIFICATES', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_CARRIER_MESSAGING_SERVICE', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_VOICE_INTERACTION_SERVICE', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_CARRIER_SERVICES', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.QUERY_DO_NOT_ASK_CREDENTIALS_ON_BOOT', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.KILL_UID', 'signature|installer', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.LOCAL_MAC_ADDRESS', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.PEERS_MAC_ADDRESS', 'signature|setup', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.DISPATCH_NFC_MESSAGE', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MODIFY_DAY_NIGHT_MODE', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_EPHEMERAL_APPS', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.RECEIVE_MEDIA_RESOURCE_USAGE', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_SOUND_TRIGGER', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.DISPATCH_PROVISIONING_MESSAGE', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_BLOCKED_NUMBERS', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.WRITE_BLOCKED_NUMBERS', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_VR_LISTENER_SERVICE', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_VR_MANAGER', 'signature', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.UPDATE_LOCK_TASK_PACKAGES', 'signature|setup', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SUBSTITUTE_NOTIFICATION_APP_NAME', 'signature|privileged', '" + NO_PERMISSION_GROUP.first + "', '" + PERMISSION_FLAG_NONE + "');\n";

    public static String getCmdGrantGetUsageStats() {
        return CMD_GRANT_GET_USAGE_STATS;
    }

    public static String getCmdRevokeGetUsageStats() {
        return CMD_REVOKE_GET_USAGE_STATS;
    }

    public static String getPlaceRequestedExtra() {
        return PLACE_REQUESTED_EXTRA;
    }

    public static String getCmdGrantRealGetTasks() {
        return CMD_GRANT_REAL_GET_TASKS;
    }

    public static String getCmdRevokeRealGetTasks() {
        return CMD_REVOKE_REAL_GET_TASKS;
    }

    public static String getCmdGrantWriteSecureSettings() {
        return CMD_GRANT_WRITE_SECURE_SETTINGS;
    }

    public static String getCmdRevokeWriteSecureSettings() {
        return CMD_REVOKE_WRITE_SECURE_SETTINGS;
    }

    public static String getPrefBreakfastTemporalKey() {
        return PREF_BREAKFAST_TEMPORAL_KEY;
    }

    public static String getPrefWorkMorningTemporalKey() {
        return PREF_WORK_MORNING_TEMPORAL_KEY;
    }

    public static String getPrefLunchTemporalKey() {
        return PREF_LUNCH_TEMPORAL_KEY;
    }

    public static String getPrefWorkAfternoonTemporalKey() {
        return PREF_WORK_AFTERNOON_TEMPORAL_KEY;
    }

    public static String getPrefFamilyTemporalKey() {
        return PREF_FAMILY_TEMPORAL_KEY;
    }

    public static String getPrefDinnerTemporalKey() {
        return PREF_DINNER_TEMPORAL_KEY;
    }

    public static String getPrefAloneTemporalKey() {
        return PREF_ALONE_TEMPORAL_KEY;
    }

    public static String getPrefDndTemporalKey() {
        return PREF_DND_TEMPORAL_KEY;
    }

    public static String getPrefMondayTemporalKey() {
        return PREF_MONDAY_TEMPORAL_KEY;
    }

    public static String getPrefTuesdayTemporalKey() {
        return PREF_TUESDAY_TEMPORAL_KEY;
    }

    public static String getPrefWednesdayTemporalKey() {
        return PREF_WEDNESDAY_TEMPORAL_KEY;
    }

    public static String getPrefThursdayTemporalKey() {
        return PREF_THURSDAY_TEMPORAL_KEY;
    }

    public static String getPrefFridayTemporalKey() {
        return PREF_FRIDAY_TEMPORAL_KEY;
    }

    public static String getPrefSaturdayTemporalKey() {
        return PREF_SATURDAY_TEMPORAL_KEY;
    }

    public static String getPrefSundayTemporalKey() {
        return PREF_SUNDAY_TEMPORAL_KEY;
    }

    public static String getPrefWeekdayTemporalKey() {
        return PREF_WEEKDAY_TEMPORAL_KEY;
    }

    public static String getPrefAnydaytimeTemporalKey() {
        return PREF_ANYDAYTIME_TEMPORAL_KEY;
    }

    public static String getPrefWeekendTemporalKey() {
        return PREF_WEEKEND_TEMPORAL_KEY;
    }

    public static String getPrefPersonalTemporalKey() {
        return PREF_PERSONAL_TEMPORAL_KEY;
    }

    public static String getPrefProfessionalTemporalKey() {
        return PREF_PROFESSIONAL_TEMPORAL_KEY;
    }

    public static String getPrefAnytimeTemporalKey() {
        return PREF_ANYTIME_TEMPORAL_KEY;
    }

    public static double getRiskForPerm(String appOpName) {
        return 1.0;
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

    public static int getMillisecondsPerSecond() {
        return MILLISECONDS_PER_SECOND;
    }

    public static int getUpdateIntervalInSeconds() {
        return UPDATE_INTERVAL_IN_SECONDS;
    }

    public static long getUpdateInterval() {
        return UPDATE_INTERVAL;
    }

    public static int getFastestIntervalInSeconds() {
        return FASTEST_INTERVAL_IN_SECONDS;
    }

    public static long getFastestInterval() {
        return FASTEST_INTERVAL;
    }

    public static String getPrefKeyPoliciesDownloaded() {
        return PREF_KEY_POLICIES_DOWNLOADED;
    }

    public static String getPrefStartKey() {
        return PREF_START_KEY;
    }

    public static String getPrefEndKey() {
        return PREF_END_KEY;
    }

    public static String getLogIntent() {
        return LOG_INTENT;
    }

    public static String getPrefKeyGeofenceList() {
        return PREF_KEY_GEOFENCE_LIST;
    }

    public static String getPrefKeyTimefenceList() {
        return PREF_KEY_TIMEFENCE_LIST;
    }

    public static String getBroadcastIntentCommandApp() {
        return BROADCAST_INTENT_COMMAND_APP;
    }

    public static String getSharedPreferencesName() {
        return SHARED_PREFERENCES_NAME;
    }

    public static String getResultDataKey() {
        return RESULT_DATA_KEY;
    }

    public static String getLocationDataExtra() {
        return LOCATION_DATA_EXTRA;
    }

    public static String getGeofencesAddedKey() {
        return GEOFENCES_ADDED_KEY;
    }

    public static String getNoFlags() {
        return NO_FLAGS;
    }

    public static String getNormalProtectionLevel() {
        return NORMAL_PROTECTION_LEVEL;
    }

    public static float getRadiusOf200Meters() {
        return RADIUS_OF_200_METERS;
    }

    public static String getAddressRequestedExtra() {
        return ADDRESS_REQUESTED_EXTRA;
    }

    public static String getCurrAddressKey() {
        return CURR_ADDRESS_KEY;
    }

    public static String getPrefKeyContextTypeLocation() {
        return PREF_KEY_CONTEXT_TYPE_LOCATION;
    }

    public static String getPrefKeyContextTypeActivity() {
        return PREF_KEY_CONTEXT_TYPE_ACTIVITY;
    }

    public static String getPrefKeyContextTypeTemporal() {
        return PREF_KEY_CONTEXT_TYPE_TEMPORAL;
    }

    public static String getPrefKeyContextTypePresence() {
        return PREF_KEY_CONTEXT_TYPE_PRESENCE;
    }

    public static long getDefaultTimeSlot() {
        return DEFAULT_TIME_SLOT;
    }

    public static String getPrefKeyLocationInstancesHaveBeenSet() {
        return PREF_KEY_LOCATION_INSTANCES_HAVE_BEEN_SET;
    }

    public static String getPrefKeyActivityInstancesHaveBeenSet() {
        return PREF_KEY_ACTIVITY_INSTANCES_HAVE_BEEN_SET;
    }

    public static String getPrefKeyPresenceInstancesHaveBeenSet() {
        return PREF_KEY_PRESENCE_INSTANCES_HAVE_BEEN_SET;
    }

    public static String getPrefKeyTemporalInstancesHaveBeenSet() {
        return PREF_KEY_TEMPORAL_INSTANCES_HAVE_BEEN_SET;
    }

    public static String getPrefKeyListOfLocationInstances() {
        return PREF_KEY_LIST_OF_LOCATION_INSTANCES;
    }

    public static String getPrefKeyListOfActivityInstances() {
        return PREF_KEY_LIST_OF_ACTIVITY_INSTANCES;
    }

    public static String getPrefKeyListOfPresenceInstances() {
        return PREF_KEY_LIST_OF_PRESENCE_INSTANCES;
    }

    public static String getPrefKeyListOfTemporalInstances() {
        return PREF_KEY_LIST_OF_TEMPORAL_INSTANCES;
    }

    public static String getPrefHomeLocationKey() {
        return PREF_HOME_LOCATION_KEY;
    }

    public static String getPrefWorkLocationKey() {
        return PREF_WORK_LOCATION_KEY;
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

    public static String getAppReceiver() {
        return APP_RECEIVER;
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

    public static String getContextDefaultWorkLocation() {
        return CONTEXT_DEFAULT_WORK_LOCATION;
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

    public static String getContextDefaultRelationship() {
        return CONTEXT_DEFAULT_RELATIONSHIP;
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

    public static String getPrefKeyPermGroupLabel() {
        return PREF_KEY_PERM_GROUP_LABEL;
    }

    public static String getPrefKeyPermGroupName() {
        return PREF_KEY_PERM_GROUP_NAME;
    }

    public static String getPrefKeyLocaInstancesCreated() {
        return PREF_KEY_LOCA_INSTANCES_CREATED;
    }

    public static String getPrefKeyTimeInstancesCreated() {
        return PREF_KEY_TIME_INSTANCES_CREATED;
    }

    public static String getPrefKeyPresInstancesCreated() {
        return PREF_KEY_PRES_INSTANCES_CREATED;
    }

    public static String getPrefKeyActiInstancesCreated() {
        return PREF_KEY_ACTI_INSTANCES_CREATED;
    }

    public static float getSmallestDisplacement() {
        return SMALLEST_DISPLACEMENT;
    }

    public static String getInsertStatementGooglePermissions() {
        return INSERT_STATEMENT_GOOGLE_PERMISSIONS;
    }

    public static String getFlierPdfFileName() {
        return FLIER_PDF_FILE_NAME;
    }

    public static String getPrefKeyUserConsent() {
        return PREF_KEY_USER_CONSENT;
    }

    public static String getPrefKeyUserAgreementPageNumber() {
        return PREF_KEY_USER_AGREEMENT_PAGE_NUMBER;
    }

    public static String getPrefKeyUserAgreementCopied() {
        return PREF_KEY_USER_AGREEMENT_COPIED;
    }

    public static long getLaunchDetectInterval() {
        return LAUNCH_DETECT_INTERVAL;
    }

    public static int getLaunchDetectionIntervalInSeconds() {
        return LAUNCH_DETECTION_INTERVAL_IN_SECONDS;
    }

    public static String getPrefKeyLastRunningApp() {
        return PREF_KEY_LAST_RUNNING_APP;
    }

    public static String getPrefKeyShouldShowAgreementSnackbar() {
        return PREF_KEY_SHOULD_SHOW_AGREEMENT_SNACKBAR;
    }

    public static String getPrefKeyUserDeniedPermissions() {
        return PREF_KEY_USER_DENIED_PERMISSIONS;
    }

    public static String getPrefKeyUserDeniedUsageStatsPermissions() {
        return PREF_KEY_USER_DENIED_USAGE_STATS_PERMISSIONS;
    }

    public static String getBackPressedUserAgreementScreen() {
        return BACK_PRESSED_USER_AGREEMENT_SCREEN;
    }

    public static String getCmdGrantReadLogsPermissionForApp() {
        return CMD_GRANT_READ_LOGS_PERMISSION_FOR_APP;
    }

    public static String getCmdRevokeReadLogsPermissionForApp() {
        return CMD_REVOKE_READ_LOGS_PERMISSION_FOR_APP;
    }

    public static String getCmdGrantPackageUsageStatsPermissionForApp() {
        return CMD_GRANT_PACKAGE_USAGE_STATS_PERMISSION_FOR_APP;
    }

    public static String getCmdRevokePackageUsageStatsPermissionForApp() {
        return CMD_REVOKE_PACKAGE_USAGE_STATS_PERMISSION_FOR_APP;
    }

    public static String getCmdGrantUpdateAppOpsStats() {
        return CMD_GRANT_UPDATE_APP_OPS_STATS;
    }

    public static String getCmdRevokeUpdateAppOpsStats() {
        return CMD_REVOKE_UPDATE_APP_OPS_STATS;
    }

    public static String getCmdGrantGetAppOpsStats() {
        return CMD_GRANT_GET_APP_OPS_STATS;
    }

    public static String getCmdRevokeGetAppOpsStats() {
        return CMD_REVOKE_GET_APP_OPS_STATS;
    }

    public static String getCmdGrantManageAppOpsRestrictions() {
        return CMD_GRANT_MANAGE_APP_OPS_RESTRICTIONS;
    }

    public static String getCmdRevokeManageAppOpsRestrictions() {
        return CMD_REVOKE_MANAGE_APP_OPS_RESTRICTIONS;
    }

    public static String getCalendarPermissionGroupDesc() {
        return CALENDAR_PERMISSION_GROUP_DESC;
    }

    public static String getCameraPermissionGroupDesc() {
        return CAMERA_PERMISSION_GROUP_DESC;
    }

    public static String getContactsPermissionGroupDesc() {
        return CONTACTS_PERMISSION_GROUP_DESC;
    }

    public static String getLocationPermissionGroupDesc() {
        return LOCATION_PERMISSION_GROUP_DESC;
    }

    public static String getMicrophonePermissionGroupDesc() {
        return MICROPHONE_PERMISSION_GROUP_DESC;
    }

    public static String getPhonePermissionGroupDesc() {
        return PHONE_PERMISSION_GROUP_DESC;
    }

    public static String getSensorsPermissionGroupDesc() {
        return SENSORS_PERMISSION_GROUP_DESC;
    }

    public static String getSmsPermissionGroupDesc() {
        return SMS_PERMISSION_GROUP_DESC;
    }

    public static String getStoragePermissionGroupDesc() {
        return STORAGE_PERMISSION_GROUP_DESC;
    }

    public static String getSystemToolsPermissionGroupDesc() {
        return SYSTEM_TOOLS_PERMISSION_GROUP_DESC;
    }

    public static String getCarInformationPermissionGroupDesc() {
        return CAR_INFORMATION_PERMISSION_GROUP_DESC;
    }

    public static String getNoPermissionGroupDesc() {
        return NO_PERMISSION_GROUP_DESC;
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

    public static String getPrefKeyUserContinueClicked() {
        return PREF_KEY_USER_CONTINUE_CLICKED;
    }

    public static String getLauncherName(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }

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

    public static String getPrefKeyTemporalLabel() {
        return PREF_KEY_TEMPORAL_LABEL;
    }

    public static String getNeverRepeats() {
        return NEVER_REPEATS;
    }

    public static String getNotYet() {
        return NOT_YET;
    }

    public static CharSequence getTimeText(boolean showEmptyText, Timestamp time) {
        if (time.getTime() > 0) {
            return DateUtils.getRelativeTimeSpanString(time.getTime(),
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_RELATIVE);
        }
        return showEmptyText ? NOT_YET : "";
    }
}