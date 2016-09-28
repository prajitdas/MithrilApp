package edu.umbc.cs.ebiquity.mithril.data.helpers;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.data.dataloaders.DataGenerator;
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
	
	// Fields for the database tables
	// Table for Requester information
	private final static String REQID = "id"; // ID of a request
	private final static String REQNAME = "name"; // ID from App table from which a request was received

    // Table for Resource requested information
	private final static String RESID = "id"; // ID of a resource on the device
	private final static String RESNAME = "name"; // Meaningful name of the resource on the device

    // Table for User Context information
	private final static String CONTEXTID = "id"; // ID of the context instance
	private final static String LOCATION = "location"; // Location context
	private final static String IDENTITY = "identity"; // Identity context; this is redundant as because we are working on a single device
	private final static String ACTIVITY = "activity"; // Activity context
	private final static String PRESENCEINFO = "presenceinfo"; // If we could get presence information of others then we can do relationship based privacy solutions
	private final static String TIME = "time"; // Temporal information; the time instance when the current context was captured

    // Table for Action taken information
    // 0 for denied, 1 for allowed
	private final static String ACTIONID = "id"; // ID of an action taken
	private final static String ACTION = "action"; // Action taken for a certain scenario
    private final static String ACTIONRESID = "resrcid"; // Resource that was requested
    private final static String ACTIONREQID = "reqtrid"; // Requester that sent the request
    private final static String ACTIONCONID = "contxtid"; // Context in which the request was made

    // Table for Policy information
	private final static String POLRULID = "id"; // ID of policy defined
	private final static String POLRULNAME = "name"; // Policy short name
	private final static String POLRULREQID = "reqtrid"; // Requester that sent the request
	private final static String POLRULRESID = "resrcid"; // Resource that was requested
	private final static String POLRULCNTID = "contxtid"; // Context in which the request was made
	private final static String POLRULACTID = "actionid"; // Action will be denoted as: 0 for to deny, 1 for allow

    // Table for Violation information
	private final static String VIOLATIONID = "id"; // ID of violation captured
	private final static String VIOLATIONDESC = "description"; //
	private final static String VIOLATIONOFRULID = "ruleid";
	private final static String VIOLATIONMARKER = "marker";

    // Table for Installed application information
    private final static String APPDESCRIPTION = "description";
    private final static String APPASSOCIATEDPROCNAME = "assocprocname";
    private final static String APPTARGETSDKVERSION = "targetSdkVersion";
    private final static String APPICON = "icon";
    private final static String APPNAME = "appName";
    private final static String APPPACKAGENAME = "packageName";
    private final static String APPVERSIONINFO = "versionInfo";

    // Table for Permission information
    private final static String PERMNAME = "name";
    private final static String PERMPROTECTIONLEVEL = "protectionlevel";
    private final static String PERMGROUP = "permissiongroup";
    private final static String PERMFLAG = "permissionflag";

    // Table for App permission
    private final static String APPPERMAPPID = "appid";
    private final static String APPPERMPERMID = "permid";

	// database declarations
	private final static int DATABASE_VERSION = 1;

	private final static String REQUESTERS_TABLE_NAME = "requesters";
	private final static String RESOURCES_TABLE_NAME = "resources";
	private final static String CONTEXT_TABLE_NAME = "context";
	private final static String ACTION_TABLE_NAME = "actions";
	private final static String POLICY_RULES_TABLE_NAME = "policyrules";
	private final static String VIOLATIONS_TABLE_NAME = "violations";
	private final static String APP_DATA_TABLE_NAME = "appdata";
    private final static String PERMISSIONS_TABLE_NAME = "permissions";
    private final static String APP_PERMISSIONS_TABLE_NAME = "appperm";

	private Context context;
	
	/**
	 * Table creation statements
	 */	
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
	
	private final static String CREATE_ACTION_TABLE =  " CREATE TABLE " + getActionTableName() + " (" +
			ACTIONID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
			ACTION + " TEXT NOT NULL DEFAULT 'grant');";
	
	private final static String CREATE_POLICY_RULES_TABLE =  " CREATE TABLE " + getPolicyRulesTableName() + " (" +
			POLRULID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
			POLRULNAME + " TEXT NOT NULL DEFAULT '*', " + 
			POLRULREQID + " INTEGER NOT NULL REFERENCES " + getRequestersTableName() + "(" + REQID + "), " +
			POLRULRESID + " INTEGER NOT NULL REFERENCES " + getResourcesTableName() + "(" + RESID + "), " +
			POLRULCNTID + " INTEGER NOT NULL REFERENCES " + getContextTableName() + "(" + CONTEXTID + "), " +
			POLRULACTID + " INTEGER NOT NULL REFERENCES " + getActionTableName() + "(" + ACTIONID + ") "
					+ ");";
	
	private final static String CREATE_VIOLATIONS_TABLE =  " CREATE TABLE " + getViolationsTableName() + " (" +
			VIOLATIONID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
			VIOLATIONDESC + " TEXT NOT NULL DEFAULT '*', " + 
			VIOLATIONOFRULID + " INTEGER NOT NULL, " +
			VIOLATIONMARKER + " INTEGER NOT NULL DEFAULT 0, " +
			"CONSTRAINT violationsToPolicyRuleFK " +
			"FOREIGN KEY ("+ VIOLATIONOFRULID +") " +
			"REFERENCES " + MithrilDBHelper.getPolicyRulesTableName() + "(id) " +
			"ON DELETE CASCADE);";
	
	/**
	 * Database creation constructor
	 * @param context
	 */
	public MithrilDBHelper(Context context) {
		super(context, MithrilApplication.getConstDatabaseName(), null, DATABASE_VERSION);
		this.setContext(context); 
	}

	public String getDatabaseName() {
		return MithrilApplication.getConstDatabaseName();
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
	public static int getDatabaseVersion() {
		return DATABASE_VERSION;
	}

	/**
	 * Table creation happens in onCreate this method also loads the default data
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_REQUESTERS_TABLE);
		db.execSQL(CREATE_RESOURCES_TABLE);
		db.execSQL(CREATE_CONTEXT_TABLE);
		db.execSQL(CREATE_ACTION_TABLE);
		db.execSQL(CREATE_POLICY_RULES_TABLE);
		db.execSQL(CREATE_VIOLATIONS_TABLE);
		//The following method loads the database with the default data on creation of the database
		loadDefaultDataIntoDB(db);
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
		db.execSQL("DROP TABLE IF EXISTS " +  getViolationsTableName());
		db.execSQL("DROP TABLE IF EXISTS " +  getPolicyRulesTableName());
		db.execSQL("DROP TABLE IF EXISTS " +  getRequestersTableName());
		db.execSQL("DROP TABLE IF EXISTS " +  getResourcesTableName());
		db.execSQL("DROP TABLE IF EXISTS " +  getContextTableName());
		db.execSQL("DROP TABLE IF EXISTS " +  getActionTableName());
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

	public long addPolicyRule(SQLiteDatabase db, PolicyRule aPolicyRule) {
		long insertedRowId;
		ContentValues values = new ContentValues();
		values.put(POLRULNAME, aPolicyRule.getName());
		values.put(POLRULREQID, aPolicyRule.getRequester().getId());
		values.put(POLRULRESID, aPolicyRule.getResource().getId());
		values.put(POLRULCNTID, aPolicyRule.getContext().getId());
		values.put(POLRULACTID, aPolicyRule.getAction().getId());
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
	 * Finds all violations
	 * @param db
	 * @return
	 */
	public List<Violation> findAllViolations(SQLiteDatabase db) {
		// Select Violation Query
		String selectQuery = "SELECT " +
				getViolationsTableName() + "." + VIOLATIONID + "," +
				getViolationsTableName() + "." + VIOLATIONDESC + "," +
				getViolationsTableName() + "." + VIOLATIONOFRULID + "," +
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
				getViolationsTableName() + "." + VIOLATIONID + "," +
				getViolationsTableName() + "." + VIOLATIONDESC + "," +
				getViolationsTableName() + "." + VIOLATIONOFRULID + "," +
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
				getPolicyRulesTableName() + "." + POLRULID + "," +
				getPolicyRulesTableName() + "." + POLRULNAME + "," +
				getRequestersTableName() + "." + REQNAME + "," +
				getResourcesTableName() + "." + RESNAME + "," +
				getContextTableName() + "." + PRESENCEINFO + "," +
				getContextTableName() + "." + ACTIVITY + "," +
				getContextTableName() + "." + IDENTITY + "," +
				getContextTableName() + "." + LOCATION + "," +
				getContextTableName() + "." + TIME + "," +
				getActionTableName() + "." + ACTION + 
				" FROM " + 
				getPolicyRulesTableName() +
				" LEFT JOIN " + getRequestersTableName() + 
				" ON " + getPolicyRulesTableName() + "." + POLRULREQID + 
				" = " +  getRequestersTableName() + "." + REQID +
				" LEFT JOIN " + getResourcesTableName() + 
				" ON " + getPolicyRulesTableName() + "." + POLRULRESID + 
				" = " +  getResourcesTableName() + "." + RESID + 
				" LEFT JOIN " + getContextTableName() + 
				" ON " + getPolicyRulesTableName() + "." + POLRULCNTID + 
				" = " +  getContextTableName() + "." + CONTEXTID + 
				" LEFT JOIN " + getActionTableName() + 
				" ON " + getPolicyRulesTableName() + "." + POLRULACTID + 
				" = " +  getActionTableName() + "." + ACTIONID + ";";

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
					
					ArrayList<Identity> presenceInfoList = new ArrayList<Identity>();
					presenceInfoList.add(new Identity(cursor.getString(4)));
					
					UserContext contextCondition = new UserContext(
							new PresenceInfo(presenceInfoList), 
							new InferredActivity(cursor.getString(5)),
							new Identity(cursor.getString(6)),
							new InferredLocation(cursor.getString(7)),
							new DeviceTime(cursor.getString(8)));
					
					policyRule.setContext(contextCondition);
					
					if(Integer.parseInt(cursor.getString(9)) == 2)
						policyRule.setAction(new RuleAction(Action.ALLOW));
					else if(Integer.parseInt(cursor.getString(9)) == 1)
						policyRule.setAction(new RuleAction(Action.ALLOW_WITH_CAVEAT));
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
				getPolicyRulesTableName() + "." + POLRULID + "," +
				getPolicyRulesTableName() + "." + POLRULNAME + "," +
				getRequestersTableName() + "." + REQNAME + "," +
				getResourcesTableName() + "." + RESNAME + "," +
				getContextTableName() + "." + PRESENCEINFO + "," +
				getContextTableName() + "." + ACTIVITY + "," +
				getContextTableName() + "." + IDENTITY + "," +
				getContextTableName() + "." + LOCATION + "," +
				getContextTableName() + "." + TIME + "," +
				getActionTableName() + "." + ACTION + 
				" FROM " + 
				getPolicyRulesTableName() +
				" LEFT JOIN " + getRequestersTableName() + 
				" ON " + getPolicyRulesTableName() + "." + POLRULREQID + 
				" = " +  getRequestersTableName() + "." + REQID +
				" LEFT JOIN " + getResourcesTableName() + 
				" ON " + getPolicyRulesTableName() + "." + POLRULRESID + 
				" = " +  getResourcesTableName() + "." + RESID + 
				" LEFT JOIN " + getContextTableName() + 
				" ON " + getPolicyRulesTableName() + "." + POLRULCNTID + 
				" = " +  getContextTableName() + "." + CONTEXTID + 
				" LEFT JOIN " + getActionTableName() + 
				" ON " + getPolicyRulesTableName() + "." + POLRULACTID + 
				" = " +  getActionTableName() + "." + ACTIONID + 
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
				
				UserContext contextCondition = new UserContext(
						new PresenceInfo(presenceInfoList), 
						new InferredActivity(cursor.getString(5)),
						new Identity(cursor.getString(6)),
						new InferredLocation(cursor.getString(7)),
						new DeviceTime(cursor.getString(8)));
				
				policyRule.setContext(contextCondition);
				
				if(Integer.parseInt(cursor.getString(9)) == 2)
					policyRule.setAction(new RuleAction(Action.ALLOW));
				else if(Integer.parseInt(cursor.getString(9)) == 1)
					policyRule.setAction(new RuleAction(Action.ALLOW_WITH_CAVEAT));
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
				getPolicyRulesTableName() + "." + POLRULID + "," +
				getPolicyRulesTableName() + "." + POLRULNAME + "," +
				getRequestersTableName() + "." + REQNAME + "," +
				getResourcesTableName() + "." + RESNAME + "," +
				getContextTableName() + "." + PRESENCEINFO + "," +
				getContextTableName() + "." + ACTIVITY + "," +
				getContextTableName() + "." + IDENTITY + "," +
				getContextTableName() + "." + LOCATION + "," +
				getContextTableName() + "." + TIME + "," +
				getActionTableName() + "." + ACTION + 
				" FROM " + 
				getPolicyRulesTableName() +
				" LEFT JOIN " + getRequestersTableName() + 
				" ON " + getPolicyRulesTableName() + "." + POLRULREQID + 
				" = " +  getRequestersTableName() + "." + REQID +
				" LEFT JOIN " + getResourcesTableName() + 
				" ON " + getPolicyRulesTableName() + "." + POLRULRESID + 
				" = " +  getResourcesTableName() + "." + RESID + 
				" LEFT JOIN " + getContextTableName() + 
				" ON " + getPolicyRulesTableName() + "." + POLRULCNTID + 
				" = " +  getContextTableName() + "." + CONTEXTID + 
				" LEFT JOIN " + getActionTableName() + 
				" ON " + getPolicyRulesTableName() + "." + POLRULACTID + 
				" = " +  getActionTableName() + "." + ACTIONID + 
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
				
				UserContext contextCondition = new UserContext(
						new PresenceInfo(presenceInfoList), 
						new InferredActivity(cursor.getString(5)),
						new Identity(cursor.getString(6)),
						new InferredLocation(cursor.getString(7)),
						new DeviceTime(cursor.getString(8)));
				
				policyRule.setContext(contextCondition);
				
				if(Integer.parseInt(cursor.getString(9)) == 2)
					policyRule.setAction(new RuleAction(Action.ALLOW));
				else if(Integer.parseInt(cursor.getString(9)) == 1)
					policyRule.setAction(new RuleAction(Action.ALLOW_WITH_CAVEAT));
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
				getRequestersTableName() + "." + REQID + "," +
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
				getResourcesTableName() + "." + RESID + "," +
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
				getActionTableName() + "." + ACTIONID + "," +
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
				getContextTableName() + "." + CONTEXTID + "," +
				getContextTableName() + "." + PRESENCEINFO + "," +
				getContextTableName() + "." + ACTIVITY + "," +
				getContextTableName() + "." + LOCATION + "," +
				getContextTableName() + "." + IDENTITY + "," +
				getContextTableName() + "." + TIME + 
				" FROM " + 
				getContextTableName() + 
				" WHERE "  +   
				getContextTableName() + "." + RESID + " = " + id + ";";

		UserContext context = new UserContext();
		
		try{
			Cursor cursor = db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				context.setId(Integer.parseInt(cursor.getString(0)));

				ArrayList<Identity> presenceIdList = new ArrayList<Identity>();
				presenceIdList.add(new Identity(cursor.getString(1)));
				PresenceInfo presenceInfo = new PresenceInfo(presenceIdList);
				context.setPresenceInfo(presenceInfo);
				
				context.setActivity(new InferredActivity(cursor.getString(2)));
				context.setLocation(new InferredLocation(cursor.getString(3)));
				context.setTime(new DeviceTime(cursor.getString(4)));
			}
		} catch(SQLException e) {
            throw new SQLException("Could not find " + e);
		}
		return context;
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
		values.put(POLRULCNTID, aPolicyRule.getContext().getId());
//		values.put(POLRULACTID, aPolicyRule.getAction().getId());
		try {
			return db.update(getPolicyRulesTableName(), values, POLRULID + " = ?", new String[] { String.valueOf(aPolicyRule.getId()) });
		} catch(SQLException e) {
			throw new SQLException("Exception " + e + " error updating Context ID: " + aPolicyRule.getContext().getId());
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
	 * Table name getters
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
	
	/**
	 * method to load the default set of policies into the database
	 * @param db reference to the db instance
	 */
	private void loadDefaultDataIntoDB(SQLiteDatabase db) {
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
				findContextByID(db, 1),
				findActionByID(db, 1)
				));
		//loads violations
		for(PolicyRule policyRule : findAllPolicies(db))
			addViolation(db, new Violation(policyRule.toString(), 1, policyRule.getId(), false));
	}
}