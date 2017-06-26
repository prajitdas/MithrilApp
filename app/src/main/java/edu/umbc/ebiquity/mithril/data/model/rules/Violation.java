package edu.umbc.ebiquity.mithril.data.model.rules;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;

public class Violation implements Parcelable {
    private long policyId;
    private long appId;
    private long oprId;
    private String appStr; // App string
    private String opStr; // operation string
    private boolean asked; // user was asked about this violation
    private boolean tvfv; //marked as true or false violation
    private Timestamp detectedAtTime;
    private Timestamp feedbackTime;
    private List<Long> ctxtIds;
    private int count;

    public Violation(long policyId, long appId, long oprId, String appStr, String opStr, boolean asked, boolean tvfv, Timestamp detectedAtTime, List<Long> ctxtIds, int count) {
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
    }

    protected Violation(Parcel in) {
        policyId = in.readLong();
        appId = in.readLong();
        oprId = in.readLong();
        appStr = in.readString();
        opStr = in.readString();
        asked = in.readByte() != 0;
        tvfv = in.readByte() != 0;
        count = in.readInt();
        detectedAtTime = new Timestamp(in.readLong());
        feedbackTime = new Timestamp(in.readLong());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(policyId);
        dest.writeLong(appId);
        dest.writeLong(oprId);
        dest.writeString(appStr);
        dest.writeString(opStr);
        dest.writeByte((byte) (asked ? 1 : 0));
        dest.writeByte((byte) (tvfv ? 1 : 0));
        dest.writeInt(count);
        dest.writeLong(detectedAtTime.getTime());
        dest.writeLong(feedbackTime.getTime());
    }

    @Override
    public int describeContents() {
        return 0;
    }

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

    @Override
    public String toString() {
        return "Policy: " + policyId + " for app: " + appStr + " with access: " + opStr + " violated at: " + detectedAtTime;
    }

    public String getContextsString(Context context) {
        if(ctxtIds == null || ctxtIds.size() == 0)
            return "empty context?!";
        Log.d(MithrilAC.getDebugTag(), "Size found: "+Long.toString(ctxtIds.size()));
        Log.d(MithrilAC.getDebugTag(), "Long value found: "+Long.toString(ctxtIds.get(0)));
        SQLiteDatabase mithrilDB = MithrilDBHelper.getHelper(context).getWritableDatabase();
        StringBuffer ctxtIdString = new StringBuffer();
        for (Long ctxt : ctxtIds) {
            Log.d(MithrilAC.getDebugTag(), "Long value found: "+Long.toString(ctxt));
            ctxtIdString.append(MithrilDBHelper.getHelper(context).findContextByID(mithrilDB, ctxt).second);
            ctxtIdString.append(",");
        }
        ctxtIdString.deleteCharAt(ctxtIdString.length()-1);
        return ctxtIdString.toString();
    }

    public String getCtxtIdString() {
        if(ctxtIds == null || ctxtIds.size() == 0)
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

    public long getOprId() {
        return oprId;
    }

    public void setOprId(long oprId) {
        this.oprId = oprId;
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