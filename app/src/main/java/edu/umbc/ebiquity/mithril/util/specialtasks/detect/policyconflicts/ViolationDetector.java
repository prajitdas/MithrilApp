package edu.umbc.ebiquity.mithril.util.specialtasks.detect.policyconflicts;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.data.model.components.AppData;
import edu.umbc.ebiquity.mithril.data.model.rules.Action;
import edu.umbc.ebiquity.mithril.data.model.rules.PolicyRule;
import edu.umbc.ebiquity.mithril.data.model.rules.Resource;
import edu.umbc.ebiquity.mithril.data.model.rules.Violation;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticUserContext;
import edu.umbc.ebiquity.mithril.simulations.DataGenerator;
import edu.umbc.ebiquity.mithril.util.specialtasks.collections.MithrilCollections;
import edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions.ContextImplementationMissingException;
import edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions.SemanticInconsistencyException;
import edu.umbc.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

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
     * <p>
     * The above thing was the algorithm before. We have to improve it...
     * <p>
     * 1) Detect app launch and operation.
     * 2) Detect what is the current context.
     * we may find our that we are at an
     * unknown location or
     * in presence of an unknown person or
     * doing a previously unknown activity
     * if we do, then we add these as default_context_type_timestamp
     * and we have to ask user to define a label for the context in a notification like Google Now or just ask them later
     * 3) Search for policies that define for app launch and operation a series of context pieces of significance.
     * 4) If current context is an exact match or a subset of policy's context pieces, user has allowed this behavior before.
     * Subset: Policy states that during lunch hours on a weekday at work in presence of colleague allow camera access to social media apps.
     * Context is we are at work and its lunch hours on a weekday but no colleague is with us.
     * 5) If current context is
     * a) A superset of policy context
     * context is: lunch hours during weekday with boss, policy is for lunch hours on a weekday
     * or b) Is an unknown context
     * disjoint sets most probably, intersection is null
     * context is: we are at an unknown location and camera is accessed
     * or c) Context is known but does not match policy's context of significance
     * intersection set is non-null
     * context is: we are with our boss, having lunch at work on a weekday
     * and policy defines what to do if colleague is present and not when boss is present
     * In all the above three scenario we have app launch that is not governed by allow rules, i.e. we have a VIOLATION!
     * <p>
     * Proper subset definition: A proper subset of a set A is a subset of A that is not equal to A.
     * In other words, if B is a proper subset of A, then all elements of B are in A but A contains at least one element that is not in B.
     * For example, if A={1,3,5} then B={1,5} is a proper subset of A. The set C={1,3,5} is a subset of A, but it is an improper subset of A
     * since C=A. The set D={1,4} is not even a subset of A, since 4 is not an element of A.
     */
    public static void detectViolation(Context context,
                                       String currentPackageName,
                                       List<Resource> resources,
                                       List<SemanticUserContext> semanticUserContexts) throws SemanticInconsistencyException {
        if (semanticUserContexts.size() == 0) {
            Log.e(MithrilAC.getDebugTag(), "Houston, we have a problem! We can't detect current context");
            return;
        }
        if (resources.size() == 0) {
            if (PermissionHelper.isPermissionGranted(context, Manifest.permission.GET_APP_OPS_STATS) != PackageManager.PERMISSION_GRANTED)
                Log.e(MithrilAC.getDebugTag(), "We do not have GET_APP_OPS_STATS permission!");
            return;
        }
        if (resources.get(0).getOp() == AppOpsManager.OP_NONE) {
            Log.e(MithrilAC.getDebugTag(), "Houston, we have another problem! We couldn't figure out the operation for " + currentPackageName);
            return;
        }

        SQLiteDatabase mithrilDB = MithrilDBHelper.getHelper(context).getWritableDatabase();
        Set<Long> currentContextSet = populateCurrentContext(mithrilDB, context, semanticUserContexts);
        List<Long> currentContextList = new ArrayList<>(currentContextSet);
        Collections.sort(currentContextList);

        // Let's test the rules we found
        for (Resource currentResource : resources) {
            Action actionForCurrentOperationAndApp = Action.DENY;
            int lastOperationPerformed = currentResource.getOp();
            List<PolicyRule> policyRules = MithrilDBHelper.getHelper(context).findAllPoliciesForAppWhenPerformingOp(mithrilDB, currentPackageName, lastOperationPerformed);
            if (policyRules.size() > 0) {
                long currentPolicyId = -1;
                List<PolicyRule> tempRules = new ArrayList<>();
                Map<Long, List<PolicyRule>> policyRuleMap = new HashMap<>();
                for (PolicyRule policyRule : policyRules) {
                    if (currentPolicyId == policyRule.getPolicyId())
                        tempRules.add(policyRule);
                    else {
                        if (currentPolicyId != -1)
                            policyRuleMap.put(policyRule.getPolicyId(), tempRules);
                        currentPolicyId = policyRule.getPolicyId();
                    }
                }
                for (Map.Entry<Long, List<PolicyRule>> policyRuleMapEntry : policyRuleMap.entrySet()) {
                    //Found some policies let's group them by policy Id
                    Set<Long> policyContextSet = new HashSet<>();
                    List<Long> policyContextList = new ArrayList<>(policyContextSet);
                    Collections.sort(policyContextList);
                    for (PolicyRule policyRule : policyRuleMapEntry.getValue())
                        policyContextSet.add(policyRule.getCtxId());
                    /**
                     * If current context is a subset of policy context or they are equal then we get true for the following test
                     * We have assumed a closed world. Explicit access has to be defined.
                     * Although a deny rule may be used in a closed world, it may also create policy conflicts.
                     * For example:
                     * 1) Rule A states allow access to camera during lunch hours
                     * 2) Rule B states deny access to camera at work
                     * The conflict arises from the fact that we might be at work during lunch hours, what happens then?
                     * We can ask the user about this. As in, should we allow camera access at work during lunch hours?
                     * But now we have a new problem. Suppose that we have a rule that states that in presence of a
                     * supervisor don't allow access to camera. Another rule states that in presence of a colleague
                     * allow access to camera. A third rule states that allow access at a restaurant. What happens
                     * if we are at a restaurant in our work place and having a team lunch with our colleagues and bosses?
                     * There are too many conflicting rules to handle and the system will become increasingly difficult
                     * to handle or use because we will be asking the users too many questions.
                     * However, a safe bet is that if there is any rule that states when every one of these contextual
                     * situations apply only then allow access then we are using a restrictive but safe access principle.
                     */
                    if (MithrilCollections.isExactMatchSet(policyContextSet, currentContextSet)) {
                        /**
                         * We have an exact context match! Current context is an exact match for rule context.
                         * We have to do something...
                         */
                        Log.d(MithrilAC.getDebugTag(), "Exact match. Do something! operation:" + lastOperationPerformed + " policy:" + policyContextSet + " current context:" + currentContextSet);
                        for (PolicyRule rule : policyRules) {
                            //Rule has an deny action, we have a violation to ask questions about
                            if (rule.getAction().equals(Action.DENY)) {
                                if (actionForCurrentOperationAndApp.equals(Action.ALLOW))
                                    throw new SemanticInconsistencyException("Same policy has conflicting actions for different context");
                                else
                                    actionForCurrentOperationAndApp = Action.DENY;
                                //Rule has a deny action, we have a violation
                                Log.d(MithrilAC.getDebugTag(),
                                        "This is a scenario where we have deny rules in the KB. " +
                                                "We were not supposed to have this Something is wrong!");
                                /**
                                 * We have a violation! All violations start as a false violation and they are
                                 * deemed true by user feedback. They may also be explicitly defined as false.
                                 * In which case we need to change the policy... We ask for more feedback.
                                 */
                                handleViolation(context,
                                        mithrilDB,
                                        new Violation(
                                                rule.getPolicyId(),
                                                rule.getAppId(),
                                                rule.getOp(),
                                                rule.getAppStr(),
                                                rule.getOpStr(),
                                                false,
                                                true,
                                                new Timestamp(System.currentTimeMillis()),
                                                policyContextList,
                                                1,
                                                currentResource
                                        )
                                );
                            } else {
                                if (actionForCurrentOperationAndApp.equals(Action.DENY))
                                    throw new SemanticInconsistencyException("Same policy has conflicting actions for different context");
                                else
                                    actionForCurrentOperationAndApp = Action.ALLOW;
                            }
                        }
                    } else if (MithrilCollections.isSubset(policyContextSet, currentContextSet)) {
                        /**
                         * We have a subset context match! Policy context is a proper subset match for rule context.
                         * We have to do something...
                         */
                        Log.d(MithrilAC.getDebugTag(), "Subset match. Do something! operation:" + lastOperationPerformed + " policy:" + policyContextSet + " current context:" + currentContextSet);
                        for (PolicyRule rule : policyRules) {
                            //Rule has an deny action, we have a violation to ask questions about
                            if (rule.getAction().equals(Action.DENY)) {
                                if (actionForCurrentOperationAndApp.equals(Action.ALLOW))
                                    throw new SemanticInconsistencyException("Same policy has conflicting actions for different context");
                                else
                                    actionForCurrentOperationAndApp = Action.DENY;
                                //Rule has a deny action, we have a violation
                                Log.d(MithrilAC.getDebugTag(),
                                        "This is a scenario where we have deny rules in the KB. " +
                                                "We were not supposed to have this Something is wrong!");
                                /**
                                 * We have a violation! All violations start as a false violation and they are
                                 * deemed true by user feedback. They may also be explicitly defined as false.
                                 * In which case we need to change the policy... We ask for more feedback.
                                 */
                                handleViolation(context,
                                        mithrilDB,
                                        new Violation(
                                                rule.getPolicyId(),
                                                rule.getAppId(),
                                                rule.getOp(),
                                                rule.getAppStr(),
                                                rule.getOpStr(),
                                                false,
                                                true,
                                                new Timestamp(System.currentTimeMillis()),
                                                policyContextList,
                                                1,
                                                currentResource
                                        )
                                );
                            } else {
                                if (actionForCurrentOperationAndApp.equals(Action.DENY))
                                    throw new SemanticInconsistencyException("Same policy has conflicting actions for different context");
                                else
                                    actionForCurrentOperationAndApp = Action.ALLOW;
                            }
                        }
                    } else {
                        /**
                         * Neither did we have an exact context match nor did we have a subset of policy context match.
                         * Therefore, one of the following conditions hold true:
                         *      a) Current context is a superset of policy context
                         *      b) Current context is disjoint from policy context may or may not be unknown contexts
                         *      c) Current context has an intersection with policy context but has additional
                         *      conditions not in policy.
                         * This means we have a violation scenario. We could have a scenario here such that the current context is unknown.
                         * Are we looking at too specific a context? We don't know what to do in
                         * this context and maybe we could use some ML here too? However, right now we ask user feedback...
                         *
                         * We should add the context to the context table, the policy to the policy table and keep it disabled until user enables it
                         * Context are by default enabled.
                         * PolicyRules are by default disabled.
                         * Violations are by default marked true.
                         */
                        Log.d(MithrilAC.getDebugTag(), "No match. Perhaps it's a superset or complete mismatch.. we don't know what to do, ask user operation:" + lastOperationPerformed + " policy:" + policyContextSet + " current context:" + currentContextSet);
                        int newPolicyId = MithrilDBHelper.getHelper(context).findMaxPolicyId(mithrilDB) + 1;
                        for (long currCtxtId : currentContextSet) {
                            Pair<String, String> ctxtTypeLabel = MithrilDBHelper.getHelper(context).findContextByID(mithrilDB, currCtxtId);
                            AppData app = MithrilDBHelper.getHelper(context).findAppByAppPkgName(mithrilDB, currentPackageName);
                            long appId = MithrilDBHelper.getHelper(context).findAppIdByAppPkgName(mithrilDB, currentPackageName);
//                            MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(
//                                    newPolicyId,
//                                    currentPackageName,
//                                    app.getAppName(), // the name returned is not correct we have find the method that fixes that
//                                    lastOperationPerformed, // Manifest.permission.ACCESS_FINE_LOCATION,
//                                    ctxtTypeLabel.second,
//                                    ctxtTypeLabel.first,
//                                    Action.ALLOW,
//                                    mithrilDB, context)
//                            );
                            handleViolation(context,
                                    mithrilDB,
                                    new Violation(
                                            newPolicyId,
                                            appId,
                                            lastOperationPerformed,
                                            app.getAppName(), // the name returned is not correct we have find the method that fixes that
                                            AppOpsManager.opToName(lastOperationPerformed),
                                            false,
                                            true,
                                            new Timestamp(System.currentTimeMillis()),
                                            currentContextList,
                                            1,
                                            currentResource
                                    )
                            );
                        }
                    }
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
                //            mithrilDB.close();
            } else {
                /**
                 * No rules were found... for the app and operation combo! We perhaps have a default violation...
                 * Since we are using a Closed World Assumption, we are stating that explicit permissions
                 * have to be defined. So anything that is not explicitly allowed we consider to be denied.
                 * Perhaps we have opportunity for ML here? For example if we have seen that user allows
                 * Social media apps access to certain things in certain context before, we may make an
                 * assumption that user will allow a new social media app. This is an extrapolation but
                 * this is where we could have a RL system with a goal of predicting users' preferred policy
                 * and use user feedback as +ve or -ve reinforcement.
                 */
                Log.d(MithrilAC.getDebugTag(), "Default violation match scenario. Do something!");
                int newPolicyId = MithrilDBHelper.getHelper(context).findMaxPolicyId(mithrilDB) + 1;
                Log.d(MithrilAC.getDebugTag(), "context set size is: "+currentContextSet.size());
                long[] currentContextArray = setLowestLevelCurrentContext(mithrilDB, context, semanticUserContexts, currentContextSet);
                Log.d(MithrilAC.getDebugTag(), "context set size changed to: "+currentContextSet.size());
                for (int i = 0; i < currentContextArray.length; i++) {
                    Pair<String, String> ctxtTypeLabel = MithrilDBHelper.getHelper(context).findContextByID(mithrilDB, currentContextArray[i]);
                    AppData app = MithrilDBHelper.getHelper(context).findAppByAppPkgName(mithrilDB, currentPackageName);
                    long appId = MithrilDBHelper.getHelper(context).findAppIdByAppPkgName(mithrilDB, currentPackageName);
                    Log.d(MithrilAC.getDebugTag(),
                            Integer.toString(newPolicyId) +
                                    currentPackageName +
                                    app.getAppName() +
                                    lastOperationPerformed +
                                    ctxtTypeLabel.second +
                                    ctxtTypeLabel.first);
                    MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(
                            newPolicyId,
                            currentPackageName,
                            app.getAppName(), // the name returned is not correct we have find the method that fixes that
                            lastOperationPerformed, // Manifest.permission.ACCESS_FINE_LOCATION,
                            ctxtTypeLabel.second,
                            ctxtTypeLabel.first,
                            Action.ALLOW,
                            mithrilDB, context)
                    );
                    handleViolation(context,
                            mithrilDB,
                            new Violation(
                                    newPolicyId,
                                    appId,
                                    lastOperationPerformed,
                                    app.getAppName(), // the name returned is not correct we have find the method that fixes that
                                    AppOpsManager.opToName(lastOperationPerformed), // Manifest.permission.ACCESS_FINE_LOCATION,
                                    false,
                                    true,
                                    new Timestamp(System.currentTimeMillis()),
                                    currentContextList,
                                    1,
                                    currentResource
                            )
                    );
                }
            }
        }
    }

    private static void handleViolation(Context context, SQLiteDatabase mithrilDB, Violation violation) {
        try {
            MithrilDBHelper.getHelper(context).addViolation(mithrilDB, violation);
        } catch (SQLException e) {
            Log.d(MithrilAC.getDebugTag(), violation.toString());
            Violation foundViolation = MithrilDBHelper.getHelper(context).findViolationByPolicyAppOpPolId(mithrilDB, violation.getAppId(), violation.getOprId(), violation.getPolicyId());
            foundViolation.setCount(violation.getCount() + 1);
            MithrilDBHelper.getHelper(context).updateViolation(mithrilDB, foundViolation);
        }
    }

    private static Set<Long> populateCurrentContext(SQLiteDatabase mithrilDB, Context context, List<SemanticUserContext> semanticUserContexts) {
        Set<Long> currentContextIds = new HashSet<>();
        try {
            for (SemanticUserContext semanticUserContext : semanticUserContexts) {
                if (semanticUserContext == null)
                    Log.d(MithrilAC.getDebugTag(), "got null");
                Log.d(MithrilAC.getDebugTag(), semanticUserContext.getLabel() + semanticUserContext.getType());
                long contextId = MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB, semanticUserContext.getLabel(), semanticUserContext.getType());
                //We found an unknown context, let's add that to the KB.
                if (contextId == -1)
                    contextId = MithrilDBHelper.getHelper(context).addContext(
                            mithrilDB,
                            semanticUserContext.getType(),
                            semanticUserContext.getLabel(),
                            true,
                            semanticUserContext.getLevel());
                currentContextIds.add(contextId);
                Log.d(MithrilAC.getDebugTag(), "got contextid" + contextId);
            }
        } catch (ContextImplementationMissingException e) {
            Log.e(MithrilAC.getDebugTag(), e.getMessage());
        }
        return currentContextIds;
    }

    private static long[] setLowestLevelCurrentContext(SQLiteDatabase mithrilDB,
                                                          Context context,
                                                          List<SemanticUserContext> semanticUserContexts,
                                                          Set<Long> contextIds) {
        long[] currentContextIds = new long[4];
        int lowestTemporal = Integer.MAX_VALUE;
        int lowestLocation = Integer.MAX_VALUE;
        int lowestPresence = Integer.MAX_VALUE;
        int lowestActivity = Integer.MAX_VALUE;
        try {
            for(Long contextId : contextIds) {
                Pair<String, String> labelType = MithrilDBHelper.getHelper(context).findContextByID(mithrilDB, contextId);
                for (SemanticUserContext semanticUserContext : semanticUserContexts) {
                    if (labelType.first.equals(semanticUserContext.getType()) && labelType.second.equals(semanticUserContext.getLabel())) {
                        if (semanticUserContext.getType().equals(MithrilAC.getPrefKeyContextTypeTemporal())) {
                            if (semanticUserContext.getLevel() < lowestTemporal) {
                                currentContextIds[0] = contextId;
                                lowestTemporal = semanticUserContext.getLevel();
                            }
                        } else if (semanticUserContext.getType().equals(MithrilAC.getPrefKeyContextTypeLocation())) {
                            if (semanticUserContext.getLevel() < lowestLocation) {
                                currentContextIds[1] = contextId;
                                lowestLocation = semanticUserContext.getLevel();
                            }
                        } else if (semanticUserContext.getType().equals(MithrilAC.getPrefKeyContextTypePresence())) {
                            if (semanticUserContext.getLevel() < lowestPresence) {
                                currentContextIds[2] = contextId;
                                lowestPresence = semanticUserContext.getLevel();
                            }
                        } else if (semanticUserContext.getType().equals(MithrilAC.getPrefKeyContextTypeActivity())) {
                            if (semanticUserContext.getLevel() < lowestActivity) {
                                currentContextIds[3] = contextId;
                                lowestActivity = semanticUserContext.getLevel();
                            }
                        }
                    }
                }
            }
        } catch (ContextImplementationMissingException e) {
            Log.e(MithrilAC.getDebugTag(), e.getMessage());
        }
        return currentContextIds;
    }
}