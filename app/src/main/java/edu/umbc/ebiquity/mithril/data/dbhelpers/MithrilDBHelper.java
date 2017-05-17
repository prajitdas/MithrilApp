package edu.umbc.ebiquity.mithril.data.dbhelpers;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.model.PolicyRule;
import edu.umbc.ebiquity.mithril.data.model.Violation;
import edu.umbc.ebiquity.mithril.data.model.components.AppData;
import edu.umbc.ebiquity.mithril.data.model.components.PermData;
import edu.umbc.ebiquity.mithril.data.model.rules.actions.Action;
import edu.umbc.ebiquity.mithril.data.model.rules.actions.RuleAction;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticUserContext;
import edu.umbc.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticActivity;
import edu.umbc.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticIdentity;
import edu.umbc.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticLocation;
import edu.umbc.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticNearActors;
import edu.umbc.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticTime;
import edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions.PermissionWasUpdateException;

public class MithrilDBHelper extends SQLiteOpenHelper {
    // Database declarations
    private final static int DATABASE_VERSION = 1;
    // private final static int DATABASE_VERSION = (int) System.currentTimeMillis();
    // DO NOT DO THIS!!!
    // THIS IS CREATING A NEW VERSION OF DATABASE ON EACH APP LAUNCH THUS TAKING UP HUGE AMOUNTS OF STORAGE SPACE AND SLOWING DOWN THE COMPLETE APP!

    private final static String DATABASE_NAME = MithrilApplication.getDatabaseName();

    /**
     * Following are table names in our database
     */
    private final static String APPS_TABLE_NAME = "apps";
    private final static String PERMISSIONS_TABLE_NAME = "permissions";
    private final static String APP_PERM_TABLE_NAME = "appperm";
    private final static String POLICY_RULES_TABLE_NAME = "policyrules";
    private final static String CONTEXT_TABLE_NAME = "context";
    private final static String CONTEXT_LOG_TABLE_NAME = "contextlog";
    private final static String ACTION_LOG_TABLE_NAME = "actionlog";
    private final static String VIOLATIONS_LOG_TABLE_NAME = "violationlog";
    private final static String APP_PERM_VIEW_NAME = "apppermview";

    /**
     * Following are table creation statements
     * -- Table 1: apps
     CREATE TABLE apps (
     id int NOT NULL AUTO_INCREMENT,
     uid int NOT NULL,
     description text NULL,
     assocprocname text NULL,
     targetsdkver int NOT NULL,
     icon blob,
     label text NOT NULL,
     name text NOT NULL,
     verinfo text NOT NULL,
     installed bool NOT NULL DEFAULT true,
     type int NOT NULL,
     installdate timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP,
     UNIQUE INDEX apps_unique_key (name),
     CONSTRAINT apps_pk PRIMARY KEY (id)
     ) COMMENT 'Table showing metadata for apps';
     */
    private final static String APPID = "id"; // ID of an installed app
    private final static String APPUID = "uid";
    private final static String APPDESCRIPTION = "description";
    private final static String APPASSOCIATEDPROCNAME = "assocprocname";
    private final static String APPTARGETSDKVERSION = "targetsdkver";
    private final static String APPICON = "icon";
    private final static String APPNAME = "label";
    private final static String APPPACKAGENAME = "name";
    private final static String APPVERSIONINFO = "verinfo";
    private final static String APPINSTALLED = "installed"; // boolean value that represents whether an app is installed or not
    private final static String APPTYPE = "type";
    private final static String APPINSTALLTIME = "installdate";
    private final static String CREATE_APPS_TABLE = " CREATE TABLE " + getAppsTableName() + " (" +
            APPID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            APPUID + " INTEGER NOT NULL, " +
            APPDESCRIPTION + " TEXT NULL, " +
            APPASSOCIATEDPROCNAME + " TEXT NULL, " +
            APPTARGETSDKVERSION + " TEXT NOT NULL, " +
            APPICON + " BLOB, " +
            APPNAME + " TEXT NOT NULL, " +
            APPPACKAGENAME + " TEXT NOT NULL, " + // Only the package name is unique, rest may repeat
            APPVERSIONINFO + " TEXT NOT NULL, " +
            APPINSTALLED + " BOOL NOT NULL DEFAULT 1, " +
            APPTYPE + " TEXT NOT NULL," +
            APPINSTALLTIME + " timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP," +
            "CONSTRAINT apps_unique_key UNIQUE(" + APPPACKAGENAME + ") " +
            ");";

    /**
     * -- Table 2: permissions
     CREATE TABLE permissions (
     id int NOT NULL AUTO_INCREMENT,
     name text NOT NULL,
     label text NOT NULL,
     protectionlvl text NOT NULL,
     permgrp text NULL,
     flags text NULL,
     description text NULL,
     icon blob,
     UNIQUE INDEX permissions_unique_name (name),
     CONSTRAINT permissions_pk PRIMARY KEY (id)
     ) COMMENT 'Table showing metadata for permissions';
     */
    private final static String PERMID = "id"; // ID of a known permission on the device
    private final static String PERMNAME = "name";
    private final static String PERMLABEL = "label";
    private final static String PERMPROTECTIONLEVEL = "protectionlvl";
    private final static String PERMGROUP = "permgrp";
    private final static String PERMFLAG = "flags";
    private final static String PERMDESC = "description";
    private final static String PERMICON = "icon";
    private final static String CREATE_PERMISSIONS_TABLE = "CREATE TABLE " + getPermissionsTableName() + " (" +
            PERMID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PERMNAME + " TEXT NOT NULL, " +
            PERMLABEL + " TEXT NULL, " +
            PERMPROTECTIONLEVEL + " TEXT NOT NULL, " +
            PERMGROUP + " TEXT NULL, " +
            PERMFLAG + " TEXT NULL, " +
            PERMDESC + " TEXT NULL, " +
            PERMICON + " BLOB, " +
            "CONSTRAINT permissions_unique_name UNIQUE(" + PERMNAME + ") " +
            ");";

    /**
     * -- Table 3: appperm
     CREATE TABLE appperm (
     id int NOT NULL AUTO_INCREMENT,
     apps_id int NOT NULL,
     permissions_id int NOT NULL,
     granted int NOT NULL,
     CONSTRAINT appperm_pk PRIMARY KEY (id,apps_id,permissions_id)
     ) COMMENT 'Table showing apps and permissions';

     ALTER TABLE appperm ADD CONSTRAINT appperm_apps FOREIGN KEY appperm_apps (apps_id)
     REFERENCES apps (id) ON DELETE CASCADE;

     ALTER TABLE appperm ADD CONSTRAINT appperm_permissions FOREIGN KEY appperm_permissions (permissions_id)
     REFERENCES permissions (id);
     * ----------------------------------------------------------------------------------------------------------------
     * This table represents all the apps and their corresponding permissions. We also want to store the association between an app and an api call or a resource access.
     */
    private final static String APPPERMRESAPPID = "appsid"; // ID from resource table
    private final static String APPPERMRESPERID = "permissionsid"; // ID from permission table
    private final static String APPPERMGRANTED = "granted"; // Is this permission granted to the app?
    private final static String CREATE_APP_PERM_TABLE = "CREATE TABLE " + getAppPermTableName() + " (" +
            APPPERMRESAPPID + " INTEGER NOT NULL, " +
            APPPERMRESPERID + " INTEGER NOT NULL, " +
            APPPERMGRANTED + " INTEGER NOT NULL, " +
            "FOREIGN KEY(" + APPPERMRESAPPID + ") REFERENCES " + getAppsTableName() + "(" + APPID + ") ON DELETE CASCADE, " +
            "FOREIGN KEY(" + APPPERMRESPERID + ") REFERENCES " + getPermissionsTableName() + "(" + PERMID + "), " +
            "CONSTRAINT appperm_pk PRIMARY KEY (" + APPPERMRESAPPID + "," + APPPERMRESPERID + ")" +
            ");";

    /**
     * -- Table 4: context
     CREATE TABLE context (
     id int NOT NULL AUTO_INCREMENT,
     location text NULL,
     activity text NULL,
     temporal text NULL,
     presence_info text NULL,
     CONSTRAINT context_pk PRIMARY KEY (id)
     ) COMMENT 'Table showing context instances';
     * --------------------------------------------------------------------------------------------------------------------------
     * An entry is made into this table every time we determine a change in context.
     * This could be where we could do energy efficient stuff as in we can save battery by determining context from historical data or some other way.
     */
    private final static String CONTEXTID = "id"; // ID of the context instance
    private final static String CONTEXTIDENTITY = "identity"; // SemanticIdentity context; this is redundant as because we are working on a single device
    private final static String CONTEXTLOCATION = "location"; // SemanticLocation context
    private final static String CONTEXTACTIVITY = "activity"; // SemanticActivity context
    private final static String CONTEXTPRESENCEINFO = "presenceinfo"; // If we could get presence information of others then we can do relationship based privacy solutions
    private final static String CONTEXTTEMPORAL = "temporal"; // SemanticTemporal information; the time instance when the current context was captured
    private final static String CREATE_CONTEXT_TABLE = "CREATE TABLE " + getContextTableName() + " (" +
            CONTEXTID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            CONTEXTIDENTITY + " TEXT NOT NULL DEFAULT 'user'," +
            CONTEXTLOCATION + " TEXT NULL," +
            CONTEXTACTIVITY + " TEXT NULL," +
            CONTEXTTEMPORAL + " TEXT NULL," +
            CONTEXTPRESENCEINFO + " TEXT NULL" +
            ");";

