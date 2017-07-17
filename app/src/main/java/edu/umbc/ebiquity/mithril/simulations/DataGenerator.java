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
                            ++policyId, // auto increment policy!
                            app.getPackageName(), app.getAppName(), // app info
                            permActionEntry.getKey(), // permission info
                            MithrilAC.getPrefHomeLocationKey(), // important context label
                            MithrilAC.getPrefKeyContextTypeLocation(), // important context type
                            permActionEntry.getValue(), // the action allow/block
                            true, // Enabling the default policies
                            mithrilDB, // DB ref
                            context // app context
                    )
            );
            /** Apply CROWDSOURCED policy at home location */

            /** Apply *****BLOCK***** policy during DND hours */
            MithrilDBHelper.getHelper(context).addPolicyRule(
                    mithrilDB,
                    DataGenerator.createPolicyRule(
                            ++policyId, // auto increment policy!
                            app.getPackageName(), app.getAppName(), // app info
                            permActionEntry.getKey(), // permission info
                            MithrilAC.getPrefDndTemporalKey(), // important context label
                            MithrilAC.getPrefKeyContextTypeTemporal(), // important context type
                            actionDeny,//permActionEntry.getValue(), // the action allow/block
                            true, // Enabling the default policies
                            mithrilDB, // DB ref
                            context // app context
                    )
            );
            /** Apply *****BLOCK***** policy during DND hours */

            /** Apply CROWDSOURCED policy at work location */
            MithrilDBHelper.getHelper(context).addPolicyRule(
                    mithrilDB,
                    DataGenerator.createPolicyRule(
                            ++policyId, // auto increment policy!
                            app.getPackageName(), app.getAppName(), // app info
                            permActionEntry.getKey(), // permission info
                            MithrilAC.getPrefWorkLocationKey(), // important context label
                            MithrilAC.getPrefKeyContextTypeLocation(), // important context type
                            permActionEntry.getValue(), // the action allow/block
                            true, // Enabling the default policies
                            mithrilDB, // DB ref
                            context // app context
                    )
            );
            /** Apply CROWDSOURCED policy at work location */

            /** Apply *****BLOCK***** policy at work location, in presence of boss */
            MithrilDBHelper.getHelper(context).addPolicyRule(
                    mithrilDB,
                    DataGenerator.createPolicyRule(
                            ++policyId, // auto increment policy!
                            app.getPackageName(), app.getAppName(), // app info
                            permActionEntry.getKey(), // permission info
                            MithrilAC.getPrefWorkLocationKey(), // important context label
                            MithrilAC.getPrefKeyContextTypeLocation(), // important context type
                            actionDeny,//permActionEntry.getValue(), // the action allow/block
                            true, // Enabling the default policies
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
                            true, // Enabling the default policies
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
                                              boolean enabled,
                                              SQLiteDatabase mithrilDB,
                                              Context context) {
        long appId, ctxtId;
        appId = MithrilDBHelper.getHelper(context).findAppIdByAppPkgName(mithrilDB, appPkgName);
        ctxtId = MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB, contextLabel, contextType);
        int opCode = AppOpsManager.permissionToOpCode(op);
        if (op.equals(Manifest.permission.SYSTEM_ALERT_WINDOW))
            opCode = 24;
        else if(op.equals(Manifest.permission.ACCESS_NOTIFICATIONS))
            opCode = 25;
        if (appId == -1 || ctxtId == -1)
            return null;
        return new PolicyRule(
                policyId,
                appId,
                ctxtId,
                opCode,
                action,
                action.getActionString(),
                appName,
                contextLabel,
                op,
                enabled
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
            permActionMap.put(android.Manifest.permission.READ_SMS, actionAllow);
        } else {
            permActionMap.put(Manifest.permission.ADD_VOICEMAIL, actionDeny);
            permActionMap.put(android.Manifest.permission.READ_SMS, actionDeny);
        }
        return permActionMap;
    }

    private static Map<String, Action> getContactsPolicies() {
        if (permValues.get("contacts") >= 0.5) {
            permActionMap.put(android.Manifest.permission.READ_CONTACTS, actionAllow);
            permActionMap.put(android.Manifest.permission.WRITE_CONTACTS, actionAllow);
        } else {
            permActionMap.put(android.Manifest.permission.READ_CONTACTS, actionDeny);
            permActionMap.put(android.Manifest.permission.WRITE_CONTACTS, actionDeny);
        }
        return permActionMap;
    }

    private static Map<String, Action> getMediaPolicies() {
        if (permValues.get("media") >= 0.5) {
            permActionMap.put(android.Manifest.permission.CAMERA, actionAllow);
            permActionMap.put(android.Manifest.permission.RECORD_AUDIO, actionAllow);
        } else {
            permActionMap.put(android.Manifest.permission.CAMERA, actionDeny);
            permActionMap.put(android.Manifest.permission.RECORD_AUDIO, actionDeny);
        }
        return permActionMap;
    }

    private static Map<String, Action> getOverlayPolicies() {
        if (permValues.get("overlay") >= 0.5) {
            permActionMap.put(android.Manifest.permission.SYSTEM_ALERT_WINDOW, actionAllow);
        } else {
            permActionMap.put(android.Manifest.permission.SYSTEM_ALERT_WINDOW, actionDeny);
        }
        return permActionMap;
    }

    private static Map<String, Action> getStoragePolicies() {
        if (permValues.get("storage") >= 0.5) {
            permActionMap.put(Manifest.permission.READ_EXTERNAL_STORAGE, actionAllow);
            permActionMap.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, actionAllow);
        } else {
            permActionMap.put(Manifest.permission.READ_EXTERNAL_STORAGE, actionDeny);
            permActionMap.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, actionDeny);
        }
        return permActionMap;
    }

    private static Map<String, Action> getCallingPolicies() {
        if (permValues.get("calling") >= 0.5) {
            permActionMap.put(android.Manifest.permission.CALL_PHONE, actionAllow);
            permActionMap.put(android.Manifest.permission.SEND_SMS, actionAllow);
            permActionMap.put(android.Manifest.permission.RECEIVE_SMS, actionAllow);
            permActionMap.put(android.Manifest.permission.READ_CALL_LOG, actionAllow);
            permActionMap.put(android.Manifest.permission.WRITE_CALL_LOG, actionAllow);
        } else {
            permActionMap.put(android.Manifest.permission.CALL_PHONE, actionDeny);
            permActionMap.put(android.Manifest.permission.SEND_SMS, actionDeny);
            permActionMap.put(android.Manifest.permission.RECEIVE_SMS, actionDeny);
            permActionMap.put(android.Manifest.permission.READ_CALL_LOG, actionDeny);
            permActionMap.put(android.Manifest.permission.WRITE_CALL_LOG, actionDeny);
        }
        return permActionMap;
    }

    private static Map<String, Action> getNotificationsPolicies() {
        if (permValues.get("notifications") >= 0.5) {
            permActionMap.put(android.Manifest.permission.ACCESS_NOTIFICATIONS, actionAllow);
        } else {
            permActionMap.put(android.Manifest.permission.ACCESS_NOTIFICATIONS, actionDeny);
        }
        return permActionMap;
    }

    private static Map<String, Action> getIdentificationPolicies() {
        if (permValues.get("identification") >= 0.5) {
            permActionMap.put(Manifest.permission.GET_ACCOUNTS, actionAllow);
        } else {
            permActionMap.put(Manifest.permission.GET_ACCOUNTS, actionDeny);
        }
        return permActionMap;
    }

    private static Map<String, Action> getLocationPolicies() {
        if (permValues.get("location") >= 0.5) {
            permActionMap.put(android.Manifest.permission.ACCESS_COARSE_LOCATION, actionAllow);
            permActionMap.put(android.Manifest.permission.ACCESS_FINE_LOCATION, actionAllow);
        } else {
            permActionMap.put(android.Manifest.permission.ACCESS_COARSE_LOCATION, actionDeny);
            permActionMap.put(android.Manifest.permission.ACCESS_FINE_LOCATION, actionDeny);
        }
        return permActionMap;
    }

    private static Map<String, Action> getCalendarPolicies() {
        if (permValues.get("calendar") >= 0.5) {
            permActionMap.put(android.Manifest.permission.READ_CALENDAR, actionAllow);
            permActionMap.put(android.Manifest.permission.WRITE_CALENDAR, actionAllow);
        } else {
            permActionMap.put(android.Manifest.permission.READ_CALENDAR, actionDeny);
            permActionMap.put(android.Manifest.permission.WRITE_CALENDAR, actionDeny);
        }
        return permActionMap;
    }
}