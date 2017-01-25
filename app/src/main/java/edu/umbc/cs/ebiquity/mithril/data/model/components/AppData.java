package edu.umbc.cs.ebiquity.mithril.data.model.components;

import android.graphics.Bitmap;

import java.util.Map;

/**
* Helper class for managing content
*/
public class AppData implements Comparable<AppData>{
    private String appDescription;
    private Map<String, Boolean> permissions;
    private String associatedProcessName;
    private int	targetSdkVersion;
    private Bitmap icon;
    private String appName;
    private String packageName;
    private String versionInfo;
    private boolean installed;
    private String appType;
    private int uid;

    public AppData(String appDescription,
                   String associatedProcessName,
                   int targetSdkVersion,
                   Bitmap icon,
                   String appName,
                   String packageName,
                   String versionInfo,
                   String appType,
                   int uid) {
        setAppDescription(appDescription);
        setAssociatedProcessName(associatedProcessName);
        setTargetSdkVersion(targetSdkVersion);
        setIcon(icon);
        setAppName(appName);
        setPackageName(packageName);
        setVersionInfo(versionInfo);
        setAppType(appType);
        setUid(uid);
    }

    public AppData() {
    }

    public String getAppDescription() {
        return appDescription;
    }

    public void setAppDescription(String appDescription) {
        this.appDescription = appDescription;
    }

    public Map<String, Boolean> getPermissions() {
        return permissions;
    }

    public void setPermissions(Map<String, Boolean> permissions) {
        this.permissions = permissions;
    }

    public String getAssociatedProcessName() {
        return associatedProcessName;
    }

    public void setAssociatedProcessName(String associatedProcessName) {
        this.associatedProcessName = associatedProcessName;
    }

    public int getTargetSdkVersion() {
        return targetSdkVersion;
    }

    public void setTargetSdkVersion(int targetSdkVersion) {
        this.targetSdkVersion = targetSdkVersion;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionInfo() {
        return versionInfo;
    }

    public void setVersionInfo(String versionInfo) {
        this.versionInfo = versionInfo;
    }

    public boolean isInstalled() {
        return installed;
    }

    public void setInstalled(boolean installed) {
        this.installed = installed;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @Override
    public int compareTo(AppData another) {
        return this.getAppName().compareTo(another.getAppName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppData)) return false;

        AppData appData = (AppData) o;

        if (getTargetSdkVersion() != appData.getTargetSdkVersion()) return false;
        if (isInstalled() != appData.isInstalled()) return false;
        if (getUid() != appData.getUid()) return false;
        if (!getAppDescription().equals(appData.getAppDescription())) return false;
        if (!getPermissions().equals(appData.getPermissions())) return false;
        if (!getAssociatedProcessName().equals(appData.getAssociatedProcessName())) return false;
        if (!getIcon().equals(appData.getIcon())) return false;
        if (!getAppName().equals(appData.getAppName())) return false;
        if (!getPackageName().equals(appData.getPackageName())) return false;
        if (!getVersionInfo().equals(appData.getVersionInfo())) return false;
        return getAppType().equals(appData.getAppType());

    }

    @Override
    public int hashCode() {
        int result = getAppDescription().hashCode();
        result = 31 * result + getPermissions().hashCode();
        result = 31 * result + getAssociatedProcessName().hashCode();
        result = 31 * result + getTargetSdkVersion();
        result = 31 * result + getIcon().hashCode();
        result = 31 * result + getAppName().hashCode();
        result = 31 * result + getPackageName().hashCode();
        result = 31 * result + getVersionInfo().hashCode();
        result = 31 * result + (isInstalled() ? 1 : 0);
        result = 31 * result + getAppType().hashCode();
        result = 31 * result + getUid();
        return result;
    }

    @Override
    public String toString() {
        return "AppData{" +
                "appDescription='" + appDescription + '\'' +
                ", permissions=" + permissions +
                ", associatedProcessName='" + associatedProcessName + '\'' +
                ", targetSdkVersion=" + targetSdkVersion +
                ", icon=" + icon +
                ", appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", versionInfo='" + versionInfo + '\'' +
                ", installed=" + installed +
                ", appType='" + appType + '\'' +
                ", uid=" + uid +
                '}';
    }
}