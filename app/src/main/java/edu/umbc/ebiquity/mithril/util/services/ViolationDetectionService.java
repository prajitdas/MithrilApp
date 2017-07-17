package edu.umbc.ebiquity.mithril.util.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.data.model.rules.Action;
import edu.umbc.ebiquity.mithril.data.model.rules.PolicyRule;
import edu.umbc.ebiquity.mithril.data.model.rules.Resource;
import edu.umbc.ebiquity.mithril.data.model.rules.Violation;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticActivity;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticLocation;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticNearActor;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticTime;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticUserContext;
import edu.umbc.ebiquity.mithril.ui.activities.CoreActivity;
import edu.umbc.ebiquity.mithril.ui.activities.InstanceCreationActivity;
import edu.umbc.ebiquity.mithril.util.specialtasks.detect.context.DetectTemporalContext;
import edu.umbc.ebiquity.mithril.util.specialtasks.detect.runningapps.AppLaunchDetector;
import edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions.AddressKeyMissingError;
import edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions.ContextImplementationMissingException;
import edu.umbc.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

public class ViolationDetectionService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private AppLaunchDetector appLaunchDetector;
    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;
    private Context context;
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;
    private GoogleApiClient mGoogleApiClient;
    private GoogleApiClient mGooglePlacesApiClient;
    private Location mCurrentLocation;
    private Place mCurrentPlace;
    private Pair<String, ArrayList<Resource>> pkgOpPair;
    private boolean servicesAvailable;
    private boolean mInProgress;
    private boolean mPlacesInProgress;
    private SQLiteDatabase mithrilDB;

    /**
     * Tracks whether the user has requested an address. Becomes true when the user requests an
     * address and false when the address (or an error message) is delivered.
     * The user requests an address by pressing the Fetch Address button. This may happen
     * before GoogleApiClient connects. This activity uses this boolean to keep track of the
     * user's intent. If the value is true, the activity tries to fetch the address as soon as
     * GoogleApiClient connects.
     */
    private boolean mPlaceRequested;
    private AddressResultReceiver mAddressResultReceiver;
    private Map<String, SemanticLocation> currentSemanticLocations = new HashMap<>();
    private String currentPackageName;
    private ArrayList<Resource> resourcesUsed = new ArrayList<>();

    private static void handleViolation(Context context,
                                        SQLiteDatabase mithrilDB,
                                        long polId,
                                        long appId,
                                        int op,
                                        String appname,
                                        String operationName,
                                        boolean asked,
                                        boolean tvfv,
                                        Timestamp detectTime,
                                        List<Long> contextList,
                                        int violationCount,
                                        Resource resource) {
        Violation violation = new Violation(polId, appId, op, appname, operationName, asked, tvfv, detectTime, contextList, violationCount, resource);
        try {
            MithrilDBHelper.getHelper(context).addViolation(mithrilDB, violation);
        } catch (SQLException e) {
            Log.d(MithrilAC.getDebugTag(), violation.toString());
            Violation foundViolation = MithrilDBHelper.getHelper(context).findViolationByPolicyAppOpPolId(mithrilDB, violation.getAppId(), violation.getOprId(), violation.getPolicyId());
            foundViolation.setCount(foundViolation.getCount() + 1);
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
            for (Long contextId : contextIds) {
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
    public void detectViolation() {
//        if (semanticUserContexts.size() == 0) {
//            Log.e(MithrilAC.getDebugTag(), "Houston, we have a problem! We can't detect current context");
//            return;
//        }
//        if (resources.size() == 0) {
//            if (PermissionHelper.isPermissionGranted(context, Manifest.permission.GET_APP_OPS_STATS) != PackageManager.PERMISSION_GRANTED)
//                Log.e(MithrilAC.getDebugTag(), "We do not have GET_APP_OPS_STATS permission!");
//            return;
//        }
//        if (resources.get(0).getOp() == AppOpsManager.OP_NONE) {
//            Log.e(MithrilAC.getDebugTag(), "Houston, we have another problem! We couldn't figure out the operation for " + currentPackageName);
//            return;
//        }

//        Set<Long> currentContextSet = populateCurrentContext(mithrilDB, context, semanticUserContexts);
//        List<Long> currentContextList = new ArrayList<>(currentContextSet);
//        Collections.sort(currentContextList);

        // Let's test the rules we found
        for (Resource currentResource : resourcesUsed) {
//            Action actionForCurrentOperationAndApp = Action.DENY;
            int lastOperationPerformed = currentResource.getOp();
//            int newPolicyId = MithrilDBHelper.getHelper(context).findMaxPolicyId(mithrilDB) + 1;
            List<PolicyRule> policyRules = MithrilDBHelper.getHelper(context).findAllPoliciesForAppWhenPerformingOp(mithrilDB, currentPackageName, lastOperationPerformed);
            if (policyRules.size() > 0) {
                long currentPolicyId = -1;
                // Get all rules for current policy. There are multiple rules because each rule represents a context condition.
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
                    Log.d(MithrilAC.getDebugTag(), "currentPolicyId:" + currentPolicyId);
                }
                Log.d(MithrilAC.getDebugTag(), "current policies found:" + policyRuleMap.size());
                // Now we have a map of policyId and rules that are part of that policyId
                for (Map.Entry<Long, List<PolicyRule>> policyRuleMapEntry : policyRuleMap.entrySet()) {
                    //Found some policies let's group them by policy Id
//                    Set<Long> policyContextSet = new HashSet<>();
//                    List<Long> policyContextList = new ArrayList<>(policyContextSet);
//                    Collections.sort(policyContextList);
                    if (isPolicyApplicable(policyRuleMapEntry.getValue())) {
                        List<PolicyRule> rules = new ArrayList<>();
                        List<Long> contextIds = new ArrayList<>();
                        for (PolicyRule rule : policyRuleMapEntry.getValue()) {
                            rules.add(rule);
                            contextIds.add(rule.getCtxId());
                        }
                        if (rules.get(0).getAction().equals(Action.DENY)) {
                            /**
                             * We have a violation! All violations start as a false violation and they are
                             * deemed true by user feedback. They may also be explicitly defined as false.
                             * In which case we need to change the policy... We ask for more feedback.
                             */
                            handleViolation(
                                    context,
                                    mithrilDB,
                                    rules.get(0).getPolicyId(),
                                    rules.get(0).getAppId(),
                                    rules.get(0).getOp(),
                                    rules.get(0).getAppStr(),
                                    rules.get(0).getOpStr(),
                                    false,
                                    true,
                                    new Timestamp(System.currentTimeMillis()),
                                    contextIds,
                                    1,
                                    currentResource
                            );
                        }
                    }
                }
            }
        }
    }

    private boolean isPolicyApplicable(List<PolicyRule> policyRules) {
        boolean policyApplicable = false;
        for (PolicyRule policyRule : policyRules) {
            Gson retrieveDataGson = new Gson();
            String retrieveDataJson;
            Pair<String, String> contextTypeLabel = MithrilDBHelper.getHelper(context).findContextByID(mithrilDB, policyRule.getCtxId());
            SemanticUserContext semanticUserContext;
            if (contextTypeLabel.first.equals(MithrilAC.getPrefKeyContextTypeTemporal())) {
                retrieveDataJson = sharedPrefs.getString(contextTypeLabel.second, "");
                semanticUserContext = retrieveDataGson.fromJson(retrieveDataJson, SemanticTime.class);
                for (SemanticTime time : getSemanticTimes()) {
                    if (time.equals(semanticUserContext))
                        policyApplicable = true;
                    else
                        return false;
                }
            } else if (contextTypeLabel.first.equals(MithrilAC.getPrefKeyContextTypeLocation())) {
                retrieveDataJson = sharedPrefs.getString(contextTypeLabel.second, "");
                semanticUserContext = retrieveDataGson.fromJson(retrieveDataJson, SemanticLocation.class);
                for (SemanticLocation location : getSemanticLocations()) {
                    if (location.equals(semanticUserContext))
                        policyApplicable = true;
                    else
                        return false;
                }
            } else if (contextTypeLabel.first.equals(MithrilAC.getPrefKeyContextTypeTemporal())) {
                retrieveDataJson = sharedPrefs.getString(contextTypeLabel.second, "");
                semanticUserContext = retrieveDataGson.fromJson(retrieveDataJson, SemanticNearActor.class);
                for (SemanticNearActor nearActor : getSemanticNearActors()) {
                    if (nearActor.equals(semanticUserContext))
                        policyApplicable = true;
                    else
                        return false;
                }
            } else if (contextTypeLabel.first.equals(MithrilAC.getPrefKeyContextTypeTemporal())) {
                retrieveDataJson = sharedPrefs.getString(contextTypeLabel.second, "");
                semanticUserContext = retrieveDataGson.fromJson(retrieveDataJson, SemanticActivity.class);
                for (SemanticActivity activity : getSemanticActivities()) {
                    if (activity.equals(semanticUserContext))
                        policyApplicable = true;
                    else
                        return false;
                }
            }
        }
        return policyApplicable;
    }
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
                     *
//                    if (MithrilCollections.isExactMatchSet(policyContextSet, currentContextSet)) {
//                        /**
//                         * We have an exact context match! Current context is an exact match for rule context.
//                         * We have to do something...
//                         *
//                        Log.d(MithrilAC.getDebugTag(), "Exact match. Do something! operation:" + lastOperationPerformed + " policy:" + policyContextSet + " current context:" + currentContextSet);
//                        for (PolicyRule rule : policyRules) {
//                            //Rule has an deny action, we have a violation to ask questions about
//                            if (rule.getAction().equals(Action.DENY)) {
//                                if (actionForCurrentOperationAndApp.equals(Action.ALLOW))
//                                    throw new SemanticInconsistencyException("Same policy has conflicting actions for different context");
//                                else
//                                    actionForCurrentOperationAndApp = Action.DENY;
//                                //Rule has a deny action, we have a violation
//                                Log.d(MithrilAC.getDebugTag(),
//                                        "This is a scenario where we have deny rules in the KB. " +
//                                                "We were not supposed to have this Something is wrong!");
//                                /**
//                                 * We have a violation! All violations start as a false violation and they are
//                                 * deemed true by user feedback. They may also be explicitly defined as false.
//                                 * In which case we need to change the policy... We ask for more feedback.
//                                 *
//                                handleViolation(
//                                        context,
//                                        mithrilDB,
//                                        rule.getPolicyId(),
//                                        rule.getAppId(),
//                                        rule.getOp(),
//                                        rule.getAppStr(),
//                                        rule.getOpStr(),
//                                        false,
//                                        true,
//                                        new Timestamp(System.currentTimeMillis()),
//                                        policyContextList,
//                                        1,
//                                        currentResource
//                                );
//                            } else {
//                                if (actionForCurrentOperationAndApp.equals(Action.DENY))
//                                    throw new SemanticInconsistencyException("Same policy has conflicting actions for different context");
//                                else
//                                    actionForCurrentOperationAndApp = Action.ALLOW;
//                            }
//                        }
                }
//                    else if (MithrilCollections.isSubset(policyContextSet, currentContextSet)) {
//                        /**
//                         * We have a subset context match! Policy context is a proper subset match for rule context.
//                         * We have to do something...
//                         *
//                        Log.d(MithrilAC.getDebugTag(), "Subset match. Do something! operation:" + lastOperationPerformed + " policy:" + policyContextSet + " current context:" + currentContextSet);
//                        for (PolicyRule rule : policyRules) {
//                            //Rule has an deny action, we have a violation to ask questions about
//                            if (rule.getAction().equals(Action.DENY)) {
//                                if (actionForCurrentOperationAndApp.equals(Action.ALLOW))
//                                    throw new SemanticInconsistencyException("Same policy has conflicting actions for different context");
//                                else
//                                    actionForCurrentOperationAndApp = Action.DENY;
//                                //Rule has a deny action, we have a violation
//                                Log.d(MithrilAC.getDebugTag(),
//                                        "This is a scenario where we have deny rules in the KB. " +
//                                                "We were not supposed to have this Something is wrong!");
//                                /**
//                                 * We have a violation! All violations start as a false violation and they are
//                                 * deemed true by user feedback. They may also be explicitly defined as false.
//                                 * In which case we need to change the policy... We ask for more feedback.
//                                 *
//                                handleViolation(
//                                        context,
//                                        mithrilDB,
//                                        rule.getPolicyId(),
//                                        rule.getAppId(),
//                                        rule.getOp(),
//                                        rule.getAppStr(),
//                                        rule.getOpStr(),
//                                        false,
//                                        true,
//                                        new Timestamp(System.currentTimeMillis()),
//                                        policyContextList,
//                                        1,
//                                        currentResource
//                                );
//                            } else {
//                                if (actionForCurrentOperationAndApp.equals(Action.DENY))
//                                    throw new SemanticInconsistencyException("Same policy has conflicting actions for different context");
//                                else
//                                    actionForCurrentOperationAndApp = Action.ALLOW;
//                            }
//                        }
//                    } else {
//                        /**
//                         * Neither did we have an exact context match nor did we have a subset of policy context match.
//                         * Therefore, one of the following conditions hold true:
//                         *      a) Current context is a superset of policy context
//                         *      b) Current context is disjoint from policy context may or may not be unknown contexts
//                         *      c) Current context has an intersection with policy context but has additional
//                         *      conditions not in policy.
//                         * This means we have a violation scenario. We could have a scenario here such that the current context is unknown.
//                         * Are we looking at too specific a context? We don't know what to do in
//                         * this context and maybe we could use some ML here too? However, right now we ask user feedback...
//                         *
//                         * We should add the context to the context table, the policy to the policy table and keep it disabled until user enables it
//                         * Context are by default enabled.
//                         * PolicyRules are by default disabled.
//                         * Violations are by default marked true.
//                         *
//                        Log.d(MithrilAC.getDebugTag(), "No match. Perhaps it's a superset or complete mismatch.. we don't know what to do, ask user operation:" + lastOperationPerformed + " policy:" + policyContextSet + " current context:" + currentContextSet);
//                        long[] currentContextArray = setLowestLevelCurrentContext(mithrilDB, context, semanticUserContexts, currentContextSet);
//                        List<Long> lowestContextList = new ArrayList<>();
//                        for (int i = 0; i < currentContextArray.length; i++)
//                            if (currentContextArray[i] != 0)
//                                lowestContextList.add(currentContextArray[i]);
//                        for (int i = 0; i < currentContextArray.length; i++) {
//                            Pair<String, String> ctxtTypeLabel = MithrilDBHelper.getHelper(context).findContextByID(mithrilDB, currentContextArray[i]);
//                            AppData app = MithrilDBHelper.getHelper(context).findAppByAppPkgName(mithrilDB, currentPackageName);
//                            long appId = MithrilDBHelper.getHelper(context).findAppIdByAppPkgName(mithrilDB, currentPackageName);
////                            MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(
////                                    newPolicyId,
////                                    currentPackageName,
////                                    app.getAppName(), // the name returned is not correct we have find the method that fixes that
////                                    lastOperationPerformed, // Manifest.permission.ACCESS_FINE_LOCATION,
////                                    ctxtTypeLabel.second,
////                                    ctxtTypeLabel.first,
////                                    Action.ALLOW,
////                                    mithrilDB, context)
////                            );
//                            handleViolation(
//                                    context,
//                                    mithrilDB,
//                                    newPolicyId,
//                                    appId,
//                                    lastOperationPerformed,
//                                    app.getAppName(), // the name returned is not correct we have find the method that fixes that
//                                    AppOpsManager.opToName(lastOperationPerformed),
//                                    false,
//                                    true,
//                                    new Timestamp(System.currentTimeMillis()),
//                                    lowestContextList,
//                                    1,
//                                    currentResource
//                            );
//                        }
//                    }
//                }

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
                 *
                //            mithrilDB.close();
            }
            /**
             * We are not doing the default deny violation detection any more
             * /
             else {
             /**
             * No rules were found... for the app and operation combo! We perhaps have a default violation...
             * Since we are using a Closed World Assumption, we are stating that explicit permissions
             * have to be defined. So anything that is not explicitly allowed we consider to be denied.
             * Perhaps we have opportunity for ML here? For example if we have seen that user allows
             * Social media apps access to certain things in certain context before, we may make an
             * assumption that user will allow a new social media app. This is an extrapolation but
             * this is where we could have a RL system with a goal of predicting users' preferred policy
             * and use user feedback as +ve or -ve reinforcement.
             * /
             Log.d(MithrilAC.getDebugTag(), "Default violation match scenario. Do something!");
             long[] currentContextArray = setLowestLevelCurrentContext(mithrilDB, context, semanticUserContexts, currentContextSet);
             List<Long> lowestContextList = new ArrayList<>();
             for (int i = 0; i < currentContextArray.length; i++)
             if (currentContextArray[i] != 0)
             lowestContextList.add(currentContextArray[i]);
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
             AppOpsManager.opToPermission(lastOperationPerformed), // Manifest.permission.ACCESS_FINE_LOCATION,
             ctxtTypeLabel.second,
             ctxtTypeLabel.first,
             Action.ALLOW,
             false,
             mithrilDB, context)
             );
             handleViolation(
             context,
             mithrilDB,
             newPolicyId,
             appId,
             lastOperationPerformed,
             app.getAppName(), // the name returned is not correct we have find the method that fixes that
             AppOpsManager.opToName(lastOperationPerformed), // Manifest.permission.ACCESS_FINE_LOCATION,
             false,
             true,
             new Timestamp(System.currentTimeMillis()),
             lowestContextList,
             1,
             currentResource
             );
             }
             }*
        }
    } */

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        context = this;
        mInProgress = false;
        mPlacesInProgress = false;
        mithrilDB = MithrilDBHelper.getHelper(context).getWritableDatabase();
        servicesAvailable = servicesConnected();
        sharedPrefs = getSharedPreferences(MithrilAC.getSharedPreferencesName(), Context.MODE_PRIVATE);
        editor = getSharedPreferences(MithrilAC.getSharedPreferencesName(), Context.MODE_PRIVATE).edit();
        // Set defaults, then update using values stored in the Bundle.
        mPlaceRequested = false;
        mAddressResultReceiver = new AddressResultReceiver(new Handler(), this);
        /* Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        setUpLocationClientIfNeeded();
        try {
            appLaunchDetector = new AppLaunchDetector(this);
            if (mTimer != null) {
                mTimer.cancel();
            } else {// recreate new
                mTimer = new Timer();
            }
        } catch (NullPointerException e) {
            Log.d(MithrilAC.getDebugTag(), "Check if we have the right permissions, we probably could not instantiate the detector");
        }
    }

    @Override
    public void onDestroy() {
        mInProgress = false;
        mPlacesInProgress = false;
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        currentPackageName = intent.getExtras().getString(MithrilAC.getCurrentPackageName());
        resourcesUsed = intent.getParcelableArrayListExtra(MithrilAC.getUsedResources());

        detectViolation();

        if (!servicesAvailable || mGoogleApiClient.isConnected() || mInProgress)
            return START_STICKY;

        setUpLocationClientIfNeeded();
        if (!mGoogleApiClient.isConnected() || !mGoogleApiClient.isConnecting() && !mInProgress) {
            mInProgress = true;
            mGoogleApiClient.connect();
        }

        if (!mGooglePlacesApiClient.isConnected() || !mGooglePlacesApiClient.isConnecting() && !mPlacesInProgress) {
            mPlacesInProgress = true;
            mGooglePlacesApiClient.connect();
        }

        mTimer.scheduleAtFixedRate(new LaunchedAppDetectTimerTask(), 0, MithrilAC.getLaunchDetectInterval());

        return START_STICKY;
    }

    private class LaunchedAppDetectTimerTask extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    pkgOpPair = appLaunchDetector.getForegroundApp(context);
                    if (pkgOpPair != null) {
                        if (sharedPrefs.contains(MithrilAC.getPrefKeyLastRunningApp())) {
                            if (!sharedPrefs.getString(MithrilAC.getPrefKeyLastRunningApp(), "").equals(pkgOpPair.first)) {
                                //last running app is not same as currently running one
                                //detect violation, if any
                                //no need to change sharedprefs
                                editor.putString(MithrilAC.getPrefKeyLastRunningApp(), pkgOpPair.first);
                                editor.apply();

                                requestLastLocation();
                                guessCurrentPlace();

                                detectViolation();
                            } else {
                                //currently running app is same as previously detected app
                                //nothing to do
                            }
                        } else {
                            //there's no known last running app
                            //add to sharedprefs currently running app and detect violation, if any
                            editor.putString(MithrilAC.getPrefKeyLastRunningApp(), pkgOpPair.first);
                            editor.apply();

                            requestLastLocation();
                            guessCurrentPlace();

                            detectViolation();
                        }
                    } else {
                        //null! nothing to do
                    }
                }
            });
        }
    }

    private boolean servicesConnected() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result))
                PermissionHelper.toast(this, "Could not connect to the Google API services");
            return false;
        }
        return true;
    }

    private void setUpLocationClientIfNeeded() {
        if (mGoogleApiClient == null)
            buildGoogleApiClient();
        if (mGooglePlacesApiClient == null)
            buildGooglePlacesApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(ViolationDetectionService.this)
                .addOnConnectionFailedListener(ViolationDetectionService.this)
                .build();
    }

    protected synchronized void buildGooglePlacesApiClient() {
        mGooglePlacesApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(ViolationDetectionService.this)
                .addOnConnectionFailedListener(ViolationDetectionService.this)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (mGoogleApiClient.isConnected())
            requestLastLocation();
        if (mGooglePlacesApiClient.isConnected())
            guessCurrentPlace();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(MithrilAC.getDebugTag(), "Disconnected. Please re-connect.");
        mInProgress = false;
        mGoogleApiClient = null;
        mGooglePlacesApiClient = null;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(MithrilAC.getDebugTag(), "Connection failed");
        mInProgress = false;
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
    }

    private List<SemanticUserContext> getSemanticContexts() {
        List<SemanticUserContext> semanticUserContextList = new ArrayList<>();

        //We are always at some location... where are we now? Also we are only in one place at a time
        if (mGoogleApiClient.isConnected()) {
            for (SemanticLocation semanticLocation : getSemanticLocations())
                semanticUserContextList.add(semanticLocation);

            //Do we know the semantic temporal contexts?
            for (SemanticTime semanticTime : getSemanticTimes())
                semanticUserContextList.add(semanticTime);

            //Do we detect any presence?
            for (SemanticNearActor semanticNearActor : getSemanticNearActors())
                semanticUserContextList.add(semanticNearActor);

            //Do we know of any activity significant to the user?
            for (SemanticActivity semanticActivity : getSemanticActivities())
                semanticUserContextList.add(semanticActivity);

            for (SemanticUserContext semanticUserContext : semanticUserContextList)
                Log.d(MithrilAC.getDebugTag(), semanticUserContext.getClass().getName());

//            Log.d(MithrilAC.getDebugTag(), "Size" + semanticUserContextList.size());
            return semanticUserContextList;
        }
        return new ArrayList<>();
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startSearchAddressIntentService(Location location) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        mPlaceRequested = true;
        intent.putExtra(MithrilAC.getPlaceRequestedExtra(), mPlaceRequested);
        intent.putExtra(MithrilAC.getCurrAddressKey(), "curr_sem_loc");

        // Pass the result receiver as an extra to the service.
        intent.putExtra(MithrilAC.getAppReceiver(), mAddressResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(MithrilAC.getLocationDataExtra(), location);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }

    private List<SemanticActivity> getSemanticActivities() {
        return new ArrayList<>();
    }

    private List<SemanticNearActor> getSemanticNearActors() {
        return new ArrayList<>();
    }

    private List<SemanticTime> getSemanticTimes() {
        Gson retrieveDataGson = new Gson();
        String retrieveDataJson;
        Map<String, SemanticTime> knownSemanticTimesMap = new HashMap<>();
        Map<String, ?> allPrefs;
        try {
            allPrefs = sharedPrefs.getAll();
            for (Map.Entry<String, ?> aPref : allPrefs.entrySet()) {
                if (aPref.getKey().startsWith(MithrilAC.getPrefKeyContextTypeTemporal())) {
                    retrieveDataJson = sharedPrefs.getString(aPref.getKey(), "");
                    SemanticTime semanticTime = retrieveDataGson.fromJson(retrieveDataJson, SemanticTime.class);
                    //Get all known semantic times
                    knownSemanticTimesMap.put(semanticTime.getLabel(), semanticTime);
                }
            }
        } catch (NullPointerException e) {
            Log.d(MithrilAC.getDebugTag(), "Prefs empty somehow?!");
        } catch (Exception e) {
            Log.d(MithrilAC.getDebugTag(), "came here");
        }
        return DetectTemporalContext.whatTimeIsItNow(knownSemanticTimesMap);
    }

    private void requestLastLocation() {
        try {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//            Log.d(MithrilAC.getDebugTag(), "Location found: "
//                    + String.valueOf(mCurrentLocation.getLatitude())
//                    + String.valueOf(mCurrentLocation.getLongitude()));
            if (mGooglePlacesApiClient.isConnected())
                guessCurrentPlace();
            else if (!mGooglePlacesApiClient.isConnected() || !mGooglePlacesApiClient.isConnecting() && !mPlacesInProgress) {
                mPlacesInProgress = true;
                mGooglePlacesApiClient.connect();
            }
        } catch (SecurityException e) {
            Log.d(MithrilAC.getDebugTag(), e.getMessage());
        }
    }

    private List<SemanticLocation> getSemanticLocations() {
        List<SemanticLocation> semanticLocations = new ArrayList<>();
        Gson retrieveDataGson = new Gson();
        String retrieveDataJson;
        List<SemanticLocation> knownSemanticLocations = new ArrayList<>();
        Map<String, ?> allPrefs;
        try {
            allPrefs = sharedPrefs.getAll();
            for (Map.Entry<String, ?> aPref : allPrefs.entrySet()) {
                if (aPref.getKey().startsWith(MithrilAC.getPrefKeyContextTypeLocation())) {
                    retrieveDataJson = sharedPrefs.getString(aPref.getKey(), "");
                    SemanticLocation knownSemanticLocation = retrieveDataGson.fromJson(retrieveDataJson, SemanticLocation.class);
                    //Get all known semantic locations
                    knownSemanticLocations.add(knownSemanticLocation);
                }
            }
        } catch (NullPointerException e) {
            Log.d(MithrilAC.getDebugTag(), "Prefs empty somehow?!");
        } catch (Exception e) {
            Log.d(MithrilAC.getDebugTag(), "came here");
        }
        /**
         * We are parsing all known locations and we know the current location's distance to them.
         * Let's determine if we are at a certain known location and at what is that location.
         */
        String placeToRetrieve = new String();
