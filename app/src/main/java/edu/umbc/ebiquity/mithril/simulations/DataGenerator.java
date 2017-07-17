package edu.umbc.ebiquity.mithril.simulations;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.data.model.components.AppData;
import edu.umbc.ebiquity.mithril.data.model.rules.Action;
import edu.umbc.ebiquity.mithril.data.model.rules.PolicyRule;
import edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions.SemanticInconsistencyException;

public class DataGenerator {
    private static Action actionAllow = Action.ALLOW;
    private static Action actionDeny = Action.DENY;
    private static Map<String, Double> permValues = new HashMap<>();
    private static Map<String, Action> permActionMap = new HashMap<>();

    public static void setPolicy(SQLiteDatabase mithrilDB, Context context, AppData app) throws SemanticInconsistencyException {
        permValues = MithrilDBHelper.getHelper(context).findDefaultRulesForAppCategory(mithrilDB, app.getAppCategory());
        getPoliciesForCategory();
        int policyId = MithrilDBHelper.getHelper(context).findMaxPolicyId(mithrilDB);
        if (policyId == -1)
            policyId = 0;
        for (Map.Entry<String, Action> permActionEntry : permActionMap.entrySet()) {
            /** Apply CROWDSOURCED policy at home location */
            MithrilDBHelper.getHelper(context).addPolicyRule(
                    mithrilDB,
                    DataGenerator.createPolicyRule(
                            policyId++, // auto increment policy!
                            app.getPackageName(), app.getAppName(), // app info
                            permActionEntry.getKey(), // permission info
                            MithrilAC.getPrefHomeLocationKey(), // important context label
                            MithrilAC.getPrefKeyContextTypeLocation(), // important context type
                            permActionEntry.getValue(), // the action allow/block
                            mithrilDB, // DB ref
                            context // app context
                    )
            );
            /** Apply CROWDSOURCED policy at home location */

            /** Apply *****BLOCK***** policy during DND hours */
            MithrilDBHelper.getHelper(context).addPolicyRule(
                    mithrilDB,
                    DataGenerator.createPolicyRule(
                            policyId++, // auto increment policy!
                            app.getPackageName(), app.getAppName(), // app info
                            permActionEntry.getKey(), // permission info
                            MithrilAC.getPrefDndTemporalKey(), // important context label
                            MithrilAC.getPrefKeyContextTypeTemporal(), // important context type
                            actionDeny,//permActionEntry.getValue(), // the action allow/block
                            mithrilDB, // DB ref
                            context // app context
                    )
            );
            /** Apply *****BLOCK***** policy during DND hours */

            /** Apply CROWDSOURCED policy at work location */
            MithrilDBHelper.getHelper(context).addPolicyRule(
                    mithrilDB,
                    DataGenerator.createPolicyRule(
                            policyId++, // auto increment policy!
                            app.getPackageName(), app.getAppName(), // app info
                            permActionEntry.getKey(), // permission info
                            MithrilAC.getPrefWorkLocationKey(), // important context label
                            MithrilAC.getPrefKeyContextTypeLocation(), // important context type
                            permActionEntry.getValue(), // the action allow/block
                            mithrilDB, // DB ref
                            context // app context
                    )
            );
            /** Apply CROWDSOURCED policy at work location */

            /** Apply *****BLOCK***** policy at work location, in presence of boss */
            MithrilDBHelper.getHelper(context).addPolicyRule(
                    mithrilDB,
                    DataGenerator.createPolicyRule(
                            policyId++, // auto increment policy!
                            app.getPackageName(), app.getAppName(), // app info
                            permActionEntry.getKey(), // permission info
                            MithrilAC.getPrefWorkLocationKey(), // important context label
                            MithrilAC.getPrefKeyContextTypeLocation(), // important context type
                            actionDeny,//permActionEntry.getValue(), // the action allow/block
                            mithrilDB, // DB ref
                            context // app context
                    )
            );
            MithrilDBHelper.getHelper(context).addPolicyRule(
                    mithrilDB,
                    DataGenerator.createPolicyRule(
                            policyId, // auto increment policy!
                            app.getPackageName(), app.getAppName(), // app info
                            permActionEntry.getKey(), // permission info
                            MithrilAC.getPrefBossPresenceKey(), // important context label
                            MithrilAC.getPrefKeyContextTypePresence(), // important context type
                            actionDeny,//permActionEntry.getValue(), // the action allow/block
                            mithrilDB, // DB ref
                            context // app context
                    )
            );
            /** Apply *****BLOCK***** policy at work location, in presence of boss */
        }
    }

