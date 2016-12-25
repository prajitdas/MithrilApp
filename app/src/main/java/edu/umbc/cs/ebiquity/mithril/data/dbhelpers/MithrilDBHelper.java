package edu.umbc.cs.ebiquity.mithril.data.dbhelpers;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.database.Cursor;
import android.database.SQLException;
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
import java.util.List;
import java.util.regex.Pattern;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.R;
import edu.umbc.cs.ebiquity.mithril.data.model.AppData;
import edu.umbc.cs.ebiquity.mithril.data.model.PermData;
import edu.umbc.cs.ebiquity.mithril.data.model.Violation;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.PolicyRule;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.actions.Action;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.actions.RuleAction;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.SemanticUserContext;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticActivity;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticIdentity;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticLocation;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticNearActors;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticTime;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.protectedresources.Resource;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.requesters.Requester;

public class MithrilDBHelper extends SQLiteOpenHelper {
	// Database declarations
    private final static int DATABASE_VERSION = (int) System.currentTimeMillis();
    private final static String DATABASE_NAME = MithrilApplication.getDatabaseName();

	/**
	 * Following are table names in our database
	 */
    private final static String ACTION_TABLE_NAME = "actionlog";
    private final static String APP_PERM_TABLE_NAME = "appperm";
    private final static String APP_DATA_TABLE_NAME = "apps";
    private final static String CONTEXT_TABLE_NAME = "contextlog";
    private final static String PERMISSIONS_TABLE_NAME = "permissions";
    private final static String POLICY_RULES_TABLE_NAME = "policyrules";
    private final static String REQUESTERS_TABLE_NAME = "requesters";
    private final static String RESOURCES_TABLE_NAME = "resources";
    private final static String VIOLATIONS_TABLE_NAME = "violationlog";
    private final static String APP_PERM_VIEW_NAME = "apppermview";

    /**
     * -- Table 1: actionlog
     * CREATE TABLE actionlog (
     * id INTEGER PRIMARY KEY AUTOINCREMENT,
     * resources_id INTEGER NOT NULL,
     * context_id INTEGER NOT NULL,
     * requesters_id INTEGER NOT NULL,
     * time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
     * action INTEGER NOT NULL
     * ) COMMENT 'Table showing actions taken for each context, resource, requester tuple';
     * ----------------------------------------------------------------------------------------------------------------
     * 0 for denied, 1 for allowed
     * Makes a record everytime an action is taken for a certain requester, resource, context and applicable policy
     * This is the action log table. Stores every action taken whether
     */
    private final static String ACTIONID = "id"; // ID of an action taken

    /**
     * Following are table creation statements
     */
    private final static String ACTIONRESID = "resources_id"; // Resource that was requested
    private final static String ACTIONCONID = "context_id"; // Context in which the request was made
    private final static String ACTIONREQID = "requesters_id"; // Requester that sent the request
    private final static String ACTIONTIME = "time"; // Time when action was taken
    private final static String ACTION = "action"; // Action that was taken for a certain scenario

    private final static String CREATE_ACTION_TABLE = "CREATE TABLE " + getActionTableName() + " (" +
            ACTIONID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ACTIONREQID + " INTEGER NOT NULL, " +
            ACTIONRESID + " INTEGER NOT NULL, " +
            ACTIONCONID + " INTEGER NOT NULL, " +
            ACTIONTIME + " INTEGER NOT NULL  DEFAULT CURRENT_TIMESTAMP, " +
            ACTION + " INTEGER NOT NULL, " +
            "FOREIGN KEY(context_id) REFERENCES contextlog(id), " +
            "FOREIGN KEY(requesters_id) REFERENCES requesters(id), " +
            "FOREIGN KEY(resources_id) REFERENCES resources(id) " +
            ");";
    //    private final static String ACTIONPRLID = "polrulid"; // Policy rule id from the policy table that was used to determine the action

    /**
     * -- Table 2: appperm
     * CREATE TABLE appperm (
     * apps_id INTEGER NOT NULL,
     * permissions_id INTEGER NOT NULL
     * ) COMMENT 'Table showing apps and permissions';
     * ----------------------------------------------------------------------------------------------------------------
     * This table represents all the apps and their corresponding permissions. We also want to store the association between an app and an api call or a resource access.
     */
    private final static String APPPERMRESAPPID = "apps_id"; // ID from resource table
    private final static String APPPERMRESPERID = "permissions_id"; // ID from permission table

    private final static String CREATE_APP_PERM_TABLE = "CREATE TABLE " + getAppPermTableName() + " (" +
            APPPERMRESAPPID + " INTEGER NOT NULL, " +
            APPPERMRESPERID + " INTEGER NOT NULL, " +
            "FOREIGN KEY(apps_id) REFERENCES apps(id) ON DELETE CASCADE, " +
            "FOREIGN KEY(permissions_id) REFERENCES permissions(id), " +
            "CONSTRAINT appperm_pk PRIMARY KEY (" +
            APPPERMRESAPPID + "," +
            APPPERMRESPERID + ")" +
            ");";

    /**
     * -- Table 3: apps
     * CREATE TABLE apps (
     * id INTEGER PRIMARY KEY AUTOINCREMENT,
     * uid INTEGER NOT NULL,
     * description TEXT NULL,
     * assocprocname TEXT NULL,
     * targetsdkver INTEGER NOT NULL,
     * icon blob NOT NULL,
     * label TEXT NOT NULL,
     * name TEXT NOT NULL,
     * verinfo TEXT NOT NULL,
     * installed bool NOT NULL DEFAULT true,
     * type INTEGER NOT NULL,
     * installdate timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
     * requesters_id INTEGER NOT NULL,
     * CONSTRAINT apps_unique_key UNIQUE(name)
     * ) COMMENT 'Table showing metadata for apps';
     */
    private final static String APPID = "id"; // ID of an installed app
    // Below columns store information colelcted on the phone about app
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
    private final static String APPREQID = "requesters_id";

    private final static String CREATE_APP_DATA_TABLE = " CREATE TABLE " + getAppDataTableName() + " (" +
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
            APPREQID + " INTEGER NOT NULL DEFAULT 0," +
            "FOREIGN KEY(requesters_id) REFERENCES requesters(id), " +
            "CONSTRAINT apps_unique_key UNIQUE(" + APPPACKAGENAME + ") " +
            ");";

