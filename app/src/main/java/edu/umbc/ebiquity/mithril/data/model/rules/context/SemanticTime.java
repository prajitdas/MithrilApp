package edu.umbc.ebiquity.mithril.data.model.rules.context;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.data.model.rules.RepeatFrequency;

public class SemanticTime implements Parcelable, SemanticUserContext {
    private boolean repeats;
    private RepeatFrequency repeatFrequency;
    private Timestamp first;
    private long period;
    private String inferredTime;
    private final String type = MithrilApplication.getPrefKeyContextTypeTemporal();

    protected SemanticTime(Parcel in) {
        repeats = in.readByte() != 0;
        period = in.readLong();
        inferredTime = in.readString();
    }

    public static final Creator<SemanticTime> CREATOR = new Creator<SemanticTime>() {
        @Override
        public SemanticTime createFromParcel(Parcel in) {
            return new SemanticTime(in);
        }

        @Override
        public SemanticTime[] newArray(int size) {
            return new SemanticTime[size];
        }
    };

    public SemanticTime(boolean repeats, RepeatFrequency repeatFrequency, Timestamp first, long period, String inferredTime) {
        this.repeats = repeats;
        this.repeatFrequency = repeatFrequency;
        this.first = first;
        this.period = period;
        this.inferredTime = inferredTime;
    }

    public SemanticTime(boolean repeats, RepeatFrequency repeatFrequency, Timestamp first, String inferredTime) {
        this.repeats = repeats;
        this.repeatFrequency = repeatFrequency;
        this.first = first;
        this.period = MithrilApplication.getDefaultTimeSlot();
        this.inferredTime = inferredTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (repeats ? 1 : 0));
        dest.writeLong(period);
        dest.writeString(inferredTime);
        dest.writeString(type);
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public void setLabel(String label) {

    }

    public boolean isRepeats() {
        return repeats;
    }

    public void setRepeats(boolean repeats) {
        this.repeats = repeats;
    }

    public RepeatFrequency getRepeatFrequency() {
        return repeatFrequency;
    }

    public void setRepeatFrequency(RepeatFrequency repeatFrequency) {
        this.repeatFrequency = repeatFrequency;
    }

    public Timestamp getFirst() {
        return first;
    }

    public void setFirst(Timestamp first) {
        this.first = first;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public String getInferredTime() {
        return inferredTime;
    }

    public void setInferredTime(String inferredTime) {
        this.inferredTime = inferredTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SemanticTime)) return false;

        SemanticTime that = (SemanticTime) o;

        if (isRepeats() != that.isRepeats()) return false;
        if (getPeriod() != that.getPeriod()) return false;
        if (getRepeatFrequency() != that.getRepeatFrequency()) return false;
        if (!getFirst().equals(that.getFirst())) return false;
        if (!getInferredTime().equals(that.getInferredTime())) return false;
        return getType().equals(that.getType());
    }

    @Override
    public int hashCode() {
        int result = (isRepeats() ? 1 : 0);
        result = 31 * result + getRepeatFrequency().hashCode();
        result = 31 * result + getFirst().hashCode();
        result = 31 * result + (int) (getPeriod() ^ (getPeriod() >>> 32));
        result = 31 * result + getInferredTime().hashCode();
        result = 31 * result + getType().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "SemanticTime{" +
                "repeats=" + repeats +
                ", repeatFrequency=" + repeatFrequency +
                ", first=" + first +
                ", period=" + period +
                ", inferredTime='" + inferredTime + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}