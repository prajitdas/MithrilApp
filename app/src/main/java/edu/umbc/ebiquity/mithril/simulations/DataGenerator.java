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
    public static PolicyRule generateSocialMediaCameraAccessRuleForHome(Context context) {
        MithrilDBHelper mithrilDBHelper = new MithrilDBHelper(context);
        SQLiteDatabase mithrilDB = mithrilDBHelper.getWritableDatabase();

        String name = "SocialMediaCameraAccessRuleForHome";
        int ctxId = mithrilDBHelper.findContextIdByLabelAndType(mithrilDB,
                MithrilApplication.getPrefHomeLocationKey(),
                MithrilApplication.getPrefKeyLocation());
        int appId = mithrilDBHelper.findAppIdByName(mithrilDB, "com.twitter.android");
        Action action = Action.ALLOW;
        PolicyRule policyRule = new PolicyRule(name, ctxId, appId, action, AppOpsManager.permissionToOpCode(android.Manifest.permission.CAMERA));
        return policyRule;
    }

    public static PolicyRule generateSocialMediaLocationAccessRuleForHome(Context context) {
        MithrilDBHelper mithrilDBHelper = new MithrilDBHelper(context);
        SQLiteDatabase mithrilDB = mithrilDBHelper.getWritableDatabase();

        String name = "SocialMediaLocationAccessRuleForHome";
        int ctxId = mithrilDBHelper.findContextIdByLabelAndType(mithrilDB,
                MithrilApplication.getPrefHomeLocationKey(),
                MithrilApplication.getPrefKeyLocation());
        int appId = mithrilDBHelper.findAppIdByName(mithrilDB, "com.twitter.android");
        Action action = Action.ALLOW;
        PolicyRule policyRule = new PolicyRule(name, ctxId, appId, action, AppOpsManager.permissionToOpCode(Manifest.permission.ACCESS_FINE_LOCATION));
        return policyRule;
    }

    public static PolicyRule generateSocialMediaCameraAccessRuleForWork(Context context) {
        MithrilDBHelper mithrilDBHelper = new MithrilDBHelper(context);
        SQLiteDatabase mithrilDB = mithrilDBHelper.getWritableDatabase();

        String name = "SocialMediaCameraAccessRuleForWork";
        int ctxId = mithrilDBHelper.findContextIdByLabelAndType(mithrilDB,
                MithrilApplication.getPrefWorkLocationKey(),
                MithrilApplication.getPrefKeyLocation());
        int appId = mithrilDBHelper.findAppIdByName(mithrilDB, "com.twitter.android");
        Action action = Action.DENY;
        PolicyRule policyRule = new PolicyRule(name, ctxId, appId, action, AppOpsManager.permissionToOpCode(android.Manifest.permission.CAMERA));
        return policyRule;
    }

    public static PolicyRule generateSocialMediaLocationAccessRuleForWork(Context context) {
        MithrilDBHelper mithrilDBHelper = new MithrilDBHelper(context);
        SQLiteDatabase mithrilDB = mithrilDBHelper.getWritableDatabase();

        String name = "SocialMediaLocationAccessRuleForWork";
        int ctxId = mithrilDBHelper.findContextIdByLabelAndType(mithrilDB,
                MithrilApplication.getPrefWorkLocationKey(),
                MithrilApplication.getPrefKeyLocation());
        int appId = mithrilDBHelper.findAppIdByName(mithrilDB, "com.twitter.android");
        Action action = Action.DENY;
        PolicyRule policyRule = new PolicyRule(name, ctxId, appId, action, AppOpsManager.permissionToOpCode(Manifest.permission.ACCESS_FINE_LOCATION));
        return policyRule;
    }
}