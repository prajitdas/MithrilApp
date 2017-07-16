package edu.umbc.ebiquity.mithril.simulations;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.data.model.rules.Action;
import edu.umbc.ebiquity.mithril.data.model.rules.PolicyRule;
import edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions.SemanticInconsistencyException;

public class DataGenerator {
    /************************************************ End of policies ************************************************/

    private static Action actionAllow = Action.ALLOW;
    private static Action actionDeny = Action.DENY;

    public static void setPolicy(SQLiteDatabase mithrilDB, Context context, String appName, String appPkgName, String appCategory) throws SemanticInconsistencyException {
        Map<String, Action> permActionMap = getPoliciesForCategory(appCategory);
        for (Map.Entry<String, Action> permActionEntry : permActionMap.entrySet())
            MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(1,
                    appPkgName, appName,
                    AppOpsManager.permissionToOpCode(permActionEntry.getKey()),
                    MithrilAC.getPrefHomeLocationKey(), MithrilAC.getPrefKeyContextTypeLocation(),
                    actionAllow, mithrilDB, context));
        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(1,
                "com.twitter.android", "Twitter",
                AppOpsManager.permissionToOpCode(Manifest.permission.CAMERA),
                MithrilAC.getPrefWeekendTemporalKey(), MithrilAC.getPrefKeyContextTypeTemporal(),
                actionAllow, mithrilDB, context));
    }

    private static Map<String, Action> getPoliciesForCategory(String appCategory) {
        Map<String, Action> permActionMap = new HashMap<>();
        /**
         * values greater than or equal to 0.5 are allow permissions
         */
        if (appCategory.equals("family")) {
            /** "messages": 0.6388888888888888 */
            /** "contacts": 0.5555555555555556 */
            /** "media": 0.6111111111111112 */
            /** "overlay": 0.8055555555555556 */
            /** "storage": 0.7777777777777778 */
            /** "calling": 0.5833333333333334 */
            /** "notifications": 0.4444444444444444 */
            /** "identification": 0.2777777777777778 */
            /** "location": 0.5 */
            /** "calendar": 0.6388888888888888 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("communication")) {
            /** "messages": 0.5077399380804953 */
            /** "contacts": 0.5510835913312694 */
            /** "media": 0.5634674922600619 */
            /** "overlay": 0.7368421052631579 */
            /** "storage": 0.7631578947368421 */
            /** "calling": 0.48761609907120745 */
            /** "notifications": 0.6269349845201239 */
            /** "identification": 0.2678018575851393 */
            /** "location": 0.3560371517027864 */
            /** "calendar": 0.5077399380804953 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("puzzle")) {
            /** "messages": 0.4666666666666667 */
            /** "contacts": 0.44285714285714284 */
            /** "media": 0.4642857142857143 */
            /** "overlay": 0.6952380952380952 */
            /** "storage": 0.6547619047619048 */
            /** "calling": 0.42142857142857143 */
            /** "notifications": 0.4142857142857143 */
            /** "identification": 0.15 */
            /** "location": 0.4 */
            /** "calendar": 0.4666666666666667 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("weather")) {
            /** "messages": 0.44919786096256686 */
            /** "contacts": 0.43315508021390375 */
            /** "media": 0.40106951871657753 */
            /** "overlay": 0.6042780748663101 */
            /** "storage": 0.6684491978609626 */
            /** "calling": 0.37433155080213903 */
            /** "notifications": 0.5828877005347594 */
            /** "identification": 0.1657754010695187 */
            /** "location": 0.5882352941176471 */
            /** "calendar": 0.44919786096256686 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("personalization")) {
            /** "messages": 0.41272430668841764 */
            /** "contacts": 0.3964110929853181 */
            /** "media": 0.4094616639477977 */
            /** "overlay": 0.6574225122349103 */
            /** "storage": 0.6150081566068516 */
            /** "calling": 0.3866231647634584 */
            /** "notifications": 0.48776508972267535 */
            /** "identification": 0.265905383360522 */
            /** "location": 0.36541598694942906 */
            /** "calendar": 0.41272430668841764 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("music_n_audio")) {
            /** "messages": 0.43640897755610975 */
            /** "contacts": 0.39650872817955113 */
            /** "media": 0.543640897755611 */
            /** "overlay": 0.685785536159601 */
            /** "storage": 0.7082294264339152 */
            /** "calling": 0.36159600997506236 */
            /** "notifications": 0.5411471321695761 */
            /** "identification": 0.16458852867830423 */
            /** "location": 0.30174563591022446 */
            /** "calendar": 0.43640897755610975 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("transportation")) {
            /** "messages": 0.48161764705882354 */
            /** "contacts": 0.4411764705882353 */
            /** "media": 0.45588235294117646 */
            /** "overlay": 0.6176470588235294 */
            /** "storage": 0.6029411764705882 */
            /** "calling": 0.4227941176470588 */
            /** "notifications": 0.5661764705882353 */
            /** "identification": 0.16176470588235295 */
            /** "location": 0.5919117647058824 */
            /** "calendar": 0.48161764705882354 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("media_n_video")) {
            /** "messages": 0.44966442953020136 */
            /** "contacts": 0.37583892617449666 */
            /** "media": 0.5100671140939598 */
            /** "overlay": 0.674496644295302 */
            /** "storage": 0.761744966442953 */
            /** "calling": 0.348993288590604 */
            /** "notifications": 0.5201342281879194 */
            /** "identification": 0.18456375838926176 */
            /** "location": 0.33221476510067116 */
            /** "calendar": 0.44966442953020136 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("education")) {
            /** "messages": 0.5843520782396088 */
            /** "contacts": 0.30317848410757947 */
            /** "media": 0.5599022004889975 */
            /** "overlay": 0.7506112469437652 */
            /** "storage": 0.7481662591687042 */
            /** "calling": 0.24938875305623473 */
            /** "notifications": 0.6356968215158925 */
            /** "identification": 0.12469437652811736 */
            /** "location": 0.2762836185819071 */
            /** "calendar": 0.5843520782396088 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("tools")) {
            /** "messages": 0.4894948591864104 */
            /** "contacts": 0.4403218596334376 */
            /** "media": 0.4765310683951721 */
            /** "overlay": 0.6691998211890925 */
            /** "storage": 0.707644166294144 */
            /** "calling": 0.3956191327670988 */
            /** "notifications": 0.5766651765757711 */
            /** "identification": 0.2637460885113992 */
            /** "location": 0.40411265087170317 */
            /** "calendar": 0.4894948591864104 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("casual")) {
            /** "messages": 0.6204081632653061 */
            /** "contacts": 0.5836734693877551 */
            /** "media": 0.6122448979591837 */
            /** "overlay": 0.8204081632653061 */
            /** "storage": 0.7673469387755102 */
            /** "calling": 0.5469387755102041 */
            /** "notifications": 0.5265306122448979 */
            /** "identification": 0.3020408163265306 */
            /** "location": 0.5387755102040817 */
            /** "calendar": 0.6204081632653061 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("health_n_fitness")) {
            /** "messages": 0.4863013698630137 */
            /** "contacts": 0.4178082191780822 */
            /** "media": 0.5136986301369864 */
            /** "overlay": 0.6301369863013698 */
            /** "storage": 0.6815068493150684 */
            /** "calling": 0.4178082191780822 */
            /** "notifications": 0.5993150684931506 */
            /** "identification": 0.18835616438356165 */
            /** "location": 0.4006849315068493 */
            /** "calendar": 0.4863013698630137 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("educational")) {
            /** "messages": 0.4 */
            /** "contacts": 0.3 */
            /** "media": 0.4666666666666667 */
            /** "overlay": 0.6333333333333333 */
            /** "storage": 0.6333333333333333 */
            /** "calling": 0.3 */
            /** "notifications": 0.4666666666666667 */
            /** "identification": 0.16666666666666666 */
            /** "location": 0.2 */
            /** "calendar": 0.4 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("productivity")) {
            /** "messages": 0.556745182012848 */
            /** "contacts": 0.4732334047109208 */
            /** "media": 0.5449678800856531 */
            /** "overlay": 0.7044967880085653 */
            /** "storage": 0.778372591006424 */
            /** "calling": 0.4229122055674518 */
            /** "notifications": 0.6156316916488223 */
            /** "identification": 0.25267665952890794 */
            /** "location": 0.3950749464668094 */
            /** "calendar": 0.556745182012848 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("arcade")) {
            /** "messages": 0.5809018567639257 */
            /** "contacts": 0.5384615384615384 */
            /** "media": 0.5623342175066313 */
            /** "overlay": 0.8169761273209549 */
            /** "storage": 0.8010610079575596 */
            /** "calling": 0.4986737400530504 */
            /** "notifications": 0.47214854111405835 */
            /** "identification": 0.2864721485411141 */
            /** "location": 0.506631299734748 */
            /** "calendar": 0.5809018567639257 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("entertainment")) {
            /** "messages": 0.524416135881104 */
            /** "contacts": 0.4309978768577495 */
            /** "media": 0.5180467091295117 */
            /** "overlay": 0.6963906581740976 */
            /** "storage": 0.6815286624203821 */
            /** "calling": 0.3970276008492569 */
            /** "notifications": 0.5902335456475584 */
            /** "identification": 0.21868365180467092 */
            /** "location": 0.37367303609341823 */
            /** "calendar": 0.524416135881104 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("unknown")) {
            /** "messages": 0.5251863564490625 */
            /** "contacts": 0.4946916647842783 */
            /** "media": 0.5437090580528575 */
            /** "overlay": 0.733453806189293 */
            /** "storage": 0.7214818161283036 */
            /** "calling": 0.47458775694601313 */
            /** "notifications": 0.548226790151344 */
            /** "identification": 0.3383781341766433 */
            /** "location": 0.4334763948497854 */
            /** "calendar": 0.5251863564490625 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("sports")) {
            /** "messages": 0.4298642533936652 */
            /** "contacts": 0.3755656108597285 */
            /** "media": 0.4072398190045249 */
            /** "overlay": 0.6968325791855203 */
            /** "storage": 0.6289592760180995 */
            /** "calling": 0.3257918552036199 */
            /** "notifications": 0.5113122171945701 */
            /** "identification": 0.18099547511312217 */
            /** "location": 0.3665158371040724 */
            /** "calendar": 0.4298642533936652 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("strategy")) {
            /** "messages": 0.6719367588932806 */
            /** "contacts": 0.6086956521739131 */
            /** "media": 0.6324110671936759 */
            /** "overlay": 0.8221343873517787 */
            /** "storage": 0.8458498023715415 */
            /** "calling": 0.5889328063241107 */
            /** "notifications": 0.4505928853754941 */
            /** "identification": 0.28063241106719367 */
            /** "location": 0.5731225296442688 */
            /** "calendar": 0.6719367588932806 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("music")) {
            /** "messages": 0.4117647058823529 */
            /** "contacts": 0.35294117647058826 */
            /** "media": 0.6470588235294118 */
            /** "overlay": 0.8235294117647058 */
            /** "storage": 0.7647058823529411 */
            /** "calling": 0.35294117647058826 */
            /** "notifications": 0.5294117647058824 */
            /** "identification": 0.35294117647058826 */
            /** "location": 0.4117647058823529 */
            /** "calendar": 0.4117647058823529 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("board")) {
            /** "messages": 0.49295774647887325 */
            /** "contacts": 0.43661971830985913 */
            /** "media": 0.49295774647887325 */
            /** "overlay": 0.647887323943662 */
            /** "storage": 0.5915492957746479 */
            /** "calling": 0.43661971830985913 */
            /** "notifications": 0.5633802816901409 */
            /** "identification": 0.22535211267605634 */
            /** "location": 0.39436619718309857 */
            /** "calendar": 0.49295774647887325 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("trivia")) {
            /** "messages": 0.6666666666666666 */
            /** "contacts": 0.6666666666666666 */
            /** "media": 0.6111111111111112 */
            /** "overlay": 0.9166666666666666 */
            /** "storage": 0.75 */
            /** "calling": 0.6111111111111112 */
            /** "notifications": 0.6666666666666666 */
            /** "identification": 0.1388888888888889 */
            /** "location": 0.3611111111111111 */
            /** "calendar": 0.6666666666666666 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("racing")) {
            /** "messages": 0.5411764705882353 */
            /** "contacts": 0.47058823529411764 */
            /** "media": 0.5882352941176471 */
            /** "overlay": 0.8588235294117647 */
            /** "storage": 0.8588235294117647 */
            /** "calling": 0.4117647058823529 */
            /** "notifications": 0.5882352941176471 */
            /** "identification": 0.3176470588235294 */
            /** "location": 0.4 */
            /** "calendar": 0.5411764705882353 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("news_n_magazines")) {
            /** "messages": 0.39589442815249265 */
            /** "contacts": 0.36363636363636365 */
            /** "media": 0.4310850439882698 */
            /** "overlay": 0.6451612903225806 */
            /** "storage": 0.6803519061583577 */
            /** "calling": 0.30498533724340177 */
            /** "notifications": 0.5425219941348973 */
            /** "identification": 0.13196480938416422 */
            /** "location": 0.31671554252199413 */
            /** "calendar": 0.39589442815249265 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("role_playing")) {
            /** "messages": 0.8404669260700389 */
            /** "contacts": 0.7937743190661478 */
            /** "media": 0.8482490272373541 */
            /** "overlay": 0.9105058365758755 */
            /** "storage": 0.9066147859922179 */
            /** "calling": 0.7859922178988327 */
            /** "notifications": 0.4669260700389105 */
            /** "identification": 0.25680933852140075 */
            /** "location": 0.7821011673151751 */
            /** "calendar": 0.8404669260700389 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("shopping")) {
            /** "messages": 0.5255972696245734 */
            /** "contacts": 0.41638225255972694 */
            /** "media": 0.48464163822525597 */
            /** "overlay": 0.7064846416382252 */
            /** "storage": 0.6484641638225256 */
            /** "calling": 0.3993174061433447 */
            /** "notifications": 0.6313993174061433 */
            /** "identification": 0.11604095563139932 */
            /** "location": 0.36177474402730375 */
            /** "calendar": 0.5255972696245734 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("finance")) {
            /** "messages": 0.5311778290993071 */
            /** "contacts": 0.42032332563510394 */
            /** "media": 0.5196304849884527 */
            /** "overlay": 0.7136258660508084 */
            /** "storage": 0.6535796766743649 */
            /** "calling": 0.4064665127020785 */
            /** "notifications": 0.6374133949191686 */
            /** "identification": 0.3094688221709007 */
            /** "location": 0.418013856812933 */
            /** "calendar": 0.5311778290993071 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("business")) {
            /** "messages": 0.5330578512396694 */
            /** "contacts": 0.4132231404958678 */
            /** "media": 0.49586776859504134 */
            /** "overlay": 0.7272727272727273 */
            /** "storage": 0.6983471074380165 */
            /** "calling": 0.359504132231405 */
            /** "notifications": 0.6363636363636364 */
            /** "identification": 0.24380165289256198 */
            /** "location": 0.3140495867768595 */
            /** "calendar": 0.5330578512396694 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("photography")) {
            /** "messages": 0.4558303886925795 */
            /** "contacts": 0.36395759717314485 */
            /** "media": 0.657243816254417 */
            /** "overlay": 0.6713780918727915 */
            /** "storage": 0.8303886925795053 */
            /** "calling": 0.33568904593639576 */
            /** "notifications": 0.568904593639576 */
            /** "identification": 0.1872791519434629 */
            /** "location": 0.36395759717314485 */
            /** "calendar": 0.4558303886925795 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("libraries_n_demo")) {
            /** "messages": 0.5192307692307693 */
            /** "contacts": 0.46153846153846156 */
            /** "media": 0.5192307692307693 */
            /** "overlay": 0.6538461538461539 */
            /** "storage": 0.7307692307692307 */
            /** "calling": 0.5 */
            /** "notifications": 0.6538461538461539 */
            /** "identification": 0.3269230769230769 */
            /** "location": 0.4423076923076923 */
            /** "calendar": 0.5192307692307693 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("adventure")) {
            /** "messages": 0.6666666666666666 */
            /** "contacts": 0.5729166666666666 */
            /** "media": 0.6875 */
            /** "overlay": 0.78125 */
            /** "storage": 0.8125 */
            /** "calling": 0.5729166666666666 */
            /** "notifications": 0.5833333333333334 */
            /** "identification": 0.23958333333333334 */
            /** "location": 0.5208333333333334 */
            /** "calendar": 0.6666666666666666 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("word")) {
            /** "messages": 0.41025641025641024 */
            /** "contacts": 0.38461538461538464 */
            /** "media": 0.4358974358974359 */
            /** "overlay": 0.7692307692307693 */
            /** "storage": 0.717948717948718 */
            /** "calling": 0.38461538461538464 */
            /** "notifications": 0.48717948717948717 */
            /** "identification": 0.15384615384615385 */
            /** "location": 0.41025641025641024 */
            /** "calendar": 0.41025641025641024 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("family_braingames")) {
            /** "messages": 1.0 */
            /** "contacts": 1.0 */
            /** "media": 1.0 */
            /** "overlay": 1.0 */
            /** "storage": 1.0 */
            /** "calling": 1.0 */
            /** "notifications": 1.0 */
            /** "identification": 0.0 */
            /** "location": 1.0 */
            /** "calendar": 1.0 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("card")) {
            /** "messages": 0.5185185185185185 */
            /** "contacts": 0.5277777777777778 */
            /** "media": 0.5185185185185185 */
            /** "overlay": 0.6296296296296297 */
            /** "storage": 0.6944444444444444 */
            /** "calling": 0.5092592592592593 */
            /** "notifications": 0.3888888888888889 */
            /** "identification": 0.24074074074074073 */
            /** "location": 0.5185185185185185 */
            /** "calendar": 0.5185185185185185 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("family_action")) {
            /** "messages": 0.25 */
            /** "contacts": 0.25 */
            /** "media": 0.25 */
            /** "overlay": 0.25 */
            /** "storage": 0.25 */
            /** "calling": 0.0 */
            /** "notifications": 0.25 */
            /** "identification": 0.25 */
            /** "location": 0.0 */
            /** "calendar": 0.25 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("lifestyle")) {
            /** "messages": 0.5572519083969466 */
            /** "contacts": 0.47837150127226463 */
            /** "media": 0.5776081424936387 */
            /** "overlay": 0.732824427480916 */
            /** "storage": 0.6564885496183206 */
            /** "calling": 0.44529262086513993 */
            /** "notifications": 0.6666666666666666 */
            /** "identification": 0.17557251908396945 */
            /** "location": 0.4681933842239186 */
            /** "calendar": 0.5572519083969466 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("comics")) {
            /** "messages": 0.5 */
            /** "contacts": 0.4411764705882353 */
            /** "media": 0.35294117647058826 */
            /** "overlay": 0.6764705882352942 */
            /** "storage": 0.7647058823529411 */
            /** "calling": 0.2647058823529412 */
            /** "notifications": 0.6176470588235294 */
            /** "identification": 0.17647058823529413 */
            /** "location": 0.38235294117647056 */
            /** "calendar": 0.5 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("medical")) {
            /** "messages": 0.5689655172413793 */
            /** "contacts": 0.3793103448275862 */
            /** "media": 0.603448275862069 */
            /** "overlay": 0.7931034482758621 */
            /** "storage": 0.7586206896551724 */
            /** "calling": 0.3793103448275862 */
            /** "notifications": 0.7241379310344828 */
            /** "identification": 0.25862068965517243 */
            /** "location": 0.41379310344827586 */
            /** "calendar": 0.5689655172413793 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("casino")) {
            /** "messages": 0.717391304347826 */
            /** "contacts": 0.717391304347826 */
            /** "media": 0.717391304347826 */
            /** "overlay": 0.8913043478260869 */
            /** "storage": 0.8478260869565217 */
            /** "calling": 0.6739130434782609 */
            /** "notifications": 0.5652173913043478 */
            /** "identification": 0.3695652173913043 */
            /** "location": 0.6956521739130435 */
            /** "calendar": 0.717391304347826 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("simulation")) {
            /** "messages": 0.59375 */
            /** "contacts": 0.5234375 */
            /** "media": 0.5859375 */
            /** "overlay": 0.8046875 */
            /** "storage": 0.796875 */
            /** "calling": 0.5 */
            /** "notifications": 0.5390625 */
            /** "identification": 0.3359375 */
            /** "location": 0.5234375 */
            /** "calendar": 0.59375 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("social")) {
            /** "messages": 0.5893416927899686 */
            /** "contacts": 0.49843260188087773 */
            /** "media": 0.6489028213166145 */
            /** "overlay": 0.7523510971786834 */
            /** "storage": 0.8181818181818182 */
            /** "calling": 0.445141065830721 */
            /** "notifications": 0.7115987460815048 */
            /** "identification": 0.25391849529780564 */
            /** "location": 0.3981191222570533 */
            /** "calendar": 0.5893416927899686 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("action")) {
            /** "messages": 0.6541095890410958 */
            /** "contacts": 0.571917808219178 */
            /** "media": 0.6267123287671232 */
            /** "overlay": 0.8493150684931506 */
            /** "storage": 0.8595890410958904 */
            /** "calling": 0.5582191780821918 */
            /** "notifications": 0.523972602739726 */
            /** "identification": 0.3356164383561644 */
            /** "location": 0.5547945205479452 */
            /** "calendar": 0.6541095890410958 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("books_n_reference")) {
            /** "messages": 0.515625 */
            /** "contacts": 0.440625 */
            /** "media": 0.50625 */
            /** "overlay": 0.715625 */
            /** "storage": 0.75 */
            /** "calling": 0.396875 */
            /** "notifications": 0.6 */
            /** "identification": 0.2 */
            /** "location": 0.44375 */
            /** "calendar": 0.515625 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        } else if (appCategory.equals("travel_n_local")) {
            /** "messages": 0.4585492227979275 */
            /** "contacts": 0.42487046632124353 */
            /** "media": 0.48186528497409326 */
            /** "overlay": 0.6088082901554405 */
            /** "storage": 0.6709844559585493 */
            /** "calling": 0.40414507772020725 */
            /** "notifications": 0.572538860103627 */
            /** "identification": 0.20207253886010362 */
            /** "location": 0.6295336787564767 */
            /** "calendar": 0.4585492227979275 */
            for (String permission : getMessagesPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getContactsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getMediaPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getOverlayPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getStoragePolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCallingPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getNotificationsPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getIdentificationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getLocationPolicies())
                permActionMap.put(permission, actionAllow);
            for (String permission : getCalendarPolicies())
                permActionMap.put(permission, actionAllow);
        }

        return permActionMap;
    }

    private static List<String> getMessagesPolicies() {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.ADD_VOICEMAIL);
        permissions.add(Manifest.permission.WRITE_VOICEMAIL);
        permissions.add(Manifest.permission.READ_VOICEMAIL);
        permissions.add(Manifest.permission.READ_SMS);
        permissions.add(Manifest.permission.WRITE_SMS);
        permissions.add(Manifest.permission.SEND_SMS_NO_CONFIRMATION);
        return permissions;
    }

    private static List<String> getContactsPolicies() {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.READ_CONTACTS);
        permissions.add(Manifest.permission.WRITE_CONTACTS);
        return permissions;
    }

    private static List<String> getMediaPolicies() {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.RECORD_AUDIO);
        permissions.add(Manifest.permission.CAPTURE_VIDEO_OUTPUT);
        permissions.add(Manifest.permission.CAPTURE_SECURE_VIDEO_OUTPUT);
        permissions.add(Manifest.permission.REMOTE_AUDIO_PLAYBACK);
        return permissions;
    }

    private static List<String> getOverlayPolicies() {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.SYSTEM_ALERT_WINDOW);
        return permissions;
    }

    private static List<String> getStoragePolicies() {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.WRITE_MEDIA_STORAGE);
        permissions.add(Manifest.permission.STORAGE_INTERNAL);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permissions;
    }

    private static List<String> getCallingPolicies() {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CALL_PHONE);
        permissions.add(Manifest.permission.SEND_SMS);
        permissions.add(Manifest.permission.RECEIVE_SMS);
        permissions.add(Manifest.permission.READ_CALL_LOG);
        permissions.add(Manifest.permission.WRITE_CALL_LOG);
        return permissions;
    }

    private static List<String> getNotificationsPolicies() {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.ACCESS_NOTIFICATIONS);
        permissions.add(Manifest.permission.ACCESS_NOTIFICATION_POLICY);
        permissions.add(Manifest.permission.MANAGE_NOTIFICATIONS);
        return permissions;
    }

    private static List<String> getIdentificationPolicies() {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.GET_ACCOUNTS);
        permissions.add(Manifest.permission.GET_ACCOUNTS_PRIVILEGED);
        permissions.add(Manifest.permission.REAL_GET_TASKS);
        return permissions;
    }

    private static List<String> getLocationPolicies() {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.LOCATION_HARDWARE);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.INSTALL_LOCATION_PROVIDER);
        permissions.add(Manifest.permission.CONTROL_LOCATION_UPDATES);
        return permissions;
    }

    private static List<String> getCalendarPolicies() {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.READ_CALENDAR);
        permissions.add(Manifest.permission.WRITE_CALENDAR);
        return permissions;
    }


