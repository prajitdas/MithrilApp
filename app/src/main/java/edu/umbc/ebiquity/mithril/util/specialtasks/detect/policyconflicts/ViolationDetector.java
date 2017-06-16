package edu.umbc.ebiquity.mithril.util.specialtasks.detect.policyconflicts;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import com.google.gson.Gson;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.data.model.rules.Action;
import edu.umbc.ebiquity.mithril.data.model.rules.PolicyRule;
import edu.umbc.ebiquity.mithril.data.model.rules.Violation;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticActivity;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticLocation;

/**
 * Created by prajit on 12/13/16.
 */

public class ViolationDetector {
//    public static void setSemanticUserContext(SemanticUserContext aSemanticUserContext, Context context) {
//        semanticUserContext = aSemanticUserContext;
//        if (detectedAddress == null)
//            semanticUserContext = null;//.setSemanticLocation(null);
//        else
//            semanticUserContext = new SemanticLocation();
        //TODO FIX THIS!!!
//        SemanticLocation semanticLocation = new SemanticLocation();
        // TODO 1451649600 Is equivalent to: 01/01/2016 @ 12:00pm (UTC)
//        SemanticTime semanticTime = new SemanticTime(false, RepeatFrequency.WEEKDAYS, new Timestamp(1451649600), "Lunch");
        // getLocality() ("Mountain View", for example)
        // getAdminArea() ("CA", for example)
        // getPostalCode() ("94043", for example)
        // getCountryCode() ("US", for example)
        // getCountryName() ("United States", for example)
//        if (level.equals("home"))
//        semanticLocation.setInferredLocation(detectedAddress.getPostalCode());
//        else if (level.equals("work"))
//            semanticLocation.setInferredLocation(detectedAddress.getPostalCode());
//        else if (level.equals("city"))
//            semanticLocation.setInferredLocation(detectedAddress.getLocality());
//        else if (level.equals("building"))
//            semanticLocation.setInferredLocation(detectedAddress.getPremises());
//        if (contextLevel.equals(MithrilAC.getPrefKeyCurrentLocation()))
//            semanticUserContext.setSemanticLocation(semanticLocation);
//        else if (contextLevel.equals("loctime")) {
//            semanticUserContext.setSemanticLocation(semanticLocation);
//            semanticUserContext.setSemanticTime(semanticTime);
//        }
//    }

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
    public static void detectViolation(Context context, String currentPackageName, int operationPerformed, SemanticLocation semanticLocation) { //throws CWAException {
        SQLiteDatabase mithrilDB = MithrilDBHelper.getHelper(context).getWritableDatabase();
//        List<Integer> currentContext = MithrilDBHelper.getHelper(context).findCurrentContextFromLogs(mithrilDB);
        int id = MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB, semanticLocation.getLabel(), semanticLocation.getType());

//        try {
        List<PolicyRule> rulesForApp = MithrilDBHelper.getHelper(context).findAllPoliciesForAppWhenPerformingOp(mithrilDB, currentPackageName, operationPerformed);
        // Let's test the rules we found
        if (rulesForApp.size() > 0) {
            for (PolicyRule rule : rulesForApp) {
                Log.d(MithrilAC.getDebugTag(), "Found rule: " +
                        rule.getName() +
                        Integer.toString(rule.getCtxId()) +
                        semanticLocation.getInferredLocation());
                //There is a rule for this app with current context as it's context
                if (id == rule.getCtxId()) {
                    //Rule has a deny action, we may have a violation
                    if (rule.getAction().equals(Action.DENY)) {
                        Log.d(MithrilAC.getDebugTag(), "Eureka!");
                        MithrilDBHelper.getHelper(context).addViolation(mithrilDB,
                                new Violation(rule.getAppId(),
                                        rule.getCtxId(),
                                        rule.getOp(),
                                        rule.getName(),
                                        false,
                                        new Timestamp(System.currentTimeMillis())
                                )
                        );
                    } //Rule has an allow action, nothing to do
                    else if (rule.getAction().equals(Action.ALLOW)) {
                        Log.d(MithrilAC.getDebugTag(), "No violation for: " + currentPackageName);
                    }
                } // Rule context does not match current context, we might have an error here, check carefully
                else {
                    Log.d(MithrilAC.getDebugTag(),
                            "Rule context does not match current context for: " +
                                    currentPackageName +
                                    " " +
                                    Integer.toString(rule.getCtxId()));
                }
            }
        } //No rules found! We have a default violation... Opportunity for ML?
        else {
//            Log.d(MithrilAC.getDebugTag(), "Default violation scenario. Do something!");
//            for(Integer currCtxtId : currentContext) {
//                MithrilDBHelper.getHelper(context).addViolation(mithrilDB,
//                        new Violation(
//                                MithrilDBHelper.getHelper(context).findAppIdByName(mithrilDB, currentPackageName),
//                                currCtxtId,
//                                operationPerformed,
//                                MithrilAC.getPolRulDefaultRule(),
//                                false,
//                                new Timestamp(System.currentTimeMillis())
//                        )
//                );
//            }
        }
                //Rule was a deny, we may have a violation
//                        Violation violation;
//                        //Is this allowed?
//                        //Do we need temporal context?
//                        if (rule.getContextType().equals(MithrilAC.getPrefKeyTemporal())) {
//                            violation = weNeedTimeViolationCheck(context, new SemanticTime(rule.getSemanticContextLabel()));
//                            if(violation != null)
//                                mithrilDBHelper.addViolation(mithrilDB, violation);
//                        }
//                        //Do we need location?
//                        else if (rule.getContextType().equals(MithrilAC.getPrefKeyLocation())) {
//                            violation = weNeedLocationViolationCheck(context, new SemanticLocation(rule.getSemanticContextLabel()));
//                            if(violation != null)
//                                mithrilDBHelper.addViolation(mithrilDB, violation);
//                        }
//                        //Do we need activity?
//                        else if (rule.getContextType().equals(MithrilAC.getPrefKeyActivity()))
//                            weNeedActivityViolationCheck();
//                        //Do we need nearby actors?
//                        else if (rule.getContextType().equals(MithrilAC.getPrefKeyPresence()))
//                            weNeedNearActorsViolationCheck();
//                } else {
//                    Log.e(MithrilAC.getDebugTag(), "Serious error! DB contains deny rules. This violates our CWA");
////                    throw new CWAException(); //Something is wrong!!!! We have a Closed World Assumption we cannot have deny rules...
//                }
//            }
//        } //No rules found! We have a violation...
//        else {
//        }
//            if (MithrilDBHelper.getHelper(context).findAppTypeByAppPkgName(mithrilDB, currentPackageName).equals(MithrilAC.getPrefKeyUserAppsDisplay())) {

