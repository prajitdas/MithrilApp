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
        SQLiteDatabase mithrilDB = MithrilDBHelper.getHelper(context).getWritableDatabase();

        String name = "SocialMediaCameraAccessRuleForHome";
        int ctxId = MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB,
                MithrilApplication.getPrefHomeLocationKey(),
                MithrilApplication.getPrefKeyLocation());
        int appId = MithrilDBHelper.getHelper(context).findAppIdByName(mithrilDB, "com.twitter.android");
        Action action = Action.ALLOW;
        PolicyRule policyRule = new PolicyRule(name, ctxId, appId, action, AppOpsManager.permissionToOpCode(android.Manifest.permission.CAMERA));

        closeDB(mithrilDB);
        return policyRule;
    }

    public static PolicyRule generateSocialMediaLocationAccessRuleForHome(Context context) {
        SQLiteDatabase mithrilDB = MithrilDBHelper.getHelper(context).getWritableDatabase();

        String name = "SocialMediaLocationAccessRuleForHome";
        int ctxId = MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB,
                MithrilApplication.getPrefHomeLocationKey(),
                MithrilApplication.getPrefKeyLocation());
        int appId = MithrilDBHelper.getHelper(context).findAppIdByName(mithrilDB, "com.twitter.android");
        Action action = Action.ALLOW;
        PolicyRule policyRule = new PolicyRule(name, ctxId, appId, action, AppOpsManager.permissionToOpCode(Manifest.permission.ACCESS_FINE_LOCATION));

        closeDB(mithrilDB);
        return policyRule;
    }

    public static PolicyRule generateSocialMediaCameraAccessRuleForWork(Context context) {
        SQLiteDatabase mithrilDB = MithrilDBHelper.getHelper(context).getWritableDatabase();

        String name = "SocialMediaCameraAccessRuleForWork";
        int ctxId = MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB,
                MithrilApplication.getPrefWorkLocationKey(),
                MithrilApplication.getPrefKeyLocation());
        int appId = MithrilDBHelper.getHelper(context).findAppIdByName(mithrilDB, "com.twitter.android");
        Action action = Action.DENY;
        PolicyRule policyRule = new PolicyRule(name, ctxId, appId, action, AppOpsManager.permissionToOpCode(android.Manifest.permission.CAMERA));

        closeDB(mithrilDB);
        return policyRule;
    }

    public static PolicyRule generateSocialMediaLocationAccessRuleForWork(Context context) {
        SQLiteDatabase mithrilDB = MithrilDBHelper.getHelper(context).getWritableDatabase();

        String name = "SocialMediaLocationAccessRuleForWork";
        int ctxId = MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB,
                MithrilApplication.getPrefWorkLocationKey(),
                MithrilApplication.getPrefKeyLocation());
        int appId = MithrilDBHelper.getHelper(context).findAppIdByName(mithrilDB, "com.twitter.android");
        Action action = Action.DENY;
        PolicyRule policyRule = new PolicyRule(name, ctxId, appId, action, AppOpsManager.permissionToOpCode(Manifest.permission.ACCESS_FINE_LOCATION));

        closeDB(mithrilDB);
        return policyRule;
    }

    private static void closeDB(SQLiteDatabase mithrilDB) {
        if (mithrilDB != null)
            mithrilDB.close();
    }
}