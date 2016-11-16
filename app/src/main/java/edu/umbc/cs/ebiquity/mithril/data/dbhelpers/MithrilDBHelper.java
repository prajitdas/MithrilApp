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

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.R;
import edu.umbc.cs.ebiquity.mithril.data.model.AppData;
import edu.umbc.cs.ebiquity.mithril.data.model.PermData;
import edu.umbc.cs.ebiquity.mithril.data.model.Violation;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.PolicyRule;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.actions.Action;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.actions.RuleAction;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.UserContext;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.DeviceTime;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.Identity;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.InferredActivity;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.InferredLocation;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.PresenceInfo;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.protectedresources.Resource;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.requesters.Requester;

public class MithrilDBHelper extends SQLiteOpenHelper {
	// Database declarations
    private final static int DATABASE_VERSION = 1;
    private final static String DATABASE_NAME = MithrilApplication.getConstDatabaseName();

	// Table 1 for Requester information
    private final static String REQID = "id"; // ID of a request
	private final static String REQNAME = "name"; // Name from App table from which a request was received

	// Table 2 for Resource requested information
	private final static String RESID = "id"; // ID of a resource on the device
	// Fields for the database tables
	private final static String RESNAME = "name"; // Meaningful name of the resource on the device

	// Table 3 for User Context information.
    // An entry is made into this table everytime we determine a change in context.
    // This could be where we could do energy efficient stuff as in we can save battery by determining context from historical data or some other way.
	private final static String CONTEXTID = "id"; // ID of the context instance
	private final static String LOCATION = "location"; // Location context
	private final static String IDENTITY = "identity"; // Identity context; this is redundant as because we are working on a single device
	private final static String ACTIVITY = "activity"; // Activity context
	private final static String PRESENCEINFO = "presenceinfo"; // If we could get presence information of others then we can do relationship based privacy solutions
	private final static String TIME = "time"; // Temporal information; the time instance when the current context was captured

	// Table 4 for storing define Policy information
    private final static String POLRULID = "id"; // ID of policy defined
    private final static String POLRULNAME = "name"; // Policy short name
    private final static String POLRULREQID = "reqtrid"; // Requester that sent the request
    private final static String POLRULRESID = "resrcid"; // Resource that was requested
    private final static String POLRULCNTXT = "context"; // Context in which the request was made.
    // This will be a general text that will have to be "reasoned" about!
    // If this says policy is applicable @home then we have to be able to determine that context available represents "home".
    private final static String POLRULACTIN = "action"; // Action will be denoted as: 0 for to deny, 1 for allow

	// Table 5 for Action taken information
    // 0 for denied, 1 for allowed
    // Makes a record everytime an action is taken for a certain requester, resource, context and applicable policy
    // This is the action log table. Stores every action taken whether
	private final static String ACTIONID = "id"; // ID of an action taken
    private final static String ACTIONREQID = "reqtrid"; // Requester that sent the request
	private final static String ACTIONRESID = "resrcid"; // Resource that was requested
    private final static String ACTIONCONID = "contxtid"; // Context in which the request was made
    private final static String ACTIONPRLID = "polrulid"; // Policy rule id from the policy table that was used to determine the action.
    private final static String ACTION = "action"; // Action that was taken for a certain scenario

	// Table 6 for Violation information
	private final static String VIOLATIONID = "id"; // ID of violation captured
	private final static String VIOLATIONDESC = "description"; // An appropriate description of the violation. No idea how this will be generated but
    // we could simply use the requester, resource, context, policy name and action taken as a summary description. We could also concatenate information with the notice that
    // we have a violation of a policy - "policy name". Additionally we could state that
	private final static String VIOLATIONOFRULID = "polrulid"; // policy rule id that was violated
	private final static String VIOLATIONMARKER = "marker";

	// Table 7 for Installed application information
    private final static String APPID = "id"; // ID of an installed app
    // Below columns store information colelcted on the phone about app
	private final static String APPUID = "uid";
    private final static String APPDESCRIPTION = "description";
    private final static String APPASSOCIATEDPROCNAME = "assocprocname";
    private final static String APPTARGETSDKVERSION = "targetSdkVersion";
    private final static String APPICON = "icon";
    private final static String APPNAME = "appName";
    private final static String APPPACKAGENAME = "packageName";
    private final static String APPVERSIONINFO = "versionInfo";
    private final static String APPINSTALLED = "installed"; // boolean value that represents whether an app is installed or not
    private final static String APPTYPE = "type";