                //            PermissionHelper.toast(context, "Mithril detects user app launch: " + currentPackageName, Toast.LENGTH_SHORT).show();
        //            Log.d(MithrilAC.getDebugTag(), "Mithril detects user app launch: " + currentPackageName);
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
        //                if (contextLevel.equals(MithrilAC.getPrefKeyCurrentLocation())
                //                        && !currSemanticUserContext.getSemanticLocation().getInferredLocation().equals("21227")) {
                //                    if (currentPackageName.equals("com.google.android.youtube")) {
        //                        editor.putString(MithrilAC.getPrefKeyAppPkgName(), currentPackageName);
        //                        editor.putString(MithrilAC.getPrefKeyCurrentLocation(), "Home");
                ////                        PermissionHelper.toast(context, "Rule 1 violation detected!");
        //                        Log.d(MithrilAC.getDebugTag(), "Rule 1 violation detected!");
                //                    }
                //                }
                //                //Rule 2 is allow youtube at work during lunch hours
                //                else if (contextLevel.equals("loctime")
                //                        && !currSemanticUserContext.getSemanticLocation().getInferredLocation().equals("21250")
                //                        && !currSemanticUserContext.getSemanticTime().getDeviceTime().equals("Lunch")) {
                //                    if (currentPackageName.equals("com.google.android.youtube")) {
        //                        editor.putString(MithrilAC.getPrefKeyAppPkgName(), currentPackageName);
        //                        editor.putString(MithrilAC.getPrefKeyCurrentLocation(), "Work");
        //                        editor.putString(MithrilAC.getPrefKeyCurrentTime(), "Lunch");
                ////                        editor.apply();
                ////                        PermissionHelper.toast(context, "Rule 2 violation detected!");
        //                        Log.d(MithrilAC.getDebugTag(), "Rule 2 violation detected!");
                //                    }
                //                }
                //                //Rule 3 is allow SQLite at work
        //                else if (contextLevel.equals(MithrilAC.getPrefKeyCurrentLocation())
                //                        && !currSemanticUserContext.getSemanticLocation().getInferredLocation().equals("21250")) {
                //                    if (currentPackageName.equals("oliver.ehrenmueller.dbadmin")) {
        //                        editor.putString(MithrilAC.getPrefKeyAppPkgName(), currentPackageName);
        //                        editor.putString(MithrilAC.getPrefKeyCurrentLocation(), "Work");
                ////                editor.apply();
                ////                        PermissionHelper.toast(context, "Rule 3 violation detected!");
        //                        Log.d(MithrilAC.getDebugTag(), "Rule 2 violation detected!");
                //                    }
                //                }
                ////            // If no rules are broken then we will show no violations
                ////            else {
        ////                editor.remove(MithrilAC.getPrefKeyCurrentLocation());
        ////                editor.remove(MithrilAC.getPrefKeyCurrentTime());
                ////                PermissionHelper.toast(context, "All good!");
                ////            }
                //
                //                editor.apply();
                //            }
//            }
//        } catch (SQLException e) {
//            Log.e(MithrilAC.getDebugTag(), e.getMessage() + " it seems there is no policy for this app!");
//        }
        mithrilDB.close();
    }

//    private static void weNeedIdentityViolationCheck() {
//
//    }
//
//    private static void weNeedNearActorsViolationCheck() {
//
//    }
//
//    private static Violation weNeedTimeViolationCheck(Context context, SemanticTime semanticTime) {
////        if(semanticTime.getInferredTime() != currentTime)
////            return new Violation();
//        return null;
//    }
//
//    private static void weNeedActivityViolationCheck() {
//
//    }
//
//    private static Violation weNeedLocationViolationCheck(Context context, SemanticLocation semanticLocation) {
//        SharedPreferences sharedPref = context.getSharedPreferences(MithrilAC.getSharedPreferencesName(), Context.MODE_PRIVATE);
//
//        Gson gson = new Gson();
//        String json = null;
//
////        json = sharedPref.getString(MithrilAC.getPrefKeyCurrentAddress(), null);
////        detectedAddress = gson.fromJson(json, Address.class);
////
////        json = sharedPref.getString(MithrilAC.getPrefKeyCurrentLocation(), null);
////        detectedLocation = gson.fromJson(json, Location.class);
////        if(semanticLocation.getInferredLocation() != currentLocation)
////            return new Violation();
//        return null;
//    }
}