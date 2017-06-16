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
    public static PolicyRule generateSocialMediaCameraAccessRuleForHome(SQLiteDatabase mithrilDB, Context context) {
        return new PolicyRule("Camera used at Home",
                MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB,
                        MithrilAC.getPrefHomeLocationKey(),
                        MithrilAC.getPrefKeyContextTypeLocation()),
                MithrilDBHelper.getHelper(context).findAppIdByName(mithrilDB, "com.twitter.android"),
                Action.DENY,
                AppOpsManager.permissionToOpCode(android.Manifest.permission.CAMERA));
    }

    public static PolicyRule generateSocialMediaLocationAccessRuleForHome(SQLiteDatabase mithrilDB, Context context) {
        Log.d(MithrilAC.getDebugTag(), MithrilAC.getPrefHomeLocationKey() + MithrilAC.getPrefKeyContextTypeLocation());
        return new PolicyRule("Location accessed at Home",
                MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB,
                        MithrilAC.getPrefHomeLocationKey(),
                        MithrilAC.getPrefKeyContextTypeLocation()),
                MithrilDBHelper.getHelper(context).findAppIdByName(mithrilDB, "com.twitter.android"),
                Action.DENY,
                AppOpsManager.permissionToOpCode(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    public static PolicyRule generateSocialMediaCameraAccessRuleForWork(SQLiteDatabase mithrilDB, Context context) {
        return new PolicyRule("Camera used at Work",
                MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB,
                        MithrilAC.getPrefWorkLocationKey(),
                        MithrilAC.getPrefKeyContextTypeLocation()),
                MithrilDBHelper.getHelper(context).findAppIdByName(mithrilDB, "com.twitter.android"),
                Action.DENY,
                AppOpsManager.permissionToOpCode(android.Manifest.permission.CAMERA));
    }

    public static PolicyRule generateSocialMediaLocationAccessRuleForWork(SQLiteDatabase mithrilDB, Context context) {
        return new PolicyRule("Location accessed at Work",
                MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB,
                        MithrilAC.getPrefWorkLocationKey(),
                        MithrilAC.getPrefKeyContextTypeLocation()),
                MithrilDBHelper.getHelper(context).findAppIdByName(mithrilDB, "com.twitter.android"),
                Action.DENY,
                AppOpsManager.permissionToOpCode(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    public static PolicyRule generateEmailClientLocationAccessRuleForWork(SQLiteDatabase mithrilDB, Context context) {
        return new PolicyRule("Location accessed at Work",
                MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB,
                        MithrilAC.getPrefWorkLocationKey(),
                        MithrilAC.getPrefKeyContextTypeLocation()),
                MithrilDBHelper.getHelper(context).findAppIdByName(mithrilDB, "com.google.android.gm"),
                Action.DENY,
                AppOpsManager.permissionToOpCode(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    public static PolicyRule generateEmailClientReadContactsAccessRuleForWork(SQLiteDatabase mithrilDB, Context context) {
        return new PolicyRule("Contacts read at Work",
                MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB,
                        MithrilAC.getPrefWorkLocationKey(),
                        MithrilAC.getPrefKeyContextTypeLocation()),
                MithrilDBHelper.getHelper(context).findAppIdByName(mithrilDB, "com.google.android.gm"),
                Action.DENY,
                AppOpsManager.permissionToOpCode(Manifest.permission.READ_CONTACTS));
    }

    public static PolicyRule generateEmailClientWriteContactsAccessRuleForWork(SQLiteDatabase mithrilDB, Context context) {
        return new PolicyRule("Contacts saved at Work",
                MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB,
                        MithrilAC.getPrefWorkLocationKey(),
                        MithrilAC.getPrefKeyContextTypeLocation()),
                MithrilDBHelper.getHelper(context).findAppIdByName(mithrilDB, "com.google.android.gm"),
                Action.DENY,
                AppOpsManager.permissionToOpCode(Manifest.permission.WRITE_CONTACTS));
    }

    public static PolicyRule generateEmailClientWriteStorageAccessRuleForWork(SQLiteDatabase mithrilDB, Context context) {
        return new PolicyRule("Saved to phone storage at Work",
                MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB,
                        MithrilAC.getPrefWorkLocationKey(),
                        MithrilAC.getPrefKeyContextTypeLocation()),
                MithrilDBHelper.getHelper(context).findAppIdByName(mithrilDB, "com.google.android.gm"),
                Action.DENY,
                AppOpsManager.permissionToOpCode(Manifest.permission.WRITE_EXTERNAL_STORAGE));
    }

    public static PolicyRule generateEmailClientReadStorageAccessRuleForWork(SQLiteDatabase mithrilDB, Context context) {
        return new PolicyRule("Read phone storage at Work",
                MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB,
                        MithrilAC.getPrefWorkLocationKey(),
                        MithrilAC.getPrefKeyContextTypeLocation()),
                MithrilDBHelper.getHelper(context).findAppIdByName(mithrilDB, "com.google.android.gm"),
                Action.DENY,
                AppOpsManager.permissionToOpCode(Manifest.permission.READ_EXTERNAL_STORAGE));
    }
}