    /**
     * -- Table 5: policyrules
     CREATE TABLE policyrules (
     id int NOT NULL AUTO_INCREMENT,
     name text NOT NULL,
     action int NOT NULL,
     apps_id int NOT NULL,
     context_id int NOT NULL,
     CONSTRAINT policyrules_pk PRIMARY KEY (id)
     ) COMMENT 'Table showing policy rules defined for apps and requested resources in given context';

     ALTER TABLE policyrules ADD CONSTRAINT policyrules_apps FOREIGN KEY policyrules_apps (apps_id)
     REFERENCES apps (id);

     ALTER TABLE policyrules ADD CONSTRAINT policyrules_context FOREIGN KEY policyrules_context (context_id)
     REFERENCES context (id);
     */
    private final static String POLRULID = "id"; // ID of policy defined
    private final static String POLRULNAME = "name"; // Policy short name
    private final static String POLRULACTIN = "action"; // Action will be denoted as: 0 for to deny, 1 for allow
    private final static String POLRULAPPID = "appid"; // App id that sent the request
    private final static String POLRULCTXID = "ctxid"; // context id in which requested
    private final static String CREATE_POLICY_RULES_TABLE = "CREATE TABLE " + getPolicyRulesTableName() + " (" +
            POLRULID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            POLRULNAME + " TEXT NOT NULL, " +
            POLRULACTIN + " INTEGER NOT NULL, " +
            POLRULAPPID + " INTEGER NOT NULL, " +
            POLRULCTXID + " INTEGER NOT NULL, " +
            "FOREIGN KEY(" + POLRULAPPID + ") REFERENCES " + getAppsTableName() + "(" + APPID + ") ON DELETE CASCADE, " +
            "FOREIGN KEY(" + POLRULCTXID + ") REFERENCES " + getContextTableName() + "(" + CONTEXTID + ")" +
            ");";

    /**
     * -- Table 6: contextlog
     CREATE TABLE contextlog (
     id int NOT NULL AUTO_INCREMENT,
     time timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP,
     context_id int NOT NULL,
     CONSTRAINT contextlog_pk PRIMARY KEY (id)
     ) COMMENT 'Table showing log of current user context';

     ALTER TABLE contextlog ADD CONSTRAINT contextlog_context FOREIGN KEY contextlog_context (context_id)
     REFERENCES context (id);
     * --------------------------------------------------------------------------------------------------------------------------
     * An entry is made into this table every time we determine a change in context.
     * This could be where we could do energy efficient stuff as in we can save battery by determining context from historical data or some other way.
     */
    private final static String CTXTLOGID = "id"; // ID of the context instance
    private final static String CTXTID = "contextid"; // Semantic context id
    private final static String CTXTTIME = "time"; // when this context was observed
    private final static String CREATE_CONTEXT_LOG_TABLE = "CREATE TABLE " + getContextLogTableName() + " (" +
            CTXTLOGID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            CTXTID + " INTEGER NOT NULL, " +
            CTXTTIME + " timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY(" + CTXTID + ") REFERENCES " + getContextTableName() + "(" + CONTEXTID + ")" +
            ");";

    /**
     * -- Table 7: violationlog
     CREATE TABLE violationlog (
     id int NOT NULL AUTO_INCREMENT,
     description text NOT NULL,
     marker bool NULL,
     time timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP,
     apps_id int NOT NULL,
     context_id int NOT NULL,
     CONSTRAINT violationlog_pk PRIMARY KEY (id)
     ) COMMENT 'Table showing violations recorded by MithrilAC and subsequent user feedback';

     ALTER TABLE violationlog ADD CONSTRAINT violationlog_apps FOREIGN KEY violationlog_apps (apps_id)
     REFERENCES apps (id);

     ALTER TABLE violationlog ADD CONSTRAINT violationlog_context FOREIGN KEY violationlog_context (context_id)
     REFERENCES context (id);
     */
    private final static String VIOLATIONID = "id"; // ID of violation captured
    private final static String VIOLATIONAPPID = "appid";
    private final static String VIOLATIONCTXID = "contextid";
    private final static String VIOLATIONDESC = "description"; // An appropriate description of the violation. No idea how this will be generated but
    // This will be a general text that will have to be "reasoned" about!
    // If this says policy is applicable @home then we have to be able to determine that context available represents "home"
    private final static String VIOLATIONMARKER = "marker";
    private final static String VIOLATIONTIME = "time";
    private final static String CREATE_VIOLATIONS_LOG_TABLE = "CREATE TABLE " + getViolationsLogTableName() + " (" +
            VIOLATIONID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            VIOLATIONAPPID + " INTEGER NOT NULL, " +
            VIOLATIONCTXID + " INTEGER NOT NULL, " +
            VIOLATIONDESC + " TEXT, " +
            VIOLATIONMARKER + " INTEGER NOT NULL DEFAULT 0, " +
            VIOLATIONTIME + " timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY(" + VIOLATIONAPPID + ") REFERENCES " + getAppsTableName() + "(" + APPID + ") ON DELETE CASCADE, " +
            "FOREIGN KEY(" + VIOLATIONCTXID + ") REFERENCES " + getContextTableName() + "(" + CONTEXTID + ")" +
            ");";

    /**
     * -- Table 8: actionlog
     CREATE TABLE actionlog (
     id int NOT NULL AUTO_INCREMENT,
     time timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP,
     action int NOT NULL,
     apps_id int NOT NULL,
     context_id int NOT NULL,
     CONSTRAINT actionlog_pk PRIMARY KEY (id)
     ) COMMENT 'Table showing actions taken for each context, resource, requester tuple';

     ALTER TABLE actionlog ADD CONSTRAINT actionlog_apps FOREIGN KEY actionlog_apps (apps_id)
     REFERENCES apps (id);

     ALTER TABLE actionlog ADD CONSTRAINT actionlog_context FOREIGN KEY actionlog_context (context_id)
     REFERENCES context (id);
     * ----------------------------------------------------------------------------------------------------------------
     * 0 for denied, 1 for allowed
     * Makes a record every time an action is taken for a certain requester, resource, context and applicable policy
     * This is the action log table. Stores every action taken whether
     */
    private final static String ACTIONID = "id"; // ID of an action taken
    private final static String ACTIONCTXID = "contextid"; // Context in which the request was made
    private final static String ACTIONAPPID = "appid"; // App id by which request was made
    private final static String ACTIONTIME = "time"; // Time when action was taken
    private final static String ACTION = "action"; // Action that was taken for a certain scenario
    private final static String CREATE_ACTION_LOG_TABLE = "CREATE TABLE " + getActionLogTableName() + " (" +
            ACTIONID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ACTIONAPPID + " INTEGER NOT NULL, " +
            ACTIONCTXID + " INTEGER NOT NULL, " +
            ACTIONTIME + " INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP, " +
            ACTION + " INTEGER NOT NULL, " +
            "FOREIGN KEY(" + ACTIONAPPID + ") REFERENCES " + getAppsTableName() + "(" + APPID + ") ON DELETE CASCADE, " +
            "FOREIGN KEY(" + ACTIONCTXID + ") REFERENCES " + getContextTableName() + "(" + CONTEXTID + ")" +
            ");";

    /**
     * -- views
     * -- View: apppermview
     CREATE VIEW `mithril.db`.apppermview AS
     SELECT
     a.name as apppkgname,
     p.name as permname,
     p.protectionlvl as protectionlevel,
     p.label as permlabel,
     p.permgrp as permgroup
     FROM
     apps a,
     permissions p,
     appperm ap
     WHERE
     ap.apps_id = a.id
     AND
     ap.permissions_id = p.id;
     ----------------------------
     View 1 for App permissions
     This table represents all the apps and their corresponding permissions. We also want to store the association between an app and an api call or a resource access.
     */
    private final static String APPPERMVIEWAPPPKGNAME = "apppkgname"; // app package name
    private final static String APPPERMVIEWPERMNAME = "permname"; // app permission name
    private final static String APPPERMVIEWPERMPROLVL = "protectionlevel"; // app permission protection level
    private final static String APPPERMVIEWPERMLABEL = "permlabel"; // app permission label
    private final static String APPPERMVIEWPERMGROUP = "permgroup"; // app permission group
    private final static String CREATE_APP_PERM_VIEW = "CREATE VIEW " + getAppPermViewName() + " AS " +
            "SELECT " +
            getAppsTableName() + "." + APPPACKAGENAME + " AS " + APPPERMVIEWAPPPKGNAME + ", " +
            getPermissionsTableName() + "." + PERMNAME + " AS " + APPPERMVIEWPERMNAME + ", " +
            getPermissionsTableName() + "." + PERMPROTECTIONLEVEL + " AS " + APPPERMVIEWPERMPROLVL + ", " +
            getPermissionsTableName() + "." + PERMLABEL + " AS " + APPPERMVIEWPERMLABEL + ", " +
            getPermissionsTableName() + "." + PERMGROUP + " AS " + APPPERMVIEWPERMGROUP +
            " FROM " +
            getAppPermTableName() + "," +
            getPermissionsTableName() + "," +
            getAppsTableName() +
            " WHERE " +
            getAppPermTableName() + "." + APPPERMRESAPPID + " = " + getAppsTableName() + "." + APPID +
            " AND " +
            getAppPermTableName() + "." + APPPERMRESPERID + " = " + getPermissionsTableName() + "." + PERMID + ";";
    private Context context;