    /**
     * -- Table 4: contextlog
     * CREATE TABLE contextlog (
     * id INTEGER PRIMARY KEY AUTOINCREMENT,
     * identity TEXT NOT NULL DEFAULT 'user',
     * location TEXT NULL,
     * activity TEXT NULL,
     * temporal TEXT NULL,
     * presenceinfo TEXT NULL,
     * time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
     * ) COMMENT 'Table showing log of current user context';
     * --------------------------------------------------------------------------------------------------------------------------
     * An entry is made into this table everytime we determine a change in context.
     * This could be where we could do energy efficient stuff as in we can save battery by determining context from historical data or some other way.
     */
    private final static String CONTEXTID = "id"; // ID of the context instance
    private final static String IDENTITY = "identity"; // SemanticIdentity context; this is redundant as because we are working on a single device
    private final static String LOCATION = "location"; // SemanticLocation context
    private final static String ACTIVITY = "activity"; // SemanticActivity context
    private final static String PRESENCEINFO = "presenceinfo"; // If we could get presence information of others then we can do relationship based privacy solutions
    private final static String TEMPORAL = "temporal"; // SemanticTemporal information; the time instance when the current context was captured
    private final static String CONTEXTTIME = "time"; // SemanticTemporal information; the time instance when the current context was captured

    private final static String CREATE_CONTEXT_TABLE = "CREATE TABLE " + getContextTableName() + " (" +
            CONTEXTID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            IDENTITY + " TEXT NOT NULL DEFAULT 'user'," +
            LOCATION + " TEXT NULL," +
            ACTIVITY + " TEXT NULL," +
            TEMPORAL + " TEXT NULL," +
            PRESENCEINFO + " TEXT NULL," +
            CONTEXTTIME + " timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP " +
            ");";

    /**
     * -- Table 5: permissions
     * CREATE TABLE permissions (
     * id INTEGER PRIMARY KEY AUTOINCREMENT,
     * name TEXT NOT NULL,
     * label TEXT NOT NULL,
     * protectionlvl TEXT NOT NULL,
     * permgrp TEXT NULL,
     * flag TEXT NULL,
     * description TEXT NULL,
     * icon blob NOT NULL,
     * resources_id INTEGER NOT NULL,
     * CONSTRAINT permissions_unique_name UNIQUE(name)
     * ) COMMENT 'Table showing metadata for permissions';
     */
    private final static String PERMID = "id"; // ID of a known permission on the device
    // Once a permission is known, we will get the meta information about them
    private final static String PERMNAME = "name";
    private final static String PERMLABEL = "label";
    private final static String PERMPROTECTIONLEVEL = "protectionlvl";
    private final static String PERMGROUP = "permgrp";
    private final static String PERMFLAG = "flags";
    private final static String PERMDESC = "description";
    private final static String PERMICON = "icon";
    private final static String PERMRESNAME = "resources_id";

    private final static String CREATE_PERMISSIONS_TABLE = "CREATE TABLE " + getPermissionsTableName() + " (" +
            PERMID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PERMNAME + " TEXT NOT NULL, " +
            PERMLABEL + " TEXT NOT NULL, " +
            PERMPROTECTIONLEVEL + " TEXT NOT NULL, " +
            PERMGROUP + " TEXT NULL, " +
            PERMFLAG + " TEXT NULL, " +
            PERMDESC + " TEXT NULL, " +
            PERMICON + " BLOB, " +
            PERMRESNAME + " INTEGER NOT NULL DEFAULT 0, " +
            "FOREIGN KEY(resources_id) REFERENCES resources(id), " +
            "CONSTRAINT permissions_unique_name UNIQUE(" + PERMNAME + ") " +
            ");";

    /**
     * -- Table 6: policyrules
     * CREATE TABLE policyrules (
     * id INTEGER PRIMARY KEY AUTOINCREMENT,
     * name TEXT NOT NULL,
     * action INTEGER NOT NULL,
     * context_id INTEGER NOT NULL,
     * requesters_id INTEGER NOT NULL,
     * resources_id INTEGER NOT NULL
     * ) COMMENT 'Table showing policy rules defined for apps and requested resources in given context';
     */
    private final static String POLRULID = "id"; // ID of policy defined
    private final static String POLRULNAME = "name"; // Policy short name
    private final static String POLRULACTIN = "action"; // Action will be denoted as: 0 for to deny, 1 for allow
    private final static String POLRULCNTXT = "context_id"; // Context in which the request was made.
    private final static String POLRULREQID = "requesters_id"; // Requester that sent the request
    private final static String POLRULRESID = "resources_id"; // Resource that was requested
    // This will be a general text that will have to be "reasoned" about!
    // If this says policy is applicable @home then we have to be able to determine that context available represents "home"

    private final static String CREATE_POLICY_RULES_TABLE = "CREATE TABLE " + getPolicyRulesTableName() + " (" +
            POLRULID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            POLRULNAME + " TEXT NOT NULL, " +
            POLRULACTIN + " INTEGER NOT NULL, " +
            POLRULCNTXT + " INTEGER NOT NULL, " +
            POLRULREQID + " INTEGER NOT NULL, " +
            POLRULRESID + " INTEGER NOT NULL, " +
            "FOREIGN KEY(context_id) REFERENCES contextlog(id), " +
            "FOREIGN KEY(requesters_id) REFERENCES requesters(id), " +
            "FOREIGN KEY(resources_id) REFERENCES resources(id) " +
            ");";

    /**
     * -- Table 7: requesters
     * CREATE TABLE requesters (
     * id INTEGER PRIMARY KEY AUTOINCREMENT,
     * name TEXT NOT NULL
     * ) COMMENT 'Table showing metadata for requesters of user data';
     */
    private final static String REQID = "id"; // ID of a request
    private final static String REQNAME = "name"; // Name from App table from which a request was received

    private final static String CREATE_REQUESTERS_TABLE = "CREATE TABLE " + getRequestersTableName() + " (" +
            REQID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            REQNAME + " TEXT NOT NULL " +
            ");";

    /**
     * -- Table 8: resources
     * CREATE TABLE resources (
     * id INTEGER PRIMARY KEY AUTOINCREMENT,
     * name TEXT NOT NULL
     * ) COMMENT 'Table showing metadata for resource being requested';
     */
    private final static String RESID = "id"; // ID of a resource on the device
    // Fields for the database tables
    private final static String RESNAME = "name"; // Meaningful name of the resource on the device

    private final static String CREATE_RESOURCES_TABLE = "CREATE TABLE " + getResourcesTableName() + " (" +
            RESID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            RESNAME + " TEXT NOT NULL " +
            ");";

    /**
     * -- Table 9: violationlog
     * CREATE TABLE violationlog (
     * id INTEGER PRIMARY KEY AUTOINCREMENT,
     * resources_id INTEGER NOT NULL,
     * requesters_id INTEGER NOT NULL,
     * context_id INTEGER NOT NULL,
     * description TEXT NOT NULL,
     * marker bool DEFAULT,
     * time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
     * ) COMMENT 'Table showing violations recorded by MithrilAC and subsequent user feedback';
     */
    private final static String VIOLATIONID = "id"; // ID of violation captured
    private final static String VIOLATIONRESID = "resources_id";
    private final static String VIOLATIONREQID = "requesters_id";
    private final static String VIOLATIONCTXID = "context_id";
    private final static String VIOLATIONDESC = "description"; // An appropriate description of the violation. No idea how this will be generated but
    // we could simply use the requester, resource, context, policy name and action taken as a summary description. We could also concatenate information with the notice that
    // we have a violation of a policy - "policy name". Additionally we could state that
//    private final static String VIOLATIONOFRULID = "polrulid"; // policy rule id that was violated
    private final static String VIOLATIONMARKER = "marker";
    private final static String VIOLATIONTIME = "time";