	// Table 8 for Permission information
    private final static String PERMID = "id"; // ID of a known permission on the device
    // Once a permission is known, we will get the meta information about them
    private final static String PERMNAME = "name";
    private final static String PERMPROTECTIONLEVEL = "protectionlevel";
	private final static String PERMGROUP = "permgroup";
	private final static String PERMFLAG = "flag";
    private final static String PERMDESC = "description";
    private final static String PERMICON = "icon";
    private final static String PERMLABEL = "label";
    private final static String PERMRESNAME = "resource";

	// Table 9 for App permission
    // This table represents all the apps and their corresponding permissions. We also want to store the association between an app and an api call or a resource access.
    private final static String APPPERMRESID = "id"; // ID for this table
    private final static String APPPERMRESAPPID = "appid"; // ID from resource table
    private final static String APPPERMRESPERID = "permid"; // ID from permission table

	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * Table column definitions complete
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * Following are table names in our database
	 */
	private final static String REQUESTERS_TABLE_NAME = "requesters";
	private final static String RESOURCES_TABLE_NAME = "resources";
	private final static String CONTEXT_TABLE_NAME = "context";
	private final static String ACTION_TABLE_NAME = "actions";
	private final static String POLICY_RULES_TABLE_NAME = "policyrules";
	private final static String VIOLATIONS_TABLE_NAME = "violations";
    private final static String APP_DATA_TABLE_NAME = "appdata";
    private final static String PERMISSIONS_TABLE_NAME = "permissions";
    private final static String APP_PERM_TABLE_NAME = "appperm";
	/**
     * Following are table creation statements
	 */
    private final static String CREATE_APP_DATA_TABLE =  " CREATE TABLE " + getAppDataTableName() + " (" +
            APPID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            APPUID + " INTEGER NOT NULL, " +
            APPDESCRIPTION + " TEXT NOT NULL DEFAULT '*', " +
            APPASSOCIATEDPROCNAME + " TEXT, " +
            APPTARGETSDKVERSION + " TEXT NOT NULL DEFAULT '*', " +
            APPICON + " BLOB, " +
            APPNAME + " TEXT NOT NULL DEFAULT '*', " +
            APPPACKAGENAME + " TEXT UNIQUE NOT NULL DEFAULT '*', " + // Only the package name is unique, rest may repeat
            APPVERSIONINFO + " TEXT NOT NULL DEFAULT '*', " +
            APPINSTALLED + " INTEGER NOT NULL DEFAULT 1, " +
            APPTYPE + " TEXT NOT NULL DEFAULT '*');";

	private final static String CREATE_REQUESTERS_TABLE =  " CREATE TABLE " + getRequestersTableName() + " (" +
			REQID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			REQNAME + " TEXT NOT NULL DEFAULT '*');";

	private final static String CREATE_RESOURCES_TABLE =  " CREATE TABLE " + getResourcesTableName() + " (" +
			RESID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			RESNAME + " TEXT NOT NULL DEFAULT '*');";

	private final static String CREATE_CONTEXT_TABLE =  " CREATE TABLE " + getContextTableName() + " (" +
			CONTEXTID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			LOCATION + " TEXT NOT NULL DEFAULT '*', " +
			IDENTITY + " TEXT NOT NULL DEFAULT 'USER', " +
			ACTIVITY +  " TEXT NOT NULL DEFAULT '*', " +
			PRESENCEINFO +  " TEXT NOT NULL DEFAULT '*', " +
			TIME +  " TEXT NOT NULL DEFAULT '*');";

	private final static String CREATE_POLICY_RULES_TABLE =  " CREATE TABLE " + getPolicyRulesTableName() + " (" +
			POLRULID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			POLRULNAME + " TEXT NOT NULL DEFAULT '*', " +
			POLRULREQID + " INTEGER NOT NULL REFERENCES " + getRequestersTableName() + "(" + REQID + "), " +
			POLRULRESID + " INTEGER NOT NULL REFERENCES " + getResourcesTableName() + "(" + RESID + "), " +
			POLRULCNTXT + " TEXT NOT NULL DEFAULT '*'," +
            POLRULACTIN + " INTEGER NOT NULL DEFAULT 0);";

	private final static String CREATE_ACTION_TABLE =  " CREATE TABLE " + getActionTableName() + " (" +
            ACTIONID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ACTIONREQID + " INTEGER NOT NULL REFERENCES " + getRequestersTableName() + "(" + REQID + "), " +
            ACTIONRESID + " INTEGER NOT NULL REFERENCES " + getResourcesTableName() + "(" + RESID + "), " +
            ACTIONCONID + " INTEGER NOT NULL REFERENCES " + getContextTableName() + "(" + CONTEXTID + "), " +
            ACTIONPRLID + " INTEGER NOT NULL REFERENCES " + getPolicyRulesTableName() + "(" + POLRULID + "), " +
            ACTION + " INTEGER NOT NULL DEFAULT 0);";

