package edu.umbc.ebiquity.mithril.data.model.rules.context;

import android.os.Parcel;
import android.os.Parcelable;

import edu.umbc.ebiquity.mithril.MithrilApplication;

public class SemanticNearActor implements Parcelable, SemanticUserContext {
    private String inferredRelationship;
    private final String type = MithrilApplication.getPrefKeyContextTypePresence();
    private boolean enabled = false;

    protected SemanticNearActor(Parcel in) {
        inferredRelationship = in.readString();
        enabled = in.readByte() != 0;
    }

    public static final Creator<SemanticNearActor> CREATOR = new Creator<SemanticNearActor>() {
        @Override
        public SemanticNearActor createFromParcel(Parcel in) {
            return new SemanticNearActor(in);
        }

        @Override
        public SemanticNearActor[] newArray(int size) {
            return new SemanticNearActor[size];
        }
    };

    public String getInferredRelationship() {
        return inferredRelationship;
    }

    public void setInferredRelationship(String inferredRelationship) {
        this.inferredRelationship = inferredRelationship;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getLabel() {
        return inferredRelationship;
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
    public void setLabel(String label) {
        inferredRelationship = label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SemanticNearActor)) return false;

        SemanticNearActor that = (SemanticNearActor) o;

        if (isEnabled() != that.isEnabled()) return false;
        if (!getInferredRelationship().equals(that.getInferredRelationship())) return false;
        return getType() != null ? getType().equals(that.getType()) : that.getType() == null;
    }

    @Override
    public int hashCode() {
        int result = getInferredRelationship().hashCode();
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        result = 31 * result + (isEnabled() ? 1 : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(inferredRelationship);
        dest.writeByte((byte) (enabled ? 1 : 0));
    }
}