    private final static String CREATE_VIOLATIONS_TABLE = "CREATE TABLE " + getViolationsTableName() + " (" +
            VIOLATIONID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            VIOLATIONRESID + " INTEGER NOT NULL, " +
            VIOLATIONREQID + " INTEGER NOT NULL, " +
            VIOLATIONCTXID + " INTEGER NOT NULL, " +
            VIOLATIONDESC + " TEXT, " +
            VIOLATIONMARKER + " INTEGER NOT NULL DEFAULT 0, " +
            VIOLATIONTIME + " timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY(context_id) REFERENCES contextlog(id), " +
            "FOREIGN KEY(requesters_id) REFERENCES requesters(id), " +
            "FOREIGN KEY(resources_id) REFERENCES resources(id) " +
            ");";

    /**
     * -- views
     * -- View: apppermview
     * CREATE VIEW `mithril.db`.apppermview AS
     * SELECT
     * a.name as apppkgname,
     * p.name as permname,
     * p.protectionlvl as protectionlevel,
     * p.label as permlabel,
     * p.permgrp as permgroup
     * FROM
     * apps a,
     * permissions p,
     * appperm ap
     * WHERE
     * ap.apps_id = a.id
     * AND
     * ap.permissions_id = p.id;
     */
    // View 1 for App permissions
    // This table represents all the apps and their corresponding permissions. We also want to store the association between an app and an api call or a resource access.
    private final static String APPPERMVIEWAPPPKGNAME = "apppkgname"; // app package name
    private final static String APPPERMVIEWPERMNAME = "permname"; // app permission name
    private final static String APPPERMVIEWPERMPROLVL = "protectionlevel"; // app permission protection level
    private final static String APPPERMVIEWPERMLABEL = "permlabel"; // app permission label
    private final static String APPPERMVIEWPERMGROUP = "permgroup"; // app permission group

    private final static String CREATE_APP_PERM_VIEW = "CREATE VIEW " + getAppPermViewName() + " AS " +
			"SELECT " +
			getAppDataTableName() + "." + APPPACKAGENAME + " AS " + APPPERMVIEWAPPPKGNAME + ", " +
			getPermissionsTableName() + "." + PERMNAME + " AS " + APPPERMVIEWPERMNAME + ", " +
			getPermissionsTableName() + "." + PERMPROTECTIONLEVEL + " AS " + APPPERMVIEWPERMPROLVL + ", " +
			getPermissionsTableName() + "." + PERMLABEL + " AS " + APPPERMVIEWPERMLABEL + ", " +
			getPermissionsTableName() + "." + PERMGROUP + " AS " + APPPERMVIEWPERMGROUP +
			" FROM " +
			getAppPermTableName() + "," +
			getPermissionsTableName() + "," +
			getAppDataTableName() +
			" WHERE " +
			getAppPermTableName() + "." + APPPERMRESAPPID + " = " + getAppDataTableName() + "." + APPID +
			" AND " +
			getAppPermTableName() + "." + APPPERMRESPERID + " = " + getPermissionsTableName() + "." + PERMID + ";";

    private Context context;

	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * Table creation statements complete
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * Database creation constructor
	 * @param aContext
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
	public static String getPolicyRulesTableName() {
		return POLICY_RULES_TABLE_NAME;
	}

	public static String getRequestersTableName() {
		return REQUESTERS_TABLE_NAME;
	}

	public static String getResourcesTableName() {
		return RESOURCES_TABLE_NAME;
	}

	public static String getContextTableName() {
		return CONTEXT_TABLE_NAME;
	}

	public static String getActionTableName() {
		return ACTION_TABLE_NAME;
	}

	public static String getViolationsTableName() {
		return VIOLATIONS_TABLE_NAME;
	}

	public static String getAppDataTableName() {
		return APP_DATA_TABLE_NAME;
	}

	public static String getPermissionsTableName() {
		return PERMISSIONS_TABLE_NAME;
	}

	public static String getAppPermTableName() {
		return APP_PERM_TABLE_NAME;
	}

	public static String getAppPermViewName() {
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
            db.execSQL(CREATE_REQUESTERS_TABLE);
            db.execSQL(CREATE_RESOURCES_TABLE);
            db.execSQL(CREATE_APP_DATA_TABLE);
            db.execSQL(CREATE_PERMISSIONS_TABLE);
            db.execSQL(CREATE_APP_PERM_TABLE);
            db.execSQL(CREATE_CONTEXT_TABLE);
            db.execSQL(CREATE_POLICY_RULES_TABLE);
            db.execSQL(CREATE_ACTION_TABLE);
            db.execSQL(CREATE_VIOLATIONS_TABLE);

            db.execSQL(CREATE_APP_PERM_VIEW);
        } catch (SQLException sqlException) {
            Log.e(MithrilApplication.getDebugTag(), "Following error occurred while creating the SQLite DB - " + sqlException.getMessage());
        } catch (Exception e) {
            Log.e(MithrilApplication.getDebugTag(), "Some other error occurred while creating the SQLite DB - " + e.getMessage());
        }
        insertHardcodedGooglePermissions(db);
        //Load all the permissions that are known for Android into the database. We will refer to them in the future.
		loadAndroidPermissionsIntoDB(db);
		//Load all the apps and app permissions that are known for this device into the database. We will refer to them in the future.
		loadRealAppDataIntoDB(db);
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
	 * @param db
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

        db.execSQL("DROP TABLE IF EXISTS " + getViolationsTableName());
        db.execSQL("DROP TABLE IF EXISTS " + getActionTableName());
        db.execSQL("DROP TABLE IF EXISTS " + getPolicyRulesTableName());
        db.execSQL("DROP TABLE IF EXISTS " + getContextTableName());
        db.execSQL("DROP TABLE IF EXISTS " + getAppPermTableName());
        db.execSQL("DROP TABLE IF EXISTS " + getPermissionsTableName());
        db.execSQL("DROP TABLE IF EXISTS " + getAppDataTableName());
        db.execSQL("DROP TABLE IF EXISTS " + getResourcesTableName());
        db.execSQL("DROP TABLE IF EXISTS " + getRequestersTableName());

