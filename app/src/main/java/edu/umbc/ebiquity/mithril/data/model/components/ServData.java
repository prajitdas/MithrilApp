package edu.umbc.ebiquity.mithril.data.model.components;

import android.support.annotation.NonNull;

/**
 * Created by Prajit on 1/25/2017.
 * Constants
 * FLAG_EXTERNAL_SERVICE - If set, the service can be bound and run in the calling application's package, rather than the package in which it is declared.
 * FLAG_ISOLATED_PROCESS - If set, the service will run in its own isolated process.
 * FLAG_SINGLE_USER - If set, a single instance of the service will run for all users on the device.
 * FLAG_STOP_WITH_TASK - If set, the service will automatically be stopped by the system if the user removes a task that is rooted in one of the application's activities.
 */

public class ServData implements Comparable<ServData> {
    private String permission;
    private int flags;
    private String description;
    private boolean enabled;
    private boolean exported;
    private String name;
    private String packageName;
    private String label;
    private String appName;
    private String appType;
    private Resource resource;

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isExported() {
        return exported;
    }

    public void setExported(boolean exported) {
        this.exported = exported;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServData)) return false;

        ServData servData = (ServData) o;

        if (getFlags() != servData.getFlags()) return false;
        if (isEnabled() != servData.isEnabled()) return false;
        if (isExported() != servData.isExported()) return false;
        if (!getPermission().equals(servData.getPermission())) return false;
        if (!getDescription().equals(servData.getDescription())) return false;
        if (!getName().equals(servData.getName())) return false;
        if (!getPackageName().equals(servData.getPackageName())) return false;
        if (!getLabel().equals(servData.getLabel())) return false;
        if (!getAppName().equals(servData.getAppName())) return false;
        if (!getAppType().equals(servData.getAppType())) return false;
        return getResource().equals(servData.getResource());

    }

    @Override
    public int hashCode() {
        int result = getPermission().hashCode();
        result = 31 * result + getFlags();
        result = 31 * result + getDescription().hashCode();
        result = 31 * result + (isEnabled() ? 1 : 0);
        result = 31 * result + (isExported() ? 1 : 0);
        result = 31 * result + getName().hashCode();
        result = 31 * result + getPackageName().hashCode();
        result = 31 * result + getLabel().hashCode();
        result = 31 * result + getAppName().hashCode();
        result = 31 * result + getAppType().hashCode();
        result = 31 * result + getResource().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ServData{" +
                "permission='" + permission + '\'' +
                ", flags=" + flags +
                ", description='" + description + '\'' +
                ", enabled=" + enabled +
                ", exported=" + exported +
                ", name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                ", label='" + label + '\'' +
                ", appName='" + appName + '\'' +
                ", appType='" + appType + '\'' +
                ", resource=" + resource +
                '}';
    }

    @Override
    public int compareTo(@NonNull ServData o) {
        return 0;
    }
}