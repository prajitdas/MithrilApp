package edu.umbc.ebiquity.mithril.util.specialtasks.detect.policyconflicts;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Location;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.data.model.Action;
import edu.umbc.ebiquity.mithril.data.model.PolicyRule;
import edu.umbc.ebiquity.mithril.data.model.Violation;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticLocation;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticTime;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticUserContext;
import edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions.CWAException;

/**
 * Created by prajit on 12/13/16.
 */

public class ViolationDetector {
    private static SemanticUserContext semanticUserContext;
    private static Action action;

    private static Address detectedAddress;
    private static Location detectedLocation;

    private static SemanticUserContext getCurrentSemanticUserContext() {
        return semanticUserContext;
    }

    public static void setSemanticUserContext(SemanticUserContext aSemanticUserContext, Context context) {
        semanticUserContext = aSemanticUserContext;
        if (detectedAddress == null)
            semanticUserContext = null;//.setSemanticLocation(null);
        else
            semanticUserContext = new SemanticLocation();
        //TODO FIX THIS!!!
        SemanticLocation semanticLocation = new SemanticLocation();
        SemanticTime semanticTime = new SemanticTime("Lunch");
        // getLocality() ("Mountain View", for example)
        // getAdminArea() ("CA", for example)
        // getPostalCode() ("94043", for example)
        // getCountryCode() ("US", for example)
        // getCountryName() ("United States", for example)
//        if (level.equals("home"))
        semanticLocation.setInferredLocation(detectedAddress.getPostalCode());
//        else if (level.equals("work"))
//            semanticLocation.setInferredLocation(detectedAddress.getPostalCode());
//        else if (level.equals("city"))
//            semanticLocation.setInferredLocation(detectedAddress.getLocality());
//        else if (level.equals("building"))
//            semanticLocation.setInferredLocation(detectedAddress.getPremises());
//        if (contextLevel.equals(MithrilApplication.getPrefKeyCurrentLocation()))
//            semanticUserContext.setSemanticLocation(semanticLocation);
//        else if (contextLevel.equals("loctime")) {
//            semanticUserContext.setSemanticLocation(semanticLocation);
//            semanticUserContext.setSemanticTime(semanticTime);
//        }
    }

    public static Action getAction() {
        return action;
    }

    public static void setAction(Action anAction) {
        action = anAction;
    }

    /**
     * Algorithm for violation detection:
     * Design assumption: Given a policy, a policy rule, has
     * a) a requester = app being launched
     * b) a resource = something that the app starts (we are not doing this, maybe we should, if we can) how about it's the permission that the app possesses?
     * c) a context = includes location, activity, presence info, temporal info with respect to a user's state
     * d) an action = We are using Closed World Assumption (CWA) in this system. We will only state what is allowed.
     * The assumption is that if a particular scenario is not stated explicitly then it's a case when action is to deny.
     * -> These attributes are then used by our system to handle access control decisions or to determine policy violations depending on what state the system is in.
     * 1) When a request from an app is detected, select all policy rules that contain the current requester-resource combo.
     * 2) Determine the context information
     * 3) Search policy list for a rule that matches the current combo of requester-resource-context combo
     * 4) If no such rule is found then detect this as a violation and request a user action on it or if in execution mode, block this access
     */