    /**
     * -------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * Table creation statements complete
     * -------------------------------------------------------------------------------------------------------------------------------------------------------------------
     * Database creation constructor
     *
     * @param aContext needs context to create DB
     */
    public MithrilDBHelper(Context aContext) {
        super(aContext, DATABASE_NAME, null, DATABASE_VERSION);
        setContext(aContext);
    }

    /**
     * Table name getters
     *
     * @return the table name
     */
    private static String getPolicyRulesTableName() {
        return POLICY_RULES_TABLE_NAME;
    }

    private static String getContextTableName() {
        return CONTEXT_TABLE_NAME;
    }

    private static String getContextLogTableName() {
        return CONTEXT_LOG_TABLE_NAME;
    }

    private static String getActionLogTableName() {
        return ACTION_LOG_TABLE_NAME;
    }

    private static String getViolationsLogTableName() {
        return VIOLATIONS_LOG_TABLE_NAME;
    }

    private static String getAppsTableName() {
        return APPS_TABLE_NAME;
    }

    private static String getPermissionsTableName() {
        return PERMISSIONS_TABLE_NAME;
    }

    private static String getAppPermTableName() {
        return APP_PERM_TABLE_NAME;
    }

    private static String getAppPermViewName() {
        return APP_PERM_VIEW_NAME;
    }

    private static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    /**
     * Table creation happens in onCreate this method also loads the default data
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_APPS_TABLE);
            db.execSQL(CREATE_PERMISSIONS_TABLE);
            db.execSQL(CREATE_APP_PERM_TABLE);
            db.execSQL(CREATE_CONTEXT_TABLE);
            db.execSQL(CREATE_POLICY_RULES_TABLE);
            db.execSQL(CREATE_CONTEXT_LOG_TABLE);
            db.execSQL(CREATE_VIOLATIONS_LOG_TABLE);
            db.execSQL(CREATE_ACTION_LOG_TABLE);

            db.execSQL(CREATE_APP_PERM_VIEW);
        } catch (SQLException sqlException) {
            Log.e(MithrilApplication.getDebugTag(), "Following error occurred while creating the SQLite DB - " + sqlException.getMessage());
        } catch (Exception e) {
            Log.e(MithrilApplication.getDebugTag(), "Some other error occurred while creating the SQLite DB - " + e.getMessage());
        }
        loadDB(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    /**
     * Method to drop all tables in the database; Very dangerous
     *
     * @param db database instance
     */
    public void deleteAllData(SQLiteDatabase db) {
        dropDBObjects(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MithrilDBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ". Old data will be destroyed");
        dropDBObjects(db);
    }

    private void dropDBObjects(SQLiteDatabase db) {
        db.execSQL("DROP VIEW IF EXISTS " + getAppPermViewName());

        db.execSQL("DROP TABLE IF EXISTS " + getActionLogTableName());
        db.execSQL("DROP TABLE IF EXISTS " + getViolationsLogTableName());
        db.execSQL("DROP TABLE IF EXISTS " + getContextLogTableName());
        db.execSQL("DROP TABLE IF EXISTS " + getPolicyRulesTableName());
        db.execSQL("DROP TABLE IF EXISTS " + getContextTableName());
        db.execSQL("DROP TABLE IF EXISTS " + getAppPermTableName());
        db.execSQL("DROP TABLE IF EXISTS " + getPermissionsTableName());
        db.execSQL("DROP TABLE IF EXISTS " + getAppsTableName());

        onCreate(db);
    }

    private void loadDB(SQLiteDatabase db) {
        insertHardcodedGooglePermissions(db);
        //Load all the permissions that are known for Android into the database. We will refer to them in the future.
        loadAndroidPermissionsIntoDB(db);
        //Load all the apps and app permissions that are known for this device into the database. We will refer to them in the future.
        loadRealAppDataIntoDB(db);
        //We have to get the policies from somewhere. The best case scenario would be a server that gives us the policies.
        loadPoliciesForApps(db);
        //The following method loads the database with the default dummy data on creation of the database
        //THIS WILL NOT BE USED ANYMORE
        //loadDefaultDataIntoDB(db);
    }

    private void insertHardcodedGooglePermissions(SQLiteDatabase db) {
        try {
            db.execSQL(MithrilApplication.getInsertStatementGooglePermissions());
        } catch (SQLException sqlException) {
            Log.e(MithrilApplication.getDebugTag(), "Following error occurred while inserting data in SQLite DB - " + sqlException.getMessage());
        } catch (Exception e) {
            Log.e(MithrilApplication.getDebugTag(), "Some other error occurred while inserting data in SQLite DB - " + e.getMessage());
        }
    }

    private void loadAndroidPermissionsIntoDB(SQLiteDatabase db) {
//		Log.d(MithrilApplication.getDebugTag(), "I came to loadAndroidPermissionsIntoDB");
        PackageManager packageManager = getContext().getPackageManager();

        List<PermissionGroupInfo> permissionGroupInfoList = packageManager.getAllPermissionGroups(PackageManager.GET_META_DATA);
//		Log.d(MithrilApplication.getDebugTag(), "Size is: " + Integer.toString(permisisonGroupInfoList.size()));
        permissionGroupInfoList.add(null);

        for (PermissionGroupInfo permissionGroupInfo : permissionGroupInfoList) {
            String groupName = permissionGroupInfo == null ? null : permissionGroupInfo.name;
//            if(groupName == null)
//                Log.d(MithrilApplication.getDebugTag(), "Result is: null");
//            else
//                Log.d(MithrilApplication.getDebugTag(), "Result is: "+groupName);
            try {
                for (PermissionInfo permissionInfo : packageManager.queryPermissionsByGroup(groupName, 0)) {
//                    if (permissionInfo.group == null)
                    if (groupName == null)
                        addPermission(db, getPermData(packageManager, permissionInfo));
                    else
                        addPermission(db, getPermData(packageManager, groupName, permissionInfo));
                }
            } catch (NameNotFoundException exception) {
                Log.e(MithrilApplication.getDebugTag(), "Some error due to " + exception.getMessage());
            } catch (PermissionWasUpdateException exception) {
                Log.e(MithrilApplication.getDebugTag(), "PermissionWasUpdateException: Ignore this? " + exception.getMessage());
            }
        }
    }

