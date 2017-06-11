package edu.umbc.ebiquity.mithril.simulations;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.data.model.rules.Action;
import edu.umbc.ebiquity.mithril.data.model.rules.PolicyRule;

public class DataGenerator {
    public static PolicyRule generateSocialMediaCameraAccessRuleForHome(SQLiteDatabase mithrilDB, Context context) {
        return new PolicyRule("SocialMediaCameraAccessRuleForHome",
                MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB,
                        MithrilApplication.getPrefHomeLocationKey(),
                        MithrilApplication.getPrefKeyContextTypeLocation()),
                MithrilDBHelper.getHelper(context).findAppIdByName(mithrilDB, "com.twitter.android"),
                Action.ALLOW,
                AppOpsManager.permissionToOpCode(android.Manifest.permission.CAMERA));
    }

    public static PolicyRule generateSocialMediaLocationAccessRuleForHome(SQLiteDatabase mithrilDB, Context context) {
        return new PolicyRule("SocialMediaLocationAccessRuleForHome",
                MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB,
                        MithrilApplication.getPrefHomeLocationKey(),
                        MithrilApplication.getPrefKeyContextTypeLocation()),
                MithrilDBHelper.getHelper(context).findAppIdByName(mithrilDB, "com.twitter.android"),
                Action.ALLOW,
                AppOpsManager.permissionToOpCode(Manifest.permission.ACCESS_FINE_LOCATION));
    }

    public static PolicyRule generateSocialMediaCameraAccessRuleForWork(SQLiteDatabase mithrilDB, Context context) {
        return new PolicyRule("SocialMediaCameraAccessRuleForWork",
                MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB,
                        MithrilApplication.getPrefWorkLocationKey(),
                        MithrilApplication.getPrefKeyContextTypeLocation()),
                MithrilDBHelper.getHelper(context).findAppIdByName(mithrilDB, "com.twitter.android"),
                Action.DENY,
                AppOpsManager.permissionToOpCode(android.Manifest.permission.CAMERA));
    }

    public static PolicyRule generateSocialMediaLocationAccessRuleForWork(SQLiteDatabase mithrilDB, Context context) {
        return new PolicyRule("SocialMediaLocationAccessRuleForWork",
                MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB,
                        MithrilApplication.getPrefWorkLocationKey(),
                        MithrilApplication.getPrefKeyContextTypeLocation()),
                MithrilDBHelper.getHelper(context).findAppIdByName(mithrilDB, "com.twitter.android"),
                Action.DENY,
                AppOpsManager.permissionToOpCode(Manifest.permission.ACCESS_FINE_LOCATION));
    }
}