//    /************ Social Media apps to be allowed camera access at home if it's the weekend ************/
//    public static void setPolicySocialMediaCameraAccessAtHomeOnWeekends(SQLiteDatabase mithrilDB, Context context)
//            throws SemanticInconsistencyException {
//        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(1,
//                "com.twitter.android", "Twitter",
//                AppOpsManager.permissionToOpCode(Manifest.permission.CAMERA),
//                MithrilAC.getPrefHomeLocationKey(), MithrilAC.getPrefKeyContextTypeLocation(),
//                actionAllow, mithrilDB, context));
//        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(1,
//                "com.twitter.android", "Twitter",
//                AppOpsManager.permissionToOpCode(Manifest.permission.CAMERA),
//                MithrilAC.getPrefWeekendTemporalKey(), MithrilAC.getPrefKeyContextTypeTemporal(),
//                actionAllow, mithrilDB, context));
//    }
//
//    /************ Social Media apps to be allowed camera access at work if it's a weekday and it's lunch hours ************/
//    public static void setPolicySocialMediaCameraAccessAtWorkOnWeekdaysDuringLunchHours(SQLiteDatabase mithrilDB, Context context)
//            throws SemanticInconsistencyException {
//        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(2,
//                "com.twitter.android", "Twitter",
//                AppOpsManager.permissionToOpCode(Manifest.permission.CAMERA),
//                MithrilAC.getPrefWorkLocationKey(), MithrilAC.getPrefKeyContextTypeLocation(),
//                actionAllow, mithrilDB, context));
//        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(2,
//                "com.twitter.android", "Twitter",
//                AppOpsManager.permissionToOpCode(Manifest.permission.CAMERA),
//                MithrilAC.getPrefWeekdayTemporalKey(), MithrilAC.getPrefKeyContextTypeTemporal(),
//                actionAllow, mithrilDB, context));
//        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(2,
//                "com.twitter.android", "Twitter",
//                AppOpsManager.permissionToOpCode(Manifest.permission.CAMERA),
//                MithrilAC.getPrefLunchTemporalKey(), MithrilAC.getPrefKeyContextTypeTemporal(),
//                actionAllow, mithrilDB, context));
//    }
//
//    /************ Social Media apps to be allowed location access at home if it's a weekday and it's evening personal time ************/
//    public static void setPolicySocialMediaLocationAccessAtHomeOnWeekdaysDuringEveningPersonalHours(SQLiteDatabase mithrilDB, Context context)
//            throws SemanticInconsistencyException {
//        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(3,
//                "com.twitter.android", "Twitter",
//                AppOpsManager.permissionToOpCode(Manifest.permission.ACCESS_FINE_LOCATION),
//                MithrilAC.getPrefWorkLocationKey(), MithrilAC.getPrefKeyContextTypeLocation(),
//                actionAllow, mithrilDB, context));
//        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(3,
//                "com.twitter.android", "Twitter",
//                AppOpsManager.permissionToOpCode(Manifest.permission.ACCESS_FINE_LOCATION),
//                MithrilAC.getPrefWeekdayTemporalKey(), MithrilAC.getPrefKeyContextTypeTemporal(),
//                actionAllow, mithrilDB, context));
//        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(3,
//                "com.twitter.android", "Twitter",
//                AppOpsManager.permissionToOpCode(Manifest.permission.ACCESS_FINE_LOCATION),
//                MithrilAC.getPrefAloneTemporalKey(), MithrilAC.getPrefKeyContextTypeTemporal(),
//                actionAllow, mithrilDB, context));
//    }
//
//    /************ Chat apps to be allowed sms access at work if it's a weekday and it's lunch hours ************/
//    public static void setPolicyChatAppsReadSmsAccessAtWork(SQLiteDatabase mithrilDB, Context context)
//            throws SemanticInconsistencyException {
//        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(4,
//                "com.google.android.talk", "Hangouts",
//                AppOpsManager.permissionToOpCode(Manifest.permission.READ_SMS),
//                MithrilAC.getPrefWorkLocationKey(), MithrilAC.getPrefKeyContextTypeLocation(),
//                actionAllow, mithrilDB, context));
//    }
//
//    public static void setPolicyChatAppsReceiveSmsAccessAtWork(SQLiteDatabase mithrilDB, Context context)
//            throws SemanticInconsistencyException {
//        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(5,
//                "com.google.android.talk", "Hangouts",
//                AppOpsManager.permissionToOpCode(Manifest.permission.RECEIVE_SMS),
//                MithrilAC.getPrefWorkLocationKey(), MithrilAC.getPrefKeyContextTypeLocation(),
//                actionAllow, mithrilDB, context));
//    }
//
//    public static void setPolicyChatAppsSendSmsAccessAtWork(SQLiteDatabase mithrilDB, Context context)
//            throws SemanticInconsistencyException {
//        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(6,
//                "com.google.android.talk", "Hangouts",
//                AppOpsManager.permissionToOpCode(Manifest.permission.SEND_SMS),
//                MithrilAC.getPrefWorkLocationKey(), MithrilAC.getPrefKeyContextTypeLocation(),
//                actionAllow, mithrilDB, context));
//    }
//
//    /************ Email clients to be allowed calendar access at work if it's a weekday ************/
//    public static void setPolicyEmailClientsReadCalendarAccessAtWorkDuringWeekdays(SQLiteDatabase mithrilDB, Context context)
//            throws SemanticInconsistencyException {
//        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(7,
//                "com.google.android.gm", "Gmail",
//                AppOpsManager.permissionToOpCode(Manifest.permission.READ_CALENDAR),
//                MithrilAC.getPrefWorkLocationKey(), MithrilAC.getPrefKeyContextTypeLocation(),
//                actionAllow, mithrilDB, context));
//        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(7,
//                "com.google.android.gm", "Gmail",
//                AppOpsManager.permissionToOpCode(Manifest.permission.READ_CALENDAR),
//                MithrilAC.getPrefWeekdayTemporalKey(), MithrilAC.getPrefKeyContextTypeTemporal(),
//                actionAllow, mithrilDB, context));
//    }
//
//    public static void setPolicyEmailClientsWriteCalendarAccessAtWorkDuringWeekdays(SQLiteDatabase mithrilDB, Context context)
//            throws SemanticInconsistencyException {
//        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(8,
//                "com.google.android.gm", "Gmail",
//                AppOpsManager.permissionToOpCode(Manifest.permission.WRITE_CALENDAR),
//                MithrilAC.getPrefWorkLocationKey(), MithrilAC.getPrefKeyContextTypeLocation(),
//                actionAllow, mithrilDB, context));
//        MithrilDBHelper.getHelper(context).addPolicyRule(mithrilDB, DataGenerator.createPolicyRule(8,
//                "com.google.android.gm", "Gmail",
//                AppOpsManager.permissionToOpCode(Manifest.permission.WRITE_CALENDAR),
//                MithrilAC.getPrefWeekdayTemporalKey(), MithrilAC.getPrefKeyContextTypeTemporal(),
//                actionAllow, mithrilDB, context));
//    }

    public static PolicyRule createPolicyRule(long policyId,
                                              String appPkgName,
                                              String appName,
                                              int op,
                                              String contextLabel,
                                              String contextType,
                                              Action action,
                                              SQLiteDatabase mithrilDB, Context context) {
        long appId, ctxtId;
        appId = MithrilDBHelper.getHelper(context).findAppIdByAppPkgName(mithrilDB, appPkgName);
        ctxtId = MithrilDBHelper.getHelper(context).findContextIdByLabelAndType(mithrilDB, contextLabel, contextType);
        Log.d(MithrilAC.getDebugTag(), "OpCode: " + String.valueOf(op));
        Log.d(MithrilAC.getDebugTag(), "AppOps: " + AppOpsManager.opToPermission(op));
        if (appId == -1 || ctxtId == -1)
            return null;
        return new PolicyRule(
                policyId,
                appId,
                ctxtId,
                op,
                action,
                action.getActionString(),
                appName,
                contextLabel,
                AppOpsManager.opToPermission(op),
                false
        );
    }
}