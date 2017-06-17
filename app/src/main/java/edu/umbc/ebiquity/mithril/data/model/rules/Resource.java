package edu.umbc.ebiquity.mithril.data.model.rules;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by prajit on 5/9/17.
 */

public class Resource implements Parcelable{
    private String resourceName;
    private long beginTimeStamp;
    private long endTimeStamp;
    private long lastTimeUsed;
    private String relativeLastTimeUsed;
    private long totalTimeInForeground;
    private int launchCount;
    private String label;
    private Drawable icon;
    private int riskLevel;

    protected Resource(Parcel in) {
        resourceName = in.readString();
        beginTimeStamp = in.readLong();
        endTimeStamp = in.readLong();
        lastTimeUsed = in.readLong();
        relativeLastTimeUsed = in.readString();
        totalTimeInForeground = in.readLong();
        launchCount = in.readInt();
        label = in.readString();
        riskLevel = in.readInt();
    }

    public Resource() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(resourceName);
        dest.writeLong(beginTimeStamp);
        dest.writeLong(endTimeStamp);
        dest.writeLong(lastTimeUsed);
        dest.writeString(relativeLastTimeUsed);
        dest.writeLong(totalTimeInForeground);
        dest.writeInt(launchCount);
        dest.writeString(label);
        dest.writeInt(riskLevel);
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

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public long getBeginTimeStamp() {
        return beginTimeStamp;
    }

    public void setBeginTimeStamp(long beginTimeStamp) {
        this.beginTimeStamp = beginTimeStamp;
    }

    public long getEndTimeStamp() {
        return endTimeStamp;
    }

    public void setEndTimeStamp(long endTimeStamp) {
        this.endTimeStamp = endTimeStamp;
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

    public long getTotalTimeInForeground() {
        return totalTimeInForeground;
    }

    public void setTotalTimeInForeground(long totalTimeInForeground) {
        this.totalTimeInForeground = totalTimeInForeground;
    }

    public int getLaunchCount() {
        return launchCount;
    }

    public void setLaunchCount(int launchCount) {
        this.launchCount = launchCount;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public int getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(int riskLevel) {
        this.riskLevel = riskLevel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Resource)) return false;

        Resource resource = (Resource) o;

        if (getBeginTimeStamp() != resource.getBeginTimeStamp()) return false;
        if (getEndTimeStamp() != resource.getEndTimeStamp()) return false;
        if (getLastTimeUsed() != resource.getLastTimeUsed()) return false;
        if (getTotalTimeInForeground() != resource.getTotalTimeInForeground()) return false;
        if (getLaunchCount() != resource.getLaunchCount()) return false;
        if (getRiskLevel() != resource.getRiskLevel()) return false;
        if (!getResourceName().equals(resource.getResourceName())) return false;
        if (getRelativeLastTimeUsed() != null ? !getRelativeLastTimeUsed().equals(resource.getRelativeLastTimeUsed()) : resource.getRelativeLastTimeUsed() != null)
            return false;
        if (!getLabel().equals(resource.getLabel())) return false;
        return getIcon() != null ? getIcon().equals(resource.getIcon()) : resource.getIcon() == null;
    }

    @Override
    public int hashCode() {
        int result = getResourceName().hashCode();
        result = 31 * result + (int) (getBeginTimeStamp() ^ (getBeginTimeStamp() >>> 32));
        result = 31 * result + (int) (getEndTimeStamp() ^ (getEndTimeStamp() >>> 32));
        result = 31 * result + (int) (getLastTimeUsed() ^ (getLastTimeUsed() >>> 32));
        result = 31 * result + (getRelativeLastTimeUsed() != null ? getRelativeLastTimeUsed().hashCode() : 0);
        result = 31 * result + (int) (getTotalTimeInForeground() ^ (getTotalTimeInForeground() >>> 32));
        result = 31 * result + getLaunchCount();
        result = 31 * result + getLabel().hashCode();
        result = 31 * result + (getIcon() != null ? getIcon().hashCode() : 0);
        result = 31 * result + getRiskLevel();
        return result;
    }
}