    public static PolicyRule createPolicyRule(long policyId,
                                              String appPkgName,
                                              String appName,
                                              String op,
                                              String contextLabel,
                                              String contextType,
                                              Action action,
                                              SQLiteDatabase mithrilDB, Context context) {
        long appId, ctxtId;
        appId = MithrilDBHelper.getHelper(context).findAppIdByAppPkgName(mithrilDB, appPkgName);
        ctxtId = MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB, contextLabel, contextType);
        Log.d(MithrilAC.getDebugTag(), "operation: " + op);
        Log.d(MithrilAC.getDebugTag(), "op: " + AppOpsManager.permissionToOpCode(op));
        Log.d(MithrilAC.getDebugTag(), "AppOps: " + AppOpsManager.opToPermission(AppOpsManager.permissionToOpCode(op)));
        if (appId == -1 || ctxtId == -1)
            return null;
        return new PolicyRule(
                policyId,
                appId,
                ctxtId,
                AppOpsManager.permissionToOpCode(op),
                action,
                action.getActionString(),
                appName,
                contextLabel,
                AppOpsManager.opToPermission(AppOpsManager.permissionToOpCode(op)),
                false
        );
    }

    private static void getPoliciesForCategory() {
        permActionMap.clear();
        getMessagesPolicies();
        getContactsPolicies();
        getMediaPolicies();
        getOverlayPolicies();
        getStoragePolicies();
        getCallingPolicies();
        getNotificationsPolicies();
        getIdentificationPolicies();
        getLocationPolicies();
        getCalendarPolicies();
    }

    private static Map<String, Action> getMessagesPolicies() {
        if (permValues.get("messages") >= 0.5) {
            permActionMap.put(Manifest.permission.ADD_VOICEMAIL, actionAllow);
            permActionMap.put(Manifest.permission.WRITE_VOICEMAIL, actionAllow);
            permActionMap.put(Manifest.permission.READ_VOICEMAIL, actionAllow);
            permActionMap.put(Manifest.permission.READ_SMS, actionAllow);
            permActionMap.put(Manifest.permission.WRITE_SMS, actionAllow);
            permActionMap.put(Manifest.permission.SEND_SMS_NO_CONFIRMATION, actionAllow);
        } else {
            permActionMap.put(Manifest.permission.ADD_VOICEMAIL, actionDeny);
            permActionMap.put(Manifest.permission.WRITE_VOICEMAIL, actionDeny);
            permActionMap.put(Manifest.permission.READ_VOICEMAIL, actionDeny);
            permActionMap.put(Manifest.permission.READ_SMS, actionDeny);
            permActionMap.put(Manifest.permission.WRITE_SMS, actionDeny);
            permActionMap.put(Manifest.permission.SEND_SMS_NO_CONFIRMATION, actionDeny);
        }
        return permActionMap;
    }

    private static Map<String, Action> getContactsPolicies() {
        if (permValues.get("contacts") >= 0.5) {
            permActionMap.put(Manifest.permission.READ_CONTACTS, actionAllow);
            permActionMap.put(Manifest.permission.WRITE_CONTACTS, actionAllow);
        } else {
            permActionMap.put(Manifest.permission.READ_CONTACTS, actionDeny);
            permActionMap.put(Manifest.permission.WRITE_CONTACTS, actionDeny);
        }
        return permActionMap;
    }

    private static Map<String, Action> getMediaPolicies() {
        if (permValues.get("media") >= 0.5) {
            permActionMap.put(Manifest.permission.CAMERA, actionAllow);
            permActionMap.put(Manifest.permission.RECORD_AUDIO, actionAllow);
            permActionMap.put(Manifest.permission.CAPTURE_VIDEO_OUTPUT, actionAllow);
            permActionMap.put(Manifest.permission.CAPTURE_SECURE_VIDEO_OUTPUT, actionAllow);
            permActionMap.put(Manifest.permission.REMOTE_AUDIO_PLAYBACK, actionAllow);
        } else {
            permActionMap.put(Manifest.permission.CAMERA, actionDeny);
            permActionMap.put(Manifest.permission.RECORD_AUDIO, actionDeny);
            permActionMap.put(Manifest.permission.CAPTURE_VIDEO_OUTPUT, actionDeny);
            permActionMap.put(Manifest.permission.CAPTURE_SECURE_VIDEO_OUTPUT, actionDeny);
            permActionMap.put(Manifest.permission.REMOTE_AUDIO_PLAYBACK, actionDeny);
        }
        return permActionMap;
    }

    private static Map<String, Action> getOverlayPolicies() {
        if (permValues.get("overlay") >= 0.5) {
            permActionMap.put(Manifest.permission.SYSTEM_ALERT_WINDOW, actionAllow);
        } else {
            permActionMap.put(Manifest.permission.SYSTEM_ALERT_WINDOW, actionDeny);
        }
        return permActionMap;
    }

    private static Map<String, Action> getStoragePolicies() {
        if (permValues.get("storage") >= 0.5) {
            permActionMap.put(Manifest.permission.WRITE_MEDIA_STORAGE, actionAllow);
            permActionMap.put(Manifest.permission.STORAGE_INTERNAL, actionAllow);
            permActionMap.put(Manifest.permission.READ_EXTERNAL_STORAGE, actionAllow);
            permActionMap.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, actionAllow);
        } else {
            permActionMap.put(Manifest.permission.WRITE_MEDIA_STORAGE, actionDeny);
            permActionMap.put(Manifest.permission.STORAGE_INTERNAL, actionDeny);
            permActionMap.put(Manifest.permission.READ_EXTERNAL_STORAGE, actionDeny);
            permActionMap.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, actionDeny);
        }
        return permActionMap;
    }

    private static Map<String, Action> getCallingPolicies() {
        if (permValues.get("calling") >= 0.5) {
            permActionMap.put(Manifest.permission.CALL_PHONE, actionAllow);
            permActionMap.put(Manifest.permission.SEND_SMS, actionAllow);
            permActionMap.put(Manifest.permission.RECEIVE_SMS, actionAllow);
            permActionMap.put(Manifest.permission.READ_CALL_LOG, actionAllow);
            permActionMap.put(Manifest.permission.WRITE_CALL_LOG, actionAllow);
        } else {
            permActionMap.put(Manifest.permission.CALL_PHONE, actionDeny);
            permActionMap.put(Manifest.permission.SEND_SMS, actionDeny);
            permActionMap.put(Manifest.permission.RECEIVE_SMS, actionDeny);
            permActionMap.put(Manifest.permission.READ_CALL_LOG, actionDeny);
            permActionMap.put(Manifest.permission.WRITE_CALL_LOG, actionDeny);
        }
        return permActionMap;
    }

    private static Map<String, Action> getNotificationsPolicies() {
        if (permValues.get("notifications") >= 0.5) {
            permActionMap.put(Manifest.permission.ACCESS_NOTIFICATIONS, actionAllow);
            permActionMap.put(Manifest.permission.ACCESS_NOTIFICATION_POLICY, actionAllow);
            permActionMap.put(Manifest.permission.MANAGE_NOTIFICATIONS, actionAllow);
        } else {
            permActionMap.put(Manifest.permission.ACCESS_NOTIFICATIONS, actionDeny);
            permActionMap.put(Manifest.permission.ACCESS_NOTIFICATION_POLICY, actionDeny);
            permActionMap.put(Manifest.permission.MANAGE_NOTIFICATIONS, actionDeny);
        }
        return permActionMap;
    }

    private static Map<String, Action> getIdentificationPolicies() {
        if (permValues.get("identification") >= 0.5) {
            permActionMap.put(Manifest.permission.GET_ACCOUNTS, actionAllow);
            permActionMap.put(Manifest.permission.GET_ACCOUNTS_PRIVILEGED, actionAllow);
            permActionMap.put(Manifest.permission.REAL_GET_TASKS, actionAllow);
        } else {
            permActionMap.put(Manifest.permission.GET_ACCOUNTS, actionDeny);
            permActionMap.put(Manifest.permission.GET_ACCOUNTS_PRIVILEGED, actionDeny);
            permActionMap.put(Manifest.permission.REAL_GET_TASKS, actionDeny);
        }
        return permActionMap;
    }

    private static Map<String, Action> getLocationPolicies() {
        if (permValues.get("location") >= 0.5) {
            permActionMap.put(Manifest.permission.LOCATION_HARDWARE, actionAllow);
            permActionMap.put(Manifest.permission.ACCESS_COARSE_LOCATION, actionAllow);
            permActionMap.put(Manifest.permission.ACCESS_FINE_LOCATION, actionAllow);
            permActionMap.put(Manifest.permission.INSTALL_LOCATION_PROVIDER, actionAllow);
            permActionMap.put(Manifest.permission.CONTROL_LOCATION_UPDATES, actionAllow);
        } else {
            permActionMap.put(Manifest.permission.LOCATION_HARDWARE, actionDeny);
            permActionMap.put(Manifest.permission.ACCESS_COARSE_LOCATION, actionDeny);
            permActionMap.put(Manifest.permission.ACCESS_FINE_LOCATION, actionDeny);
            permActionMap.put(Manifest.permission.INSTALL_LOCATION_PROVIDER, actionDeny);
            permActionMap.put(Manifest.permission.CONTROL_LOCATION_UPDATES, actionDeny);
        }
        return permActionMap;
    }

    private static Map<String, Action> getCalendarPolicies() {
        if (permValues.get("calendar") >= 0.5) {
            permActionMap.put(Manifest.permission.READ_CALENDAR, actionAllow);
            permActionMap.put(Manifest.permission.WRITE_CALENDAR, actionAllow);
        } else {
            permActionMap.put(Manifest.permission.READ_CALENDAR, actionDeny);
            permActionMap.put(Manifest.permission.WRITE_CALENDAR, actionDeny);
        }
        return permActionMap;
    }
}