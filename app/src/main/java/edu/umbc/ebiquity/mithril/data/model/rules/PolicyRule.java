package edu.umbc.ebiquity.mithril.data.model.rules;

import android.os.Parcel;
import android.os.Parcelable;

public class PolicyRule implements Parcelable{
    private int id; // ID of policy defined; this will be used to determine if multiple rows belong to the same policy
    private long appId; // App id that sent the request
    private long ctxId; // context id in which requested
    private int op; // operation
    private Action action; // Action will be denoted as: 0 for to deny, 1 for allow
    private String actStr; // Action string
    private String appStr; // App string
    private String ctxStr; // context string
    private String opStr; // operation string
    private boolean enabled; // policy enabled or not

    protected PolicyRule(Parcel in) {
        id = in.readInt();
        appId = in.readInt();
        ctxId = in.readInt();
        op = in.readInt();
        actStr = in.readString();
        appStr = in.readString();
        ctxStr = in.readString();
        opStr = in.readString();
        enabled = in.readByte() != 0;
    }

    public PolicyRule(int id, long appId, long ctxId, int op, Action action, String actStr, String appStr, String ctxStr, String opStr, boolean enabled) {
        this.id = id;
        this.appId = appId;
        this.ctxId = ctxId;
        this.op = op;
        this.action = action;
        this.actStr = actStr;
        this.appStr = appStr;
        this.ctxStr = ctxStr;
        this.opStr = opStr;
        this.enabled = enabled;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeLong(appId);
        dest.writeLong(ctxId);
        dest.writeInt(op);
        dest.writeString(actStr);
        dest.writeString(appStr);
        dest.writeString(ctxStr);
        dest.writeString(opStr);
        dest.writeByte((byte) (enabled ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PolicyRule> CREATOR = new Creator<PolicyRule>() {
        @Override
        public PolicyRule createFromParcel(Parcel in) {
            return new PolicyRule(in);
        }

        @Override
        public PolicyRule[] newArray(int size) {
            return new PolicyRule[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public long getCtxId() {
        return ctxId;
    }

    public void setCtxId(long ctxId) {
        this.ctxId = ctxId;
    }

    public int getOp() {
        return op;
    }

    public void setOp(int op) {
        this.op = op;
    }

    public String getActStr() {
        return actStr;
    }

    public void setActStr(String actStr) {
        this.actStr = actStr;
    }

    public String getAppStr() {
        return appStr;
    }

    public void setAppStr(String appStr) {
        this.appStr = appStr;
    }

    public String getCtxStr() {
        return ctxStr;
    }

    public void setCtxStr(String ctxStr) {
        this.ctxStr = ctxStr;
    }

    public String getOpStr() {
        return opStr;
    }

    public void setOpStr(String opStr) {
        this.opStr = opStr;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

        @Override
    public String toString() {
        return "Policy: "+Integer.toString(getId())+" for app: "+getAppStr()+" with access to: "+getOpStr()+" in context: "+getCtxStr()+" is "+getActStr();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PolicyRule)) return false;

        PolicyRule that = (PolicyRule) o;

        if (getId() != that.getId()) return false;
        if (getAppId() != that.getAppId()) return false;
        if (getCtxId() != that.getCtxId()) return false;
        if (getOp() != that.getOp()) return false;
        if (isEnabled() != that.isEnabled()) return false;
        if (getAction() != that.getAction()) return false;
        if (!getActStr().equals(that.getActStr())) return false;
        if (!getAppStr().equals(that.getAppStr())) return false;
        if (!getCtxStr().equals(that.getCtxStr())) return false;
        return getOpStr().equals(that.getOpStr());
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (int) (getAppId() ^ (getAppId() >>> 32));
        result = 31 * result + (int) (getCtxId() ^ (getCtxId() >>> 32));
        result = 31 * result + getOp();
        result = 31 * result + getAction().hashCode();
        result = 31 * result + getActStr().hashCode();
        result = 31 * result + getAppStr().hashCode();
        result = 31 * result + getCtxStr().hashCode();
        result = 31 * result + getOpStr().hashCode();
        result = 31 * result + (isEnabled() ? 1 : 0);
        return result;
    }
}