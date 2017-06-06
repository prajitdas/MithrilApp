package edu.umbc.ebiquity.mithril.data.model.rules.context;

import android.os.Parcel;
import android.os.Parcelable;

import edu.umbc.ebiquity.mithril.MithrilApplication;

public class SemanticTime implements Parcelable, SemanticUserContext {
    public static final Parcelable.Creator<SemanticTime> CREATOR =
            new Parcelable.Creator<SemanticTime>() {
                @Override
                public SemanticTime createFromParcel(Parcel in) {
                    String inferredTime = in.readString();
                    Long rawTime = in.readLong();
                    return new SemanticTime(inferredTime, rawTime);
                }

                @Override
                public SemanticTime[] newArray(int size) {
                    return new SemanticTime[size];
                }
            };
    private long rawTime;
    private String inferredTime;
    private String type;

    public SemanticTime() {
        this.inferredTime = MithrilApplication.getContextDefaultTime();
    }

    public SemanticTime(String inferredTime) {
        this.inferredTime = inferredTime;
    }

    public SemanticTime(String inferredTime, Long rawTime) {
        this.inferredTime = inferredTime;
        this.rawTime = rawTime;
    }

    public long getRawTime() {
        return rawTime;
    }

    public void setRawTime(long getRawTime) {
        this.rawTime = getRawTime;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SemanticTime other = (SemanticTime) obj;
        if (inferredTime == null) {
            if (other.inferredTime != null) {
                return false;
            }
        } else if (!inferredTime.equals(other.inferredTime)) {
            return false;
        }
        return true;
    }

    /**
     * @return the inferredTime
     */
    public String getInferredTime() {
        return inferredTime;
    }

    /**
     * @param inferredTime the inferredTime to set
     */
    public void setInferredTime(String inferredTime) {
        this.inferredTime = inferredTime;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((inferredTime == null) ? 0 : inferredTime.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return inferredTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(rawTime);
        dest.writeString(inferredTime);
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType() {
        this.type = MithrilApplication.getPrefKeyTemporal();
    }

    @Override
    public String getLabel() {
        return inferredTime;
    }

    @Override
    public void setLabel(String label) {
        inferredTime = label;
    }
}