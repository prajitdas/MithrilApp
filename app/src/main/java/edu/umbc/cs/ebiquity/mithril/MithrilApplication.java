package edu.umbc.cs.ebiquity.mithril;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Created by Prajit Kumar Das on 5/1/2016.
 */
public class MithrilApplication extends Application {
    /**
     * Public stuff! Make them private if you can...
     */
    public static final int ALL_PERMISSIONS_MITHRIL_REQUEST_CODE = 100;
    public static final int USER_AGREEMENT_READ_REQUEST_CODE = 200;
    public static final int USER_CONSENT_RECEIVED_REQUEST_CODE = 300;

    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;

    public static final String APP_PACKAGE_NAME_SELF = "edu.umbc.cs.ebiquity.mithril";

    public static final String RECEIVER = APP_PACKAGE_NAME_SELF + ".RECEIVER";
    public static final String RESULT_DATA_KEY = APP_PACKAGE_NAME_SELF + ".RESULT_DATA_KEY";
    public static final String ADDRESS_REQUESTED_EXTRA = "ADDRESS_REQUESTED_EXTRA";
    public static final String LOCATION_DATA_EXTRA = APP_PACKAGE_NAME_SELF + ".LOCATION_DATA_EXTRA";
    public static final String GEOFENCES_ADDED_KEY = APP_PACKAGE_NAME_SELF + ".GEOFENCES_ADDED_KEY";

    public static final String ADDRESS_KEY = "ADDRESS_KEY";
    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
    /**
     * For this sample, geofences expire after twelve hours.
     */
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 1609; // 1 mile, 1.6 km
    /**
     * Map for storing information about airports in the San Francisco bay area.
     */
    public static final HashMap<String, LatLng> BALTIMORE_COUNTY_LANDMARKS = new HashMap<String, LatLng>();

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
    private static final String PERMISSION_NO_GROUP = "no-groups";
    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency for app launch detection in seconds
    private static final int LAUNCH_DETECTION_INTERVAL_IN_SECONDS = 10;
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
    private static final String PREF_KEY_CURRENT_TIME = "time";
    private static final String PREF_KEY_CURRENT_LOCATION = "currloc";
    private static final String PREF_KEY_LOCATION = "location";
    private static final String PREF_KEY_CURRENT_ADDRESS = "curraddr";
    private static final String PREF_KEY_WHAT_LEVEL = "level";

    //Make sure they match the values from preferences.xml
    private static final String PREF_ALL_DONE_KEY = "prefsDone";

    private static final String PREF_LOCATION_CONTEXT_ENABLE_KEY = "enableLocationContext";
    private static final String PREF_HOME_LOCATION_KEY = "homeLocation";
    private static final String PREF_WORK_LOCATION_KEY = "workLocation";

    private static final String PREF_PRESENCE_INFO_CONTEXT_ENABLE_KEY = "enablePresenceInfoContext";
    private static final String PREF_PRESENCE_INFO_COLLEAGUE_KEY = "presenceInfoSupervisor";
    private static final String PREF_PRESENCE_INFO_SUPERVISOR_KEY = "presenceInfoColleague";

    private static final String PREF_TEMPORAL_CONTEXT_ENABLE_KEY = "enableTemporalContext";

    private static final String PREF_WORK_DAYS_KEY = "workDays";
    private static final String PREF_WORK_HOURS_START_KEY = "workHoursStart";
    private static final String PREF_WORK_HOURS_END_KEY = "workHoursEnd";

    private static final String PREF_DND_DAYS_KEY = "dndDays";
    private static final String PREF_DND_HOURS_START_KEY = "dndHoursStart";
    private static final String PREF_DND_HOURS_END_KEY = "dndHoursEnd";

    private static final String PREF_MONDAY = "Monday";
    private static final String PREF_TUESDAY = "Tuesday";
    private static final String PREF_WEDNESDAY = "Wednesday";
    private static final String PREF_THURSDAY = "Thursday";
    private static final String PREF_FRIDAY = "Friday";
    private static final String PREF_SATURDAY = "Saturday";
    private static final String PREF_SUNDAY = "Sunday";
    //End of preferences.xml

