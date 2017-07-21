package edu.umbc.ebiquity.mithril;

import android.Manifest;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static final int SUCCESS_RESULT = 6;
    public static final int FAILURE_RESULT = 7;
    public static final int APP_INSTALL_REQUEST_CODES_1 = 1;
    public static final int APP_INSTALL_REQUEST_CODES_2 = 2;
    public static final int APP_INSTALL_REQUEST_CODES_3 = 3;
    public static final int APP_INSTALL_REQUEST_CODES_4 = 4;
    public static final int APP_INSTALL_REQUEST_CODES_5 = 5;
    public static final int APP_INSTALL_REQUEST_CODES_6 = 6;
    public static final int APP_INSTALL_REQUEST_CODES_7 = 7;
    public static final int APP_INSTALL_REQUEST_CODES_8 = 8;
    public static final int APP_INSTALL_REQUEST_CODES_9 = 9;
    public static final int APP_INSTALL_REQUEST_CODES_10 = 10;
    public static final String MITHRIL_APP_PACKAGE_NAME = "edu.umbc.ebiquity.mithril";
    public static final String PHONE_NOT_ROOTED_MITHRIL_BYE_BYE_MESSAGE = "Your phone is not rooted\nMithril won't work on this device\nSorry and thanks for participating in our survey";
    public static final String MITHRIL_BYE_BYE_MESSAGE = "Bye! Thanks for helping with our survey...";
    private static final String MITHRIL_FIREBASE_SERVER_KEY_USERS = "users";
    private static final String MITHRIL_FIREBASE_SERVER_KEY_POLICIES = "policies";
    private static final String MITHRIL_FIREBASE_SERVER_KEY_APPS = "apps";
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
    private static final String FEEDBACK_UPLOAD_RESULT_KEY = "uploadResult";
    private static final String FEEDBACK_QUESTION_1 = "fq1";
    private static final String FEEDBACK_QUESTION_2 = "fq2";
    private static final String FEEDBACK_QUESTION_3 = "fq3";
    private static final String FEEDBACK_QUESTION_EXTRA = "fqextra";
    private static final String FEEDBACK_QUESTION_4 = "fq4";
    private static final String FEEDBACK_QUESTION_5 = "fq5";
    private static final String FEEDBACK_QUESTION_6 = "fq6";
    private static final String FEEDBACK_QUESTION_7 = "fq7";
    private static final String FEEDBACK_QUESTION_8 = "fq8";
    private static final String FEEDBACK_QUESTION_9 = "fq9";
    private static final String FEEDBACK_QUESTION_10 = "fq10";
    private static final String FEEDBACK_QUESTION_DATA_KEY = "feedbackDataKey";
    private static final String FEEDBACK_QUESTION_DATA_TIME_KEY = "feedbackDataTimeKey";
    private static final String FEEDBACK_URL = "http://104.154.36.223/bot/feedback/";
    private static final String RANDOM_USER_ID = "randomUserId";
    private static final String APP_CATEGORY_UNKNOWN = "unknownAppCategory";
    private static final String APPS_INSTALLED = "appsInstalled";
    private static final Map<String, String> PERM_CAT_MAP = new HashMap<String, String>() {
        {
            put(Manifest.permission.ADD_VOICEMAIL, "messages");
            put(Manifest.permission.READ_SMS, "messages");

            put(Manifest.permission.READ_CONTACTS, "contacts");
            put(Manifest.permission.WRITE_CONTACTS, "contacts");

            put(Manifest.permission.CAMERA, "media");
            put(Manifest.permission.RECORD_AUDIO, "media");

            put(Manifest.permission.SYSTEM_ALERT_WINDOW, "overlay");

            put(Manifest.permission.READ_EXTERNAL_STORAGE, "storage");
            put(Manifest.permission.WRITE_EXTERNAL_STORAGE, "storage");

            put(Manifest.permission.CALL_PHONE, "calling");
            put(Manifest.permission.SEND_SMS, "calling");
            put(Manifest.permission.RECEIVE_SMS, "calling");
            put(Manifest.permission.READ_CALL_LOG, "calling");
            put(Manifest.permission.WRITE_CALL_LOG, "calling");

            put(Manifest.permission.ACCESS_NOTIFICATIONS, "notifications");

            put(Manifest.permission.GET_ACCOUNTS, "identification");

            put(Manifest.permission.ACCESS_COARSE_LOCATION, "location");
            put(Manifest.permission.ACCESS_FINE_LOCATION, "location");

            put(Manifest.permission.READ_CALENDAR, "calendar");
            put(Manifest.permission.WRITE_CALENDAR, "calendar");
        }
    };
    private static final List<String> APP_PACKAGE_NAMES = new ArrayList<String>() {
        {
            add(new String("com.adobe.reader"));
			add(new String("com.xodo.pdf.reader"))
            add(new String("com.augmentedminds.waveAlarm"));
            add(new String("com.google.android.deskclock"));
            add(new String("com.groupme.android"));
            add(new String("com.whatsapp"));
            add(new String("net.sourceforge.opencamera"));
            add(new String("com.google.android.GoogleCamera"));
        }
    };
    private static final List<String> APP_NAMES = new ArrayList<String>() {
        {
            add(new String("Adobe Acrobat Reader")); // PDF reader
            add(new String("Xodo PDF Reader & Editor")); // PDF reader
            add(new String("Wave Alarm - Alarm Clock")); // Alarm clock
            add(new String("Clock")); // Alarm clock
            add(new String("GroupMe")); // communication
            add(new String("WhatsApp Messenger")); // communication
            add(new String("Open Camera")); // photography
            add(new String("Google Camera")); // photography
        }
    };
    private static final List<String> APP_CATS_FOR_TEST = new ArrayList<String>() {
        {
            add(new String("productivity"));
            add(new String("productivity"));
            add(new String("tools"));
            add(new String("tools"));
            add(new String("communication"));
            add(new String("communication"));
            add(new String("photography"));
            add(new String("photography"));
        }
    };
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
    private static final String CMD_DETECT_APP_LAUNCH = "logcat -d ActivityManager:I *:S | grep 'LAUNCHER' | cut -f5 -d'=' | cut -f1 -d'/'";
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
    private static final String PREF_BOSS_PRESENCE_KEY = "Boss";
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
    private static final String PREF_TIME_INSTANT_SUNRISE_TEMPORAL_KEY = "Sunrise"; //Sunrise in a locale
    private static final String PREF_TIME_INSTANT_SUNSET_TEMPORAL_KEY = "Sunset"; // Sunset in a locale
    private static final String PREF_MORNING_TEMPORAL_KEY = "Morning"; //0800 - 1200
    private static final String PREF_AFTERNOON_TEMPORAL_KEY = "Afternoon"; //1200 - 1600
    private static final String PREF_EVENING_TEMPORAL_KEY = "Evening"; //1600 - 2100
    private static final String PREF_DINNER_TEMPORAL_KEY = "Dinner"; //1900 - 1930
    private static final String PREF_NIGHT_TEMPORAL_KEY = "Night"; //2100 - 0800
    private static final String PREF_HOLIDAY_TEMPORAL_KEY = "Holiday"; //Official holiday
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
    private static final String CONTEXT_ARRAY_TYPES[] = {PREF_KEY_CONTEXT_TYPE_ACTIVITY, PREF_KEY_CONTEXT_TYPE_TEMPORAL, PREF_KEY_CONTEXT_TYPE_PRESENCE, PREF_KEY_CONTEXT_TYPE_LOCATION};
    private static final String CONTEXT_ARRAY_ACTIVITY[] = {"Activity", "Personal_Activity", "Professional_Activity", "Date", "Entertainment",
            "Exercising", "Sleeping", "Partying", "Playing", "Running", "Driving", "Biking", "Walking", "Running", "Working"};
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
    private static final String INSERT_STATEMENT_DEFAULT_POLICIES = "INSERT INTO defpol (category, permission, value) VALUES\n" +
            "('family', 'messages', 0.638888888889),\n" +
            "('family', 'overlay', 0.805555555556),\n" +
            "('family', 'media', 0.611111111111),\n" +
            "('family', 'contacts', 0.555555555556),\n" +
            "('family', 'storage', 0.777777777778),\n" +
            "('family', 'calling', 0.583333333333),\n" +
            "('family', 'notifications', 0.444444444444),\n" +
            "('family', 'identification', 0.277777777778),\n" +
            "('family', 'location', 0.5),\n" +
            "('family', 'appCount', 36),\n" +
            "('family', 'calendar', 0.638888888889),\n" +
            "('family', 'email', 0.638888888889),\n" +
            "('communication', 'messages', 0.50773993808),\n" +
            "('communication', 'overlay', 0.736842105263),\n" +
            "('communication', 'media', 0.56346749226),\n" +
            "('communication', 'contacts', 0.551083591331),\n" +
            "('communication', 'storage', 0.763157894737),\n" +
            "('communication', 'calling', 0.487616099071),\n" +
            "('communication', 'notifications', 0.62693498452),\n" +
            "('communication', 'identification', 0.267801857585),\n" +
            "('communication', 'location', 0.356037151703),\n" +
            "('communication', 'appCount', 646),\n" +
            "('communication', 'calendar', 0.50773993808),\n" +
            "('communication', 'email', 0.50773993808),\n" +
            "('puzzle', 'messages', 0.466666666667),\n" +
            "('puzzle', 'overlay', 0.695238095238),\n" +
            "('puzzle', 'media', 0.464285714286),\n" +
            "('puzzle', 'contacts', 0.442857142857),\n" +
            "('puzzle', 'storage', 0.654761904762),\n" +
            "('puzzle', 'calling', 0.421428571429),\n" +
            "('puzzle', 'notifications', 0.414285714286),\n" +
            "('puzzle', 'identification', 0.15),\n" +
            "('puzzle', 'location', 0.4),\n" +
            "('puzzle', 'appCount', 420),\n" +
            "('puzzle', 'calendar', 0.466666666667),\n" +
            "('puzzle', 'email', 0.466666666667),\n" +
            "('weather', 'messages', 0.449197860963),\n" +
            "('weather', 'overlay', 0.604278074866),\n" +
            "('weather', 'media', 0.401069518717),\n" +
            "('weather', 'contacts', 0.433155080214),\n" +
            "('weather', 'storage', 0.668449197861),\n" +
            "('weather', 'calling', 0.374331550802),\n" +
            "('weather', 'notifications', 0.582887700535),\n" +
            "('weather', 'identification', 0.16577540107),\n" +
            "('weather', 'location', 0.588235294118),\n" +
            "('weather', 'appCount', 187),\n" +
            "('weather', 'calendar', 0.449197860963),\n" +
            "('weather', 'email', 0.449197860963),\n" +
            "('personalization', 'messages', 0.412724306688),\n" +
            "('personalization', 'overlay', 0.657422512235),\n" +
            "('personalization', 'media', 0.409461663948),\n" +
            "('personalization', 'contacts', 0.396411092985),\n" +
            "('personalization', 'storage', 0.615008156607),\n" +
            "('personalization', 'calling', 0.386623164763),\n" +
            "('personalization', 'notifications', 0.487765089723),\n" +
            "('personalization', 'identification', 0.265905383361),\n" +
            "('personalization', 'location', 0.365415986949),\n" +
            "('personalization', 'appCount', 613),\n" +
            "('personalization', 'calendar', 0.412724306688),\n" +
            "('personalization', 'email', 0.412724306688),\n" +
            "('music_n_audio', 'messages', 0.436408977556),\n" +
            "('music_n_audio', 'overlay', 0.68578553616),\n" +
            "('music_n_audio', 'media', 0.543640897756),\n" +
            "('music_n_audio', 'contacts', 0.39650872818),\n" +
            "('music_n_audio', 'storage', 0.708229426434),\n" +
            "('music_n_audio', 'calling', 0.361596009975),\n" +
            "('music_n_audio', 'notifications', 0.54114713217),\n" +
            "('music_n_audio', 'identification', 0.164588528678),\n" +
            "('music_n_audio', 'location', 0.30174563591),\n" +
            "('music_n_audio', 'appCount', 401),\n" +
            "('music_n_audio', 'calendar', 0.436408977556),\n" +
            "('music_n_audio', 'email', 0.436408977556),\n" +
            "('shopping', 'messages', 0.525597269625),\n" +
            "('shopping', 'overlay', 0.706484641638),\n" +
            "('shopping', 'media', 0.484641638225),\n" +
            "('shopping', 'contacts', 0.41638225256),\n" +
            "('shopping', 'storage', 0.648464163823),\n" +
            "('shopping', 'calling', 0.399317406143),\n" +
            "('shopping', 'notifications', 0.631399317406),\n" +
            "('shopping', 'identification', 0.116040955631),\n" +
            "('shopping', 'location', 0.361774744027),\n" +
            "('shopping', 'appCount', 293),\n" +
            "('shopping', 'calendar', 0.525597269625),\n" +
            "('shopping', 'email', 0.525597269625),\n" +
            "('media_n_video', 'messages', 0.44966442953),\n" +
            "('media_n_video', 'overlay', 0.674496644295),\n" +
            "('media_n_video', 'media', 0.510067114094),\n" +
            "('media_n_video', 'contacts', 0.375838926174),\n" +
            "('media_n_video', 'storage', 0.761744966443),\n" +
            "('media_n_video', 'calling', 0.348993288591),\n" +
            "('media_n_video', 'notifications', 0.520134228188),\n" +
            "('media_n_video', 'identification', 0.184563758389),\n" +
            "('media_n_video', 'location', 0.332214765101),\n" +
            "('media_n_video', 'appCount', 298),\n" +
            "('media_n_video', 'calendar', 0.44966442953),\n" +
            "('media_n_video', 'email', 0.44966442953),\n" +
            "('strategy', 'messages', 0.671936758893),\n" +
            "('strategy', 'overlay', 0.822134387352),\n" +
            "('strategy', 'media', 0.632411067194),\n" +
            "('strategy', 'contacts', 0.608695652174),\n" +
            "('strategy', 'storage', 0.845849802372),\n" +
            "('strategy', 'calling', 0.588932806324),\n" +
            "('strategy', 'notifications', 0.450592885375),\n" +
            "('strategy', 'identification', 0.280632411067),\n" +
            "('strategy', 'location', 0.573122529644),\n" +
            "('strategy', 'appCount', 253),\n" +
            "('strategy', 'calendar', 0.671936758893),\n" +
            "('strategy', 'email', 0.671936758893),\n" +
            "('tools', 'messages', 0.489494859186),\n" +
            "('tools', 'overlay', 0.669199821189),\n" +
            "('tools', 'media', 0.476531068395),\n" +
            "('tools', 'contacts', 0.440321859633),\n" +
            "('tools', 'storage', 0.707644166294),\n" +
            "('tools', 'calling', 0.395619132767),\n" +
            "('tools', 'notifications', 0.576665176576),\n" +
            "('tools', 'identification', 0.263746088511),\n" +
            "('tools', 'location', 0.404112650872),\n" +
            "('tools', 'appCount', 2237),\n" +
            "('tools', 'calendar', 0.489494859186),\n" +
            "('tools', 'email', 0.489494859186),\n" +
            "('casual', 'messages', 0.620408163265),\n" +
            "('casual', 'overlay', 0.820408163265),\n" +
            "('casual', 'media', 0.612244897959),\n" +
            "('casual', 'contacts', 0.583673469388),\n" +
            "('casual', 'storage', 0.767346938776),\n" +
            "('casual', 'calling', 0.54693877551),\n" +
            "('casual', 'notifications', 0.526530612245),\n" +
            "('casual', 'identification', 0.302040816327),\n" +
            "('casual', 'location', 0.538775510204),\n" +
            "('casual', 'appCount', 245),\n" +
            "('casual', 'calendar', 0.620408163265),\n" +
            "('casual', 'email', 0.620408163265),\n" +
            "('health_n_fitness', 'messages', 0.486301369863),\n" +
            "('health_n_fitness', 'overlay', 0.630136986301),\n" +
            "('health_n_fitness', 'media', 0.513698630137),\n" +
            "('health_n_fitness', 'contacts', 0.417808219178),\n" +
            "('health_n_fitness', 'storage', 0.681506849315),\n" +
            "('health_n_fitness', 'calling', 0.417808219178),\n" +
            "('health_n_fitness', 'notifications', 0.599315068493),\n" +
            "('health_n_fitness', 'identification', 0.188356164384),\n" +
            "('health_n_fitness', 'location', 0.400684931507),\n" +
            "('health_n_fitness', 'appCount', 292),\n" +
            "('health_n_fitness', 'calendar', 0.486301369863),\n" +
            "('health_n_fitness', 'email', 0.486301369863),\n" +
            "('educational', 'messages', 0.4),\n" +
            "('educational', 'overlay', 0.633333333333),\n" +
            "('educational', 'media', 0.466666666667),\n" +
            "('educational', 'contacts', 0.3),\n" +
            "('educational', 'storage', 0.633333333333),\n" +
            "('educational', 'calling', 0.3),\n" +
            "('educational', 'notifications', 0.466666666667),\n" +
            "('educational', 'identification', 0.166666666667),\n" +
            "('educational', 'location', 0.2),\n" +
            "('educational', 'appCount', 30),\n" +
            "('educational', 'calendar', 0.4),\n" +
            "('educational', 'email', 0.4),\n" +
            "('education', 'messages', 0.58435207824),\n" +
            "('education', 'overlay', 0.750611246944),\n" +
            "('education', 'media', 0.559902200489),\n" +
            "('education', 'contacts', 0.303178484108),\n" +
            "('education', 'storage', 0.748166259169),\n" +
            "('education', 'calling', 0.249388753056),\n" +
            "('education', 'notifications', 0.635696821516),\n" +
            "('education', 'identification', 0.124694376528),\n" +
            "('education', 'location', 0.276283618582),\n" +
            "('education', 'appCount', 409),\n" +
            "('education', 'calendar', 0.58435207824),\n" +
            "('education', 'email', 0.58435207824),\n" +
            "('productivity', 'messages', 0.556745182013),\n" +
            "('productivity', 'overlay', 0.704496788009),\n" +
            "('productivity', 'media', 0.544967880086),\n" +
            "('productivity', 'contacts', 0.473233404711),\n" +
            "('productivity', 'storage', 0.778372591006),\n" +
            "('productivity', 'calling', 0.422912205567),\n" +
            "('productivity', 'notifications', 0.615631691649),\n" +
            "('productivity', 'identification', 0.252676659529),\n" +
            "('productivity', 'location', 0.395074946467),\n" +
            "('productivity', 'appCount', 934),\n" +
            "('productivity', 'calendar', 0.556745182013),\n" +
            "('productivity', 'email', 0.556745182013),\n" +
            "('arcade', 'messages', 0.580901856764),\n" +
            "('arcade', 'overlay', 0.816976127321),\n" +
            "('arcade', 'media', 0.562334217507),\n" +
            "('arcade', 'contacts', 0.538461538462),\n" +
            "('arcade', 'storage', 0.801061007958),\n" +
            "('arcade', 'calling', 0.498673740053),\n" +
            "('arcade', 'notifications', 0.472148541114),\n" +
            "('arcade', 'identification', 0.286472148541),\n" +
            "('arcade', 'location', 0.506631299735),\n" +
            "('arcade', 'appCount', 377),\n" +
            "('arcade', 'calendar', 0.580901856764),\n" +
            "('arcade', 'email', 0.580901856764),\n" +
            "('entertainment', 'messages', 0.524416135881),\n" +
            "('entertainment', 'overlay', 0.696390658174),\n" +
            "('entertainment', 'media', 0.51804670913),\n" +
            "('entertainment', 'contacts', 0.430997876858),\n" +
            "('entertainment', 'storage', 0.68152866242),\n" +
            "('entertainment', 'calling', 0.397027600849),\n" +
            "('entertainment', 'notifications', 0.590233545648),\n" +
            "('entertainment', 'identification', 0.218683651805),\n" +
            "('entertainment', 'location', 0.373673036093),\n" +
            "('entertainment', 'appCount', 471),\n" +
            "('entertainment', 'calendar', 0.524416135881),\n" +
            "('entertainment', 'email', 0.524416135881),\n" +
            "('unknown', 'messages', 0.525186356449),\n" +
            "('unknown', 'overlay', 0.733453806189),\n" +
            "('unknown', 'media', 0.543709058053),\n" +
            "('unknown', 'contacts', 0.494691664784),\n" +
            "('unknown', 'storage', 0.721481816128),\n" +
            "('unknown', 'calling', 0.474587756946),\n" +
            "('unknown', 'notifications', 0.548226790151),\n" +
            "('unknown', 'identification', 0.338378134177),\n" +
            "('unknown', 'location', 0.43347639485),\n" +
            "('unknown', 'appCount', 4427),\n" +
            "('unknown', 'calendar', 0.525186356449),\n" +
            "('unknown', 'email', 0.525186356449),\n" +
            "('sports', 'messages', 0.429864253394),\n" +
            "('sports', 'overlay', 0.696832579186),\n" +
            "('sports', 'media', 0.407239819005),\n" +
            "('sports', 'contacts', 0.37556561086),\n" +
            "('sports', 'storage', 0.628959276018),\n" +
            "('sports', 'calling', 0.325791855204),\n" +
            "('sports', 'notifications', 0.511312217195),\n" +
            "('sports', 'identification', 0.180995475113),\n" +
            "('sports', 'location', 0.366515837104),\n" +
            "('sports', 'appCount', 221),\n" +
            "('sports', 'calendar', 0.429864253394),\n" +
            "('sports', 'email', 0.429864253394),\n" +
            "('travel_n_local', 'messages', 0.458549222798),\n" +
            "('travel_n_local', 'overlay', 0.608808290155),\n" +
            "('travel_n_local', 'media', 0.481865284974),\n" +
            "('travel_n_local', 'contacts', 0.424870466321),\n" +
            "('travel_n_local', 'storage', 0.670984455959),\n" +
            "('travel_n_local', 'calling', 0.40414507772),\n" +
            "('travel_n_local', 'notifications', 0.572538860104),\n" +
            "('travel_n_local', 'identification', 0.20207253886),\n" +
            "('travel_n_local', 'location', 0.629533678756),\n" +
            "('travel_n_local', 'appCount', 386),\n" +
            "('travel_n_local', 'calendar', 0.458549222798),\n" +
            "('travel_n_local', 'email', 0.458549222798),\n" +
            "('music', 'messages', 0.411764705882),\n" +
            "('music', 'overlay', 0.823529411765),\n" +
            "('music', 'media', 0.647058823529),\n" +
            "('music', 'contacts', 0.352941176471),\n" +
            "('music', 'storage', 0.764705882353),\n" +
            "('music', 'calling', 0.352941176471),\n" +
            "('music', 'notifications', 0.529411764706),\n" +
            "('music', 'identification', 0.352941176471),\n" +
            "('music', 'location', 0.411764705882),\n" +
            "('music', 'appCount', 17),\n" +
            "('music', 'calendar', 0.411764705882),\n" +
            "('music', 'email', 0.411764705882),\n" +
            "('board', 'messages', 0.492957746479),\n" +
            "('board', 'overlay', 0.647887323944),\n" +
            "('board', 'media', 0.492957746479),\n" +
            "('board', 'contacts', 0.43661971831),\n" +
            "('board', 'storage', 0.591549295775),\n" +
            "('board', 'calling', 0.43661971831),\n" +
            "('board', 'notifications', 0.56338028169),\n" +
            "('board', 'identification', 0.225352112676),\n" +
            "('board', 'location', 0.394366197183),\n" +
            "('board', 'appCount', 71),\n" +
            "('board', 'calendar', 0.492957746479),\n" +
            "('board', 'email', 0.492957746479),\n" +
            "('trivia', 'messages', 0.666666666667),\n" +
            "('trivia', 'overlay', 0.916666666667),\n" +
            "('trivia', 'media', 0.611111111111),\n" +
            "('trivia', 'contacts', 0.666666666667),\n" +
            "('trivia', 'storage', 0.75),\n" +
            "('trivia', 'calling', 0.611111111111),\n" +
            "('trivia', 'notifications', 0.666666666667),\n" +
            "('trivia', 'identification', 0.138888888889),\n" +
            "('trivia', 'location', 0.361111111111),\n" +
            "('trivia', 'appCount', 36),\n" +
            "('trivia', 'calendar', 0.666666666667),\n" +
            "('trivia', 'email', 0.666666666667),\n" +
            "('racing', 'messages', 0.541176470588),\n" +
            "('racing', 'overlay', 0.858823529412),\n" +
            "('racing', 'media', 0.588235294118),\n" +
            "('racing', 'contacts', 0.470588235294),\n" +
            "('racing', 'storage', 0.858823529412),\n" +
            "('racing', 'calling', 0.411764705882),\n" +
            "('racing', 'notifications', 0.588235294118),\n" +
            "('racing', 'identification', 0.317647058824),\n" +
            "('racing', 'location', 0.4),\n" +
            "('racing', 'appCount', 85),\n" +
            "('racing', 'calendar', 0.541176470588),\n" +
            "('racing', 'email', 0.541176470588),\n" +
            "('news_n_magazines', 'messages', 0.395894428152),\n" +
            "('news_n_magazines', 'overlay', 0.645161290323),\n" +
            "('news_n_magazines', 'media', 0.431085043988),\n" +
            "('news_n_magazines', 'contacts', 0.363636363636),\n" +
            "('news_n_magazines', 'storage', 0.680351906158),\n" +
            "('news_n_magazines', 'calling', 0.304985337243),\n" +
            "('news_n_magazines', 'notifications', 0.542521994135),\n" +
            "('news_n_magazines', 'identification', 0.131964809384),\n" +
            "('news_n_magazines', 'location', 0.316715542522),\n" +
            "('news_n_magazines', 'appCount', 341),\n" +
            "('news_n_magazines', 'calendar', 0.395894428152),\n" +
            "('news_n_magazines', 'email', 0.395894428152),\n" +
            "('role_playing', 'messages', 0.84046692607),\n" +
            "('role_playing', 'overlay', 0.910505836576),\n" +
            "('role_playing', 'media', 0.848249027237),\n" +
            "('role_playing', 'contacts', 0.793774319066),\n" +
            "('role_playing', 'storage', 0.906614785992),\n" +
            "('role_playing', 'calling', 0.785992217899),\n" +
            "('role_playing', 'notifications', 0.466926070039),\n" +
            "('role_playing', 'identification', 0.256809338521),\n" +
            "('role_playing', 'location', 0.782101167315),\n" +
            "('role_playing', 'appCount', 257),\n" +
            "('role_playing', 'calendar', 0.84046692607),\n" +
            "('role_playing', 'email', 0.84046692607),\n" +
            "('transportation', 'messages', 0.481617647059),\n" +
            "('transportation', 'overlay', 0.617647058824),\n" +
            "('transportation', 'media', 0.455882352941),\n" +
            "('transportation', 'contacts', 0.441176470588),\n" +
            "('transportation', 'storage', 0.602941176471),\n" +
            "('transportation', 'calling', 0.422794117647),\n" +
            "('transportation', 'notifications', 0.566176470588),\n" +
            "('transportation', 'identification', 0.161764705882),\n" +
            "('transportation', 'location', 0.591911764706),\n" +
            "('transportation', 'appCount', 272),\n" +
            "('transportation', 'calendar', 0.481617647059),\n" +
            "('transportation', 'email', 0.481617647059),\n" +
            "('finance', 'messages', 0.531177829099),\n" +
            "('finance', 'overlay', 0.713625866051),\n" +
            "('finance', 'media', 0.519630484988),\n" +
            "('finance', 'contacts', 0.420323325635),\n" +
            "('finance', 'storage', 0.653579676674),\n" +
            "('finance', 'calling', 0.406466512702),\n" +
            "('finance', 'notifications', 0.637413394919),\n" +
            "('finance', 'identification', 0.309468822171),\n" +
            "('finance', 'location', 0.418013856813),\n" +
            "('finance', 'appCount', 433),\n" +
            "('finance', 'calendar', 0.531177829099),\n" +
            "('finance', 'email', 0.531177829099),\n" +
            "('business', 'messages', 0.53305785124),\n" +
            "('business', 'overlay', 0.727272727273),\n" +
            "('business', 'media', 0.495867768595),\n" +
            "('business', 'contacts', 0.413223140496),\n" +
            "('business', 'storage', 0.698347107438),\n" +
            "('business', 'calling', 0.359504132231),\n" +
            "('business', 'notifications', 0.636363636364),\n" +
            "('business', 'identification', 0.243801652893),\n" +
            "('business', 'location', 0.314049586777),\n" +
            "('business', 'appCount', 242),\n" +
            "('business', 'calendar', 0.53305785124),\n" +
            "('business', 'email', 0.53305785124),\n" +
            "('photography', 'messages', 0.455830388693),\n" +
            "('photography', 'overlay', 0.671378091873),\n" +
            "('photography', 'media', 0.657243816254),\n" +
            "('photography', 'contacts', 0.363957597173),\n" +
            "('photography', 'storage', 0.83038869258),\n" +
            "('photography', 'calling', 0.335689045936),\n" +
            "('photography', 'notifications', 0.56890459364),\n" +
            "('photography', 'identification', 0.187279151943),\n" +
            "('photography', 'location', 0.363957597173),\n" +
            "('photography', 'appCount', 283),\n" +
            "('photography', 'calendar', 0.455830388693),\n" +
            "('photography', 'email', 0.455830388693),\n" +
            "('libraries_n_demo', 'messages', 0.519230769231),\n" +
            "('libraries_n_demo', 'overlay', 0.653846153846),\n" +
            "('libraries_n_demo', 'media', 0.519230769231),\n" +
            "('libraries_n_demo', 'contacts', 0.461538461538),\n" +
            "('libraries_n_demo', 'storage', 0.730769230769),\n" +
            "('libraries_n_demo', 'calling', 0.5),\n" +
            "('libraries_n_demo', 'notifications', 0.653846153846),\n" +
            "('libraries_n_demo', 'identification', 0.326923076923),\n" +
            "('libraries_n_demo', 'location', 0.442307692308),\n" +
            "('libraries_n_demo', 'appCount', 52),\n" +
            "('libraries_n_demo', 'calendar', 0.519230769231),\n" +
            "('libraries_n_demo', 'email', 0.519230769231),\n" +
            "('adventure', 'messages', 0.666666666667),\n" +
            "('adventure', 'overlay', 0.78125),\n" +
            "('adventure', 'media', 0.6875),\n" +
            "('adventure', 'contacts', 0.572916666667),\n" +
            "('adventure', 'storage', 0.8125),\n" +
            "('adventure', 'calling', 0.572916666667),\n" +
            "('adventure', 'notifications', 0.583333333333),\n" +
            "('adventure', 'identification', 0.239583333333),\n" +
            "('adventure', 'location', 0.520833333333),\n" +
            "('adventure', 'appCount', 96),\n" +
            "('adventure', 'calendar', 0.666666666667),\n" +
            "('adventure', 'email', 0.666666666667),\n" +
            "('lifestyle', 'messages', 0.557251908397),\n" +
            "('lifestyle', 'overlay', 0.732824427481),\n" +
            "('lifestyle', 'media', 0.577608142494),\n" +
            "('lifestyle', 'contacts', 0.478371501272),\n" +
            "('lifestyle', 'storage', 0.656488549618),\n" +
            "('lifestyle', 'calling', 0.445292620865),\n" +
            "('lifestyle', 'notifications', 0.666666666667),\n" +
            "('lifestyle', 'identification', 0.175572519084),\n" +
            "('lifestyle', 'location', 0.468193384224),\n" +
            "('lifestyle', 'appCount', 393),\n" +
            "('lifestyle', 'calendar', 0.557251908397),\n" +
            "('lifestyle', 'email', 0.557251908397),\n" +
            "('family_braingames', 'messages', 1.0),\n" +
            "('family_braingames', 'overlay', 1.0),\n" +
            "('family_braingames', 'media', 1.0),\n" +
            "('family_braingames', 'contacts', 1.0),\n" +
            "('family_braingames', 'storage', 1.0),\n" +
            "('family_braingames', 'calling', 1.0),\n" +
            "('family_braingames', 'notifications', 1.0),\n" +
            "('family_braingames', 'identification', 0.0),\n" +
            "('family_braingames', 'location', 1.0),\n" +
            "('family_braingames', 'appCount', 1),\n" +
            "('family_braingames', 'calendar', 1.0),\n" +
            "('family_braingames', 'email', 1.0),\n" +
            "('card', 'messages', 0.518518518519),\n" +
            "('card', 'overlay', 0.62962962963),\n" +
            "('card', 'media', 0.518518518519),\n" +
            "('card', 'contacts', 0.527777777778),\n" +
            "('card', 'storage', 0.694444444444),\n" +
            "('card', 'calling', 0.509259259259),\n" +
            "('card', 'notifications', 0.388888888889),\n" +
            "('card', 'identification', 0.240740740741),\n" +
            "('card', 'location', 0.518518518519),\n" +
            "('card', 'appCount', 108),\n" +
            "('card', 'calendar', 0.518518518519),\n" +
            "('card', 'email', 0.518518518519),\n" +
            "('family_action', 'messages', 0.25),\n" +
            "('family_action', 'overlay', 0.25),\n" +
            "('family_action', 'media', 0.25),\n" +
            "('family_action', 'contacts', 0.25),\n" +
            "('family_action', 'storage', 0.25),\n" +
            "('family_action', 'calling', 0.0),\n" +
            "('family_action', 'notifications', 0.25),\n" +
            "('family_action', 'identification', 0.25),\n" +
            "('family_action', 'location', 0.0),\n" +
            "('family_action', 'appCount', 4),\n" +
            "('family_action', 'calendar', 0.25),\n" +
            "('family_action', 'email', 0.25),\n" +
            "('word', 'messages', 0.410256410256),\n" +
            "('word', 'overlay', 0.769230769231),\n" +
            "('word', 'media', 0.435897435897),\n" +
            "('word', 'contacts', 0.384615384615),\n" +
            "('word', 'storage', 0.717948717949),\n" +
            "('word', 'calling', 0.384615384615),\n" +
            "('word', 'notifications', 0.487179487179),\n" +
            "('word', 'identification', 0.153846153846),\n" +
            "('word', 'location', 0.410256410256),\n" +
            "('word', 'appCount', 39),\n" +
            "('word', 'calendar', 0.410256410256),\n" +
            "('word', 'email', 0.410256410256),\n" +
            "('comics', 'messages', 0.5),\n" +
            "('comics', 'overlay', 0.676470588235),\n" +
            "('comics', 'media', 0.352941176471),\n" +
            "('comics', 'contacts', 0.441176470588),\n" +
            "('comics', 'storage', 0.764705882353),\n" +
            "('comics', 'calling', 0.264705882353),\n" +
            "('comics', 'notifications', 0.617647058824),\n" +
            "('comics', 'identification', 0.176470588235),\n" +
            "('comics', 'location', 0.382352941176),\n" +
            "('comics', 'appCount', 34),\n" +
            "('comics', 'calendar', 0.5),\n" +
            "('comics', 'email', 0.5),\n" +
            "('medical', 'messages', 0.568965517241),\n" +
            "('medical', 'overlay', 0.793103448276),\n" +
            "('medical', 'media', 0.603448275862),\n" +
            "('medical', 'contacts', 0.379310344828),\n" +
            "('medical', 'storage', 0.758620689655),\n" +
            "('medical', 'calling', 0.379310344828),\n" +
            "('medical', 'notifications', 0.724137931034),\n" +
            "('medical', 'identification', 0.258620689655),\n" +
            "('medical', 'location', 0.413793103448),\n" +
            "('medical', 'appCount', 58),\n" +
            "('medical', 'calendar', 0.568965517241),\n" +
            "('medical', 'email', 0.568965517241),\n" +
            "('casino', 'messages', 0.717391304348),\n" +
            "('casino', 'overlay', 0.891304347826),\n" +
            "('casino', 'media', 0.717391304348),\n" +
            "('casino', 'contacts', 0.717391304348),\n" +
            "('casino', 'storage', 0.847826086957),\n" +
            "('casino', 'calling', 0.673913043478),\n" +
            "('casino', 'notifications', 0.565217391304),\n" +
            "('casino', 'identification', 0.369565217391),\n" +
            "('casino', 'location', 0.695652173913),\n" +
            "('casino', 'appCount', 46),\n" +
            "('casino', 'calendar', 0.717391304348),\n" +
            "('casino', 'email', 0.717391304348),\n" +
            "('simulation', 'messages', 0.59375),\n" +
            "('simulation', 'overlay', 0.8046875),\n" +
            "('simulation', 'media', 0.5859375),\n" +
            "('simulation', 'contacts', 0.5234375),\n" +
            "('simulation', 'storage', 0.796875),\n" +
            "('simulation', 'calling', 0.5),\n" +
            "('simulation', 'notifications', 0.5390625),\n" +
            "('simulation', 'identification', 0.3359375),\n" +
            "('simulation', 'location', 0.5234375),\n" +
            "('simulation', 'appCount', 128),\n" +
            "('simulation', 'calendar', 0.59375),\n" +
            "('simulation', 'email', 0.59375),\n" +
            "('social', 'messages', 0.58934169279),\n" +
            "('social', 'overlay', 0.752351097179),\n" +
            "('social', 'media', 0.648902821317),\n" +
            "('social', 'contacts', 0.498432601881),\n" +
            "('social', 'storage', 0.818181818182),\n" +
            "('social', 'calling', 0.445141065831),\n" +
            "('social', 'notifications', 0.711598746082),\n" +
            "('social', 'identification', 0.253918495298),\n" +
            "('social', 'location', 0.398119122257),\n" +
            "('social', 'appCount', 319),\n" +
            "('social', 'calendar', 0.58934169279),\n" +
            "('social', 'email', 0.58934169279),\n" +
            "('action', 'messages', 0.654109589041),\n" +
            "('action', 'overlay', 0.849315068493),\n" +
            "('action', 'media', 0.626712328767),\n" +
            "('action', 'contacts', 0.571917808219),\n" +
            "('action', 'storage', 0.859589041096),\n" +
            "('action', 'calling', 0.558219178082),\n" +
            "('action', 'notifications', 0.52397260274),\n" +
            "('action', 'identification', 0.335616438356),\n" +
            "('action', 'location', 0.554794520548),\n" +
            "('action', 'appCount', 292),\n" +
            "('action', 'calendar', 0.654109589041),\n" +
            "('action', 'email', 0.654109589041),\n" +
            "('books_n_reference', 'messages', 0.515625),\n" +
            "('books_n_reference', 'overlay', 0.715625),\n" +
            "('books_n_reference', 'media', 0.50625),\n" +
            "('books_n_reference', 'contacts', 0.440625),\n" +
            "('books_n_reference', 'storage', 0.75),\n" +
            "('books_n_reference', 'calling', 0.396875),\n" +
            "('books_n_reference', 'notifications', 0.6),\n" +
            "('books_n_reference', 'identification', 0.2),\n" +
            "('books_n_reference', 'location', 0.44375),\n" +
            "('books_n_reference', 'appCount', 320),\n" +
            "('books_n_reference', 'calendar', 0.515625),\n" +
            "('books_n_reference', 'email', 0.515625);\n";
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
    private static final String USED_RESOURCES = "usedResources";
    private static final String CURRENT_PACKAGE_NAME = "currentPackageName";
    private static int POLICY_ID;

    public static Map<String, String> getPermCatMap() {
        return PERM_CAT_MAP;
    }

    public static List<String> getAppPackageNames() {
        return APP_PACKAGE_NAMES;
    }

    public static List<String> getAppNames() {
        return APP_NAMES;
    }

    public static String getAppsInstalled() {
        return APPS_INSTALLED;
    }

    public static List<String> getAppCatsForTest() {
        return APP_CATS_FOR_TEST;
    }

    public static String getPrefBossPresenceKey() {
        return PREF_BOSS_PRESENCE_KEY;
    }

    public static String getPrefTimeInstantSunriseTemporalKey() {
        return PREF_TIME_INSTANT_SUNRISE_TEMPORAL_KEY;
    }

    public static String getPrefTimeInstantSunsetTemporalKey() {
        return PREF_TIME_INSTANT_SUNSET_TEMPORAL_KEY;
    }

    public static String getPrefMorningTemporalKey() {
        return PREF_MORNING_TEMPORAL_KEY;
    }

    public static String getPrefAfternoonTemporalKey() {
        return PREF_AFTERNOON_TEMPORAL_KEY;
    }

    public static String getPrefEveningTemporalKey() {
        return PREF_EVENING_TEMPORAL_KEY;
    }

    public static String getPrefNightTemporalKey() {
        return PREF_NIGHT_TEMPORAL_KEY;
    }

    public static String getPrefHolidayTemporalKey() {
        return PREF_HOLIDAY_TEMPORAL_KEY;
    }

    public static int getPolicyId() {
        return POLICY_ID;
    }

    public static String getAppCategoryUnknown() {
        return APP_CATEGORY_UNKNOWN;
    }

    public static String getMithrilFirebaseServerKeyUsers() {
        return MITHRIL_FIREBASE_SERVER_KEY_USERS;
    }

    public static String getMithrilFirebaseServerKeyPolicies() {
        return MITHRIL_FIREBASE_SERVER_KEY_POLICIES;
    }

    public static String getMithrilFirebaseServerKeyApps() {
        return MITHRIL_FIREBASE_SERVER_KEY_APPS;
    }

    public static String getFeedbackUploadResultKey() {
        return FEEDBACK_UPLOAD_RESULT_KEY;
    }

    public static String getRandomUserId() {
        return RANDOM_USER_ID;
    }

    public static String getFeedbackUrl() {
        return FEEDBACK_URL;
    }

    public static String getFeedbackQuestionDataTimeKey() {
        return FEEDBACK_QUESTION_DATA_TIME_KEY;
    }

    public static String getFeedbackQuestionDataKey() {
        return FEEDBACK_QUESTION_DATA_KEY;
    }

    public static String getFeedbackQuestion1() {
        return FEEDBACK_QUESTION_1;
    }

    public static String getFeedbackQuestion2() {
        return FEEDBACK_QUESTION_2;
    }

    public static String getFeedbackQuestion3() {
        return FEEDBACK_QUESTION_3;
    }

    public static String getFeedbackQuestionExtra() {
        return FEEDBACK_QUESTION_EXTRA;
    }

    public static String getFeedbackQuestion4() {
        return FEEDBACK_QUESTION_4;
    }

    public static String getFeedbackQuestion5() {
        return FEEDBACK_QUESTION_5;
    }

    public static String getFeedbackQuestion6() {
        return FEEDBACK_QUESTION_6;
    }

    public static String getFeedbackQuestion7() {
        return FEEDBACK_QUESTION_7;
    }

    public static String getFeedbackQuestion8() {
        return FEEDBACK_QUESTION_8;
    }

    public static String getFeedbackQuestion9() {
        return FEEDBACK_QUESTION_9;
    }

    public static String getFeedbackQuestion10() {
        return FEEDBACK_QUESTION_10;
    }

    public static String[] getContextArrayTypes() {
        return CONTEXT_ARRAY_TYPES;
    }

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

    public static String getCurrentPackageName() {
        return CURRENT_PACKAGE_NAME;
    }

    public static String getUsedResources() {
        return USED_RESOURCES;
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

    public static String getInsertStatementDefaultPolicies() {
        return INSERT_STATEMENT_DEFAULT_POLICIES;
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