    private void loadRealAppDataIntoDB(SQLiteDatabase db) {
        PackageManager packageManager = getContext().getPackageManager();
        int flags = PackageManager.GET_META_DATA |
                PackageManager.GET_SHARED_LIBRARY_FILES |
                PackageManager.GET_PERMISSIONS;

        for (PackageInfo pack : packageManager.getInstalledPackages(flags)) {
            if ((pack.applicationInfo.flags) != 1) {
                try {
                    AppData tempAppData = new AppData();
                    if (pack.packageName != null) {
                        //App description
                        if (pack.applicationInfo.loadDescription(packageManager) != null)
                            tempAppData.setAppDescription(pack.applicationInfo.loadDescription(packageManager).toString());
                        else
                            tempAppData.setAppDescription(MithrilApplication.getDefaultDescription());

                        //App process name
                        tempAppData.setAssociatedProcessName(pack.applicationInfo.processName);

                        //App target SDK version
                        tempAppData.setTargetSdkVersion(pack.applicationInfo.targetSdkVersion);

                        //App icon
                        if (pack.applicationInfo.loadIcon(packageManager) instanceof BitmapDrawable)
                            tempAppData.setIcon(((BitmapDrawable) pack.applicationInfo.loadIcon(packageManager)).getBitmap());
                        else {
                            tempAppData.setIcon(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_launcher));
                        }

                        //App name
                        tempAppData.setAppName(pack.applicationInfo.loadLabel(packageManager).toString());

                        //App package name
                        tempAppData.setPackageName(pack.packageName);

                        //App version info
                        tempAppData.setVersionInfo(pack.versionName);

                        //App installed or not
                        tempAppData.setInstalled(true);

                        //App type
                        if ((pack.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1)
                            tempAppData.setAppType(MithrilApplication.getPrefKeySystemAppsDisplay());
                        else
                            tempAppData.setAppType(MithrilApplication.getPrefKeyUserAppsDisplay());

                        //App uid
                        tempAppData.setUid(pack.applicationInfo.uid);

                        //App permissions
                        if (pack.requestedPermissions != null) {
                            Map<String, Boolean> requestedPermissionsMap = new HashMap<>();
                            for (String packagePermission : pack.requestedPermissions) {
                                requestedPermissionsMap.put(packagePermission, false);
                                /*
                                 * The following fix is no longer required. There was a flaw in insert permission we should have used insertOrThrow
                                 * - PKD, Dec 27, 2016. Carrie Fisher AKA Princess Leia Organa (Skywalker) passed away today at 0855 PST. The world will miss her. :(
                                 * ---------------------------------------------------------------------------------------------------------------------------------------------
                                 * The following shell script may be used to extract exact permission data.
                                 * However, that will require root access and adb shell code execution.
                                 * Perhaps we should avoid that for now.
                                 * ---------------------------------------------------------------------------------------------------------------------------------------------
                                 findRequestedLineStart=`adb shell dumpsys package com.google.android.youtube | grep -n "requested permissions:" | cut -f1 -d ':'`
                                 findRequestedLineEnd=`adb shell dumpsys package com.google.android.youtube | grep -n "install permissions:" | cut -f1 -d ':'`
                                 findInstallLineStart=`adb shell dumpsys package com.google.android.youtube | grep -n "install permissions:" | cut -f1 -d ':'`
                                 findInstallLineEnd=`adb shell dumpsys package com.google.android.youtube | grep -n "installed=true" | cut -f1 -d ':'`

                                 numLinesRequestedPermission=$((findRequestedLineEnd-findRequestedLineStart-1))
                                 adb shell dumpsys package com.google.android.youtube | grep -A $numLinesRequestedPermission "requested permissions:" | tr -d ' '

                                 numLinesInstalledPermission=$((findInstallLineEnd-findInstallLineStart-1))
                                 adb shell dumpsys package com.google.android.youtube | grep -A $numLinesInstalledPermission "install permissions:" | cut -f1 -d"=" | tr -d ' '
                                 * ---------------------------------------------------------------------------------------------------------------------------------------------
                                 */
                                tempAppData.setPermissions(requestedPermissionsMap);
                            }
                        }
                    }
                    //Insert an app into database
                    long appId = addApp(db, tempAppData);

                    //Insert permissions for an app into AppPerm
                    addAppPerm(db, tempAppData, appId);

//                    long insertedRowId = addApp(db, tempAppData);
//                    Log.d(MithrilApplication.getDebugTag(), "Inserted record id is: "+Long.toString(insertedRowId));
//				} catch (ClassCastException e){
//					Log.d(MithrilApplication.getDebugTag(), e.getMessage());
                } catch (Exception e) {
                    Log.d(MithrilApplication.getDebugTag(), e.getMessage());
                }
            }
        }
    }

    private void loadPoliciesForApps(SQLiteDatabase db) {
    }

    private PermData getPermData(PackageManager packageManager, String groupName, PermissionInfo permissionInfo) {
        PermData tempPermData = new PermData();

        tempPermData.setPermissionName(permissionInfo.name);
        //Setting the protection level
        switch (permissionInfo.protectionLevel) {
            case PermissionInfo.PROTECTION_NORMAL:
                tempPermData.setPermissionProtectionLevel(MithrilApplication.getPermissionProtectionLevelNormal());
                break;
            case PermissionInfo.PROTECTION_DANGEROUS:
                tempPermData.setPermissionProtectionLevel(MithrilApplication.getPermissionProtectionLevelDangerous());
                break;
            case PermissionInfo.PROTECTION_SIGNATURE:
                tempPermData.setPermissionProtectionLevel(MithrilApplication.getPermissionProtectionLevelSignature());
                break;
            case PermissionInfo.PROTECTION_FLAG_PRIVILEGED:
                tempPermData.setPermissionProtectionLevel(MithrilApplication.getPermissionProtectionLevelPrivileged());
                break;
            default:
                tempPermData.setPermissionProtectionLevel(MithrilApplication.getPermissionProtectionLevelUnknown());
                break;
        }

        tempPermData.setPermissionGroup(groupName);

        //Setting the protection level
        switch (permissionInfo.flags) {
            case PermissionInfo.FLAG_COSTS_MONEY:
                tempPermData.setPermissionFlag(MithrilApplication.getPermissionFlagCostsMoney());
                break;
            case PermissionInfo.FLAG_INSTALLED:
                tempPermData.setPermissionFlag(MithrilApplication.getPermissionFlagInstalled());
                break;
            default:
                tempPermData.setPermissionFlag(MithrilApplication.getPermissionFlagNone());
                break;
        }
        //Permission description can be null. We are preventing a null pointer exception here.
        tempPermData.setPermissionDescription(permissionInfo.loadDescription(packageManager)
                == null
                ? context.getResources().getString(R.string.no_description_available_txt)
                : permissionInfo.loadDescription(packageManager).toString());

        tempPermData.setPermissionIcon(getPermissionIconBitmap(permissionInfo));
        tempPermData.setPermissionLabel(permissionInfo.loadLabel(packageManager).toString());
//        Log.d(MithrilApplication.getDebugTag(), "Label: "+permissionInfo.loadLabel(packageManager).toString()+", end label");

        return tempPermData;
    }

    private PermData getPermData(PackageManager packageManager, PermissionInfo permissionInfo) {
        PermData tempPermData = new PermData();

        tempPermData.setPermissionName(permissionInfo.name);
        //Setting the protection level
        switch (permissionInfo.protectionLevel) {
            case PermissionInfo.PROTECTION_NORMAL:
                tempPermData.setPermissionProtectionLevel(MithrilApplication.getPermissionProtectionLevelNormal());
                break;
            case PermissionInfo.PROTECTION_DANGEROUS:
                tempPermData.setPermissionProtectionLevel(MithrilApplication.getPermissionProtectionLevelDangerous());
                break;
            case PermissionInfo.PROTECTION_SIGNATURE:
                tempPermData.setPermissionProtectionLevel(MithrilApplication.getPermissionProtectionLevelSignature());
                break;
            case PermissionInfo.PROTECTION_FLAG_PRIVILEGED:
                tempPermData.setPermissionProtectionLevel(MithrilApplication.getPermissionProtectionLevelPrivileged());
                break;
            default:
                tempPermData.setPermissionProtectionLevel(MithrilApplication.getPermissionProtectionLevelUnknown());
                break;
        }

        tempPermData.setPermissionGroup(MithrilApplication.getPermissionNoGroup());

        //Setting the protection level
        switch (permissionInfo.flags) {
            case PermissionInfo.FLAG_COSTS_MONEY:
                tempPermData.setPermissionFlag(MithrilApplication.getPermissionFlagCostsMoney());
                break;
            case PermissionInfo.FLAG_INSTALLED:
                tempPermData.setPermissionFlag(MithrilApplication.getPermissionFlagInstalled());
                break;
            default:
                tempPermData.setPermissionFlag(MithrilApplication.getPermissionFlagNone());
                break;
        }
        //Permission description can be null. We are preventing a null pointer exception here.
        tempPermData.setPermissionDescription(permissionInfo.loadDescription(packageManager)
                == null
                ? context.getResources().getString(R.string.no_description_available_txt)
                : permissionInfo.loadDescription(packageManager).toString());

        tempPermData.setPermissionIcon(getPermissionIconBitmap(permissionInfo));
        tempPermData.setPermissionLabel(permissionInfo.loadLabel(packageManager).toString());
//        Log.d(MithrilApplication.getDebugTag(), "Label: "+permissionInfo.loadLabel(packageManager).toString()+", end label");

        return tempPermData;
    }

    private Bitmap getPermissionIconBitmap(PermissionInfo permissionInfo) {
        PackageManager packageManager = getContext().getPackageManager();

        Drawable drawable = permissionInfo.loadIcon(packageManager);
        Bitmap bitmap;
        try {

            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        } catch (OutOfMemoryError e) {
            // Handle the error
            return null;
        }
        return bitmap;
    }

    private Bitmap getPermissionIconBitmap() {
        PackageManager packageManager = getContext().getPackageManager();

        Drawable drawable = context.getResources().getDrawable(R.drawable.clipboard_alert, context.getTheme());
        Bitmap bitmap;
        try {

            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        } catch (OutOfMemoryError e) {
            // Handle the error
            return null;
        }
        return bitmap;
    }