    private static final String PREF_KEY_APP_LAUNCH_MONITORING_SERVICE_STATE = "appLaunchMonitoringServiceState";
    private static final String PREF_KEY_LOCATION_UPDATE_SERVICE_STATE = "locationUpdateServiceState";
    private static final String PREF_KEY_ALL_APPS_DISPLAY = "allApps";
    private static final String PREF_KEY_SYSTEM_APPS_DISPLAY = "systemApps";
    private static final String PREF_KEY_USER_APPS_DISPLAY = "userApps";
    private static final String PREF_KEY_APP_DISPLAY_TYPE = "AppDisplayTypeTag";
    private static final String PREF_KEY_APP_PKG_NAME = "AppPkgNameTag";
    private static final String PREF_KEY_APP_COUNT = "AppCount";
    private static final String PREF_KEY_USER_CONSENT = "UserConsent";
    private static final String PREF_KEY_USER_AGREEMENT_PAGE_NUMBER = "UserAgreementPageNumber";
    private static final String PREF_KEY_USER_AGREEMENT_COPIED = "UserAgreementCopied";
    //End of preference keys

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
    private static final String CONTEXT_DEFAULT_WORK_LOCATION = "Default Work Location";
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
            "('android.permission.READ_PROFILE', 'normal', '" + PERMISSION_NO_GROUP + "', 'removed'),\n" +
            "('android.permission.WRITE_PROFILE', 'normal', '" + PERMISSION_NO_GROUP + "', 'removed'),\n" +
            "('android.permission.READ_SOCIAL_STREAM', 'normal', '" + PERMISSION_NO_GROUP + "', 'removed'),\n" +
            "('android.permission.WRITE_SOCIAL_STREAM', 'normal', '" + PERMISSION_NO_GROUP + "', 'removed'),\n" +
            "('android.permission.READ_USER_DICTIONARY', 'normal', '" + PERMISSION_NO_GROUP + "', 'removed'),\n" +
            "('android.permission.WRITE_USER_DICTIONARY', 'normal', '" + PERMISSION_NO_GROUP + "', 'removed'),\n" +
            "('android.permission.WRITE_SMS', 'normal', '" + PERMISSION_NO_GROUP + "', 'removed'),\n" +
            "('com.android.browser.permission.READ_HISTORY_BOOKMARKS', 'normal', '" + PERMISSION_NO_GROUP + "', 'removed'),\n" +
            "('com.android.browser.permission.WRITE_HISTORY_BOOKMARKS', 'normal', '" + PERMISSION_NO_GROUP + "', 'removed'),\n" +
            "('android.permission.AUTHENTICATE_ACCOUNTS', 'normal', '" + PERMISSION_NO_GROUP + "', 'removed'),\n" +
            "('android.permission.MANAGE_ACCOUNTS', 'normal', '" + PERMISSION_NO_GROUP + "', 'removed'),\n" +
            "('android.permission.USE_CREDENTIALS', 'normal', '" + PERMISSION_NO_GROUP + "', 'removed'),\n" +
            "('android.permission.SUBSCRIBED_FEEDS_READ', 'normal', '" + PERMISSION_NO_GROUP + "', 'removed'),\n" +
            "('android.permission.SUBSCRIBED_FEEDS_WRITE', 'normal', '" + PERMISSION_NO_GROUP + "', 'removed'),\n" +
            "('android.permission.FLASHLIGHT', 'normal', '" + PERMISSION_NO_GROUP + "', 'removed'),\n" +
            "('android.permission.SEND_RESPOND_VIA_MESSAGE', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SEND_SMS_NO_CONFIRMATION', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CARRIER_FILTER_SMS', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.RECEIVE_EMERGENCY_BROADCAST', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.RECEIVE_BLUETOOTH_MAP', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_DIRECTORY_SEARCH', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MODIFY_CELL_BROADCASTS', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('com.android.alarm.permission.SET_ALARM', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('com.android.voicemail.permission.WRITE_VOICEMAIL', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('com.android.voicemail.permission.READ_VOICEMAIL', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_LOCATION_EXTRA_COMMANDS', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.INSTALL_LOCATION_PROVIDER', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.HDMI_CEC', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.LOCATION_HARDWARE', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_MOCK_LOCATION', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.INTERNET', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_NETWORK_STATE', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_WIFI_STATE', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CHANGE_WIFI_STATE', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_WIFI_CREDENTIAL', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.TETHER_PRIVILEGED', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.RECEIVE_WIFI_CREDENTIAL_CHANGE', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.OVERRIDE_WIFI_CONFIG', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_WIMAX_STATE', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CHANGE_WIMAX_STATE', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SCORE_NETWORKS', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BLUETOOTH', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BLUETOOTH_ADMIN', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BLUETOOTH_PRIVILEGED', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BLUETOOTH_MAP', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BLUETOOTH_STACK', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.NFC', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CONNECTIVITY_INTERNAL', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CONNECTIVITY_USE_RESTRICTED_NETWORKS', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.PACKET_KEEPALIVE_OFFLOAD', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.RECEIVE_DATA_ACTIVITY_CHANGE', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.LOOP_RADIO', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.NFC_HANDOVER_STATUS', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GET_ACCOUNTS', 'dangerous', 'android.permission-group.CONTACTS', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCOUNT_MANAGER', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CHANGE_WIFI_MULTICAST_STATE', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.VIBRATE', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.WAKE_LOCK', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.TRANSMIT_IR', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MODIFY_AUDIO_SETTINGS', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_USB', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_MTP', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.HARDWARE_TEST', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_FM_RADIO', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.NET_ADMIN', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.REMOTE_AUDIO_PLAYBACK', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.TV_INPUT_HARDWARE', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CAPTURE_TV_INPUT', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.DVB_DEVICE', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_OEM_UNLOCK_STATE', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.OEM_UNLOCK_STATE', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_PDB_STATE', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.NOTIFY_PENDING_SYSTEM_UPDATE', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CAMERA_DISABLE_TRANSMIT_LED', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CAMERA_SEND_SYSTEM_EVENTS', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MODIFY_PHONE_STATE', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_PRECISE_PHONE_STATE', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_PRIVILEGED_PHONE_STATE', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.REGISTER_SIM_SUBSCRIPTION', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.REGISTER_CALL_PROVIDER', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.REGISTER_CONNECTION_MANAGER', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_INCALL_SERVICE', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_SCREENING_SERVICE', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_CONNECTION_SERVICE', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_TELECOM_CONNECTION_SERVICE', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CONTROL_INCALL_EXPERIENCE', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.RECEIVE_STK_COMMANDS', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.WRITE_MEDIA_STORAGE', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_DOCUMENTS', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CACHE_CONTENT', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.DISABLE_KEYGUARD', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GET_TASKS', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.REAL_GET_TASKS', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.START_TASKS_FROM_RECENTS', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.INTERACT_ACROSS_USERS', 'signature|privileged|development', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.INTERACT_ACROSS_USERS_FULL', 'signature|installer', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_USERS', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CREATE_USERS', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_PROFILE_AND_DEVICE_OWNERS', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GET_DETAILED_TASKS', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.REORDER_TASKS', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.REMOVE_TASKS', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_ACTIVITY_STACKS', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.START_ANY_ACTIVITY', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.RESTART_PACKAGES', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.KILL_BACKGROUND_PROCESSES', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GET_PROCESS_STATE_AND_OOM_SCORE', 'signature|privileged|development', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GET_PACKAGE_IMPORTANCE', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GET_INTENT_SENDER_INTENT', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SYSTEM_ALERT_WINDOW', 'signature|preinstalled|appop|pre23|development', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_WALLPAPER', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_WALLPAPER_HINTS', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_TIME', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_TIME_ZONE', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.EXPAND_STATUS_BAR', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('com.android.launcher.permission.INSTALL_SHORTCUT', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('com.android.launcher.permission.UNINSTALL_SHORTCUT', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_SYNC_SETTINGS', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.WRITE_SYNC_SETTINGS', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_SYNC_STATS', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_SCREEN_COMPATIBILITY', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CHANGE_CONFIGURATION', 'signature|privileged|development', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.WRITE_SETTINGS', 'signature|preinstalled|appop|pre23', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.WRITE_GSERVICES', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.FORCE_STOP_PACKAGES', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.RETRIEVE_WINDOW_CONTENT', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_ANIMATION_SCALE', 'signature|privileged|development', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.PERSISTENT_ACTIVITY', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GET_PACKAGE_SIZE', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_PREFERRED_APPLICATIONS', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.RECEIVE_BOOT_COMPLETED', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BROADCAST_STICKY', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MOUNT_UNMOUNT_FILESYSTEMS', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MOUNT_FORMAT_FILESYSTEMS', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.STORAGE_INTERNAL', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ASEC_ACCESS', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ASEC_CREATE', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ASEC_DESTROY', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ASEC_MOUNT_UNMOUNT', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ASEC_RENAME', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.WRITE_APN_SETTINGS', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CHANGE_NETWORK_STATE', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CLEAR_APP_CACHE', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ALLOW_ANY_CODEC_FOR_PLAYBACK', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_CA_CERTIFICATES', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.RECOVERY', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_JOB_SERVICE', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.UPDATE_CONFIG', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.RESET_SHORTCUT_MANAGER_THROTTLING', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.WRITE_SECURE_SETTINGS', 'signature|privileged|development', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.DUMP', 'signature|privileged|development', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_LOGS', 'signature|privileged|development', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_DEBUG_APP', 'signature|privileged|development', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_PROCESS_LIMIT', 'signature|privileged|development', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_ALWAYS_FINISH', 'signature|privileged|development', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SIGNAL_PERSISTENT_PROCESSES', 'signature|privileged|development', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GET_ACCOUNTS_PRIVILEGED', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GET_PASSWORD', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.DIAGNOSTIC', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.STATUS_BAR', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.STATUS_BAR_SERVICE', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_QUICK_SETTINGS_TILE', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.FORCE_BACK', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.UPDATE_DEVICE_STATS', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GET_APP_OPS_STATS', 'signature|privileged|development', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.UPDATE_APP_OPS_STATS', 'signature|privileged|installer', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_APP_OPS_RESTRICTIONS', 'signature|installer', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.INTERNAL_SYSTEM_WINDOW', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_APP_TOKENS', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.REGISTER_WINDOW_MANAGER_LISTENERS', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.FREEZE_SCREEN', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.INJECT_EVENTS', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.FILTER_EVENTS', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.RETRIEVE_WINDOW_TOKEN', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.FRAME_STATS', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.TEMPORARY_ENABLE_ACCESSIBILITY', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_ACTIVITY_WATCHER', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SHUTDOWN', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.STOP_APP_SWITCHES', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GET_TOP_ACTIVITY_INFO', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_INPUT_STATE', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_INPUT_METHOD', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_MIDI_DEVICE_SERVICE', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_ACCESSIBILITY_SERVICE', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_PRINT_SERVICE', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_PRINT_RECOMMENDATION_SERVICE', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_NFC_SERVICE', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_PRINT_SPOOLER_SERVICE', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_RUNTIME_PERMISSION_PRESENTER_SERVICE', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_TEXT_SERVICE', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_VPN_SERVICE', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_WALLPAPER', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_VOICE_INTERACTION', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_VOICE_KEYPHRASES', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_REMOTE_DISPLAY', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_TV_INPUT', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_TV_REMOTE_SERVICE', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.TV_VIRTUAL_REMOTE_CONTROLLER', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MODIFY_PARENTAL_CONTROLS', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_ROUTE_PROVIDER', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_DEVICE_ADMIN', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_DEVICE_ADMINS', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_ORIENTATION', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_POINTER_SPEED', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_INPUT_CALIBRATION', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_KEYBOARD_LAYOUT', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.TABLET_MODE', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.REQUEST_INSTALL_PACKAGES', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.INSTALL_PACKAGES', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CLEAR_APP_USER_DATA', 'signature|installer', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GET_APP_GRANTED_URI_PERMISSIONS', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CLEAR_APP_GRANTED_URI_PERMISSIONS', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.DELETE_CACHE_FILES', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.DELETE_PACKAGES', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MOVE_PACKAGE', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CHANGE_COMPONENT_ENABLED_STATE', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GRANT_RUNTIME_PERMISSIONS', 'signature|installer|verifier', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.INSTALL_GRANT_RUNTIME_PERMISSIONS', 'signature|installer|verifier', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.REVOKE_RUNTIME_PERMISSIONS', 'signature|installer|verifier', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.OBSERVE_GRANT_REVOKE_PERMISSIONS', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_SURFACE_FLINGER', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_FRAME_BUFFER', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_INPUT_FLINGER', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CONFIGURE_WIFI_DISPLAY', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CONTROL_WIFI_DISPLAY', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CONFIGURE_DISPLAY_COLOR_MODE', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CONTROL_VPN', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CAPTURE_AUDIO_OUTPUT', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CAPTURE_AUDIO_HOTWORD', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MODIFY_AUDIO_ROUTING', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CAPTURE_VIDEO_OUTPUT', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CAPTURE_SECURE_VIDEO_OUTPUT', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MEDIA_CONTENT_CONTROL', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BRICK', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.REBOOT', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.DEVICE_POWER', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.USER_ACTIVITY', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.NET_TUNNELING', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.FACTORY_TEST', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BROADCAST_PACKAGE_REMOVED', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BROADCAST_SMS', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BROADCAST_WAP_PUSH', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BROADCAST_NETWORK_PRIVILEGED', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MASTER_CLEAR', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CALL_PRIVILEGED', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.PERFORM_CDMA_PROVISIONING', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.PERFORM_SIM_ACTIVATION', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CONTROL_LOCATION_UPDATES', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_CHECKIN_PROPERTIES', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.PACKAGE_USAGE_STATS', 'signature|privileged|development|appop', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CHANGE_APP_IDLE_STATE', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CHANGE_DEVICE_IDLE_TEMP_WHITELIST', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BATTERY_STATS', 'signature|privileged|development', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BACKUP', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CONFIRM_FULL_BACKUP', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_REMOTEVIEWS', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_APPWIDGET', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_KEYGUARD_APPWIDGET', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MODIFY_APPWIDGET_BIND_PERMISSIONS', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CHANGE_BACKGROUND_DATA_SETTING', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GLOBAL_SEARCH', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.GLOBAL_SEARCH_CONTROL', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_SEARCH_INDEXABLES', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SET_WALLPAPER_COMPONENT', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_DREAM_STATE', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.WRITE_DREAM_STATE', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_CACHE_FILESYSTEM', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.COPY_PROTECTED_DATA', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CRYPT_KEEPER', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_NETWORK_USAGE_HISTORY', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_NETWORK_POLICY', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MODIFY_NETWORK_ACCOUNTING', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.intent.category.MASTER_CLEAR.permission.C2D_MESSAGE', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.PACKAGE_VERIFICATION_AGENT', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_PACKAGE_VERIFIER', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.INTENT_FILTER_VERIFICATION_AGENT', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_INTENT_FILTER_VERIFIER', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SERIAL_PORT', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_CONTENT_PROVIDERS_EXTERNALLY', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.UPDATE_LOCK', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_NOTIFICATIONS', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_NOTIFICATION_POLICY', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_NOTIFICATIONS', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_KEYGUARD_SECURE_STORAGE', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_FINGERPRINT', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.RESET_FINGERPRINT_LOCKOUT', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.CONTROL_KEYGUARD', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.TRUST_LISTENER', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.PROVIDE_TRUST_AGENT', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.LAUNCH_TRUST_AGENT_SETTINGS', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_TRUST_AGENT', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_NOTIFICATION_LISTENER_SERVICE', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_NOTIFICATION_RANKER_SERVICE', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_CHOOSER_TARGET_SERVICE', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_CONDITION_PROVIDER_SERVICE', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_DREAM_SERVICE', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.INVOKE_CARRIER_SETUP', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_NETWORK_CONDITIONS', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_DRM_CERTIFICATES', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_MEDIA_PROJECTION', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_INSTALL_SESSIONS', 'normal', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.REMOVE_DRM_CERTIFICATES', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_CARRIER_MESSAGING_SERVICE', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_VOICE_INTERACTION_SERVICE', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_CARRIER_SERVICES', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.QUERY_DO_NOT_ASK_CREDENTIALS_ON_BOOT', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.KILL_UID', 'signature|installer', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.LOCAL_MAC_ADDRESS', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.PEERS_MAC_ADDRESS', 'signature|setup', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.DISPATCH_NFC_MESSAGE', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MODIFY_DAY_NIGHT_MODE', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_EPHEMERAL_APPS', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.RECEIVE_MEDIA_RESOURCE_USAGE', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.MANAGE_SOUND_TRIGGER', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.DISPATCH_PROVISIONING_MESSAGE', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.READ_BLOCKED_NUMBERS', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.WRITE_BLOCKED_NUMBERS', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.BIND_VR_LISTENER_SERVICE', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.ACCESS_VR_MANAGER', 'signature', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.UPDATE_LOCK_TASK_PACKAGES', 'signature|setup', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "'),\n" +
            "('android.permission.SUBSTITUTE_NOTIFICATION_APP_NAME', 'signature|privileged', '" + PERMISSION_NO_GROUP + "', '" + PERMISSION_FLAG_NONE + "');\n";

