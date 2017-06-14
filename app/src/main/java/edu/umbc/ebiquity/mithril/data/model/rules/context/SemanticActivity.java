package edu.umbc.ebiquity.mithril.data.model.rules.context;

import android.os.Parcel;
import android.os.Parcelable;

import edu.umbc.ebiquity.mithril.MithrilApplication;

public class SemanticActivity implements Parcelable, SemanticUserContext {
    private String inferredActivity;
    private final String type = MithrilApplication.getPrefKeyContextTypeActivity();
    private boolean enabled = false;

    protected SemanticActivity(Parcel in) {
        inferredActivity = in.readString();
        enabled = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(inferredActivity);
        dest.writeString(type);
        dest.writeByte((byte) (enabled ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

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
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setLabel(String label) {
        inferredActivity = label;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SemanticActivity)) return false;

        SemanticActivity that = (SemanticActivity) o;

        if (isEnabled() != that.isEnabled()) return false;
        if (!getInferredActivity().equals(that.getInferredActivity())) return false;
        return getType().equals(that.getType());
    }

    @Override
    public int hashCode() {
        int result = getInferredActivity().hashCode();
        result = 31 * result + getType().hashCode();
        result = 31 * result + (isEnabled() ? 1 : 0);
        return result;
    }
}