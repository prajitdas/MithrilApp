package edu.umbc.ebiquity.mithril.data.model.rules;

import android.graphics.drawable.Drawable;

import java.util.List;

/**
 * Created by prajit on 5/29/17.
 */

public class AppUsageStats {
    private String packageName;
    private long beginTimeStamp;
    private long endTimeStamp;
    private long lastTimeUsed;
    private long totalTimeInForeground;
    private int launchCount;
    private List<Resource> resourcesUsed;
    private String label;
    private Drawable icon;

    public AppUsageStats() {
    }

    public AppUsageStats(String packageName,
                         long beginTimeStamp,
                         long endTimeStamp,
                         long lastTimeUsed,
                         long totalTimeInForeground,
                         int launchCount,
                         List<Resource> resourcesUsed,
                         String label,
                         Drawable icon) {
        this.packageName = packageName;
        this.beginTimeStamp = beginTimeStamp;
        this.endTimeStamp = endTimeStamp;
        this.lastTimeUsed = lastTimeUsed;
        this.totalTimeInForeground = totalTimeInForeground;
        this.launchCount = launchCount;
        this.resourcesUsed = resourcesUsed;
        this.label = label;
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
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

    public List<Resource> getResourcesUsed() {
        return resourcesUsed;
    }

    public void setResourcesUsed(List<Resource> resourcesUsed) {
        this.resourcesUsed = resourcesUsed;
    }

    public String getResourcesUsedString() {
        StringBuffer resourcesString = new StringBuffer();
        for (Resource resource : resourcesUsed)
            resourcesString.append(resource.getResourceName());
        return resourcesString.toString();
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

    @Override
    public String toString() {
        return "AppUsageStats{" +
                "packageName='" + packageName + '\'' +
                ", beginTimeStamp=" + beginTimeStamp +
                ", endTimeStamp=" + endTimeStamp +
                ", lastTimeUsed=" + lastTimeUsed +
                ", totalTimeInForeground=" + totalTimeInForeground +
                ", launchCount=" + launchCount +
                ", resourcesUsed=" + resourcesUsed +
                ", label='" + label + '\'' +
                ", icon=" + icon +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppUsageStats)) return false;

        AppUsageStats that = (AppUsageStats) o;

        if (getBeginTimeStamp() != that.getBeginTimeStamp()) return false;
        if (getEndTimeStamp() != that.getEndTimeStamp()) return false;
        if (getLastTimeUsed() != that.getLastTimeUsed()) return false;
        if (getTotalTimeInForeground() != that.getTotalTimeInForeground()) return false;
        if (getLaunchCount() != that.getLaunchCount()) return false;
        if (!getPackageName().equals(that.getPackageName())) return false;
        if (!getResourcesUsed().equals(that.getResourcesUsed())) return false;
        if (!getLabel().equals(that.getLabel())) return false;
        return getIcon().equals(that.getIcon());
    }

    @Override
    public int hashCode() {
        int result = getPackageName().hashCode();
        result = 31 * result + (int) (getBeginTimeStamp() ^ (getBeginTimeStamp() >>> 32));
        result = 31 * result + (int) (getEndTimeStamp() ^ (getEndTimeStamp() >>> 32));
        result = 31 * result + (int) (getLastTimeUsed() ^ (getLastTimeUsed() >>> 32));
        result = 31 * result + (int) (getTotalTimeInForeground() ^ (getTotalTimeInForeground() >>> 32));
        result = 31 * result + getLaunchCount();
        result = 31 * result + getResourcesUsed().hashCode();
        result = 31 * result + getLabel().hashCode();
        result = 31 * result + getIcon().hashCode();
        return result;
    }
}