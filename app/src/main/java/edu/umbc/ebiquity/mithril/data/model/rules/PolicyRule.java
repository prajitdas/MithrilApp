package edu.umbc.ebiquity.mithril.data.model.rules;

public class PolicyRule {
    private int id;
    private String name;
    private int ctxId;
    private int appId;
    private int op;
    private Action action;

    public PolicyRule(String name, int ctxId, int appId, Action action, int op) {
        this.name = name;
        this.ctxId = ctxId;
        this.appId = appId;
        this.action = action;
        this.op = op;
    }

    public PolicyRule() {

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

    public int getOp() {
        return op;
    }

    public void setOp(int op) {
        this.op = op;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
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
        if (getOp() != that.getOp()) return false;
        if (!getName().equals(that.getName())) return false;
        return getAction() == that.getAction();
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + getName().hashCode();
        result = 31 * result + getCtxId();
        result = 31 * result + getAppId();
        result = 31 * result + getOp();
        result = 31 * result + getAction().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PolicyRule{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ctxId=" + ctxId +
                ", appId=" + appId +
                ", op=" + op +
                ", action=" + action +
                '}';
    }
}