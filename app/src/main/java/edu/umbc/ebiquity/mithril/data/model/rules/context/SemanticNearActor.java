package edu.umbc.ebiquity.mithril.data.model.rules.context;

import android.os.Parcel;
import android.os.Parcelable;

import edu.umbc.ebiquity.mithril.MithrilAC;

public class SemanticNearActor extends SemanticUserContext implements Parcelable {
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
    private final String type = MithrilAC.getPrefKeyContextTypePresence();
    private String inferredRelationship;
    private boolean enabled = false;
    private int level;

    public SemanticNearActor(String inferredRelationship, boolean enabled, int level) {
        this.inferredRelationship = inferredRelationship;
        this.enabled = enabled;
        this.level = level;
    }

    protected SemanticNearActor(Parcel in) {
        inferredRelationship = in.readString();
        enabled = in.readByte() != 0;
        level = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(inferredRelationship);
        dest.writeByte((byte) (enabled ? 1 : 0));
        dest.writeInt(level);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String getLabel() {
        return inferredRelationship;
    }

    @Override
    public void setLabel(String label) {
        inferredRelationship = label;
    }

    @Override
    public String getType() {
        return type;
    }

    public String getInferredRelationship() {
        return inferredRelationship;
    }

    public void setInferredRelationship(String inferredRelationship) {
        this.inferredRelationship = inferredRelationship;
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
        return "In presence of " + inferredRelationship;
    }
}