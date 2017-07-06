package edu.umbc.ebiquity.mithril.util.networking;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import edu.umbc.ebiquity.mithril.MithrilAC;

/**
 * Created by prajit on 7/4/17.
 */

public final class JSONRequest {
    private JSONObject request;

    public JSONRequest(Map<String, Object> feedback, String userId) throws JSONException {
        for (Map.Entry<String, Object> feedbackEntry : feedback.entrySet())
            Log.e(MithrilAC.getDebugTag(), "JSON data: " + feedbackEntry.getValue());
        request = new JSONObject();
        request.put(MithrilAC.getFeedbackQuestion1(), feedback.get(MithrilAC.getFeedbackQuestion1()));
        request.put(MithrilAC.getFeedbackQuestion2(), feedback.get(MithrilAC.getFeedbackQuestion2()));
        request.put(MithrilAC.getFeedbackQuestion3(), feedback.get(MithrilAC.getFeedbackQuestion3()));
        request.put(MithrilAC.getFeedbackQuestion4(), feedback.get(MithrilAC.getFeedbackQuestion4()));
        request.put(MithrilAC.getFeedbackQuestion5(), feedback.get(MithrilAC.getFeedbackQuestion5()));
        request.put(MithrilAC.getFeedbackQuestion6(), feedback.get(MithrilAC.getFeedbackQuestion6()));
        request.put(MithrilAC.getFeedbackQuestion7(), feedback.get(MithrilAC.getFeedbackQuestion7()));
        request.put(MithrilAC.getFeedbackQuestion8(), feedback.get(MithrilAC.getFeedbackQuestion8()));
        request.put(MithrilAC.getFeedbackQuestion9(), feedback.get(MithrilAC.getFeedbackQuestion9()));
        request.put(MithrilAC.getFeedbackQuestionDataKey(), feedback.get(MithrilAC.getFeedbackQuestionDataKey()));
        request.put(MithrilAC.getFeedbackQuestionDataTimeKey(), System.currentTimeMillis());
        request.put(MithrilAC.getRandomUserId(), userId);
    }

    public JSONObject getRequest() {
        return request;
    }
}