        onCreate(db);
	}

	/**
     * -----------------------------------------------------------------------------------------------------------------------------------------------------------------
     * All CRUD(Create, Read, Update, Delete) Operations
	 */
	public long addRequester(SQLiteDatabase db, Requester aRequester) {
		long insertedRowId;
		ContentValues values = new ContentValues();
		values.put(REQNAME, aRequester.getRequesterName());
		try{
			insertedRowId = db.insert(getRequestersTableName(), null, values);
		} catch (SQLException e) {
            Log.e(MithrilApplication.getDebugTag(), "Error inserting " + values, e);
            return -1;
		}
		return insertedRowId;
	}

	public long addResource(SQLiteDatabase db, Resource aResource) {
		long insertedRowId;
		ContentValues values = new ContentValues();
		values.put(RESNAME, aResource.getResourceName());
		try {
			insertedRowId = db.insert(getResourcesTableName(), null, values);
		} catch (SQLException e) {
            Log.e(MithrilApplication.getDebugTag(), "Error inserting " + values, e);
            return -1;
		}
		return insertedRowId;
	}

    public long addContext(SQLiteDatabase db, SemanticUserContext aUserContext) {
        long insertedRowId;
		ContentValues values = new ContentValues();
		values.put(LOCATION, aUserContext.getSemanticLocation().toString());
		values.put(IDENTITY, aUserContext.getSemanticIdentity().toString());
		values.put(ACTIVITY, aUserContext.getSemanticActivity().toString());
        values.put(PRESENCEINFO, aUserContext.getSemanticNearActors().toString());
        values.put(TEMPORAL, aUserContext.getSemanticTime().toString());
		try {
			insertedRowId = db.insert(getContextTableName(), null, values);
		} catch (SQLException e) {
            Log.e(MithrilApplication.getDebugTag(), "Error inserting " + values, e);
            return -1;
		}
		return insertedRowId;
	}

	public long addRuleAction(SQLiteDatabase db, RuleAction aRuleAction) {
		long insertedRowId;
		ContentValues values = new ContentValues();
		values.put(ACTION, aRuleAction.getAction().getStatusCode());
		try {
			insertedRowId = db.insert(getActionTableName(), null, values);
		} catch (SQLException e) {
            Log.e(MithrilApplication.getDebugTag(), "Error inserting " + values, e);
            return -1;
		}
		return insertedRowId;
	}

	public long addAppData(SQLiteDatabase db, AppData anAppData) {
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
			insertedRowId = db.insert(getAppDataTableName(), null, values);
		} catch (SQLException e) {
            Log.e(MithrilApplication.getDebugTag(), "Error inserting " + values, e);
            return -1;
		}
		return insertedRowId;
	}

	//TODO We have to do a join across 3 tables and return the permissions for an app
	public long addAppPerm(SQLiteDatabase db, AppData anAppData, long appId) {
		String[] appPermissions = anAppData.getPermissions();
		long insertedRowId = -1;
		ContentValues values = new ContentValues();
		values.put(APPPERMRESAPPID, appId);
		if (appPermissions != null) {
			for (int permIdx = 0; permIdx < appPermissions.length; permIdx++) {
                long permId = findPermissionsByName(db, appPermissions[permIdx]);
                if (permId == -1)
                    return -1;
                values.put(APPPERMRESPERID, permId);
                try {
					insertedRowId = db.insert(getAppPermTableName(), null, values);
				} catch (SQLException e) {
                    Log.e(MithrilApplication.getDebugTag(), "Error inserting " + values, e);
                    return -1;
				}
			}
		}
		return insertedRowId;
	}

	public long addPermission(SQLiteDatabase db, PermData aPermData) {
		long insertedRowId;
		ContentValues values = new ContentValues();
		values.put(PERMNAME, aPermData.getPermissionName());
		values.put(PERMPROTECTIONLEVEL, aPermData.getPermissionProtectionLevel());
		values.put(PERMGROUP, aPermData.getPermissionGroup());
		values.put(PERMFLAG, aPermData.getPermissionFlag());
		values.put(PERMDESC, aPermData.getPermissionDescription());
		values.put(PERMICON, getBitmapAsByteArray(aPermData.getPermissionIcon()));
		values.put(PERMLABEL, aPermData.getPermissionLabel());
		values.put(PERMRESNAME, aPermData.getResource().getResourceName());
		try {
			insertedRowId = db.insert(getPermissionsTableName(), null, values);
		} catch (SQLException e) {
            updateConflictedGooglePermissions(db, aPermData);
            Log.e(MithrilApplication.getDebugTag(), "Error inserting " + values, e);
            return -1;
		}
		return insertedRowId;
	}

    public long addPolicyRule(SQLiteDatabase db, PolicyRule aPolicyRule) {
        long insertedRowId;
		ContentValues values = new ContentValues();
		values.put(POLRULNAME, aPolicyRule.getName());
		values.put(POLRULREQID, aPolicyRule.getRequester().getId());
		values.put(POLRULRESID, aPolicyRule.getResource().getId());
		values.put(POLRULCNTXT, aPolicyRule.getContext());
		values.put(POLRULACTIN, aPolicyRule.getAction().getId());
		try {
			insertedRowId = db.insert(getPolicyRulesTableName(), null, values);
		} catch (SQLException e) {
            Log.e(MithrilApplication.getDebugTag(), "Error inserting " + values, e);
            return -1;
		}
		return insertedRowId;
	}

	/**
	 * method to insert into violations table the violation
	 * @param db
	 * @param aViolation
	 * @return
	 */
	public long addViolation(SQLiteDatabase db, Violation aViolation) {
		long insertedRowId;
		ContentValues values = new ContentValues();
		if(aViolation.getRuleId()==-1)
			throw new SQLException("Value error!");
		values.put(VIOLATIONDESC, aViolation.getViolationDescription());
		if(aViolation.isViolationMarker())
			values.put(VIOLATIONMARKER, 1);
		else
			values.put(VIOLATIONMARKER, 0);
		try{
			insertedRowId = db.insert(getViolationsTableName(), null, values);
		} catch (SQLException e) {
            Log.e(MithrilApplication.getDebugTag(), "Error inserting " + values, e);
            return -1;
		}
		return insertedRowId;
	}

	/**
	 * Finds all apps
	 * @param db
	 * @return
	 */
	public List<AppData> findAllApps(SQLiteDatabase db) {
		// Select AppData Query
		String selectQuery = "SELECT " +
				getAppDataTableName() + "." + APPDESCRIPTION + ", " +
				getAppDataTableName() + "." + APPASSOCIATEDPROCNAME + ", " +
				getAppDataTableName() + "." + APPTARGETSDKVERSION + ", " +
				getAppDataTableName() + "." + APPICON + ", " +
				getAppDataTableName() + "." + APPNAME + ", " +
				getAppDataTableName() + "." + APPPACKAGENAME + ", " +
				getAppDataTableName() + "." + APPVERSIONINFO + ", " +
				getAppDataTableName() + "." + APPTYPE + ", " +
				getAppDataTableName() + "." + APPUID +
				" FROM " + getAppDataTableName() + ";";

		List<AppData> apps = new ArrayList<AppData>();
		Cursor cursor = db.rawQuery(selectQuery, null);
		try{
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
		} catch(SQLException e) {
			throw new SQLException("Could not find " + e);
		} finally {
			cursor.close();
		}
		return apps;
	}

	/**
	 * Finds app by name
	 *
	 * @param db
	 * @return AppData
	 */
	public AppData findAppByName(SQLiteDatabase db, String appName) {
		// Select AppData Query
		String selectQuery = "SELECT " +
				getAppDataTableName() + "." + APPDESCRIPTION + ", " +
				getAppDataTableName() + "." + APPASSOCIATEDPROCNAME + ", " +
				getAppDataTableName() + "." + APPTARGETSDKVERSION + ", " +
				getAppDataTableName() + "." + APPICON + ", " +
				getAppDataTableName() + "." + APPNAME + ", " +
				getAppDataTableName() + "." + APPPACKAGENAME + ", " +
				getAppDataTableName() + "." + APPVERSIONINFO + ", " +
				getAppDataTableName() + "." + APPINSTALLED + ", " +
				getAppDataTableName() + "." + APPINSTALLED + ", " +
				getAppDataTableName() + "." + APPTYPE + ", " +
				getAppDataTableName() + "." + APPUID +
				" FROM " + getAppDataTableName() +
				" WHERE " + getAppDataTableName() + "." + APPNAME +
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
	 *
	 * @param db
	 * @return AppData
	 */
	public AppData findAppById(SQLiteDatabase db, int appId) {
		// Select AppData Query
		String selectQuery = "SELECT " +
				getAppDataTableName() + "." + APPDESCRIPTION + ", " +
				getAppDataTableName() + "." + APPASSOCIATEDPROCNAME + ", " +
				getAppDataTableName() + "." + APPTARGETSDKVERSION + ", " +
				getAppDataTableName() + "." + APPICON + ", " +
				getAppDataTableName() + "." + APPNAME + ", " +
				getAppDataTableName() + "." + APPPACKAGENAME + ", " +
				getAppDataTableName() + "." + APPVERSIONINFO + ", " +
				getAppDataTableName() + "." + APPINSTALLED + ", " +
				getAppDataTableName() + "." + APPTYPE + ", " +
				getAppDataTableName() + "." + APPUID +
				" FROM " + getAppDataTableName() +
				" WHERE " + getAppDataTableName() + "." + APPID +
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
     *
     * @param db
     * @param appPkgName
     * @return AppData
     */
    public String findAppTypeByAppPkgName(SQLiteDatabase db, String appPkgName) {
        // Select AppType Query
        String selectQuery = "SELECT " +
                getAppDataTableName() + "." + APPTYPE +
                " FROM " + getAppDataTableName() +
                " WHERE " + getAppDataTableName() + "." + APPPACKAGENAME +
                " = '" + appPkgName +
                "';";

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
	 *
	 * @param db
	 * @param appPackageName
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

		List<PermData> permDataList = new ArrayList<PermData>();
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
	 *
	 * @param db
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
		} catch(SQLException e) {
			throw new SQLException("Could not find " + e);
		} finally {
			cursor.close();
		}
		if (permId == -1)
			Log.d(MithrilApplication.getDebugTag(), permissionName);
		return permId;
	}

	/**
	 * Finds all violations
	 * @param db
	 * @return
	 */
	public List<Violation> findAllViolations(SQLiteDatabase db) {
		// Select Violation Query
		String selectQuery = "SELECT " +
				getViolationsTableName() + "." + VIOLATIONID + ", " +
				getViolationsTableName() + "." + VIOLATIONDESC + ", " +
				getViolationsTableName() + "." + VIOLATIONMARKER +
				" FROM " + getViolationsTableName() + ";";

		List<Violation> violations = new ArrayList<Violation>();
		Cursor cursor = db.rawQuery(selectQuery, null);
		try{
			if (cursor.moveToFirst()) {
				do {
					Violation tempViolation = new Violation();
					if(Integer.parseInt(cursor.getString(3)) == 0)
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
		} catch(SQLException e) {
			throw new SQLException("Could not find " + e);
		} finally {
			cursor.close();
		}
		return violations;
	}

	/**
	 * Finds all violations
	 * @param db
	 * @return
	 */
	public List<Violation> findAllViolationsWithMarkerUnset(SQLiteDatabase db) {
		// Select Violation Query
		String selectQuery = "SELECT " +
				getViolationsTableName() + "." + VIOLATIONID + ", " +
				getViolationsTableName() + "." + VIOLATIONDESC + ", " +
				getViolationsTableName() + "." + VIOLATIONMARKER +
				" FROM " + getViolationsTableName() +
				" WHERE " + getViolationsTableName() + "." + VIOLATIONMARKER + " = 0;";

		List<Violation> violations = new ArrayList<Violation>();
		Cursor cursor = db.rawQuery(selectQuery, null);
		try{
			if (cursor.moveToFirst()) {
				do {
					Violation tempViolation = new Violation();
					if(Integer.parseInt(cursor.getString(3)) == 0)
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
		} catch(SQLException e) {
			throw new SQLException("Could not find " + e);
		} finally {
			cursor.close();
		}
		return violations;
	}

	/**
	 * Getting all policies
	 * @param db
	 * @return
	 */
	public ArrayList<PolicyRule> findAllPolicies(SQLiteDatabase db) {
		ArrayList<PolicyRule> policyRules = new ArrayList<PolicyRule>();
		// Select All Query
		String selectQuery = "SELECT " +
				getPolicyRulesTableName() + "." + POLRULID + ", " +
				getPolicyRulesTableName() + "." + POLRULNAME + ", " +
				getRequestersTableName() + "." + REQNAME + ", " +
				getResourcesTableName() + "." + RESNAME + ", " +
				getPolicyRulesTableName() + "." + POLRULCNTXT + ", " +
				getPolicyRulesTableName() + "." + POLRULACTIN +
				" FROM " +
				getPolicyRulesTableName() +
				" LEFT JOIN " + getRequestersTableName() +
				" ON " + getPolicyRulesTableName() + "." + POLRULREQID +
				" = " +  getRequestersTableName() + "." + REQID +
				" LEFT JOIN " + getResourcesTableName() +
				" ON " + getPolicyRulesTableName() + "." + POLRULRESID +
				" = " +  getResourcesTableName() + "." + RESID + ";";

		Cursor cursor = db.rawQuery(selectQuery, null);
		try{

			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					PolicyRule policyRule = new PolicyRule();
					policyRule.setId(Integer.parseInt(cursor.getString(0)));
					policyRule.setName(cursor.getString(1));
					policyRule.setRequester(new Requester(cursor.getString(2)));
					policyRule.setResource(new Resource(cursor.getString(3)));

//					ArrayList<SemanticIdentity> presenceInfoList = new ArrayList<SemanticIdentity>();
//					presenceInfoList.add(new SemanticIdentity(cursor.getString(4)));

					policyRule.setContext(cursor.getString(4));

					if(Integer.parseInt(cursor.getString(5)) == 1)
						policyRule.setAction(new RuleAction(Action.ALLOW));
					else
						policyRule.setAction(new RuleAction(Action.DENY));

					// Adding policies to list
					policyRules.add(policyRule);
				} while (cursor.moveToNext());
			}
		} catch(SQLException e) {
			throw new SQLException("Could not find " + e);
		} finally {
			cursor.close();
		}
		// return policy rules list
		return policyRules;
	}

	/**
	 * Finds a policy based on the application and the provider being accessed
	 * @param db
	 * @return
	 */
	public PolicyRule findPolicyRuleByReqRes(SQLiteDatabase db, String requester, String resource) {
		// Select Policy Query
		String selectQuery = "SELECT " +
				getPolicyRulesTableName() + "." + POLRULID + ", " +
				getPolicyRulesTableName() + "." + POLRULNAME + ", " +
				getRequestersTableName() + "." + REQNAME + ", " +
				getResourcesTableName() + "." + RESNAME + ", " +
				getPolicyRulesTableName() + "." + POLRULCNTXT + ", " +
				getPolicyRulesTableName() + "." + POLRULACTIN +
				" FROM " +
				getPolicyRulesTableName() +
				" LEFT JOIN " + getRequestersTableName() +
				" ON " + getPolicyRulesTableName() + "." + POLRULREQID +
				" = " +  getRequestersTableName() + "." + REQID +
				" LEFT JOIN " + getResourcesTableName() +
				" ON " + getPolicyRulesTableName() + "." + POLRULRESID +
				" = " + getResourcesTableName() + "." + RESID +
				" WHERE "  +
				getRequestersTableName() + "." + REQNAME + " = '" + requester + "' AND " +
				getResourcesTableName() + "." + RESNAME + " = '" + resource +
				"';";

		PolicyRule policyRule = new PolicyRule();
		Cursor cursor = db.rawQuery(selectQuery, null);
		try{
			if (cursor.moveToFirst()) {
				policyRule.setId(Integer.parseInt(cursor.getString(0)));
				policyRule.setName(cursor.getString(1));
				policyRule.setRequester(new Requester(cursor.getString(2)));
				policyRule.setResource(new Resource(cursor.getString(3)));

                ArrayList<SemanticIdentity> presenceInfoList = new ArrayList<SemanticIdentity>();
                presenceInfoList.add(new SemanticIdentity(cursor.getString(4)));

				policyRule.setContext(cursor.getString(5));

				if(Integer.parseInt(cursor.getString(6)) == 1)
					policyRule.setAction(new RuleAction(Action.ALLOW));
				else
					policyRule.setAction(new RuleAction(Action.DENY));
			}
		} catch(SQLException e) {
			throw new SQLException("Could not find " + e);
		} finally {
			cursor.close();
		}
		return policyRule;
	}

	/**
	 * Finds a policy based on the policy id
	 * @param db
	 * @param id
	 * @return
	 */
	public PolicyRule findPolicyByID(SQLiteDatabase db, int id) {
		// Select Policy Query
		String selectQuery = "SELECT "+
				getPolicyRulesTableName() + "." + POLRULID + ", " +
				getPolicyRulesTableName() + "." + POLRULNAME + ", " +
				getRequestersTableName() + "." + REQNAME + ", " +
				getResourcesTableName() + "." + RESNAME + ", " +
				getPolicyRulesTableName() + "." + POLRULCNTXT + ", " +
				getPolicyRulesTableName() + "." + POLRULACTIN +
				" FROM " +
				getPolicyRulesTableName() +
				" LEFT JOIN " + getRequestersTableName() +
				" ON " + getPolicyRulesTableName() + "." + POLRULREQID +
				" = " +  getRequestersTableName() + "." + REQID +
				" LEFT JOIN " + getResourcesTableName() +
				" ON " + getPolicyRulesTableName() + "." + POLRULRESID +
				" = " + getResourcesTableName() + "." + RESID +
				" WHERE "  +
				getPolicyRulesTableName() + "." + POLRULID + " = " + id + ";";

		PolicyRule policyRule = new PolicyRule();
		Cursor cursor = db.rawQuery(selectQuery, null);
		try{
			if (cursor.moveToFirst()) {
				policyRule.setId(Integer.parseInt(cursor.getString(0)));
				policyRule.setName(cursor.getString(1));
				policyRule.setRequester(new Requester(cursor.getString(2)));
				policyRule.setResource(new Resource(cursor.getString(3)));

                ArrayList<SemanticIdentity> presenceInfoList = new ArrayList<SemanticIdentity>();
                presenceInfoList.add(new SemanticIdentity(cursor.getString(4)));

				policyRule.setContext(cursor.getString(5));

				if(Integer.parseInt(cursor.getString(6)) == 1)
					policyRule.setAction(new RuleAction(Action.ALLOW));
				else
					policyRule.setAction(new RuleAction(Action.DENY));
			}
		} catch(SQLException e) {
			throw new SQLException("Could not find " + e);
		} finally {
			cursor.close();
		}
		return policyRule;
	}

	/**
	 * Finds a requester based on the requester id
	 * @param db
	 * @param id
	 * @return
	 */
	public Requester findRequesterByID(SQLiteDatabase db, int id) {
		// Select Query
		String selectQuery = "SELECT " +
				getRequestersTableName() + "." + REQID + ", " +
				getRequestersTableName() + "." + REQNAME +
				" FROM " +
				getRequestersTableName() +
				" WHERE "  +
				getRequestersTableName() + "." + REQID + " = " + id + ";";

		Requester requester = new Requester();
		Cursor cursor = db.rawQuery(selectQuery, null);
		try{
			if (cursor.moveToFirst()) {
				requester.setId(Integer.parseInt(cursor.getString(0)));
				requester.setRequesterName(cursor.getString(1));
			}
		} catch(SQLException e) {
			throw new SQLException("Could not find " + e);
		} finally {
			cursor.close();
		}
		return requester;
	}

	/**
	 * Finds a resource based on the resource id
	 * @param db
	 * @param id
	 * @return
	 */
	public Resource findResourceByID(SQLiteDatabase db, int id) {
		// Select Query
		String selectQuery = "SELECT "+
				getResourcesTableName() + "." + RESID + ", " +
				getResourcesTableName() + "." + RESNAME +
				" FROM " +
				getResourcesTableName() +
				" WHERE "  +
				getResourcesTableName() + "." + RESID + " = " + id + ";";

		Resource resource = new Resource();
		Cursor cursor = db.rawQuery(selectQuery, null);
		try{
			if (cursor.moveToFirst()) {
				resource.setId(Integer.parseInt(cursor.getString(0)));
				resource.setResourceName(cursor.getString(1));
			}
		} catch(SQLException e) {
			throw new SQLException("Could not find " + e);
		} finally {
			cursor.close();
		}
		return resource;
	}

	/**
	 * Finds a resource based on the resource id
	 * @param db
	 * @param id
	 * @return
	 */
	public RuleAction findActionByID(SQLiteDatabase db, int id) {
		// Select Query
		String selectQuery = "SELECT "+
				getActionTableName() + "." + ACTIONID + ", " +
				getActionTableName() + "." + ACTION +
				" FROM " +
				getActionTableName() +
				" WHERE "  +
				getActionTableName() + "." + ACTIONID + " = " + id + ";";

		RuleAction action = new RuleAction();
		Cursor cursor = db.rawQuery(selectQuery, null);
		try{
			if (cursor.moveToFirst()) {
				action.setId(Integer.parseInt(cursor.getString(0)));
				if(Integer.parseInt(cursor.getString(1)) == 2)
					action.setAction(Action.ALLOW);
				else if(Integer.parseInt(cursor.getString(1)) == 1)
					action.setAction(Action.ALLOW_WITH_CAVEAT);
				else
					action.setAction(Action.DENY);
			}
		} catch(SQLException e) {
			throw new SQLException("Could not find " + e);
		} finally {
			cursor.close();
		}
		return action;
	}

	/**
	 * Finds a resource based on the resource id
	 * @param db
	 * @param id
	 * @return
	 */
    public SemanticUserContext findContextByID(SQLiteDatabase db, int id) {
        // Select Query
		String selectQuery = "SELECT "+
				getContextTableName() + "." + CONTEXTID + ", " +
				getContextTableName() + "." + PRESENCEINFO + ", " +
				getContextTableName() + "." + ACTIVITY + ", " +
				getContextTableName() + "." + LOCATION + ", " +
                getContextTableName() + "." + IDENTITY + ", " +
                getContextTableName() + "." + TEMPORAL +
				" FROM " +
				getContextTableName() +
				" WHERE "  +
				getContextTableName() + "." + RESID + " = " + id + ";";

        SemanticUserContext userContext = new SemanticUserContext();
        Cursor cursor = db.rawQuery(selectQuery, null);
		try{
			if (cursor.moveToFirst()) {
				userContext.setId(Integer.parseInt(cursor.getString(0)));

                ArrayList<SemanticIdentity> presenceIdList = new ArrayList<SemanticIdentity>();
                presenceIdList.add(new SemanticIdentity(cursor.getString(1)));
                SemanticNearActors semanticNearActors = new SemanticNearActors(presenceIdList);
                userContext.setSemanticNearActors(semanticNearActors);

				userContext.setSemanticActivity(new SemanticActivity(cursor.getString(2)));
				userContext.setSemanticLocation(new SemanticLocation(cursor.getString(3)));
				userContext.setSemanticTime(new SemanticTime(cursor.getString(4)));
			}
		} catch(SQLException e) {
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
		return db.update(getViolationsTableName(), values, VIOLATIONID + " = ?",
				new String[] { String.valueOf(aViolation.getId()) });
	}

	/**
	 * method to update single violation
	 * Update is being removed because of the foreign key constraint as this causes an SQLException during insertion
	 */
	public int updatePolicyRuleContextId(SQLiteDatabase db, PolicyRule aPolicyRule) {
		ContentValues values = new ContentValues();
//		values.put(POLRULID, aPolicyRule.getId());
//		values.put(POLRULNAME, aPolicyRule.getName());
//		values.put(POLRULREQID, aPolicyRule.getRequester().getId());
//		values.put(POLRULRESID, aPolicyRule.getResource().getId());
		values.put(POLRULCNTXT, aPolicyRule.getContext());
//		values.put(POLRULACTID, aPolicyRule.getAction().getId());
		try {
			return db.update(getPolicyRulesTableName(), values, POLRULID + " = ?", new String[] { String.valueOf(aPolicyRule.getId()) });
		} catch(SQLException e) {
			throw new SQLException("Exception " + e + " error updating Context: " + aPolicyRule.getContext());
		}
	}

    /**
     * method to update conflicted Google permissions
     * We found extra information about the Google permissions and we are adding those in the table
     */
    public int updateConflictedGooglePermissions(SQLiteDatabase db, PermData aPermData) {
//        name, protectionlvl, permgrp, flags (these four columns should already be present)
        ContentValues values = new ContentValues();
        values.put(PERMDESC, aPermData.getPermissionDescription());
        values.put(PERMICON, getBitmapAsByteArray(aPermData.getPermissionIcon()));
        values.put(PERMLABEL, aPermData.getPermissionLabel());
        values.put(PERMRESNAME, aPermData.getResource().getResourceName());
        try {
            return db.update(getViolationsTableName(), values, PERMNAME + " = ?",
                    new String[]{String.valueOf(aPermData.getPermissionName())});
        } catch (SQLException e) {
            throw new SQLException("Exception " + e + " error updating permission: " + aPermData.getPermissionName());
        }
    }

	/**
	 * method to delete a row from a table based on the identifier
	 * @param db
	 */
	public void deleteViolation(SQLiteDatabase db, Violation aViolation) {
		try {
			db.delete(getViolationsTableName(), VIOLATIONID + " = ?",
					new String[] { String.valueOf(aViolation.getId()) });
		} catch (SQLException e) {
			throw new SQLException("Could not find " + e);
		}
	}

	/**
	 * Given a certain app uid deletes app from the database
	 *
	 * @param db
	 * @param uid
	 */
	public void deleteAppByUID(SQLiteDatabase db, int uid) {
		try {
            Log.d(MithrilApplication.getDebugTag(), "Deleting this: " + Integer.toString(uid));
            db.delete(getAppDataTableName(), APPUID + " = ?",
					new String[]{String.valueOf(uid)});
		} catch(SQLException e) {
            throw new SQLException("Could not find " + e.getMessage());
        }
	}

	public void deleteRequester(SQLiteDatabase db, Requester aRequester) {
		try {
			db.delete(getRequestersTableName(), REQID + " = ?",
					new String[]{String.valueOf(aRequester.getId()) });
		} catch(SQLException e) {
			throw new SQLException("Could not find " + e);
		}
	}

	public void deleteResource(SQLiteDatabase db, Resource aResource) {
		try {
			db.delete(getResourcesTableName(), RESID + " = ?",
					new String[]{String.valueOf(aResource.getId()) });
		} catch(SQLException e) {
			throw new SQLException("Could not find " + e);
		}
	}

    public void deleteContext(SQLiteDatabase db, SemanticUserContext aUserContext) {
        try {
			db.delete(getContextTableName(), CONTEXTID + " = ?",
					new String[]{String.valueOf(aUserContext.getId()) });
		} catch(SQLException e) {
			throw new SQLException("Could not find " + e);
		}
	}

	public void deleteRuleAction(SQLiteDatabase db, RuleAction aRuleAction) {
		try {
			db.delete(getActionTableName(), ACTIONID + " = ?",
					new String[]{String.valueOf(aRuleAction.getId()) });
		} catch(SQLException e) {
			throw new SQLException("Could not find " + e);
		}
	}

	public void deletePolicyRule(SQLiteDatabase db, PolicyRule aPolicyRule) {
		try {
			db.delete(getPolicyRulesTableName(), POLRULID + " = ?",
					new String[]{String.valueOf(aPolicyRule.getId()) });
		} catch(SQLException e) {
			throw new SQLException("Could not find " + e);
		}
	}

	public void deletePolicyRuleById(SQLiteDatabase db, int aPolicyRuleId) {
		try {
			db.delete(getPolicyRulesTableName(), POLRULID + " = ?",
					new String[]{String.valueOf(aPolicyRuleId) });
		} catch(SQLException e) {
			throw new SQLException("Could not find " + e);
		}
	}

    /**
     * -----------------------------------------------------------------------------------------------------------------------------------------------------------------
     * End of CRUD methods
     */

	public void loadRealAppDataIntoDB(SQLiteDatabase db) {
        PackageManager packageManager = getContext().getPackageManager();
        int flags = PackageManager.GET_META_DATA |
                PackageManager.GET_SHARED_LIBRARY_FILES |
				PackageManager.GET_PERMISSIONS;

		for(PackageInfo pack : packageManager.getInstalledPackages(flags)) {
			if ((pack.applicationInfo.flags) != 1) {
				try {
					AppData tempAppData = new AppData();
					if (pack.packageName != null) {
						//App description
						if(pack.applicationInfo.loadDescription(packageManager) != null)
							tempAppData.setAppDescription(pack.applicationInfo.loadDescription(packageManager).toString());
						else
                            tempAppData.setAppDescription(MithrilApplication.getDefaultDescription());

						//App process name
						tempAppData.setAssociatedProcessName(pack.applicationInfo.processName);

						//App target SDK version
						tempAppData.setTargetSdkVersion(pack.applicationInfo.targetSdkVersion);

						//App icon
						if(pack.applicationInfo.loadIcon(packageManager) instanceof BitmapDrawable)
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
						if((pack.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1)
                            tempAppData.setAppType(MithrilApplication.getPrefKeySystemAppsDisplay());
                        else
                            tempAppData.setAppType(MithrilApplication.getPrefKeyUserAppsDisplay());

						//App uid
						tempAppData.setUid(pack.applicationInfo.uid);

						//App permissions
						String[] requestedPermissions = pack.requestedPermissions;
						if(requestedPermissions != null)
							tempAppData.setPermissions(requestedPermissions);
					}
					//Insert an app into database
					long appId = addAppData(db, tempAppData);

					//Insert permissions for an app into AppPerm
					addAppPerm(db, tempAppData, appId);

//                    long insertedRowId = addAppData(db, tempAppData);
//                    Log.d(MithrilApplication.getDebugTag(), "Inserted record id is: "+Long.toString(insertedRowId));
				} catch (ClassCastException e){
					Log.d(MithrilApplication.getDebugTag(), e.getMessage());
				} catch (Exception e) {
					Log.d(MithrilApplication.getDebugTag(), e.getMessage());
				}
			}
		}
	}

	private void loadAndroidPermissionsIntoDB(SQLiteDatabase db) {
		Log.d(MithrilApplication.getDebugTag(), "I came to loadAndroidPermissionsIntoDB");
        PackageManager packageManager = getContext().getPackageManager();
        int flags = PackageManager.GET_META_DATA;

		List<PermissionGroupInfo> permisisonGroupInfoList = packageManager.getAllPermissionGroups(flags);
		Log.d(MithrilApplication.getDebugTag(), "Size is: " + Integer.toString(permisisonGroupInfoList.size()));
		permisisonGroupInfoList.add(null);

		for (PermissionGroupInfo permissionGroupInfo : permisisonGroupInfoList) {
			String groupName = permissionGroupInfo == null ? null : permissionGroupInfo.name;
			try {
				for (PermissionInfo permissionInfo : packageManager.queryPermissionsByGroup(groupName, 0)) {
                    addPermission(db, getPermData(packageManager, groupName, permissionInfo));
                }
			} catch (PackageManager.NameNotFoundException exception) {
				Log.e(MithrilApplication.getDebugTag(), "Some error due to " + exception.getMessage());
			}
		}
	}

    public PermData getPermData(PackageManager packageManager, String groupName, PermissionInfo permissionInfo) {
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
        if (groupName == null) {
            tempPermData.setPermissionGroup(MithrilApplication.getPermissionNoGroup());
            tempPermData.setResource(new Resource("nada"));
        } else {
            tempPermData.setPermissionGroup(groupName);
            String[] words = permissionInfo.group.split(Pattern.quote("."));
            //In a group, the last word is most important for group identification, so use that I guess!
            //TODO hanging logic! The code for inserting resource isn't done yet. This has to work in tandem with that! Do that ASAP...
            tempPermData.setResource(new Resource(words[words.length - 1]));
        }
        //Setting the protection level
        switch (permissionInfo.flags) {
            case PermissionInfo.FLAG_COSTS_MONEY:
                tempPermData.setPermissionFlag(MithrilApplication.getPermissionFlagCostsMoney());
                break;
            case PermissionInfo.FLAG_INSTALLED:
                tempPermData.setPermissionFlag(MithrilApplication.getPermissionFlagInstalled());
                break;
            default:
                tempPermData.setPermissionProtectionLevel(MithrilApplication.getPermissionFlagNone());
                break;
        }
        //Permission description can be null. We are preventing a null pointer exception here.
        tempPermData.setPermissionDescription(permissionInfo.loadDescription(packageManager)
                == null
                ? context.getResources().getString(R.string.no_description_available_txt)
                : permissionInfo.loadDescription(packageManager).toString());

        tempPermData.setPermissionIcon(getPermissionIconBitmap(permissionInfo));
        tempPermData.setPermissionLabel(permissionInfo.loadLabel(packageManager).toString());

        return tempPermData;
    }

    public PermData getPermData(PackageManager packageManager, String permissionName) {
        PermData tempPermData = new PermData();

        tempPermData.setPermissionName(permissionName);
        //Setting the protection level
        tempPermData.setPermissionProtectionLevel(MithrilApplication.getPermissionProtectionLevelUnknown());
        tempPermData.setPermissionGroup(MithrilApplication.getPermissionNoGroup());
        tempPermData.setResource(new Resource("nada"));
        tempPermData.setPermissionProtectionLevel(MithrilApplication.getPermissionFlagNone());
        tempPermData.setPermissionLabel(permissionName);
        tempPermData.setPermissionIcon(getPermissionIconBitmap());

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
}