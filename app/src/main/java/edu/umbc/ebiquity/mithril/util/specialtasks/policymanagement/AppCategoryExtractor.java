package edu.umbc.ebiquity.mithril.util.specialtasks.policymanagement;

import android.content.Context;
import android.util.Log;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.umbc.ebiquity.mithril.MithrilAC;

/**
 * Created by Prajit on 7/15/2017.
 */

public class AppCategoryExtractor {
    public static String loadJSONFromAsset(Context context, String fileName) {
        String json;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static String getAppCategory(Context context, String fileName, String appName) {
        try {
            JSONObject json = new JSONObject(loadJSONFromAsset(context, fileName));
            return (String) json.get(appName);
        } catch (JSONException e) {
            Log.e(MithrilAC.getDebugTag(), e.getMessage());
        }
        return null;
    }
}