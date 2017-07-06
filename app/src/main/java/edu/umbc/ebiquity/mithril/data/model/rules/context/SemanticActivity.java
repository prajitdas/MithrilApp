package edu.umbc.ebiquity.mithril.data.model.rules.context;

import android.os.Parcel;
import android.os.Parcelable;

import edu.umbc.ebiquity.mithril.MithrilAC;

public class SemanticActivity extends SemanticUserContext implements Parcelable {
    public static final Creator<SemanticActivity> CREATOR = new Creator<SemanticActivity>() {
        @Override
        public SemanticActivity createFromParcel(Parcel in) {
            return new SemanticActivity(in);
        }

        @Override
        public SemanticActivity[] newArray(int size) {
            return new SemanticActivity[size];
        }
    };
    private final String type = MithrilAC.getPrefKeyContextTypeActivity();
    private String inferredActivity;
    private boolean enabled = false;
    private int level;

    protected SemanticActivity(Parcel in) {
        inferredActivity = in.readString();
        enabled = in.readByte() != 0;
        level = in.readInt();
    }

    public SemanticActivity(String inferredActivity, boolean enabled, int level) {
        this.inferredActivity = inferredActivity;
        this.enabled = enabled;
        this.level = level;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(inferredActivity);
        dest.writeByte((byte) (enabled ? 1 : 0));
        dest.writeInt(level);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getLabel() {
        return inferredActivity;
    }

    @Override
    public void setLabel(String label) {
        inferredActivity = label;
    }

    public String getInferredActivity() {
        return inferredActivity;
    }

    public void setInferredActivity(String inferredActivity) {
        this.inferredActivity = inferredActivity;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "In activity " + inferredActivity;
    }
}