package edu.umbc.ebiquity.mithril.simulations;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.data.model.Action;
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
        PolicyRule policyRule = new PolicyRule(name, ctxId, appId, action);
        return policyRule;
    }

//    public static SemanticUserContext generateContextForSocialMediaCameraAccessRule() {
//        List<String> tempIdentityString = new ArrayList<String>();
//        for (String tempString : MithrilApplication.getContextArrayPresenceInfoIdentity()) {
//            tempIdentityString.add(tempString);
//        }
//
//        List<String> tempLocationString = new ArrayList<String>();
//        for (String tempString : MithrilApplication.getContextArrayLocation()) {
//            tempLocationString.add(tempString);
//        }
//
//        List<String> tempActivityString = new ArrayList<String>();
//        for (String tempString : MithrilApplication.getContextArrayActivity()) {
//            tempActivityString.add(tempString);
//        }
//
//        List<String> tempTimeString = new ArrayList<String>();
//        for (String tempString : MithrilApplication.getContextArrayTime()) {
//            tempTimeString.add(tempString);
//        }
//
//        SemanticUserContext userContext = new SemanticUserContext(
//                new SemanticNearActor(MithrilApplication.getContextDefaultRelationship()), // Boss
//                new SemanticActivity(tempActivityString.get(2)), //Index 2 is university talk
//                new SemanticLocation(tempLocationString.get(2)), // Index 2 is university state
//                new SemanticTime(tempTimeString.get(2))); // Index 2 is Week Day
//        return userContext;
//    }
}