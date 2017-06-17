package edu.umbc.ebiquity.mithril.simulations;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.data.model.rules.Action;
import edu.umbc.ebiquity.mithril.data.model.rules.PolicyRule;

public class DataGenerator {
    /************ Social Media apps to be allowed camera access at home if it's the weekend ************/
    public static void setPolicySocialMediaCameraAccessAtHomeOnWeekends(SQLiteDatabase mithrilDB, Context context) {
        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(1,
                        "com.twitter.android", "Twitter",
                        Manifest.permission.CAMERA,
                        MithrilAC.getPrefHomeLocationKey(), MithrilAC.getPrefKeyContextTypeLocation(),
                        actionAllow, mithrilDB, context));
        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(1,
                        "com.twitter.android", "Twitter",
                        Manifest.permission.CAMERA,
                        MithrilAC.getPrefTimeIntervalWeekendTemporalKey(), MithrilAC.getPrefKeyContextTypeTemporal(),
                        actionAllow, mithrilDB, context));
    }

    /************ Social Media apps to be allowed camera access at work if it's a weekday and it's lunch hours ************/
    public static void setPolicySocialMediaCameraAccessAtWorkOnWeekdaysDuringLunchHours(SQLiteDatabase mithrilDB, Context context) {
        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(2,
                        "com.twitter.android", "Twitter",
                        Manifest.permission.CAMERA,
                        MithrilAC.getPrefWorkLocationKey(), MithrilAC.getPrefKeyContextTypeLocation(),
                        actionAllow, mithrilDB, context));
        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(2,
                        "com.twitter.android", "Twitter",
                        Manifest.permission.CAMERA,
                        MithrilAC.getPrefTimeIntervalWeekdayTemporalKey(), MithrilAC.getPrefKeyContextTypeTemporal(),
                        actionAllow, mithrilDB, context));
        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(2,
                        "com.twitter.android", "Twitter",
                        Manifest.permission.CAMERA,
                        MithrilAC.getPrefTimeIntervalLunchTemporalKey(), MithrilAC.getPrefKeyContextTypeTemporal(),
                        actionAllow, mithrilDB, context));
    }

    /************ Social Media apps to be allowed location access at home if it's a weekday and it's evening time ************/
    public static void setPolicySocialMediaLocationAccessAtHomeOnWeekdaysDuringEveningHours(SQLiteDatabase mithrilDB, Context context) {
        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(3,
                        "com.twitter.android", "Twitter",
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        MithrilAC.getPrefWorkLocationKey(), MithrilAC.getPrefKeyContextTypeLocation(),
                        actionAllow, mithrilDB, context));
        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(3,
                        "com.twitter.android", "Twitter",
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        MithrilAC.getPrefTimeIntervalWeekdayTemporalKey(), MithrilAC.getPrefKeyContextTypeTemporal(),
                        actionAllow, mithrilDB, context));
        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(3,
                        "com.twitter.android", "Twitter",
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        MithrilAC.getPrefTimeIntervalEveningTemporalKey(), MithrilAC.getPrefKeyContextTypeTemporal(),
                        actionAllow, mithrilDB, context));
    }

    /************ Chat apps to be allowed sms access at work if it's a weekday and it's lunch hours ************/
    public static void setPolicyChatAppsReadSmsAccessAtWork(SQLiteDatabase mithrilDB, Context context) {
        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(4,
                        "com.google.android.talk", "Hangouts",
                        Manifest.permission.READ_SMS,
                        MithrilAC.getPrefWorkLocationKey(), MithrilAC.getPrefKeyContextTypeLocation(),
                        actionAllow, mithrilDB, context));
    }

    public static void setPolicyChatAppsWriteSmsAccessAtWork(SQLiteDatabase mithrilDB, Context context) {
        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(5,
                        "com.google.android.talk", "Hangouts",
                        Manifest.permission.WRITE_SMS,
                        MithrilAC.getPrefWorkLocationKey(), MithrilAC.getPrefKeyContextTypeLocation(),
                        actionAllow, mithrilDB, context));
    }

    public static void setPolicyChatAppsReceiveSmsAccessAtWork(SQLiteDatabase mithrilDB, Context context) {
        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(6,
                        "com.google.android.talk", "Hangouts",
                        Manifest.permission.RECEIVE_SMS,
                        MithrilAC.getPrefWorkLocationKey(), MithrilAC.getPrefKeyContextTypeLocation(),
                        actionAllow, mithrilDB, context));
    }

    public static void setPolicyChatAppsSendSmsAccessAtWork(SQLiteDatabase mithrilDB, Context context) {
        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(7,
                        "com.google.android.talk", "Hangouts",
                        Manifest.permission.SEND_SMS,
                        MithrilAC.getPrefWorkLocationKey(), MithrilAC.getPrefKeyContextTypeLocation(),
                        actionAllow, mithrilDB, context));
    }

    /************ Email clients to be allowed calendar access at work if it's a weekday ************/
    public static void setPolicyEmailClientsReadCalendarAccessAtWorkDuringWeekdays(SQLiteDatabase mithrilDB, Context context) {
        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(8,
                        "com.google.android.gm", "Gmail",
                        Manifest.permission.READ_CALENDAR,
                        MithrilAC.getPrefWorkLocationKey(), MithrilAC.getPrefKeyContextTypeLocation(),
                        actionAllow, mithrilDB, context));
        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(8,
                        "com.google.android.gm", "Gmail",
                        Manifest.permission.READ_CALENDAR,
                        MithrilAC.getPrefTimeIntervalWeekdayTemporalKey(), MithrilAC.getPrefKeyContextTypeTemporal(),
                        actionAllow, mithrilDB, context));
    }

    public static void setPolicyEmailClientsWriteCalendarAccessAtWorkDuringWeekdays(SQLiteDatabase mithrilDB, Context context) {
        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(9,
                        "com.google.android.gm", "Gmail",
                        Manifest.permission.WRITE_CALENDAR,
                        MithrilAC.getPrefWorkLocationKey(), MithrilAC.getPrefKeyContextTypeLocation(),
                        actionAllow, mithrilDB, context));
        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(9,
                        "com.google.android.gm", "Gmail",
                        Manifest.permission.WRITE_CALENDAR,
                        MithrilAC.getPrefTimeIntervalWeekdayTemporalKey(), MithrilAC.getPrefKeyContextTypeTemporal(),
                        actionAllow, mithrilDB, context));
    }

    /************************************************ End of policies ************************************************/
    private static Action actionAllow = Action.ALLOW;
    private static Action actionDeny = Action.DENY;
    private static PolicyRule createPolicyRule(int policyId,
                                               String appPkgName,
                                               String appName,
                                               String permString,
                                               String contextLabel,
                                               String contextType,
                                               Action action,
                                               SQLiteDatabase mithrilDB, Context context) {
        return new PolicyRule(
                policyId,
                MithrilDBHelper.getHelper(context).findAppIdByName(mithrilDB, appPkgName),
                appName,
                MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB, contextLabel, contextType),
                contextLabel,
                action,
                action.getActionString(),
                AppOpsManager.permissionToOpCode(permString),
                AppOpsManager.opToPermission(AppOpsManager.permissionToOpCode(permString)),
                false
        );
    }
}