	private final static String CREATE_VIOLATIONS_TABLE =  " CREATE TABLE " + getViolationsTableName() + " (" +
			VIOLATIONID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			VIOLATIONDESC + " TEXT NOT NULL DEFAULT '*', " +
			VIOLATIONOFRULID + " INTEGER NOT NULL, " +
			VIOLATIONMARKER + " INTEGER NOT NULL DEFAULT 0, " +
			"CONSTRAINT violationsToPolicyRuleFK " +
			"FOREIGN KEY ("+ VIOLATIONOFRULID +") " +
			"REFERENCES " + MithrilDBHelper.getPolicyRulesTableName() + "(id) " +
			"ON DELETE CASCADE);";

	private final static String CREATE_PERMISSIONS_TABLE =  " CREATE TABLE " + getPermissionsTableName() + " (" +
            PERMID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PERMNAME + " TEXT NOT NULL DEFAULT '*', " +
            PERMPROTECTIONLEVEL + " TEXT NOT NULL DEFAULT '*', " +
            PERMGROUP + " TEXT NOT NULL DEFAULT '*', " +
            PERMDESC + " TEXT NOT NULL DEFAULT '*', " +
            PERMICON + " BLOB, " +
            PERMLABEL + " TEXT NOT NULL DEFAULT '*', " +
            PERMRESNAME + " TEXT NOT NULL DEFAULT '*', " +
            PERMFLAG + " TEXT NOT NULL DEFAULT '*');";

	private final static String CREATE_APP_PERM_TABLE =  " CREATE TABLE " + getAppPermTableName() + " (" +
            APPPERMRESID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            APPPERMRESAPPID + " INTEGER NOT NULL REFERENCES " + getAppDataTableName() + "(" + APPID + "), " +
            APPPERMRESPERID + " INTEGER NOT NULL REFERENCES " + getPermissionsTableName() + "(" + PERMID + "));";

	private Context context;
	private PackageManager packageManager;
	private int flags;

	/**
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * Table creation statements complete
	 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------
	 * Database creation constructor
	 * @param context
	 */
	public MithrilDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.setContext(context);
	}

	private static byte[] getBitmapAsByteArray(Bitmap bitmap) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
		return outputStream.toByteArray();
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
            Log.d(MithrilApplication.getDebugTag(), CREATE_APP_DATA_TABLE);
            db.execSQL(CREATE_APP_DATA_TABLE);
            Log.d(MithrilApplication.getDebugTag(), CREATE_REQUESTERS_TABLE);
            db.execSQL(CREATE_REQUESTERS_TABLE);
            Log.d(MithrilApplication.getDebugTag(), CREATE_RESOURCES_TABLE);
            db.execSQL(CREATE_RESOURCES_TABLE);
            Log.d(MithrilApplication.getDebugTag(), CREATE_CONTEXT_TABLE);
            db.execSQL(CREATE_CONTEXT_TABLE);
            Log.d(MithrilApplication.getDebugTag(), CREATE_POLICY_RULES_TABLE);
            db.execSQL(CREATE_POLICY_RULES_TABLE);
            Log.d(MithrilApplication.getDebugTag(), CREATE_ACTION_TABLE);
            db.execSQL(CREATE_ACTION_TABLE);
            Log.d(MithrilApplication.getDebugTag(), CREATE_VIOLATIONS_TABLE);
            db.execSQL(CREATE_VIOLATIONS_TABLE);
            Log.d(MithrilApplication.getDebugTag(), CREATE_PERMISSIONS_TABLE);
            db.execSQL(CREATE_PERMISSIONS_TABLE);
            Log.d(MithrilApplication.getDebugTag(), CREATE_APP_PERM_TABLE);
            db.execSQL(CREATE_APP_PERM_TABLE);
        } catch (SQLException sqlException) {
            Log.e(MithrilApplication.getDebugTag(), "Following error occurred while inserting data in SQLite DB - "+sqlException.getMessage());
        } catch (Exception e) {
            Log.e(MithrilApplication.getDebugTag(), "Some other error occurred while inserting data in SQLite DB - "+e.getMessage());
        }
		//The following method loads the database with the default dummy data on creation of the database
		//THIS WILL NOT BE USED ANYMORE
