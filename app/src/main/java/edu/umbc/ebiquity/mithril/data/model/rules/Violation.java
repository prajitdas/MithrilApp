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
    private int appId;
    private int ctxId;
    private int oprId;
    private String desc;
    private boolean marker;
    private Timestamp time;

    protected Violation(Parcel in) {
        appId = in.readInt();
        ctxId = in.readInt();
        oprId = in.readInt();
        desc = in.readString();
        marker = in.readByte() != 0;
    }

    public Violation(int appId, int ctxId, int oprId, String desc, boolean marker, Timestamp time) {
        this.appId = appId;
        this.ctxId = ctxId;
        this.oprId = oprId;
        this.desc = desc;
        this.marker = marker;
        this.time = time;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(appId);
        dest.writeInt(ctxId);
        dest.writeInt(oprId);
        dest.writeString(desc);
        dest.writeByte((byte) (marker ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public int getCtxId() {
        return ctxId;
    }

    public void setCtxId(int ctxId) {
        this.ctxId = ctxId;
    }

    public int getOprId() {
        return oprId;
    }

    public void setOprId(int oprId) {
        this.oprId = oprId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isMarker() {
        return marker;
    }

    public void setMarker(boolean marker) {
        this.marker = marker;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Violation)) return false;

        Violation violation = (Violation) o;

        if (getAppId() != violation.getAppId()) return false;
        if (getCtxId() != violation.getCtxId()) return false;
        if (getOprId() != violation.getOprId()) return false;
        if (isMarker() != violation.isMarker()) return false;
        if (!getDesc().equals(violation.getDesc())) return false;
        return getTime().equals(violation.getTime());
    }

    @Override
    public int hashCode() {
        int result = getAppId();
        result = 31 * result + getCtxId();
        result = 31 * result + getOprId();
        result = 31 * result + getDesc().hashCode();
        result = 31 * result + (isMarker() ? 1 : 0);
        result = 31 * result + getTime().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return getDesc();
    }
}