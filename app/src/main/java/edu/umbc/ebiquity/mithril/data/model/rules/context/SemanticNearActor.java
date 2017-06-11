package edu.umbc.ebiquity.mithril.data.model.rules.context;

import android.os.Parcel;
import android.os.Parcelable;

import edu.umbc.ebiquity.mithril.MithrilApplication;

public class SemanticNearActor implements Parcelable, SemanticUserContext {
    public static final Parcelable.Creator<SemanticNearActor> CREATOR =
            new Parcelable.Creator<SemanticNearActor>() {
                @Override
                public SemanticNearActor createFromParcel(Parcel in) {
                    return new SemanticNearActor(in.readString());
                }

                @Override
                public SemanticNearActor[] newArray(int size) {
                    return new SemanticNearActor[size];
                }
            };
    private String inferredRelationship;
    private String type;

    public SemanticNearActor(String aRelationship) {
        inferredRelationship = aRelationship;
    }

    public SemanticNearActor() {
        inferredRelationship = MithrilApplication.getContextDefaultRelationship();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(inferredRelationship);
    }

    public String getInferredRelationship() {
        return inferredRelationship;
    }

    public void setInferredRelationship(String inferredRelationship) {
        this.inferredRelationship = inferredRelationship;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SemanticNearActor)) return false;

        SemanticNearActor that = (SemanticNearActor) o;

        return getInferredRelationship().equals(that.getInferredRelationship());
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public int hashCode() {
        return getInferredRelationship().hashCode();
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType() {
        this.type = MithrilApplication.getPrefKeyContextTypeTemporal();
    }

    @Override
    public String getLabel() {
        return inferredRelationship;
    }

    @Override
    public void setLabel(String label) {
        inferredRelationship = label;
    }
}