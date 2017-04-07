package edu.umbc.ebiquity.mithril.data.model.rules;

import edu.umbc.ebiquity.mithril.data.model.rules.actions.RuleAction;

public class PolicyRule {
    private int id;
    private String name;
    private int ctxId;
    private int appId;
    private RuleAction action;

    public PolicyRule() {
        super();
    }

    public PolicyRule(int id, String name, int ctxId, int appId, RuleAction action) {
        this.id = id;
        this.name = name;
        this.ctxId = ctxId;
        this.appId = appId;
        this.action = action;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCtxId() {
        return ctxId;
    }

    public void setCtxId(int ctxId) {
        this.ctxId = ctxId;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public RuleAction getAction() {
        return action;
    }

    public void setAction(RuleAction action) {
        this.action = action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PolicyRule)) return false;

        PolicyRule that = (PolicyRule) o;

        if (getId() != that.getId()) return false;
        if (getCtxId() != that.getCtxId()) return false;
        if (getAppId() != that.getAppId()) return false;
        if (!getName().equals(that.getName())) return false;
        return getAction().equals(that.getAction());
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getCtxId();
        result = 31 * result + getAppId();
        result = 31 * result + getAction().hashCode();
        return result;
    }
}