    public static void detectViolation(Context context, String currentPackageName) throws CWAException {
        if (currentPackageName == null)
            return;
        MithrilDBHelper mithrilDBHelper = new MithrilDBHelper(context);
        SQLiteDatabase mithrilDB = mithrilDBHelper.getWritableDatabase();

        try {
            List<PolicyRule> rulesForApp = mithrilDBHelper.findAllPoliciesByAppPkgName(mithrilDB, currentPackageName);
            //No rules found! We have a violation...
            if (rulesForApp.size() > 0) {
                for (PolicyRule rule : rulesForApp) {
                    if (rule.getAction().equals(Action.ALLOW)) {
//                        Violation violation;
//                        //Is this allowed?
//                        //Do we need temporal context?
//                        if (rule.getContextType().equals(MithrilApplication.getPrefKeyTemporal())) {
//                            violation = weNeedTimeViolationCheck(context, new SemanticTime(rule.getSemanticContextLabel()));
//                            if(violation != null)
//                                mithrilDBHelper.addViolation(mithrilDB, violation);
//                        }
//                        //Do we need location?
//                        else if (rule.getContextType().equals(MithrilApplication.getPrefKeyLocation())) {
//                            violation = weNeedLocationViolationCheck(context, new SemanticLocation(rule.getSemanticContextLabel()));
//                            if(violation != null)
//                                mithrilDBHelper.addViolation(mithrilDB, violation);
//                        }
//                        //Do we need activity?
//                        else if (rule.getContextType().equals(MithrilApplication.getPrefKeyActivity()))
//                            weNeedActivityViolationCheck();
//                        //Do we need nearby actors?
//                        else if (rule.getContextType().equals(MithrilApplication.getPrefKeyPresence()))
//                            weNeedNearActorsViolationCheck();
                    } else {
                        Log.e(MithrilApplication.getDebugTag(), "Serious error! DB contains deny rules. This violates our CWA");
                        throw new CWAException(); //Something is wrong!!!! We have a Closed World Assumption we cannot have deny rules...
                    }
                }
            }
            if (mithrilDBHelper.findAppTypeByAppPkgName(mithrilDB, currentPackageName).equals(MithrilApplication.getPrefKeyUserAppsDisplay())) {

                //            PermissionHelper.toast(context, "Mithril detects user app launch: " + currentPackageName, Toast.LENGTH_SHORT).show();
                //            Log.d(MithrilApplication.getDebugTag(), "Mithril detects user app launch: " + currentPackageName);
                /**
                 * PolicyConflictDetector object to be created and sent the requester, resource, context combo to receive a decision!!!
                 */
                /**
                 * TODO if an app is launched at a certain Semantic location, does the location we know match any policy?
                 * TODO If it does then, can we determine if this is a violation?
                 * TODO if this is a violation, then insert the information into the violation table.
                 * TODO if it is not a violation, then what do we do? **DECIDE**
                 * TODO !!!!DUMMY POLICY!!!!
                 * REMOVE THIS AFTER DEMO
                 * com.google.android.youtube launch is not allowed in US!
                 * change policy to allowed in
                 */
                //            SemanticUserContext currSemanticUserContext = getCurrentSemanticUserContext();
                //            if (currSemanticUserContext != null) {
                //                //Rule 1 is allow youtube at home
                //                if (contextLevel.equals(MithrilApplication.getPrefKeyCurrentLocation())
                //                        && !currSemanticUserContext.getSemanticLocation().getInferredLocation().equals("21227")) {
                //                    if (currentPackageName.equals("com.google.android.youtube")) {
                //                        editor.putString(MithrilApplication.getPrefKeyAppPkgName(), currentPackageName);
                //                        editor.putString(MithrilApplication.getPrefKeyCurrentLocation(), "Home");
                ////                        PermissionHelper.toast(context, "Rule 1 violation detected!");
                //                        Log.d(MithrilApplication.getDebugTag(), "Rule 1 violation detected!");
                //                    }
                //                }
                //                //Rule 2 is allow youtube at work during lunch hours
                //                else if (contextLevel.equals("loctime")
                //                        && !currSemanticUserContext.getSemanticLocation().getInferredLocation().equals("21250")
                //                        && !currSemanticUserContext.getSemanticTime().getDeviceTime().equals("Lunch")) {
                //                    if (currentPackageName.equals("com.google.android.youtube")) {
                //                        editor.putString(MithrilApplication.getPrefKeyAppPkgName(), currentPackageName);
                //                        editor.putString(MithrilApplication.getPrefKeyCurrentLocation(), "Work");
                //                        editor.putString(MithrilApplication.getPrefKeyCurrentTime(), "Lunch");
                ////                        editor.apply();
                ////                        PermissionHelper.toast(context, "Rule 2 violation detected!");
                //                        Log.d(MithrilApplication.getDebugTag(), "Rule 2 violation detected!");
                //                    }
                //                }
                //                //Rule 3 is allow SQLite at work
                //                else if (contextLevel.equals(MithrilApplication.getPrefKeyCurrentLocation())
                //                        && !currSemanticUserContext.getSemanticLocation().getInferredLocation().equals("21250")) {
                //                    if (currentPackageName.equals("oliver.ehrenmueller.dbadmin")) {
                //                        editor.putString(MithrilApplication.getPrefKeyAppPkgName(), currentPackageName);
                //                        editor.putString(MithrilApplication.getPrefKeyCurrentLocation(), "Work");
                ////                editor.apply();
                ////                        PermissionHelper.toast(context, "Rule 3 violation detected!");
                //                        Log.d(MithrilApplication.getDebugTag(), "Rule 2 violation detected!");
                //                    }
                //                }
                ////            // If no rules are broken then we will show no violations
                ////            else {
                ////                editor.remove(MithrilApplication.getPrefKeyCurrentLocation());
                ////                editor.remove(MithrilApplication.getPrefKeyCurrentTime());
                ////                PermissionHelper.toast(context, "All good!");
                ////            }
                //
                //                editor.apply();
                //            }
            }
        } catch (SQLException e) {
            Log.e(MithrilApplication.getDebugTag(), e.getMessage() + " it seems there is no policy for this app!");
        }
        mithrilDB.close();
    }

    private static void weNeedIdentityViolationCheck() {

    }

    private static void weNeedNearActorsViolationCheck() {

    }

    private static Violation weNeedTimeViolationCheck(Context context, SemanticTime semanticTime) {
//        if(semanticTime.getInferredTime() != currentTime)
//            return new Violation();
        return null;
    }

    private static void weNeedActivityViolationCheck() {

    }

    private static Violation weNeedLocationViolationCheck(Context context, SemanticLocation semanticLocation) {
        SharedPreferences sharedPref = context.getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = null;

        json = sharedPref.getString(MithrilApplication.getPrefKeyCurrentAddress(), null);
        detectedAddress = gson.fromJson(json, Address.class);

        json = sharedPref.getString(MithrilApplication.getPrefKeyCurrentLocation(), null);
        detectedLocation = gson.fromJson(json, Location.class);
//        if(semanticLocation.getInferredLocation() != currentLocation)
//            return new Violation();
        return null;
    }
}