//        boolean isFound = false;
        for (SemanticLocation currSemLoc : currentSemanticLocations.values()) {
            for (SemanticLocation knownSemanticLocation : knownSemanticLocations) {
//                Log.d(MithrilAC.getDebugTag() + "knownsemloc", knownSemanticLocation.getName() + knownSemanticLocation.getPlaceId() + knownSemanticLocation.getAddress().getAddressLine(0));
//                Log.d(MithrilAC.getDebugTag() + "currsemloc", currSemLoc.getName() + currSemLoc.getPlaceId() + currSemLoc.getAddress().getAddressLine(0));
                if (knownSemanticLocation.compareTo(currSemLoc) == 0) {// && knownSemanticLocation.comparePlaceIds(currSemLoc) == 0) {
                    placeToRetrieve = knownSemanticLocation.getPlaceId();
//                    isFound = true;
                    break;
                }
            }
        }
        if (placeToRetrieve != null) {
            for (SemanticLocation knownSemanticLocation : knownSemanticLocations) {
                if (knownSemanticLocation.getPlaceId().equals(placeToRetrieve)) {
                    semanticLocations.add(knownSemanticLocation);
                    Log.d(MithrilAC.getDebugTag(), "List of locations found: " + knownSemanticLocation.getName());
                }
            }
        } else {
            semanticLocations.add(handleUnknownLocation(mCurrentLocation));
        }
        if (semanticLocations.size() > 0)
            Log.d(MithrilAC.getDebugTag(), "This is what we matched " + semanticLocations.get(0).getLabel() + semanticLocations.get(0).getName());
        return semanticLocations;
    }

    private SemanticLocation handleUnknownLocation(Location currentLocation) {
        Log.d(MithrilAC.getDebugTag(), "We are at a new location: " + mCurrentPlace.getAddress());
        SemanticLocation semanticLocation = new SemanticLocation(
                mCurrentPlace.getName().toString(),
                currentLocation,
                mCurrentPlace.getName().toString(),
                mCurrentPlace.getId(),
                mCurrentPlace.getPlaceTypes(),
                0);
        // Send notification and log the transition details.
        sendNotification(semanticLocation);
        return semanticLocation;
    }

    private void guessCurrentPlace() {
        PendingResult<PlaceLikelihoodBuffer> result;
        try {
            result = Places.PlaceDetectionApi
                    .getCurrentPlace(mGooglePlacesApiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                    float mostLikelihood = Float.MIN_VALUE;
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        if (placeLikelihood.getLikelihood() > mostLikelihood) {
                            mostLikelihood = placeLikelihood.getLikelihood();
                            mCurrentPlace = placeLikelihood.getPlace();
//                            Log.d(MithrilAC.getDebugTag(), "Place found: " + placeLikelihood.getPlace().getName() + " with likelihood: " + placeLikelihood.getLikelihood());
                        }
                    }
//                    likelyPlaces.release();
                    startSearchAddressIntentService(mCurrentLocation);
                }
            });
        } catch (SecurityException e) {
            Log.e(MithrilAC.getDebugTag(), "security exception happened");
        }
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    private void sendNotification(SemanticLocation semanticLocation) {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(this, InstanceCreationActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(CoreActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setSmallIcon(R.drawable.map_marker)
                // In a real app, you may want to use a library like Volley to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.map_marker))
                .setColor(getResources().getColor(R.color.colorPrimary, getTheme()))
                .setContentTitle(getString(R.string.we_are_at_a_new_location) + semanticLocation.getName())
                .setContentText(getString(R.string.is_this_location_important))
                .setContentIntent(notificationPendingIntent)
                .addAction(R.drawable.content_save_all, "Save", notificationPendingIntent)
                .addAction(R.drawable.delete, "Don't save", notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }

//    private class LaunchedAppDetectTimerTask extends TimerTask {
//        @Override
//        public void run() {
//            // run on another thread
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    pkgOpPair = appLaunchDetector.getForegroundApp(context);
//                    if (pkgOpPair != null) {
//                        if (sharedPrefs.contains(MithrilAC.getPrefKeyLastRunningApp())) {
//                            if (!sharedPrefs.getString(MithrilAC.getPrefKeyLastRunningApp(), "").equals(pkgOpPair.first)) {
//                                //last running app is not same as currently running one
//                                //detect violation, if any
//                                //no need to change sharedprefs
//                                editor.putString(MithrilAC.getPrefKeyLastRunningApp(), pkgOpPair.first);
//                                editor.apply();
//
//                                requestLastLocation();
//                                guessCurrentPlace();
//
//                                try {
//                                    ViolationDetector.detectViolation(
//                                            context,
//                                            pkgOpPair.first,
//                                            pkgOpPair.second,
//                                            getSemanticContexts());
//                                } catch (SemanticInconsistencyException e) {
//                                    Log.e(MithrilAC.getDebugTag(), e.getMessage());
//                                }
//                                /**
//                                 * Once we receive the result of the address search, we can detect violation
//                                 */
//                            } else {
//                                //currently running app is same as previously detected app
//                                //nothing to do
//                            }
//                        } else {
//                            //there's no known last running app
//                            //add to sharedprefs currently running app and detect violation, if any
//                            editor.putString(MithrilAC.getPrefKeyLastRunningApp(), pkgOpPair.first);
//                            editor.apply();
//
//                            requestLastLocation();
//                            guessCurrentPlace();
//
//                            try {
//                                ViolationDetector.detectViolation(
//                                        context,
//                                        pkgOpPair.first,
//                                        pkgOpPair.second,
//                                        getSemanticContexts());
//                            } catch (SemanticInconsistencyException e) {
//                                Log.e(MithrilAC.getDebugTag(), e.getMessage());
//                            }
//                        }
//                    } else {
//                        //null! nothing to do
//                    }
//                }
//            });
//        }
//    }

    private class AddressResultReceiver extends ResultReceiver {
        private Context context;
        /**
         * The formatted location address.
         */
        private Address mAddressOutput;
        /**
         * Tracks whether the user has requested an address. Becomes true when the user requests an
         * address and false when the address (or an error message) is delivered.
         * The user requests an address by pressing the Fetch Address button. This may happen
         * before GoogleApiClient connects. This activity uses this boolean to keep track of the
         * user's intent. If the value is true, the activity tries to fetch the address as soon as
         * GoogleApiClient connects.
         */
        private boolean mPlaceRequested;

        public AddressResultReceiver(Handler handler, Context aContext) {
            super(handler);

            context = aContext;
            // Set defaults, then update using values stored in the Bundle.
            mPlaceRequested = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                mAddressOutput = new Address(context.getResources().getConfiguration().getLocales().get(0));
            else
                mAddressOutput = new Address(context.getResources().getConfiguration().locale);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            mPlaceRequested = resultData.getBoolean(MithrilAC.getAddressRequestedExtra(), false);
            String key = resultData.getString(MithrilAC.getCurrAddressKey(), null);
            if (key.equals(null))
                throw new AddressKeyMissingError();
            else {
                // Display the address string
                // or an error message sent from the intent service.
                Gson gson = new Gson();
                String json = resultData.getString(MithrilAC.getResultDataKey(), "");
                try {
                    mAddressOutput = gson.fromJson(json, Address.class);
                } catch (JsonSyntaxException e) {
                    Log.d(MithrilAC.getDebugTag(), e.getMessage());
                }

//                Log.d(MithrilAC.getDebugTag(), "We are at address: " + resultData.getString(MithrilAC.getCurrAddressKey()) + mAddressOutput);
                // Show a toast message if an address was found.
                if (resultCode == MithrilAC.SUCCESS_RESULT && mCurrentPlace != null && mCurrentLocation != null) {
                    SemanticLocation tempSemanticLocation = new SemanticLocation(key, mCurrentLocation, 0);
                    tempSemanticLocation.setName(mCurrentPlace.getName().toString());
                    tempSemanticLocation.setPlaceId(mCurrentPlace.getId());
                    tempSemanticLocation.setPlaceTypes(mCurrentPlace.getPlaceTypes());
                    tempSemanticLocation.setAddress(mAddressOutput);

                    Address address = tempSemanticLocation.getAddress();
                    Location location = tempSemanticLocation.getLocation();
                    String placeId = tempSemanticLocation.getPlaceId();
                    List<Integer> placeTypes = tempSemanticLocation.getPlaceTypes();

                    currentSemanticLocations.put(key, new SemanticLocation(location, address,
                            key,
                            false, tempSemanticLocation.getName(), placeId, placeTypes, false, 0));
                    currentSemanticLocations.put(key + "_Street", new SemanticLocation(location, address,
                            key + "_Street",
                            false, address.getThoroughfare(), placeId, placeTypes, false, 1));
                    currentSemanticLocations.put(key + "_City", new SemanticLocation(location, address,
                            key + "_City",
                            false, address.getLocality(), placeId, placeTypes, false, 2));
                    currentSemanticLocations.put(key + "_State", new SemanticLocation(location, address,
                            key + "_State",
                            false, address.getAdminArea(), placeId, placeTypes, false, 3));
                    currentSemanticLocations.put(key + "_Country", new SemanticLocation(location, address,
                            key + "_Country",
                            false, address.getCountryName(), placeId, placeTypes, false, 4));
                }
                // Reset. Enable the Fetch Address button and stop showing the progress bar.
                mPlaceRequested = false;
            }
        }
    }
}