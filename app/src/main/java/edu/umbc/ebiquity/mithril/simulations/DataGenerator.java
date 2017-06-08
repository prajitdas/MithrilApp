package edu.umbc.ebiquity.mithril.simulations;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.data.model.rules.Action;
import edu.umbc.ebiquity.mithril.data.model.PolicyRule;

public class DataGenerator {
    /**
     * This data generator is used in the loadDefaultDataIntoDB method in {@link MithrilDBHelper} to generate default policies
     * @return
     */
    public static PolicyRule generateSocialMediaCameraAccessRule(Context context) {
        MithrilDBHelper mithrilDBHelper = new MithrilDBHelper(context);
        SQLiteDatabase mithrilDB = mithrilDBHelper.getWritableDatabase();

        String name = "SocialMediaCameraAccessRule";
        int ctxId = mithrilDBHelper.findContextIdByLabelAndType(mithrilDB,
                MithrilApplication.getPrefHomeLocationKey(),
                MithrilApplication.getPrefHomeLocationKey());
        int appId = mithrilDBHelper.findAppIdByName(mithrilDB, "com.twitter.android");
        Action action = Action.DENY;
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        PolicyRule policyRule = new PolicyRule(name, ctxId, appId, action, AppOpsManager.permissionToOpCode(android.Manifest.permission.CAMERA));
        return policyRule;
    }

    public static PolicyRule generateSocialMediaLocationAccessRule(Context context) {
        MithrilDBHelper mithrilDBHelper = new MithrilDBHelper(context);
        SQLiteDatabase mithrilDB = mithrilDBHelper.getWritableDatabase();

        String name = "SocialMediaCameraAccessRule";
        int ctxId = mithrilDBHelper.findContextIdByLabelAndType(mithrilDB,
                MithrilApplication.getPrefHomeLocationKey(),
                MithrilApplication.getPrefHomeLocationKey());
        int appId = mithrilDBHelper.findAppIdByName(mithrilDB, "com.twitter.android");
        Action action = Action.DENY;
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        PolicyRule policyRule = new PolicyRule(name, ctxId, appId, action, AppOpsManager.permissionToOpCode(Manifest.permission.ACCESS_FINE_LOCATION));
        return policyRule;
    }
}