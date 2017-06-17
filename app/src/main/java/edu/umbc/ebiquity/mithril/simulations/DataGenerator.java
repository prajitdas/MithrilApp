package edu.umbc.ebiquity.mithril.simulations;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.data.model.rules.Action;
import edu.umbc.ebiquity.mithril.data.model.rules.PolicyRule;

public class DataGenerator {
    private static Action action = Action.DENY;
    public static PolicyRule generateSocialMediaCameraAccessRuleForHome(SQLiteDatabase mithrilDB, Context context) {
        return new PolicyRule(
                0,
                MithrilDBHelper.getHelper(context).findAppIdByName(mithrilDB, "com.twitter.android"),
                "Twitter",
                MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB, MithrilAC.getPrefHomeLocationKey(), MithrilAC.getPrefKeyContextTypeLocation()),
                MithrilAC.getPrefHomeLocationKey(),
                action,
                action.getActionString(),
                AppOpsManager.permissionToOpCode(android.Manifest.permission.CAMERA),
                AppOpsManager.opToPermission(AppOpsManager.permissionToOpCode(android.Manifest.permission.CAMERA)),
                true
        );
    }

    public static PolicyRule generateSocialMediaLocationAccessRuleForHome(SQLiteDatabase mithrilDB, Context context) {
        return new PolicyRule(
                0,
                MithrilDBHelper.getHelper(context).findAppIdByName(mithrilDB, "com.twitter.android"),
                "Twitter",
                MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB, MithrilAC.getPrefHomeLocationKey(), MithrilAC.getPrefKeyContextTypeLocation()),
                MithrilAC.getPrefHomeLocationKey(),
                action,
                action.getActionString(),
                AppOpsManager.permissionToOpCode(android.Manifest.permission.ACCESS_FINE_LOCATION),
                AppOpsManager.opToPermission(AppOpsManager.permissionToOpCode(android.Manifest.permission.ACCESS_FINE_LOCATION)),
                true
        );
    }

    public static PolicyRule generateSocialMediaCameraAccessRuleForWork(SQLiteDatabase mithrilDB, Context context) {
        return new PolicyRule(
                0,
                MithrilDBHelper.getHelper(context).findAppIdByName(mithrilDB, "com.twitter.android"),
                "Twitter",
                MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB, MithrilAC.getPrefWorkLocationKey(), MithrilAC.getPrefKeyContextTypeLocation()),
                MithrilAC.getPrefWorkLocationKey(),
                action,
                action.getActionString(),
                AppOpsManager.permissionToOpCode(android.Manifest.permission.CAMERA),
                AppOpsManager.opToPermission(AppOpsManager.permissionToOpCode(android.Manifest.permission.CAMERA)),
                true
        );
    }

    public static PolicyRule generateSocialMediaLocationAccessRuleForWork(SQLiteDatabase mithrilDB, Context context) {
        return new PolicyRule(
                0,
                MithrilDBHelper.getHelper(context).findAppIdByName(mithrilDB, "com.twitter.android"),
                "Twitter",
                MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB, MithrilAC.getPrefWorkLocationKey(), MithrilAC.getPrefKeyContextTypeLocation()),
                MithrilAC.getPrefWorkLocationKey(),
                action,
                action.getActionString(),
                AppOpsManager.permissionToOpCode(android.Manifest.permission.ACCESS_FINE_LOCATION),
                AppOpsManager.opToPermission(AppOpsManager.permissionToOpCode(android.Manifest.permission.ACCESS_FINE_LOCATION)),
                true
        );
    }

    public static PolicyRule generateEmailClientLocationAccessRuleForWork(SQLiteDatabase mithrilDB, Context context) {
        return new PolicyRule(
                0,
                MithrilDBHelper.getHelper(context).findAppIdByName(mithrilDB, "com.google.android.gm"),
                "Gmail",
                MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB, MithrilAC.getPrefWorkLocationKey(), MithrilAC.getPrefKeyContextTypeLocation()),
                MithrilAC.getPrefWorkLocationKey(),
                action,
                action.getActionString(),
                AppOpsManager.permissionToOpCode(android.Manifest.permission.ACCESS_FINE_LOCATION),
                AppOpsManager.opToPermission(AppOpsManager.permissionToOpCode(android.Manifest.permission.ACCESS_FINE_LOCATION)),
                true
        );
    }

    public static PolicyRule generateEmailClientReadContactsAccessRuleForWork(SQLiteDatabase mithrilDB, Context context) {
        return new PolicyRule(
                0,
                MithrilDBHelper.getHelper(context).findAppIdByName(mithrilDB, "com.google.android.gm"),
                "Gmail",
                MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB, MithrilAC.getPrefWorkLocationKey(), MithrilAC.getPrefKeyContextTypeLocation()),
                MithrilAC.getPrefWorkLocationKey(),
                action,
                action.getActionString(),
                AppOpsManager.permissionToOpCode(android.Manifest.permission.READ_CONTACTS),
                AppOpsManager.opToPermission(AppOpsManager.permissionToOpCode(android.Manifest.permission.READ_CONTACTS)),
                true
        );
    }

    public static PolicyRule generateEmailClientWriteContactsAccessRuleForWork(SQLiteDatabase mithrilDB, Context context) {
        return new PolicyRule(
                0,
                MithrilDBHelper.getHelper(context).findAppIdByName(mithrilDB, "com.google.android.gm"),
                "Gmail",
                MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB, MithrilAC.getPrefWorkLocationKey(), MithrilAC.getPrefKeyContextTypeLocation()),
                MithrilAC.getPrefWorkLocationKey(),
                action,
                action.getActionString(),
                AppOpsManager.permissionToOpCode(android.Manifest.permission.WRITE_CONTACTS),
                AppOpsManager.opToPermission(AppOpsManager.permissionToOpCode(android.Manifest.permission.WRITE_CONTACTS)),
                true
        );
    }

    public static PolicyRule generateEmailClientWriteStorageAccessRuleForWork(SQLiteDatabase mithrilDB, Context context) {
        return new PolicyRule(
                0,
                MithrilDBHelper.getHelper(context).findAppIdByName(mithrilDB, "com.google.android.gm"),
                "Gmail",
                MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB, MithrilAC.getPrefWorkLocationKey(), MithrilAC.getPrefKeyContextTypeLocation()),
                MithrilAC.getPrefWorkLocationKey(),
                action,
                action.getActionString(),
                AppOpsManager.permissionToOpCode(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                AppOpsManager.opToPermission(AppOpsManager.permissionToOpCode(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)),
                true
        );
    }

    public static PolicyRule generateEmailClientReadStorageAccessRuleForWork(SQLiteDatabase mithrilDB, Context context) {
        return new PolicyRule(
                0,
                MithrilDBHelper.getHelper(context).findAppIdByName(mithrilDB, "com.google.android.gm"),
                "Gmail",
                MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB, MithrilAC.getPrefWorkLocationKey(), MithrilAC.getPrefKeyContextTypeLocation()),
                MithrilAC.getPrefWorkLocationKey(),
                action,
                action.getActionString(),
                AppOpsManager.permissionToOpCode(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                AppOpsManager.opToPermission(AppOpsManager.permissionToOpCode(android.Manifest.permission.READ_EXTERNAL_STORAGE)),
                true);
    }
}