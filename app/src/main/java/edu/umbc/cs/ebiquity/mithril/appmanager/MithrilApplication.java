package edu.umbc.cs.ebiquity.mithril.appmanager;

import android.app.Application;
import android.hardware.camera2.params.StreamConfigurationMap;

/**
 * Created by PrajitKumar on 5/1/2016.
 */
public class MithrilApplication extends Application{
    private static final String sharedPreferencesName = "edu.umbc.cs.ebiquity.mithril.mithrilappmanager";
    private static final String debugTag = "MithrilAppManagerDebugTag";

    public static String getSharedPreferenceAppCount() {
        return sharedPreferenceAppCount;
    }

    private static final String sharedPreferenceAppCount = "AppCount";

    public static String getSharedPreferencesName() {
        return sharedPreferencesName;
    }

    public static String getDebugTag() {
        return debugTag;
    }
}