    /**
     * -----------------------------------------------------------------------------------------------------------------------------------------------------------------
     * All CRUD(Create, Read, Update, Delete) Operations
     */
    /**
     * @param db
     * @param anAppData
     * @return
     */
    public long addApp(SQLiteDatabase db, AppData anAppData) {
        long insertedRowId;
        ContentValues values = new ContentValues();
        values.put(APPDESCRIPTION, anAppData.getAppDescription());
        if (anAppData.getAssociatedProcessName() != null)
            values.put(APPASSOCIATEDPROCNAME, anAppData.getAssociatedProcessName());
        values.put(APPTARGETSDKVERSION, anAppData.getTargetSdkVersion());
        values.put(APPICON, getBitmapAsByteArray(anAppData.getIcon()));
        values.put(APPNAME, anAppData.getAppName());
        values.put(APPPACKAGENAME, anAppData.getPackageName());
        values.put(APPVERSIONINFO, anAppData.getVersionInfo());
        if (anAppData.isInstalled())
            values.put(APPINSTALLED, 1);
        else
            values.put(APPINSTALLED, 0);
        values.put(APPTYPE, anAppData.getAppType());
        values.put(APPUID, anAppData.getUid());
        try {
            insertedRowId = db.insertOrThrow(getAppsTableName(), null, values);
        } catch (SQLException e) {
            Log.e(MithrilApplication.getDebugTag(), "Error inserting " + values, e);
            return -1;
        }
        return insertedRowId;
    }

    /**
     *
     * @param db
     * @param aPermData
     * @return
     * @throws PermissionWasUpdateException
     */
    private long addPermission(SQLiteDatabase db, PermData aPermData) throws PermissionWasUpdateException {
        long insertedRowId = -1;
        ContentValues values = new ContentValues();
        values.put(PERMNAME, aPermData.getPermissionName());
        values.put(PERMLABEL, aPermData.getPermissionLabel());
        values.put(PERMPROTECTIONLEVEL, aPermData.getPermissionProtectionLevel());
        values.put(PERMGROUP, aPermData.getPermissionGroup());
        values.put(PERMFLAG, aPermData.getPermissionFlag());
        values.put(PERMDESC, aPermData.getPermissionDescription());
        values.put(PERMICON, getBitmapAsByteArray(aPermData.getPermissionIcon()));
        try {
            insertedRowId = db.insertOrThrow(getPermissionsTableName(), null, values);
        } catch (SQLiteConstraintException e) {
            updateConflictedGooglePermissions(db, aPermData);
            throw new PermissionWasUpdateException("Exception occured for " + aPermData.getPermissionName());
        } catch (SQLException e) {
            Log.e(MithrilApplication.getDebugTag(), "Error inserting " + values, e);
        }
        return insertedRowId;
    }

    /**
     *
     * @param db
     * @param anAppData
     * @param appId
     * @return
     */
    public long addAppPerm(SQLiteDatabase db, AppData anAppData, long appId) {
        int flags = PackageManager.GET_META_DATA;
        Map<String, Boolean> appPermissions = anAppData.getPermissions();
//        if(appPermissions == null)
//            Log.d(MithrilApplication.getDebugTag(), "we got null for: "+anAppData.getAppName());
        long insertedRowId = -1;
        if (appPermissions != null) {
            ContentValues values = new ContentValues();
            values.put(APPPERMRESAPPID, appId);
            for (Map.Entry<String, Boolean> appPermission : appPermissions.entrySet()) {
                long permId = findPermissionsByName(db, appPermission.getKey());
                if (permId == -1) {
                    PackageManager packageManager = getContext().getPackageManager();
                    PermissionInfo permissionInfo;

                    try {
                        permissionInfo = packageManager.getPermissionInfo(appPermission.getKey(), flags);
                    } catch (NameNotFoundException e) {
                        Log.e(MithrilApplication.getDebugTag(), "Something wrong " + e.getMessage());
                        continue;
                        //TODO This is a big problem. Why are we not getting the permission info for certain installed permissions???
                    }

                    try {
                        if (permissionInfo.group == null)
                            permId = addPermission(db, getPermData(packageManager, permissionInfo));
                        else
                            permId = addPermission(db, getPermData(packageManager, permissionInfo.group, permissionInfo));
                    } catch (PermissionWasUpdateException e) {
                        Log.e(MithrilApplication.getDebugTag(), "So the permission was potentially updated, search for the id again " + e.getMessage());
                        permId = findPermissionsByName(db, appPermission.getKey());
                    }
                }
                values.put(APPPERMRESPERID, permId);
                values.put(APPPERMGRANTED, appPermission.getValue());
                try {
                    insertedRowId = db.insertOrThrow(getAppPermTableName(), null, values);
                } catch (SQLiteConstraintException e) {
                    Log.e(MithrilApplication.getDebugTag(), "there was a SQLite Constraint Exception " + values, e);
                    return -1;
                } catch (SQLException e) {
                    Log.e(MithrilApplication.getDebugTag(), "Error inserting " + values, e);
                    return -1;
                }
            }
        }
        return insertedRowId;
    }

    /**
     *
     * @param db
     * @param aUserContext
     * @return
     */
    public long addContext(SQLiteDatabase db, SemanticUserContext aUserContext) {
        long insertedRowId;
        ContentValues values = new ContentValues();
        values.put(CONTEXTLOCATION, aUserContext.getSemanticLocation().toString());
        values.put(CONTEXTIDENTITY, aUserContext.getSemanticIdentity().toString());
        values.put(CONTEXTACTIVITY, aUserContext.getSemanticActivity().toString());
        values.put(CONTEXTPRESENCEINFO, aUserContext.getSemanticNearActors().toString());
        values.put(CONTEXTTEMPORAL, aUserContext.getSemanticTime().toString());
        try {
            insertedRowId = db.insertOrThrow(getContextTableName(), null, values);
        } catch (SQLException e) {
            Log.e(MithrilApplication.getDebugTag(), "Error inserting " + values, e);
            return -1;
        }
        return insertedRowId;
    }

    /**
     *
     * @param db
     * @param contextId
     * @return
     */
    public long addContextLog(SQLiteDatabase db, int contextId) {
        long insertedRowId;
        ContentValues values = new ContentValues();
        values.put(CTXTID, contextId);
        try {
            insertedRowId = db.insertOrThrow(getContextLogTableName(), null, values);
        } catch (SQLException e) {
            Log.e(MithrilApplication.getDebugTag(), "Error inserting " + values, e);
            return -1;
        }
        return insertedRowId;
    }

    /**
     *
     * @param db
     * @param aRuleAction
     * @return
     */
    public long addActionLog(SQLiteDatabase db, RuleAction aRuleAction) {
        long insertedRowId;
        ContentValues values = new ContentValues();
        values.put(ACTION, aRuleAction.getAction().getStatusCode());
        try {
            insertedRowId = db.insertOrThrow(getActionLogTableName(), null, values);
        } catch (SQLException e) {
            Log.e(MithrilApplication.getDebugTag(), "Error inserting " + values, e);
            return -1;
        }
        return insertedRowId;
    }

    /**
     *
     * @param db
     * @param aPolicyRule
     * @return
     */
    public long addPolicyRule(SQLiteDatabase db, PolicyRule aPolicyRule) {
        long insertedRowId;
        ContentValues values = new ContentValues();
        values.put(POLRULNAME, aPolicyRule.getName());
        values.put(POLRULACTIN, aPolicyRule.getAction().getId());
        values.put(POLRULAPPID, aPolicyRule.getAppId());
        values.put(POLRULCTXID, aPolicyRule.getCtxId());
        try {
            insertedRowId = db.insertOrThrow(getPolicyRulesTableName(), null, values);
        } catch (SQLException e) {
            Log.e(MithrilApplication.getDebugTag(), "Error inserting " + values, e);
            return -1;
        }
        return insertedRowId;
    }

    /**
     * method to insert into violations table the violation
     * @param db         database instance
     * @param aViolation a violation
     * @return returns the inserted row id
     */
    public long addViolation(SQLiteDatabase db, Violation aViolation) {
        long insertedRowId;
        ContentValues values = new ContentValues();
        if (aViolation.getRuleId() == -1)
            throw new SQLException("Value error!");
        values.put(VIOLATIONDESC, aViolation.getViolationDescription());
        if (aViolation.isViolationMarker())
            values.put(VIOLATIONMARKER, 1);
        else
            values.put(VIOLATIONMARKER, 0);
        try {
            insertedRowId = db.insertOrThrow(getViolationsLogTableName(), null, values);
        } catch (SQLException e) {
            Log.e(MithrilApplication.getDebugTag(), "Error inserting " + values, e);
            return -1;
        }
        return insertedRowId;
    }

