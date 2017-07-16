package edu.umbc.ebiquity.mithril.data.model.rules;

/**
 * Created by prajit on 7/4/17.
 */

public class DefaultRule {
    private String appCat;
    private String permission;
    private double value;

    public DefaultRule(String appCat, String permission, double value) {
        this.appCat = appCat;
        this.permission = permission;
        this.value = value;
    }

    public String getAppCat() {
        return appCat;
    }

    public void setAppCat(String appCat) {
        this.appCat = appCat;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
