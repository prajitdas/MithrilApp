package edu.umbc.ebiquity.mithril.data.model.rules.context.contextpieces;

import android.os.Parcel;
import android.os.Parcelable;

import edu.umbc.ebiquity.mithril.MithrilApplication;

public class SemanticActivity implements Parcelable {
    public static final Parcelable.Creator<SemanticActivity> CREATOR =
            new Parcelable.Creator<SemanticActivity>() {
                @Override
                public SemanticActivity createFromParcel(Parcel in) {
                    return new SemanticActivity(in.readString());
                }

                @Override
                public SemanticActivity[] newArray(int size) {
                    return new SemanticActivity[size];
                }
            };
    private String inferredActivity;

    public SemanticActivity(String inferredActivity) {
        setInferredActivity(inferredActivity);
    }

    public SemanticActivity() {
        setInferredActivity(MithrilApplication.getContextDefaultActivity());
    }

    public String getInferredActivity() {
        return inferredActivity;
    }

    public void setInferredActivity(String inferredActivity) {
        this.inferredActivity = inferredActivity;
    }

    @Override
    public String toString() {
        return "SemanticActivity{" +
                ", inferredActivity='" + inferredActivity + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(inferredActivity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SemanticActivity)) return false;

        SemanticActivity that = (SemanticActivity) o;

        return getInferredActivity().equals(that.getInferredActivity());
    }

    @Override
    public int hashCode() {
        return getInferredActivity().hashCode();
    }
}