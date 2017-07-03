package edu.umbc.ebiquity.mithril.data.model.rules;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;

public class Violation implements Parcelable {
    public static final Creator<Violation> CREATOR = new Creator<Violation>() {
        @Override
        public Violation createFromParcel(Parcel in) {
            return new Violation(in);
        }

        @Override
        public Violation[] newArray(int size) {
            return new Violation[size];
        }
    };
    private long policyId;
    private long appId;
    private int oprId;
    private Resource resource;
    private String appStr; // App string
    private String opStr; // operation string
    private boolean asked; // user was asked about this violation
    private boolean tvfv; //marked as true or false violation
    private Timestamp detectedAtTime;
    private Timestamp feedbackTime;
    private List<Long> ctxtIds = new ArrayList<>();
    private int count;

    public Violation(long policyId, long appId, int oprId, String appStr, String opStr, boolean asked, boolean tvfv, Timestamp detectedAtTime, List<Long> ctxtIds, int count, Resource resource) {
        this.policyId = policyId;
        this.appId = appId;
        this.oprId = oprId;
        this.appStr = appStr;
        this.opStr = opStr.toLowerCase();
        this.asked = asked;
        this.tvfv = tvfv;
        this.detectedAtTime = detectedAtTime;
        this.feedbackTime = new Timestamp(0);
        this.ctxtIds = ctxtIds;
        this.count = count;
        this.resource = resource;
    }

    protected Violation(Parcel in) {
        policyId = in.readLong();
        appId = in.readLong();
        oprId = in.readInt();
        resource = in.readParcelable(Resource.class.getClassLoader());
        appStr = in.readString();
        opStr = in.readString();
        asked = in.readByte() != 0;
        tvfv = in.readByte() != 0;
        count = in.readInt();
        detectedAtTime = new Timestamp(in.readLong());
        in.readList(ctxtIds, Long.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(policyId);
        dest.writeLong(appId);
        dest.writeInt(oprId);
        dest.writeParcelable(resource, flags);
        dest.writeString(appStr);
        dest.writeString(opStr);
        dest.writeByte((byte) (asked ? 1 : 0));
        dest.writeByte((byte) (tvfv ? 1 : 0));
        dest.writeInt(count);
        dest.writeLong(getDetectedAtTime().getTime());
        dest.writeList(ctxtIds);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Policy: " + policyId + " for app: " + appStr + " with access: " + opStr + " violated at: " + detectedAtTime;
    }

    public String getContextsString(Context context) {
        if (ctxtIds == null || ctxtIds.size() == 0)
            return "empty context?!";
        SQLiteDatabase mithrilDB = MithrilDBHelper.getHelper(context).getWritableDatabase();
        StringBuffer ctxtIdString = new StringBuffer();
        List<String> contextStrings = MithrilDBHelper.getHelper(context).findMostSpecificContextByID(mithrilDB, ctxtIds);
        for (String ctxtStr : contextStrings) {
            ctxtIdString.append(ctxtStr);
            ctxtIdString.append(",");
        }
        ctxtIdString.deleteCharAt(ctxtIdString.length() - 1);
        return ctxtIdString.toString();
    }

    public String getCtxtIdString() {
        if (ctxtIds == null || ctxtIds.size() == 0)
            return "";
        StringBuffer ctxtIdString = new StringBuffer();
        for (Long ctxt : ctxtIds) {
            ctxtIdString.append(ctxt);
            ctxtIdString.append(",");
        }
        return ctxtIdString.toString();
    }

    public long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(long policyId) {
        this.policyId = policyId;
    }

    public long getAppId() {
        return appId;
    }

    public void setAppId(long appId) {
        this.appId = appId;
    }

    public int getOprId() {
        return oprId;
    }

    public void setOprId(int oprId) {
        this.oprId = oprId;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public String getAppStr() {
        return appStr;
    }

    public void setAppStr(String appStr) {
        this.appStr = appStr;
    }

    public String getOpStr() {
        return opStr;
    }

    public void setOpStr(String opStr) {
        this.opStr = opStr;
    }

    public boolean isAsked() {
        return asked;
    }

    public void setAsked(boolean asked) {
        this.asked = asked;
    }

    public boolean isTvfv() {
        return tvfv;
    }

    public void setTvfv(boolean tvfv) {
        this.tvfv = tvfv;
    }

    public Timestamp getDetectedAtTime() {
        return detectedAtTime;
    }

    public void setDetectedAtTime(Timestamp detectedAtTime) {
        this.detectedAtTime = detectedAtTime;
    }

    public Timestamp getFeedbackTime() {
        return feedbackTime;
    }

    public void setFeedbackTime(Timestamp feedbackTime) {
        this.feedbackTime = feedbackTime;
    }

    public List<Long> getCtxtIds() {
        return ctxtIds;
    }

    public void setCtxtIds(List<Long> ctxtIds) {
        this.ctxtIds = ctxtIds;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}