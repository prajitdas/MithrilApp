package edu.umbc.ebiquity.mithril.util.specialtasks.detect.policyconflicts;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.data.model.rules.Action;
import edu.umbc.ebiquity.mithril.data.model.rules.PolicyRule;
import edu.umbc.ebiquity.mithril.data.model.rules.Violation;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticLocation;

/**
 * Created by prajit on 12/13/16.
 */

public class ViolationDetector {
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
        List<Integer> currentContext = new ArrayList<>();
//                MithrilDBHelper.getHelper(context).findCurrentContextFromLogs(mithrilDB);
        currentContext.add(MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB, semanticLocation.getLabel(), semanticLocation.getType()));

        Map<Integer, List<Integer>> policyMap = new HashMap<>();
//        try {
        List<PolicyRule> rulesForApp = MithrilDBHelper.getHelper(context).findAllPoliciesForAppWhenPerformingOp(mithrilDB, currentPackageName, operationPerformed);
        // Let's test the rules we found
        if (rulesForApp.size() > 0) {
            for (PolicyRule rule : rulesForApp) {
                Log.d(MithrilAC.getDebugTag(), "Found rule: " +
                        rule.toString() +
                        Integer.toString(rule.getCtxId()) +
                        semanticLocation.getInferredLocation());
                //There is a rule for this app with current context as it's context
                if (currentContext.contains(rule.getCtxId())) {
                    //Rule has a deny action, we may have a violation
                    if (rule.getAction().equals(Action.DENY)) {
                        Log.d(MithrilAC.getDebugTag(), "Eureka!");
                        MithrilDBHelper.getHelper(context).addViolation(mithrilDB,
                                new Violation(rule.getAppId(),
                                        rule.getCtxId(),
                                        rule.getOp(),
                                        rule.toString(),
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

        /**
         * PolicyConflictDetector object to be created and sent the requester, resource, context combo to receive a decision!!!
         * TODO if an app is launched at a certain Semantic location, does the location we know match any policy?
         * TODO If it does then, can we determine if this is a violation?
         * TODO if this is a violation, then insert the information into the violation table.
         * TODO if it is not a violation, then what do we do? **DECIDE**
         * TODO !!!!DUMMY POLICY!!!!
         * REMOVE THIS AFTER DEMO
         * com.google.android.youtube launch is not allowed in US!
         * change policy to allowed in
         */
        mithrilDB.close();
    }
}