    /**
     * Private stuff!
     */
    static {
        // Baltimore/Washington International Airport.
        BALTIMORE_COUNTY_LANDMARKS.put("BWI", new LatLng(39.182288, -76.669765));
        // Home.
        BALTIMORE_COUNTY_LANDMARKS.put("HOME", new LatLng(39.261761, -76.702669));
        // Work.
        BALTIMORE_COUNTY_LANDMARKS.put("WORK", new LatLng(39.253794, -76.714629));
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

    public static String getPermissionNoGroup() {
        return PERMISSION_NO_GROUP;
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

    public static String getLogIntent() {
        return LOG_INTENT;
    }

    public static String getBroadcastIntentCommandApp() {
        return BROADCAST_INTENT_COMMAND_APP;
    }

    public static String getSharedPreferencesName() {
        return SHARED_PREFERENCES_NAME;
    }

    public static String getPrefLocationContextEnableKey() {
        return PREF_LOCATION_CONTEXT_ENABLE_KEY;
    }

    public static String getPrefHomeLocationKey() {
        return PREF_HOME_LOCATION_KEY;
    }

    public static String getPrefWorkLocationKey() {
        return PREF_WORK_LOCATION_KEY;
    }

    public static String getPrefPresenceInfoContextEnableKey() {
        return PREF_PRESENCE_INFO_CONTEXT_ENABLE_KEY;
    }

    public static String getPrefPresenceInfoColleagueKey() {
        return PREF_PRESENCE_INFO_COLLEAGUE_KEY;
    }

    public static String getPrefPresenceInfoSupervisorKey() {
        return PREF_PRESENCE_INFO_SUPERVISOR_KEY;
    }

    public static String getPrefTemporalContextEnableKey() {
        return PREF_TEMPORAL_CONTEXT_ENABLE_KEY;
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

    public static String getPrefKeyCurrentLocation() {
        return PREF_KEY_CURRENT_LOCATION;
    }

    public static String getPrefKeyCurrentAddress() {
        return PREF_KEY_CURRENT_ADDRESS;
    }

    public static String getPrefKeyWhatLevel() {
        return PREF_KEY_WHAT_LEVEL;
    }

    public static String getPrefKeyCurrentTime() {
        return PREF_KEY_CURRENT_TIME;
    }

    public static String getPrefKeyLocation() {
        return PREF_KEY_LOCATION;
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

    public static String getPrefWorkDaysKey() {
        return PREF_WORK_DAYS_KEY;
    }

    public static String getPrefWorkHoursStartKey() {
        return PREF_WORK_HOURS_START_KEY;
    }

    public static String getPrefWorkHoursEndKey() {
        return PREF_WORK_HOURS_END_KEY;
    }

    public static String getPrefDndDaysKey() {
        return PREF_DND_DAYS_KEY;
    }

    public static String getPrefDndHoursStartKey() {
        return PREF_DND_HOURS_START_KEY;
    }

    public static String getPrefDndHoursEndKey() {
        return PREF_DND_HOURS_END_KEY;
    }

    public static String getPrefMonday() {
        return PREF_MONDAY;
    }

    public static String getPrefTuesday() {
        return PREF_TUESDAY;
    }

    public static String getPrefWednesday() {
        return PREF_WEDNESDAY;
    }

    public static String getPrefThursday() {
        return PREF_THURSDAY;
    }

    public static String getPrefFriday() {
        return PREF_FRIDAY;
    }

    public static String getPrefSaturday() {
        return PREF_SATURDAY;
    }

    public static String getPrefSunday() {
        return PREF_SUNDAY;
    }

    public static String getPrefAllDoneKey() {
        return PREF_ALL_DONE_KEY;
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
}