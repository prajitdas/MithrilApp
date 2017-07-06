package edu.umbc.ebiquity.mithril.data.model.rules.context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by prajit on 7/5/17.
 */

public class ContextForUpload {
    private String label;
    private String type;
    private boolean enabled;
    private int level;

    public String uploadString() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("label", this.label);
            jsonObject.put("type", this.type);
            jsonObject.put("enabled", this.enabled);
            jsonObject.put("level", this.level);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ContextForUpload(String label, String type, boolean enabled, int level) {
        this.label = label;
        this.type = type;
        this.enabled = enabled;
        this.level = level;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
