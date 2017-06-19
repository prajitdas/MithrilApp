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

    protected SemanticActivity(Parcel in) {
        inferredActivity = in.readString();
        enabled = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(inferredActivity);
        dest.writeByte((byte) (enabled ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getInferredActivity() {
        return inferredActivity;
    }

    public void setInferredActivity(String inferredActivity) {
        this.inferredActivity = inferredActivity;
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

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}