//		loadDefaultDataIntoDB(db);
        loadRealAppDataIntoDB(db);
	}

    @Override
	public void onOpen(SQLiteDatabase db) {
		db.execSQL("PRAGMA foreign_keys=ON");
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
        db.execSQL("DROP TABLE IF EXISTS " +  getAppPermTableName());
        db.execSQL("DROP TABLE IF EXISTS " +  getPermissionsTableName());
        db.execSQL("DROP TABLE IF EXISTS " +  getViolationsTableName());
        db.execSQL("DROP TABLE IF EXISTS " +  getActionTableName());
		db.execSQL("DROP TABLE IF EXISTS " +  getPolicyRulesTableName());
        db.execSQL("DROP TABLE IF EXISTS " +  getContextTableName());
        db.execSQL("DROP TABLE IF EXISTS " +  getResourcesTableName());
        db.execSQL("DROP TABLE IF EXISTS " +  getRequestersTableName());
        db.execSQL("DROP TABLE IF EXISTS " +  getAppDataTableName());
		onCreate(db);
	}

	/**
	 * All CRUD(Create, Read, Update, Delete) Operations
	 */
	public long addRequester(SQLiteDatabase db, Requester aRequester) {
		long insertedRowId;
		ContentValues values = new ContentValues();
		values.put(REQNAME, aRequester.getRequesterName());
		try{
			insertedRowId = db.insert(getRequestersTableName(), null, values);
		} catch (SQLException e) {
            Log.e(MithrilApplication.getConstDebugTag(), "Error inserting " + values, e);
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
	        Log.e(MithrilApplication.getConstDebugTag(), "Error inserting " + values, e);
	        return -1;
		}
		return insertedRowId;
	}

	public long addContext(SQLiteDatabase db, UserContext aUserContext) {
		long insertedRowId;
		ContentValues values = new ContentValues();
		values.put(LOCATION, aUserContext.getLocation().toString());
		values.put(IDENTITY, aUserContext.getIdentity().toString());
		values.put(ACTIVITY, aUserContext.getActivity().toString());
		values.put(PRESENCEINFO, aUserContext.getPresenceInfo().toString());
		values.put(TIME, aUserContext.getTime().toString());
		try {
			insertedRowId = db.insert(getContextTableName(), null, values);
		} catch (SQLException e) {
	        Log.e(MithrilApplication.getConstDebugTag(), "Error inserting " + values, e);
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
	        Log.e(MithrilApplication.getConstDebugTag(), "Error inserting " + values, e);
	        return -1;
		}
		return insertedRowId;
	}
	
	public long addAppData(SQLiteDatabase db, AppData anAppData) {
		long insertedRowId;
		ContentValues values = new ContentValues();
		values.put(APPDESCRIPTION, anAppData.getAppDescription());
        if(anAppData.getAssociatedProcessName() != null)
		    values.put(APPASSOCIATEDPROCNAME, anAppData.getAssociatedProcessName());
        values.put(APPTARGETSDKVERSION, anAppData.getTargetSdkVersion());
		values.put(APPICON, getBitmapAsByteArray(anAppData.getIcon()));
		values.put(APPNAME, anAppData.getAppName());
        values.put(APPPACKAGENAME, anAppData.getPackageName());
        values.put(APPVERSIONINFO, anAppData.getVersionInfo());
        if(anAppData.isInstalled())
            values.put(APPINSTALLED, 1);
        else
            values.put(APPINSTALLED, 0);
        values.put(APPTYPE, anAppData.getAppType());
        values.put(APPUID, anAppData.getUid());
		try {
			insertedRowId = db.insert(getAppDataTableName(), null, values);
		} catch (SQLException e) {
	        Log.e(MithrilApplication.getConstDebugTag(), "Error inserting " + values, e);
	        return -1;
		}
		return insertedRowId;
	}

	public long addAppPerm(SQLiteDatabase db, AppData anAppData, long appId) {
        String[] appPermissions = anAppData.getPermissions();
        long insertedRowId = -1;
        ContentValues values = new ContentValues();
        values.put(APPPERMRESAPPID, appId);
        for(int permIdx = 0; permIdx < appPermissions.length; permIdx++) {
            values.put(APPPERMRESPERID, findPermissionsByName(db, appPermissions[permIdx]));
            try {
                insertedRowId = db.insert(getAppPermTableName(), null, values);
            } catch (SQLException e) {
                Log.e(MithrilApplication.getConstDebugTag(), "Error inserting " + values, e);
                return -1;
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
			Log.e(MithrilApplication.getConstDebugTag(), "Error inserting " + values, e);
			return -1;
		}
		return insertedRowId;
	}

	//TODO We have to do a join across 3 tables and return the permissions for an app
    /**
     * Temporary solution setup but eventually we will the join and populate with data from our servers
     * @param db
     * @param appName
     * @return List of PermData objects
     */
	public List<PermData> getAppPermissions(SQLiteDatabase db, String appName) {
		List<PermData> permDataList = new ArrayList<PermData>();
		return permDataList;
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
            Log.e(MithrilApplication.getConstDebugTag(), "Error inserting " + values, e);
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
		values.put(VIOLATIONOFRULID, aViolation.getRuleId());
		if(aViolation.isViolationMarker())
			values.put(VIOLATIONMARKER, 1);
		else
			values.put(VIOLATIONMARKER, 0);
		try{
			insertedRowId = db.insert(getViolationsTableName(), null, values);
		} catch (SQLException e) {
            Log.e(MithrilApplication.getConstDebugTag(), "Error inserting " + values, e);
            return -1;
		}
		return insertedRowId;
	}

	/**
	 * Finds a violation based on the ruleid
	 * @param db
	 * @return
	 */
	public Violation findViolationByPolRulId(SQLiteDatabase db, int ruleid) {
		// Select Violation Query
		String selectQuery = "SELECT * FROM " + getViolationsTableName() +
					" WHERE " + getViolationsTableName() + "." + VIOLATIONOFRULID + " = " + ruleid + ";";
		Violation aViolation = null;
		try{
			Cursor cursor = db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				/**
				 * The policy id is always zero as the policy for the current user cannot change and therefore the policy is insignificant.
				 * The rules however keep on changing for the current user so the rule id is important.
				 */
				if(Integer.parseInt(cursor.getString(3)) == 0)
					aViolation = new Violation(Integer.parseInt(cursor.getString(0)),cursor.getString(1),0,Integer.parseInt(cursor.getString(2)),false);
				else
					aViolation = new Violation(Integer.parseInt(cursor.getString(0)),cursor.getString(1),0,Integer.parseInt(cursor.getString(2)),true);
			}
		} catch(SQLException e) {
            throw new SQLException("Could not find " + e);
		}
		return aViolation;
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
		try{
			Cursor cursor = db.rawQuery(selectQuery, null);
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
                } while(cursor.moveToNext());
            }
		} catch(SQLException e) {
			throw new SQLException("Could not find " + e);
		}
		return apps;
	}

    /**
     * Finds app by name
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
        try{
            Cursor cursor = db.rawQuery(selectQuery, null);
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
        } catch(SQLException e) {
            throw new SQLException("Could not find " + e);
        }
        return app;
    }

    /**
     * Finds app by id
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
        try{
            Cursor cursor = db.rawQuery(selectQuery, null);
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
        } catch(SQLException e) {
            throw new SQLException("Could not find " + e);
        }
        return app;
    }

    /**
     * Finds permissions by name
     * @param db
     * @return permissionName
     */
    public long findPermissionsByName(SQLiteDatabase db, String permissionName) {
        // Select AppData Query
        String selectQuery = "SELECT " +
                getPermissionsTableName() + "." + PERMID +
                " FROM " + getPermissionsTableName() +
                " WHERE " + getPermissionsTableName() + "." + PERMNAME +
                " = " + permissionName +
                ";";

        long permId = -1;
        try{
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                permId = Integer.parseInt(cursor.getString(0));
            }
        } catch(SQLException e) {
            throw new SQLException("Could not find " + e);
        }
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
				getViolationsTableName() + "." + VIOLATIONOFRULID + ", " +
				getViolationsTableName() + "." + VIOLATIONMARKER +
					" FROM " + getViolationsTableName() + ";";

		List<Violation> violations = new ArrayList<Violation>();
		try{
			Cursor cursor = db.rawQuery(selectQuery, null);
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
				} while(cursor.moveToNext());
			}
		} catch(SQLException e) {
            throw new SQLException("Could not find " + e);
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
				getViolationsTableName() + "." + VIOLATIONOFRULID + ", " +
				getViolationsTableName() + "." + VIOLATIONMARKER +
				" FROM " + getViolationsTableName() +
				" WHERE " + getViolationsTableName() + "." + VIOLATIONMARKER + " = 0;";

		List<Violation> violations = new ArrayList<Violation>();
		try{
			Cursor cursor = db.rawQuery(selectQuery, null);
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
				} while(cursor.moveToNext());
			}
		} catch(SQLException e) {
            throw new SQLException("Could not find " + e);
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

		try{
			Cursor cursor = db.rawQuery(selectQuery, null);

			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					PolicyRule policyRule = new PolicyRule();
					policyRule.setId(Integer.parseInt(cursor.getString(0)));
					policyRule.setName(cursor.getString(1));
					policyRule.setRequester(new Requester(cursor.getString(2)));
					policyRule.setResource(new Resource(cursor.getString(3)));

//					ArrayList<Identity> presenceInfoList = new ArrayList<Identity>();
//					presenceInfoList.add(new Identity(cursor.getString(4)));

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

		try{
			Cursor cursor = db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				policyRule.setId(Integer.parseInt(cursor.getString(0)));
				policyRule.setName(cursor.getString(1));
				policyRule.setRequester(new Requester(cursor.getString(2)));
				policyRule.setResource(new Resource(cursor.getString(3)));

				ArrayList<Identity> presenceInfoList = new ArrayList<Identity>();
				presenceInfoList.add(new Identity(cursor.getString(4)));

				policyRule.setContext(cursor.getString(5));

				if(Integer.parseInt(cursor.getString(6)) == 1)
					policyRule.setAction(new RuleAction(Action.ALLOW));
				else
					policyRule.setAction(new RuleAction(Action.DENY));
			}
		} catch(SQLException e) {
            throw new SQLException("Could not find " + e);
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

		try{
			Cursor cursor = db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				policyRule.setId(Integer.parseInt(cursor.getString(0)));
				policyRule.setName(cursor.getString(1));
				policyRule.setRequester(new Requester(cursor.getString(2)));
				policyRule.setResource(new Resource(cursor.getString(3)));

				ArrayList<Identity> presenceInfoList = new ArrayList<Identity>();
				presenceInfoList.add(new Identity(cursor.getString(4)));

				policyRule.setContext(cursor.getString(5));

				if(Integer.parseInt(cursor.getString(6)) == 1)
					policyRule.setAction(new RuleAction(Action.ALLOW));
				else
					policyRule.setAction(new RuleAction(Action.DENY));
			}
		} catch(SQLException e) {
            throw new SQLException("Could not find " + e);
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

		try{
			Cursor cursor = db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				requester.setId(Integer.parseInt(cursor.getString(0)));
				requester.setRequesterName(cursor.getString(1));
			}
		} catch(SQLException e) {
            throw new SQLException("Could not find " + e);
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

		try{
			Cursor cursor = db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				resource.setId(Integer.parseInt(cursor.getString(0)));
				resource.setResourceName(cursor.getString(1));
			}
		} catch(SQLException e) {
            throw new SQLException("Could not find " + e);
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

		try{
			Cursor cursor = db.rawQuery(selectQuery, null);
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
		}
		return action;
	}

	/**
	 * Finds a resource based on the resource id
	 * @param db
	 * @param id
	 * @return
	 */
	public UserContext findContextByID(SQLiteDatabase db, int id) {
		// Select Query
		String selectQuery = "SELECT "+
				getContextTableName() + "." + CONTEXTID + ", " +
				getContextTableName() + "." + PRESENCEINFO + ", " +
				getContextTableName() + "." + ACTIVITY + ", " +
				getContextTableName() + "." + LOCATION + ", " +
				getContextTableName() + "." + IDENTITY + ", " +
				getContextTableName() + "." + TIME +
				" FROM " +
				getContextTableName() +
				" WHERE "  +
				getContextTableName() + "." + RESID + " = " + id + ";";

		UserContext userContext = new UserContext();

		try{
			Cursor cursor = db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
                userContext.setId(Integer.parseInt(cursor.getString(0)));

				ArrayList<Identity> presenceIdList = new ArrayList<Identity>();
				presenceIdList.add(new Identity(cursor.getString(1)));
				PresenceInfo presenceInfo = new PresenceInfo(presenceIdList);
                userContext.setPresenceInfo(presenceInfo);

                userContext.setActivity(new InferredActivity(cursor.getString(2)));
                userContext.setLocation(new InferredLocation(cursor.getString(3)));
                userContext.setTime(new DeviceTime(cursor.getString(4)));
			}
		} catch(SQLException e) {
            throw new SQLException("Could not find " + e);
		}
		return userContext;
	}

	/**
	 * method to update single violation
	 */
	public int updateViolationAsTrue(SQLiteDatabase db, Violation aViolation) {
		ContentValues values = new ContentValues();
		values.put(VIOLATIONDESC, aViolation.getViolationDescription());
		values.put(VIOLATIONOFRULID, aViolation.getRuleId());
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
	 * method to delete a row from a table based on the identifier
	 * @param db
	 */
	public void deleteViolation(SQLiteDatabase db, Violation aViolation) {
		try {
			db.delete(getViolationsTableName(), VIOLATIONID + " = ?",
					new String[] { String.valueOf(aViolation.getId()) });
		} catch(SQLException e) {
			throw new SQLException("Could not find " + e);
		}
	}

    /**
     * Given a certain app uid deletes app from the database
     * @param db
     * @param uid
     */
    public void deleteAppByUID(SQLiteDatabase db, int uid) {
        try {
            db.delete(getAppDataTableName(), APPUID + " = ?",
                    new String[] { String.valueOf(uid) });
        } catch(SQLException e) {
            throw new SQLException("Could not find " + e);
        }
    }

	/**
	 * Given a certain policy id, deletes all violations related to that.
	 * Used when a particular policy is being deleted or modified
	 * Could get complicated if a single policy can have multiple violations which we are assuming at the moment to not be possible
	 * @param db
	 * @param policyId
	 */
	public void deleteAllViolationsForPoliCyRuleId(SQLiteDatabase db, int policyId) {
		try {
			db.delete(getViolationsTableName(), VIOLATIONOFRULID + " = ?",
					new String[] { String.valueOf(policyId) });
		} catch(SQLException e) {
			throw new SQLException("Could not find " + e);
		}
	}

	public void deleteRequester(SQLiteDatabase db, Requester aRequester) {
		try {
			db.delete(getRequestersTableName(), REQID + " = ?",
					new String[] { String.valueOf(aRequester.getId()) });
		} catch(SQLException e) {
	        throw new SQLException("Could not find " + e);
		}
	}

	public void deleteResource(SQLiteDatabase db, Resource aResource) {
		try {
		db.delete(getResourcesTableName(), RESID + " = ?",
				new String[] { String.valueOf(aResource.getId()) });
		} catch(SQLException e) {
	        throw new SQLException("Could not find " + e);
		}
	}

	public void deleteContext(SQLiteDatabase db, UserContext aUserContext) {
		try {
			db.delete(getContextTableName(), CONTEXTID + " = ?",
					new String[] { String.valueOf(aUserContext.getId()) });
		} catch(SQLException e) {
	        throw new SQLException("Could not find " + e);
		}
	}

	public void deleteRuleAction(SQLiteDatabase db, RuleAction aRuleAction) {
		try {
			db.delete(getActionTableName(), ACTIONID + " = ?",
					new String[] { String.valueOf(aRuleAction.getId()) });
		} catch(SQLException e) {
	        throw new SQLException("Could not find " + e);
		}
	}

	public void deletePolicyRule(SQLiteDatabase db, PolicyRule aPolicyRule) {
		try {
			db.delete(getPolicyRulesTableName(), POLRULID + " = ?",
				new String[] { String.valueOf(aPolicyRule.getId()) });
		} catch(SQLException e) {
            throw new SQLException("Could not find " + e);
		}
	}

	public void deletePolicyRuleById(SQLiteDatabase db, int aPolicyRuleId) {
		try {
			db.delete(getPolicyRulesTableName(), POLRULID + " = ?",
					new String[] { String.valueOf(aPolicyRuleId) });
		} catch(SQLException e) {
            throw new SQLException("Could not find " + e);
		}
	}

    /**
	 * method to load the default set of policies into the database
	 * THIS WILL NOT BE USED ANYMORE
	 * @param db reference to the db instance
	private void loadDefaultDataIntoDB(SQLiteDatabase db) {
        Requester requester = new Requester(findAppById(db, 1).getAppName());
        //load one requester
        addRequester(db, requester);
		//loads requesters
		for(Requester aRequester : DataGenerator.generateRequesters())
			addRequester(db, aRequester);
		//loads resources
		for(Resource aResource : DataGenerator.generateResources())
			addResource(db, aResource);
		//loads actions
		addRuleAction(db, DataGenerator.generateAllowAction());
		addRuleAction(db, DataGenerator.generateAllowWithCaveatAction());
		addRuleAction(db, DataGenerator.generateDenyAction());
		//loads context
		addContext(db, DataGenerator.generateContextForSocialMediaCameraAccessRule());
		//loads policy
		addPolicyRule(db, DataGenerator.generateSocialMediaCameraAccessRule(
				findRequesterByID(db, 1),
				findResourceByID(db, 1),
//				findContextByID(db, 1),// We changed this to textual context as we have to use reasoning to carry out this task
                "home",
				findActionByID(db, 1)
				));
		//loads violations
		for(PolicyRule policyRule : findAllPolicies(db))
			addViolation(db, new Violation(policyRule.toString(), 1, policyRule.getId(), false));
    }
	 */

	public void loadRealAppDataIntoDB(SQLiteDatabase db) {
		packageManager = getContext().getPackageManager();
		flags = PackageManager.GET_META_DATA |
				PackageManager.GET_SHARED_LIBRARY_FILES |
				PackageManager.GET_PERMISSIONS;

		//Load all the permissions that are known for Android into the database. We will refer to them in the future.
		loadAndroidPermissionsIntoDB(db);

		for(PackageInfo pack : packageManager.getInstalledPackages(flags)) {
			if ((pack.applicationInfo.flags) != 1) {
				try {
					AppData tempAppData = new AppData();
					if (pack.packageName != null) {
						//App description
						if(pack.applicationInfo.loadDescription(packageManager) != null)
							tempAppData.setAppDescription(pack.applicationInfo.loadDescription(packageManager).toString());
						else
							tempAppData.setAppDescription(MithrilApplication.getConstDefaultDescription());

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
							tempAppData.setAppType(MithrilApplication.getSystemAppsDisplayTag());
						else
							tempAppData.setAppType(MithrilApplication.getUserAppsDisplayTag());

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
		List<PermissionGroupInfo> permisisonGroupInfoList = packageManager.getAllPermissionGroups(flags);
		permisisonGroupInfoList.add(null);

        for(PermissionGroupInfo permissionGroupInfo : permisisonGroupInfoList) {
            String groupName = permissionGroupInfo == null ? null : permissionGroupInfo.name;
            try {
                for(PermissionInfo permissionInfo : packageManager.queryPermissionsByGroup(groupName, 0)) {
                    PermData tempPermData = new PermData();

                    tempPermData.setPermissionName(permissionInfo.name);
                    //Setting the protection level
                    switch(permissionInfo.protectionLevel) {
                        /**
                         * Colors from: https://design.google.com/articles/evolving-the-google-identity/
                         */
                        case PermissionInfo.PROTECTION_NORMAL:
                            tempPermData.setPermissionProtectionLevel("normal");
                            break;
                        case PermissionInfo.PROTECTION_DANGEROUS:
                            tempPermData.setPermissionProtectionLevel("dangerous");
                            break;
                        case PermissionInfo.PROTECTION_SIGNATURE:
                            tempPermData.setPermissionProtectionLevel("signature");
                            break;
                        case PermissionInfo.PROTECTION_FLAG_PRIVILEGED:
                            tempPermData.setPermissionProtectionLevel("privileged");
                            break;
                        default:
                            tempPermData.setPermissionProtectionLevel("unknown");
                            break;
                    }
					if (groupName == null)
						tempPermData.setPermissionGroup("No group");
					else
						tempPermData.setPermissionGroup(groupName);
					//Setting the protection level
                    switch(permissionInfo.flags) {
                        case PermissionInfo.FLAG_COSTS_MONEY:
                            tempPermData.setPermissionFlag("costs-money");
                            break;
                        case PermissionInfo.FLAG_INSTALLED:
                            tempPermData.setPermissionFlag("installed");
                            break;
                        default:
                            tempPermData.setPermissionProtectionLevel("no-flags");
                            break;
                    }
                    //Permission description can be null. We are preventing a null pointer exception here.
                    tempPermData.setPermissionDescription(permissionInfo.loadDescription(packageManager)
                            == null
                            ? context.getResources().getString(R.string.no_description_available_txt)
                            : permissionInfo.loadDescription(packageManager).toString());

//                    tempPermData.setPermissionIcon(((BitmapDrawable) permissionInfo.loadIcon(packageManager)).getBitmap());
                    tempPermData.setPermissionIcon(getPermissionIconBitmap(permissionInfo));
                    //TODO this is bad!!! figure out how to get the resource
                    tempPermData.setPermissionLabel("label");
//                    tempPermData.setPermissionLabel(context.getResources().getString(permissionInfo.labelRes));
                    tempPermData.setResource(new Resource("camera"));

                    addPermission(db, tempPermData);
                }
            } catch (PackageManager.NameNotFoundException exception) {
                Log.e(MithrilApplication.getDebugTag(), "Some error due to "+exception.getMessage());
            }
        }
	}

    private Bitmap getPermissionIconBitmap(PermissionInfo permissionInfo) {
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
}