    /**
     * Finds all apps
     * @param db database instance
     * @return returns list of apps
     */
    public List<AppData> findAllApps(SQLiteDatabase db) {
        // Select AppData Query
        String selectQuery = "SELECT " +
                getAppsTableName() + "." + APPDESCRIPTION + ", " +
                getAppsTableName() + "." + APPASSOCIATEDPROCNAME + ", " +
                getAppsTableName() + "." + APPTARGETSDKVERSION + ", " +
                getAppsTableName() + "." + APPICON + ", " +
                getAppsTableName() + "." + APPNAME + ", " +
                getAppsTableName() + "." + APPPACKAGENAME + ", " +
                getAppsTableName() + "." + APPVERSIONINFO + ", " +
                getAppsTableName() + "." + APPTYPE + ", " +
                getAppsTableName() + "." + APPUID +
                " FROM " + getAppsTableName() + ";";

        List<AppData> apps = new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    apps.add(
                            new AppData(
                                    cursor.getString(0),
                                    cursor.getString(1),
                                    Integer.parseInt(cursor.getString(2)),
                                    BitmapFactory.decodeByteArray(cursor.getBlob(3), 0, cursor.getBlob(3).length),
                                    cursor.getString(4),
                                    cursor.getString(5),
                                    cursor.getString(6),
                                    cursor.getString(7),
                                    Integer.parseInt(cursor.getString(8))
                            )
                    );
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            throw new SQLException("Could not find " + e);
        } finally {
            cursor.close();
        }
        return apps;
    }

    /**
     * Finds app by name
     * @param db database instance
     * @return AppData
     */
    public AppData findAppByName(SQLiteDatabase db, String appName) {
        // Select AppData Query
        String selectQuery = "SELECT " +
                getAppsTableName() + "." + APPDESCRIPTION + ", " +
                getAppsTableName() + "." + APPASSOCIATEDPROCNAME + ", " +
                getAppsTableName() + "." + APPTARGETSDKVERSION + ", " +
                getAppsTableName() + "." + APPICON + ", " +
                getAppsTableName() + "." + APPNAME + ", " +
                getAppsTableName() + "." + APPPACKAGENAME + ", " +
                getAppsTableName() + "." + APPVERSIONINFO + ", " +
                getAppsTableName() + "." + APPINSTALLED + ", " +
                getAppsTableName() + "." + APPINSTALLED + ", " +
                getAppsTableName() + "." + APPTYPE + ", " +
                getAppsTableName() + "." + APPUID +
                " FROM " + getAppsTableName() +
                " WHERE " + getAppsTableName() + "." + APPNAME +
                " = " + appName +
                ";";

        AppData app = new AppData();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                app = new AppData(
                        cursor.getString(0),
                        cursor.getString(1),
                        Integer.parseInt(cursor.getString(2)),
                        BitmapFactory.decodeByteArray(cursor.getBlob(3), 0, cursor.getBlob(3).length),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        Integer.parseInt(cursor.getString(8))
                );
            }
        } catch (SQLException e) {
            throw new SQLException("Could not find " + e);
        } finally {
            cursor.close();
        }
        return app;
    }

    /**
     * Finds app by id
     * @param db database instance
     * @return AppData
     */
    public AppData findAppById(SQLiteDatabase db, int appId) {
        // Select AppData Query
        String selectQuery = "SELECT " +
                getAppsTableName() + "." + APPDESCRIPTION + ", " +
                getAppsTableName() + "." + APPASSOCIATEDPROCNAME + ", " +
                getAppsTableName() + "." + APPTARGETSDKVERSION + ", " +
                getAppsTableName() + "." + APPICON + ", " +
                getAppsTableName() + "." + APPNAME + ", " +
                getAppsTableName() + "." + APPPACKAGENAME + ", " +
                getAppsTableName() + "." + APPVERSIONINFO + ", " +
                getAppsTableName() + "." + APPINSTALLED + ", " +
                getAppsTableName() + "." + APPTYPE + ", " +
                getAppsTableName() + "." + APPUID +
                " FROM " + getAppsTableName() +
                " WHERE " + getAppsTableName() + "." + APPID +
                " = " + appId +
                ";";

        AppData app = new AppData();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                app = new AppData(
                        cursor.getString(0),
                        cursor.getString(1),
                        Integer.parseInt(cursor.getString(2)),
                        BitmapFactory.decodeByteArray(cursor.getBlob(3), 0, cursor.getBlob(3).length),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        Integer.parseInt(cursor.getString(8))
                );
            }
        } catch (SQLException e) {
            throw new SQLException("Could not find " + e);
        } finally {
            cursor.close();
        }
        return app;
    }

    /**
     * Finds appType by appPkgName
     * @param db         database instance
     * @param appPkgName app pkg name
     * @return AppData
     */
    public String findAppTypeByAppPkgName(SQLiteDatabase db, String appPkgName) {
        // Select AppType Query
        String selectQuery = "SELECT " +
                getAppsTableName() + "." + APPTYPE +
                " FROM " + getAppsTableName() +
                " WHERE " + getAppsTableName() + "." + APPPACKAGENAME + " = '" + appPkgName + "';";

        String appType = null;
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                appType = cursor.getString(0);
            }
        } catch (SQLException e) {
            throw new SQLException("Could not find " + e);
        } finally {
            cursor.close();
        }
        return appType;
    }

    /**
     * Temporary solution setup but eventually we will the join and populate with data from our servers
     * @param db             database instance
     * @param appPackageName app pkg name
     * @return List of PermData objects
     */
    public List<PermData> findAppPermissionsByAppPackageName(SQLiteDatabase db, String appPackageName) {
        // Select AppPermData Query
        String selectQuery = "SELECT " +
                getAppPermViewName() + "." + APPPERMVIEWPERMNAME + ", " +
                getAppPermViewName() + "." + APPPERMVIEWPERMGROUP + ", " +
                getAppPermViewName() + "." + APPPERMVIEWPERMLABEL + ", " +
                getAppPermViewName() + "." + APPPERMVIEWPERMPROLVL +
                " FROM " +
                getAppPermViewName() +
                " WHERE " +
                getAppPermViewName() + "." + APPPERMVIEWAPPPKGNAME + " = '" + appPackageName + "';";

        List<PermData> permDataList = new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    PermData permData = new PermData();
                    permData.setPermissionName(cursor.getString(0));
                    permData.setPermissionGroup(cursor.getString(1));
                    permData.setPermissionLabel(cursor.getString(2));
                    permData.setPermissionProtectionLevel(cursor.getString(3));
                    permDataList.add(permData);
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.d(MithrilApplication.getDebugTag(), "Could not find " + e.getMessage());
            return null;
        } finally {
            cursor.close();
        }
        return permDataList;
    }

    /**
     * Finds permissions by name
     * @param db database instance
     * @return permissionName
     */
    public long findPermissionsByName(SQLiteDatabase db, String permissionName) {
        // Select AppData Query
        String selectQuery = "SELECT " +
                getPermissionsTableName() + "." + PERMID +
                " FROM " + getPermissionsTableName() +
                " WHERE " + getPermissionsTableName() + "." + PERMNAME +
                " = '" + permissionName +
                "';";

        long permId = -1;
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                permId = Integer.parseInt(cursor.getString(0));
            }
        } catch (SQLException e) {
            throw new SQLException("Could not find " + e);
        } finally {
            cursor.close();
        }
