package edu.umbc.ebiquity.mithril.data.model.rules;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;

public class Violation implements Parcelable {
    public static final Creator<Violation> CREATOR = new Creator<Violation>() {
        @Override
        public Violation createFromParcel(Parcel in) {
            return new Violation(in);
        }

        @Override
        public Violation[] newArray(int size) {
            return new Violation[size];
        }
    };
    private int policyId;
    private long appId;
    private int oprId;
    private String appStr; // App string
    private String opStr; // operation string
    private boolean asked; // user was asked about this violation
    private boolean tvfv; //marked as true or false violation
    private Timestamp detectedAtTime;
    private Timestamp feedbackTime;

    protected Violation(Parcel in) {
        policyId = in.readInt();
        appId = in.readLong();
        oprId = in.readInt();
        appStr = in.readString();
        opStr = in.readString();
        asked = in.readByte() != 0;
        tvfv = in.readByte() != 0;
    }

    public Violation(int policyId, long appId, int oprId, String appStr, String opStr, boolean asked, boolean tvfv, Timestamp detectedAtTime) {
        this.policyId = policyId;
        this.appId = appId;
        this.oprId = oprId;
        this.appStr = appStr;
        this.opStr = opStr;
        this.asked = asked;
        this.tvfv = tvfv;
        this.detectedAtTime = detectedAtTime;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(policyId);
        dest.writeLong(appId);
        dest.writeInt(oprId);
        dest.writeString(appStr);
        dest.writeString(opStr);
        dest.writeByte((byte) (asked ? 1 : 0));
        dest.writeByte((byte) (tvfv ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getPolicyId() {
        return policyId;
    }

    public void setPolicyId(int policyId) {
        this.policyId = policyId;
    }

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public int getOprId() {
        return oprId;
    }

    public void setOprId(int oprId) {
        this.oprId = oprId;
    }

    public String getAppStr() {
        return appStr;
    }

    public void setAppStr(String appStr) {
        this.appStr = appStr;
    }

    public String getOpStr() {
        return opStr;
    }

    public void setOpStr(String opStr) {
        this.opStr = opStr;
    }

    public boolean isAsked() {
        return asked;
    }

    public void setAsked(boolean asked) {
        this.asked = asked;
    }

    public boolean isTvfv() {
        return tvfv;
    }

    public void setTvfv(boolean tvfv) {
        this.tvfv = tvfv;
    }

    public Timestamp getDetectedAtTime() {
        return detectedAtTime;
    }

    public void setDetectedAtTime(Timestamp detectedAtTime) {
        this.detectedAtTime = detectedAtTime;
    }

    public Timestamp getFeedbackTime() {
        return feedbackTime;
    }

    public void setFeedbackTime(Timestamp feedbackTime) {
        this.feedbackTime = feedbackTime;
    }

    @Override
    public String toString() {
        return "Policy: " + policyId + " for app: " + appStr + " with access: " + opStr + " violated at: " + detectedAtTime;
    }
}