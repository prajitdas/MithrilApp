package edu.umbc.cs.ebiquity.mithril.mithrilappmanager.data.model;

import android.graphics.Bitmap;

/**
* Helper class for managing content
*/
public class AppMetadata {
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

    public AppMetadata setSelected(boolean selected) {
        this.selected = selected;
        return this;
    }

    private boolean selected;

    public AppMetadata(String packageName) {
        setPackageName(packageName);
    }

    public AppMetadata(String appDescription, String[] permissions,
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

    public AppMetadata setAppDescription(String appDescription) {
        this.appDescription = appDescription;
        return this;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public AppMetadata setPermissions(String[] permissions) {
        this.permissions = permissions;
        return this;
    }

    public String getAssociatedProcessName() {
        return associatedProcessName;
    }

    public AppMetadata setAssociatedProcessName(String associatedProcessName) {
        this.associatedProcessName = associatedProcessName;
        return this;
    }

    public int getTargetSdkVersion() {
        return targetSdkVersion;
    }

    public AppMetadata setTargetSdkVersion(int targetSdkVersion) {
        this.targetSdkVersion = targetSdkVersion;
        return this;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public AppMetadata setIcon(Bitmap icon) {
        this.icon = icon;
        return this;
    }

    public String getAppName() {
        return appName;
    }

    public AppMetadata setAppName(String appName) {
        this.appName = appName;
        return this;
    }

    public String getPackageName() {
        return packageName;
    }

    public AppMetadata setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public String getVersionInfo() {
        return versionInfo;
    }

    public AppMetadata setVersionInfo(String versionInfo) {
        this.versionInfo = versionInfo;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppMetadata)) return false;

        AppMetadata that = (AppMetadata) o;

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
        return "AppMetadata{" +
                "packageName='" + packageName + '\'' +
                ", versionInfo='" + versionInfo + '\'' +
                ", appName='" + appName + '\'' +
                '}';
    }
}