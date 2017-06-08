package edu.umbc.ebiquity.mithril.data.model.rules;

import android.graphics.drawable.Drawable;

/**
 * Created by prajit on 5/9/17.
 */

public class Resource {
    private String resourceName;
    private long beginTimeStamp;
    private long endTimeStamp;
    private long lastTimeUsed;
    private String relativeLastTimeUsed;
    private long totalTimeInForeground;
    private int launchCount;
    private String label;
    private Drawable icon;

    public Resource() {
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return super.toString();
    }

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

    public String getRelativeLastTimeUsed() {
        return relativeLastTimeUsed;
    }

    public void setRelativeLastTimeUsed(String relativeLastTimeUsed) {
        this.relativeLastTimeUsed = relativeLastTimeUsed;
    }
}