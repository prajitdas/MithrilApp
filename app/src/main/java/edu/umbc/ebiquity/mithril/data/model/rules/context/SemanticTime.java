package edu.umbc.ebiquity.mithril.data.model.rules.context;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.data.model.rules.RepeatFrequency;

public class SemanticTime implements Parcelable, SemanticUserContext {
    private RepeatFrequency repeatFrequency;
    private Timestamp first;
    private int period;
    private String inferredTime;
    private final String type = MithrilApplication.getPrefKeyContextTypeTemporal();
    private boolean enabled = false;

    protected SemanticTime(Parcel in) {
        period = in.readInt();
        inferredTime = in.readString();
        enabled = in.readByte() != 0;
        first = new Timestamp(in.readLong());
        repeatFrequency = RepeatFrequency.charSeqToRepeatFrequency(in.readCharSequence());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(period);
        dest.writeString(inferredTime);
        dest.writeByte((byte) (enabled ? 1 : 0));
        dest.writeLong(first.getTime());
        dest.writeCharSequence(repeatFrequency.getRepFreqCharSeq());
    }

    @Override
    public int describeContents() {
        return 0;
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

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getInferredTime() {
        return inferredTime;
    }

    public void setInferredTime(String inferredTime) {
        this.inferredTime = inferredTime;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getLabel() {
        return inferredTime;
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
        inferredTime = label;
    }
}