//      if (permId == -1)
//          Log.d(MithrilApplication.getDebugTag(), permissionName);
        return permId;
    }

    /**
     * Finds all violations
     * @param db database instance
     * @return all violations
     */
    public List<Violation> findAllViolations(SQLiteDatabase db) {
        // Select Violation Query
        String selectQuery = "SELECT " +
                getViolationsLogTableName() + "." + VIOLATIONID + ", " +
                getViolationsLogTableName() + "." + VIOLATIONDESC + ", " +
                getViolationsLogTableName() + "." + VIOLATIONMARKER +
                " FROM " + getViolationsLogTableName() + ";";

        List<Violation> violations = new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Violation tempViolation;
                    if (Integer.parseInt(cursor.getString(3)) == 0)
                        tempViolation = new Violation(
                                Integer.parseInt(cursor.getString(0)),
                                cursor.getString(1),
                                Integer.parseInt(cursor.getString(2)),
                                Integer.parseInt(cursor.getString(3)),
                                false);
                    else
                        tempViolation = new Violation(
                                Integer.parseInt(cursor.getString(0)),
                                cursor.getString(1),
                                Integer.parseInt(cursor.getString(2)),
                                Integer.parseInt(cursor.getString(3)),
                                true);
                    violations.add(tempViolation);
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            throw new SQLException("Could not find " + e);
        } finally {
            cursor.close();
        }
        return violations;
    }

    /**
     * Finds all violations
     * @param db database instance
     * @return all violations
     */
    public List<Violation> findAllViolationsWithMarkerUnset(SQLiteDatabase db) {
        // Select Violation Query
        String selectQuery = "SELECT " +
                getViolationsLogTableName() + "." + VIOLATIONID + ", " +
                getViolationsLogTableName() + "." + VIOLATIONDESC + ", " +
                getViolationsLogTableName() + "." + VIOLATIONMARKER +
                " FROM " + getViolationsLogTableName() +
                " WHERE " + getViolationsLogTableName() + "." + VIOLATIONMARKER + " = 0;";

        List<Violation> violations = new ArrayList<>();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    Violation tempViolation;
                    if (Integer.parseInt(cursor.getString(3)) == 0)
                        tempViolation = new Violation(
                                Integer.parseInt(cursor.getString(0)),
                                cursor.getString(1),
                                Integer.parseInt(cursor.getString(2)),
                                Integer.parseInt(cursor.getString(3)),
                                false);
                    else
                        tempViolation = new Violation(
                                Integer.parseInt(cursor.getString(0)),
                                cursor.getString(1),
                                Integer.parseInt(cursor.getString(2)),
                                Integer.parseInt(cursor.getString(3)),
                                true);
                    violations.add(tempViolation);
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            throw new SQLException("Could not find " + e);
        } finally {
            cursor.close();
        }
        return violations;
    }

    /**
     * Getting all policies
     * @param db database instance
     * @return all policies
     */
    public ArrayList<PolicyRule> findAllPolicies(SQLiteDatabase db) {
        ArrayList<PolicyRule> policyRules = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT " +
                getPolicyRulesTableName() + "." + POLRULID + ", " +
                getPolicyRulesTableName() + "." + POLRULNAME + ", " +
                getPolicyRulesTableName() + "." + POLRULAPPID + ", " +
                getPolicyRulesTableName() + "." + POLRULCTXID + ", " +
                getPolicyRulesTableName() + "." + POLRULACTIN + ", " +
                getContextLogTableName() + "." + CONTEXTPRESENCEINFO + ", " +
                getContextLogTableName() + "." + CONTEXTACTIVITY + ", " +
                getContextLogTableName() + "." + CONTEXTIDENTITY + ", " +
                getContextLogTableName() + "." + CONTEXTLOCATION + ", " +
                getContextLogTableName() + "." + CONTEXTTEMPORAL +
                " FROM " +
                getPolicyRulesTableName() + ", " + getContextTableName() +
                " WHERE " +
                getPolicyRulesTableName() + "." + POLRULCTXID + " = " + getContextTableName() + "." + CONTEXTID +
                ";";

        Cursor cursor = db.rawQuery(selectQuery, null);
        try {

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    PolicyRule policyRule = new PolicyRule();
                    policyRule.setId(Integer.parseInt(cursor.getString(0)));
                    policyRule.setName(cursor.getString(1));
                    policyRule.setAppId(Integer.parseInt(cursor.getString(2)));
                    policyRule.setCtxId(Integer.parseInt(cursor.getString(3)));
                    if (Integer.parseInt(cursor.getString(4)) == 1)
                        policyRule.setAction(new RuleAction(Action.ALLOW));
                    else
                        policyRule.setAction(new RuleAction(Action.DENY));
                    policyRule.setSemanticUserContext(new SemanticUserContext(
                            new SemanticNearActors(cursor.getString(5)),
                            new SemanticActivity(cursor.getString(6)),
                            new SemanticIdentity(cursor.getString(7)),
                            new SemanticLocation(cursor.getString(8)),
                            new SemanticTime(cursor.getString(9))
                    ));

                    // Adding policies to list
                    policyRules.add(policyRule);
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            throw new SQLException("Could not find " + e);
        } finally {
            cursor.close();
        }
        // return policy rules list
        return policyRules;
    }

    /**
     * Finds a policy based on the application being accessed
     * @param db database instance
     * @return all policies
     */
    public ArrayList<PolicyRule> findAllPoliciesByAppPkgName(SQLiteDatabase db, String appPkgName) {
        ArrayList<PolicyRule> policyRules = new ArrayList<>();
        // Select Policy Query
        String selectQuery = "SELECT " +
                getPolicyRulesTableName() + "." + POLRULID + ", " +
                getPolicyRulesTableName() + "." + POLRULNAME + ", " +
                getPolicyRulesTableName() + "." + POLRULAPPID + ", " +
                getPolicyRulesTableName() + "." + POLRULCTXID + ", " +
                getPolicyRulesTableName() + "." + POLRULACTIN + ", " +
                getContextTableName() + "." + CONTEXTPRESENCEINFO + ", " +
                getContextTableName() + "." + CONTEXTACTIVITY + ", " +
                getContextTableName() + "." + CONTEXTIDENTITY + ", " +
                getContextTableName() + "." + CONTEXTLOCATION + ", " +
                getContextTableName() + "." + CONTEXTTEMPORAL +
                " FROM " +
                getPolicyRulesTableName() + ", " + getAppsTableName() + ", " + getContextTableName() +
                " WHERE " +
                getPolicyRulesTableName() + "." + POLRULAPPID + " = " + getAppsTableName() + "." + APPID +
                " AND " +
                getPolicyRulesTableName() + "." + POLRULCTXID + " = " + getContextTableName() + "." + CONTEXTID +
                " AND " +
                getAppsTableName() + "." + APPPACKAGENAME + " = '" + appPkgName + "';";

        Cursor cursor = db.rawQuery(selectQuery, null);
        try {

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    PolicyRule policyRule = new PolicyRule();
                    policyRule.setId(Integer.parseInt(cursor.getString(0)));
                    policyRule.setName(cursor.getString(1));
                    policyRule.setAppId(Integer.parseInt(cursor.getString(2)));
                    policyRule.setCtxId(Integer.parseInt(cursor.getString(3)));
                    if (Integer.parseInt(cursor.getString(4)) == 1)
                        policyRule.setAction(new RuleAction(Action.ALLOW));
                    else
                        policyRule.setAction(new RuleAction(Action.DENY));
                    policyRule.setSemanticUserContext(new SemanticUserContext(
                            new SemanticNearActors(cursor.getString(5)),
                            new SemanticActivity(cursor.getString(6)),
                            new SemanticIdentity(cursor.getString(7)),
                            new SemanticLocation(cursor.getString(8)),
                            new SemanticTime(cursor.getString(9))
                    ));

                    // Adding policies to list
                    policyRules.add(policyRule);
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            throw new SQLException("Could not find " + e);
        } finally {
            cursor.close();
        }
        // return policy rules list
        return policyRules;
    }

    /**
     * Finds a policy based on the policy id
     * @param db database instance
     * @param id policy id
     * @return policy info
     */
    public PolicyRule findPolicyByID(SQLiteDatabase db, int id) {
        // Select Policy Query
        String selectQuery = "SELECT " +
                getPolicyRulesTableName() + "." + POLRULID + ", " +
                getPolicyRulesTableName() + "." + POLRULNAME + ", " +
                getPolicyRulesTableName() + "." + POLRULAPPID + ", " +
                getPolicyRulesTableName() + "." + POLRULCTXID + ", " +
                getPolicyRulesTableName() + "." + POLRULACTIN + ", " +
                getContextTableName() + "." + CONTEXTPRESENCEINFO + ", " +
                getContextTableName() + "." + CONTEXTACTIVITY + ", " +
                getContextTableName() + "." + CONTEXTIDENTITY + ", " +
                getContextTableName() + "." + CONTEXTLOCATION + ", " +
                getContextTableName() + "." + CONTEXTTEMPORAL +
                " FROM " +
                getPolicyRulesTableName() +
                " WHERE " +
                getPolicyRulesTableName() + "." + POLRULID + " = " + id + ";";

        PolicyRule policyRule = new PolicyRule();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                policyRule.setId(Integer.parseInt(cursor.getString(0)));
                policyRule.setName(cursor.getString(1));
                policyRule.setAppId(Integer.parseInt(cursor.getString(2)));
                policyRule.setCtxId(Integer.parseInt(cursor.getString(3)));
                if (Integer.parseInt(cursor.getString(4)) == 1)
                    policyRule.setAction(new RuleAction(Action.ALLOW));
                else
                    policyRule.setAction(new RuleAction(Action.DENY));
                policyRule.setSemanticUserContext(new SemanticUserContext(
                        new SemanticNearActors(cursor.getString(5)),
                        new SemanticActivity(cursor.getString(6)),
                        new SemanticIdentity(cursor.getString(7)),
                        new SemanticLocation(cursor.getString(8)),
                        new SemanticTime(cursor.getString(9))
                ));
            }
        } catch (SQLException e) {
            throw new SQLException("Could not find " + e);
        } finally {
            cursor.close();
        }
        return policyRule;
    }

    /**
     * Finds a resource based on the resource id
     * @param db database instance
     * @param id action id
     * @return action info
     */
    public RuleAction findActionByID(SQLiteDatabase db, int id) {
        // Select Query
        String selectQuery = "SELECT " +
                getActionLogTableName() + "." + ACTIONID + ", " +
                getActionLogTableName() + "." + ACTION +
                " FROM " +
                getActionLogTableName() +
                " WHERE " +
                getActionLogTableName() + "." + ACTIONID + " = " + id + ";";

        RuleAction action = new RuleAction();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                action.setId(Integer.parseInt(cursor.getString(0)));
                if (Integer.parseInt(cursor.getString(1)) == 2)
                    action.setAction(Action.ALLOW);
                else if (Integer.parseInt(cursor.getString(1)) == 1)
                    action.setAction(Action.ALLOW_WITH_CAVEAT);
                else
                    action.setAction(Action.DENY);
            }
        } catch (SQLException e) {
            throw new SQLException("Could not find " + e);
        } finally {
            cursor.close();
        }
        return action;
    }

    /**
     * Finds a context instance based on the context id
     * @param db database instance
     * @param id context id
     * @return context info
     */
    public SemanticUserContext findContextByID(SQLiteDatabase db, int id) {
        // Select Query
        String selectQuery = "SELECT " +
                getContextTableName() + "." + CONTEXTID + ", " +
                getContextTableName() + "." + CONTEXTPRESENCEINFO + ", " +
                getContextTableName() + "." + CONTEXTACTIVITY + ", " +
                getContextTableName() + "." + CONTEXTLOCATION + ", " +
                getContextTableName() + "." + CONTEXTIDENTITY + ", " +
                getContextTableName() + "." + CONTEXTTEMPORAL +
                " FROM " +
                getContextTableName() +
                " WHERE " +
                getContextTableName() + "." + CONTEXTID + " = " + id + ";";

        SemanticUserContext userContext = new SemanticUserContext();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            if (cursor.moveToFirst()) {
                userContext.setId(Integer.parseInt(cursor.getString(0)));

                ArrayList<SemanticIdentity> presenceIdList = new ArrayList<>();
                presenceIdList.add(new SemanticIdentity(cursor.getString(1)));
                SemanticNearActors semanticNearActors = new SemanticNearActors(presenceIdList);
                userContext.setSemanticNearActors(semanticNearActors);
                userContext.setSemanticActivity(new SemanticActivity(cursor.getString(2)));
                userContext.setSemanticLocation(new SemanticLocation(cursor.getString(3)));
                userContext.setSemanticTime(new SemanticTime(cursor.getString(4)));
            }
        } catch (SQLException e) {
            throw new SQLException("Could not find " + e);
        } finally {
            cursor.close();
        }
        return userContext;
    }

    /**
     * method to update single violation
     */
    public int updateViolationAsTrue(SQLiteDatabase db, Violation aViolation) {
        ContentValues values = new ContentValues();
        values.put(VIOLATIONDESC, aViolation.getViolationDescription());
        values.put(VIOLATIONMARKER, 1);
        return db.update(getViolationsLogTableName(), values, VIOLATIONID + " = ?",
                new String[]{String.valueOf(aViolation.getId())});
    }

    /**
     * method to update single violation
     * Update is being removed because of the foreign key constraint as this causes an SQLException during insertion
     */
    public int updatePolicyRule(SQLiteDatabase db, PolicyRule aPolicyRule) {
        SemanticUserContext semanticUserContext;
        try {
            semanticUserContext = findContextByID(db, aPolicyRule.getCtxId());
        } catch (SQLException e) {
            Log.d(MithrilApplication.getDebugTag(), "Context is unknown, creating");
            semanticUserContext = new SemanticUserContext(aPolicyRule.getSemanticUserContext().getSemanticNearActors(),
                    aPolicyRule.getSemanticUserContext().getSemanticActivity(),
                    aPolicyRule.getSemanticUserContext().getSemanticIdentity(),
                    aPolicyRule.getSemanticUserContext().getSemanticLocation(),
                    aPolicyRule.getSemanticUserContext().getSemanticTime());
            addContext(db, semanticUserContext);
        }

        //The context was found, perhaps we need to update the instance
        updateContext(db, semanticUserContext);

        ContentValues values = new ContentValues();
        values.put(POLRULID, aPolicyRule.getId());
        values.put(POLRULNAME, aPolicyRule.getName());
        values.put(POLRULAPPID, aPolicyRule.getAppId());
        values.put(POLRULCTXID, aPolicyRule.getCtxId());
        values.put(POLRULACTIN, aPolicyRule.getAction().getId());
        try {
            return db.update(getPolicyRulesTableName(), values, POLRULID + " = ?",
                    new String[]{String.valueOf(aPolicyRule.getId())});
        } catch (SQLException e) {
            throw new SQLException("Exception " + e + " error updating Context: " + aPolicyRule.getSemanticUserContext());
        }
    }

    /**
     *
     * @param db
     * @param aSemanticUserContext
     * @return
     */
    private int updateContext(SQLiteDatabase db, SemanticUserContext aSemanticUserContext) {
        ContentValues values = new ContentValues();
        values.put(CONTEXTID, aSemanticUserContext.getId());
        values.put(CONTEXTACTIVITY, aSemanticUserContext.getSemanticActivity().getInferredActivity());
        values.put(CONTEXTIDENTITY, aSemanticUserContext.getSemanticIdentity().getIdentity());
        values.put(CONTEXTLOCATION, aSemanticUserContext.getSemanticLocation().getInferredLocation());
        values.put(CONTEXTPRESENCEINFO, aSemanticUserContext.getSemanticNearActors().toString());
        values.put(CONTEXTTEMPORAL, aSemanticUserContext.getSemanticTime().getInferredTime());
        try {
            return db.update(getContextTableName(), values, CONTEXTID + " = ?",
                    new String[]{String.valueOf(aSemanticUserContext.getId())});
        } catch (SQLException e) {
            throw new SQLException("Exception " + e + " error updating Context: " + aSemanticUserContext.toString());
        }
    }

    /**
     * method to update conflicted Google permissions
     * We found extra information about the Google permissions and we are adding those in the table
     */
    private int updateConflictedGooglePermissions(SQLiteDatabase db, PermData aPermData) {
//        name, protectionlvl, permgrp, flags (these four columns should already be present)
        ContentValues values = new ContentValues();
        values.put(PERMDESC, aPermData.getPermissionDescription());
        values.put(PERMICON, getBitmapAsByteArray(aPermData.getPermissionIcon()));
        values.put(PERMLABEL, aPermData.getPermissionLabel());
        if (aPermData.getPermissionGroup().equals(MithrilApplication.getPermissionNoGroup()))
            values.put(PERMGROUP, aPermData.getPermissionGroup());
        if (aPermData.getPermissionFlag().equals(MithrilApplication.getPermissionFlagNone()))
            values.put(PERMFLAG, aPermData.getPermissionFlag());
        try {
            return db.update(getPermissionsTableName(), values, PERMNAME + " = ?",
                    new String[]{aPermData.getPermissionName()});
        } catch (SQLException e) {
            throw new SQLException("Exception " + e + " error updating permission: " + aPermData.getPermissionName());
        }
    }

    /**
     * method to delete a row from a table based on the identifier
     * @param db database instance
     */
    public void deleteViolation(SQLiteDatabase db, Violation aViolation) {
        try {
            db.delete(getViolationsLogTableName(), VIOLATIONID + " = ?",
                    new String[]{String.valueOf(aViolation.getId())});
        } catch (SQLException e) {
            throw new SQLException("Could not find " + e);
        }
    }

    /**
     * Given a certain app uid deletes app from the database
     * @param db  database instance
     * @param uid app linux UID
     */
    public void deleteAppByUID(SQLiteDatabase db, int uid) {
        try {
//            Log.d(MithrilApplication.getDebugTag(), "Deleting this: " + Integer.toString(uid));
            db.delete(getAppsTableName(), APPUID + " = ?",
                    new String[]{String.valueOf(uid)});
        } catch (SQLException e) {
            throw new SQLException("Could not find " + e.getMessage());
        }
    }

    /**
     *
     * @param db
     * @param aUserContext
     */
    public void deleteContext(SQLiteDatabase db, SemanticUserContext aUserContext) {
        try {
            db.delete(getContextLogTableName(), CONTEXTID + " = ?",
                    new String[]{String.valueOf(aUserContext.getId())});
        } catch (SQLException e) {
            throw new SQLException("Could not find " + e);
        }
    }

    /**
     *
     * @param db
     * @param aRuleAction
     */
    public void deleteRuleAction(SQLiteDatabase db, RuleAction aRuleAction) {
        try {
            db.delete(getActionLogTableName(), ACTIONID + " = ?",
                    new String[]{String.valueOf(aRuleAction.getId())});
        } catch (SQLException e) {
            throw new SQLException("Could not find " + e);
        }
    }

    /**
     *
     * @param db
     * @param aPolicyRule
     */
    public void deletePolicyRule(SQLiteDatabase db, PolicyRule aPolicyRule) {
        try {
            db.delete(getPolicyRulesTableName(), POLRULID + " = ?",
                    new String[]{String.valueOf(aPolicyRule.getId())});
        } catch (SQLException e) {
            throw new SQLException("Could not find " + e);
        }
    }

    /**
     *
     * @param db
     * @param aPolicyRuleId
     */
    public void deletePolicyRuleById(SQLiteDatabase db, int aPolicyRuleId) {
        try {
            db.delete(getPolicyRulesTableName(), POLRULID + " = ?",
                    new String[]{String.valueOf(aPolicyRuleId)});
        } catch (SQLException e) {
            throw new SQLException("Could not find " + e);
        }
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------
    // End of CRUD methods
    //
}