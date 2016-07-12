package edu.umbc.cs.ebiquity.mithril.data.model;

import android.graphics.Bitmap;

/**
* Helper class for managing content
*/
public class AppData implements Comparable<AppData>{
    private String appDescription;
    private String[] permissions;
    private String associatedProcessName;
    private int	targetSdkVersion;
    private Bitmap icon;
    private String appName;
    private String packageName;
    private String versionInfo;

    public boolean isSelected() {
        return selected;
    }

    public AppData setSelected(boolean selected) {
        this.selected = selected;
        return this;
    }

    private boolean selected;

    public AppData(String packageName) {
        setPackageName(packageName);
    }

    public AppData(String appDescription, String[] permissions,
                   String associatedProcessName,
                   int targetSdkVersion,
                   Bitmap icon, String appName,
                   String packageName,
                   String versionInfo,
                   boolean selected) {
        setAppDescription(appDescription);
        setPermissions(permissions);
        setAssociatedProcessName(associatedProcessName);
        setTargetSdkVersion(targetSdkVersion);
        setIcon(icon);
        setAppName(appName);
        setPackageName(packageName);
        setVersionInfo(versionInfo);
        setSelected(selected);
    }

    public String getAppDescription() {
        return appDescription;
    }

    public AppData setAppDescription(String appDescription) {
        this.appDescription = appDescription;
        return this;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public AppData setPermissions(String[] permissions) {
        this.permissions = permissions;
        return this;
    }

    public String getAssociatedProcessName() {
        return associatedProcessName;
    }

    public AppData setAssociatedProcessName(String associatedProcessName) {
        this.associatedProcessName = associatedProcessName;
        return this;
    }

    public int getTargetSdkVersion() {
        return targetSdkVersion;
    }

    public AppData setTargetSdkVersion(int targetSdkVersion) {
        this.targetSdkVersion = targetSdkVersion;
        return this;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public AppData setIcon(Bitmap icon) {
        this.icon = icon;
        return this;
    }

    public String getAppName() {
        return appName;
    }

    public AppData setAppName(String appName) {
        this.appName = appName;
        return this;
    }

    public String getPackageName() {
        return packageName;
    }

    public AppData setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public String getVersionInfo() {
        return versionInfo;
    }

    public AppData setVersionInfo(String versionInfo) {
        this.versionInfo = versionInfo;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppData)) return false;

        AppData that = (AppData) o;

        if (!getPackageName().equals(that.getPackageName())) return false;
        return getVersionInfo().equals(that.getVersionInfo());

    }

    @Override
    public int hashCode() {
        int result = getPackageName().hashCode();
        result = 31 * result + getVersionInfo().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "AppData{" +
                "packageName='" + packageName + '\'' +
                ", versionInfo='" + versionInfo + '\'' +
                ", appName='" + appName + '\'' +
                '}';
    }

    @Override
    public int compareTo(AppData another) {
        return this.getAppName().compareTo(another.getAppName());
    }
}