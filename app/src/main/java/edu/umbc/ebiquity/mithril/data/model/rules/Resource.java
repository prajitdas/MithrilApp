package edu.umbc.ebiquity.mithril.data.model.rules;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by prajit on 5/9/17.
 */

public class Resource implements Parcelable, Comparable {
    private String resourceName;
    private int duration;
    private int op;
    private long lastTimeUsed;
    private String relativeLastTimeUsed;
    private String group;
    private double riskLevel;
    private int mode;
    private long rejectTime;
    private int allowedCount;
    private int ignoredCount;

    public Resource(int op) {
        this.op = op;
    }

    protected Resource(Parcel in) {
        resourceName = in.readString();
        duration = in.readInt();
        op = in.readInt();
        lastTimeUsed = in.readLong();
        relativeLastTimeUsed = in.readString();
        group = in.readString();
        riskLevel = in.readDouble();
        mode = in.readInt();
        rejectTime = in.readLong();
        allowedCount = in.readInt();
        ignoredCount = in.readInt();
    }

    public Resource(String resourceName, int duration, int op, long lastTimeUsed, String relativeLastTimeUsed, String group, double riskLevel, int mode, long rejectTime, int allowedCount, int ignoredCount) {
        this.resourceName = resourceName;
        this.duration = duration;
        this.op = op;
        this.lastTimeUsed = lastTimeUsed;
        this.relativeLastTimeUsed = relativeLastTimeUsed;
        this.group = group;
        this.riskLevel = riskLevel;
        this.mode = mode;
        this.rejectTime = rejectTime;
        this.allowedCount = allowedCount;
        this.ignoredCount = ignoredCount;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(resourceName);
        dest.writeInt(duration);
        dest.writeInt(op);
        dest.writeLong(lastTimeUsed);
        dest.writeString(relativeLastTimeUsed);
        dest.writeString(group);
        dest.writeDouble(riskLevel);
        dest.writeInt(mode);
        dest.writeLong(rejectTime);
        dest.writeInt(allowedCount);
        dest.writeInt(ignoredCount);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Resource> CREATOR = new Creator<Resource>() {
        @Override
        public Resource createFromParcel(Parcel in) {
            return new Resource(in);
        }

        @Override
        public Resource[] newArray(int size) {
            return new Resource[size];
        }
    };

    @Override
    public int compareTo(@NonNull Object o) {
        return (int) (this.getLastTimeUsed() - ((Resource) o).getLastTimeUsed());
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getOp() {
        return op;
    }

    public void setOp(int op) {
        this.op = op;
    }

    public long getLastTimeUsed() {
        return lastTimeUsed;
    }

    public void setLastTimeUsed(long lastTimeUsed) {
        this.lastTimeUsed = lastTimeUsed;
    }

    public String getRelativeLastTimeUsed() {
        return relativeLastTimeUsed;
    }

    public void setRelativeLastTimeUsed(String relativeLastTimeUsed) {
        this.relativeLastTimeUsed = relativeLastTimeUsed;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public double getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(double riskLevel) {
        this.riskLevel = riskLevel;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public long getRejectTime() {
        return rejectTime;
    }

    public void setRejectTime(long rejectTime) {
        this.rejectTime = rejectTime;
    }

    public int getAllowedCount() {
        return allowedCount;
    }

    public void setAllowedCount(int allowedCount) {
        this.allowedCount = allowedCount;
    }

    public int getIgnoredCount() {
        return ignoredCount;
    }

    public void setIgnoredCount(int ignoredCount) {
        this.ignoredCount = ignoredCount;
    }

    @Override
    public String toString() {
        return "Was used: " + relativeLastTimeUsed + " has been allowed: " + allowedCount + " times and rejected: " + ignoredCount + " times";
    }
}