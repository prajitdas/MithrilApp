package edu.umbc.ebiquity.mithril.data.model.components;

/*
 * Created by Prajit on 1/25/2017.
 */

import android.support.annotation.NonNull;

public class ContentProvData implements Comparable<ContentProvData>{
    private String authority;
    private int flags;
    private String writePermission;
    private String readPermission;
    private String description;
    private boolean enabled;
    private boolean exported;
    private String processName;
    private String name;
    private String packageName;
    private String label;

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public String getWritePermission() {
        return writePermission;
    }

    public void setWritePermission(String writePermission) {
        this.writePermission = writePermission;
    }

    public String getReadPermission() {
        return readPermission;
    }

    public void setReadPermission(String readPermission) {
        this.readPermission = readPermission;
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

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContentProvData)) return false;

        ContentProvData that = (ContentProvData) o;

        if (getFlags() != that.getFlags()) return false;
        if (isEnabled() != that.isEnabled()) return false;
        if (isExported() != that.isExported()) return false;
        if (!getAuthority().equals(that.getAuthority())) return false;
        if (!getWritePermission().equals(that.getWritePermission())) return false;
        if (!getReadPermission().equals(that.getReadPermission())) return false;
        if (!getDescription().equals(that.getDescription())) return false;
        if (!getProcessName().equals(that.getProcessName())) return false;
        if (!getName().equals(that.getName())) return false;
        if (!getPackageName().equals(that.getPackageName())) return false;
        return getLabel().equals(that.getLabel());

    }

    @Override
    public int hashCode() {
        int result = getAuthority().hashCode();
        result = 31 * result + getFlags();
        result = 31 * result + getWritePermission().hashCode();
        result = 31 * result + getReadPermission().hashCode();
        result = 31 * result + getDescription().hashCode();
        result = 31 * result + (isEnabled() ? 1 : 0);
        result = 31 * result + (isExported() ? 1 : 0);
        result = 31 * result + getProcessName().hashCode();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getPackageName().hashCode();
        result = 31 * result + getLabel().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ContentProvData{" +
                "authority='" + authority + '\'' +
                ", flags=" + flags +
                ", writePermission='" + writePermission + '\'' +
                ", readPermission='" + readPermission + '\'' +
                ", description='" + description + '\'' +
                ", enabled=" + enabled +
                ", exported=" + exported +
                ", processName='" + processName + '\'' +
                ", name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                ", label='" + label + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull ContentProvData o) {
        return this.equals(o) ? 0 : 1;
    }
}