package edu.umbc.ebiquity.mithril.data.model.rules.context;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import android.icu.util.Calendar;

import java.util.List;
import java.util.Set;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.util.specialtasks.contextinstances.DayOfWeek;

@TargetApi(Build.VERSION_CODES.N)
public class SemanticTime extends SemanticUserContext implements Parcelable {
    private final String type = MithrilAC.getPrefKeyContextTypeTemporal();
    private List<DayOfWeek> dayOfWeek;
    private Calendar start;
    private Calendar end;
    private String inferredTime;
    private boolean enabled = false;

    protected SemanticTime(Parcel in) {
        inferredTime = in.readString();
        enabled = in.readByte() != 0;
        start.set(Calendar.HOUR_OF_DAY, in.readInt());
        start.set(Calendar.MINUTE, in.readInt());
        end.set(Calendar.HOUR_OF_DAY, in.readInt());
        end.set(Calendar.MINUTE, in.readInt());
        dayOfWeek = in.readArrayList(DayOfWeek.class.getClassLoader());
    }

    public SemanticTime(List<DayOfWeek> dayOfWeek, Calendar start, Calendar end, String inferredTime, boolean enabled) {
        this.dayOfWeek = dayOfWeek;
        this.start = start;
        this.end = end;
        this.inferredTime = inferredTime;
        this.enabled = enabled;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(inferredTime);
        dest.writeByte((byte) (enabled ? 1 : 0));
        dest.writeInt(start.get(Calendar.HOUR_OF_DAY));
        dest.writeInt(start.get(Calendar.MINUTE));
        dest.writeInt(end.get(Calendar.HOUR_OF_DAY));
        dest.writeInt(end.get(Calendar.MINUTE));
        dest.writeList(dayOfWeek);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SemanticTime> CREATOR = new Creator<SemanticTime>() {
        @Override
        public SemanticTime createFromParcel(Parcel in) {
            return new SemanticTime(in);
        }

        @Override
        public SemanticTime[] newArray(int size) {
            return new SemanticTime[size];
        }
    };

    public String getDayOfWeekString() {
        StringBuffer stringBufferDayOfWeek = new StringBuffer();
        if(dayOfWeek == null)
            return stringBufferDayOfWeek.toString();
        for(DayOfWeek aDay : dayOfWeek) {
            if (aDay.equals(DayOfWeek.Monday))
                stringBufferDayOfWeek.append(MithrilAC.getMonday());
            else if (aDay.equals(DayOfWeek.Tuesday))
                stringBufferDayOfWeek.append(MithrilAC.getTuesday());
            else if (aDay.equals(DayOfWeek.Wednesday))
                stringBufferDayOfWeek.append(MithrilAC.getWednesday());
            else if (aDay.equals(DayOfWeek.Thursday))
                stringBufferDayOfWeek.append(MithrilAC.getThursday());
            else if (aDay.equals(DayOfWeek.Friday))
                stringBufferDayOfWeek.append(MithrilAC.getFriday());
            else if (aDay.equals(DayOfWeek.Saturday))
                stringBufferDayOfWeek.append(MithrilAC.getSaturday());
            else if (aDay.equals(DayOfWeek.Sunday))
                stringBufferDayOfWeek.append(MithrilAC.getSunday());
        }
        return stringBufferDayOfWeek.toString();
    }

    @Override
    public String toString() {
        return "\"" + inferredTime + "\" time context repeats every " + getDayOfWeekString();
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getLabel() {
        return inferredTime;
    }

    @Override
    public void setLabel(String label) {
        inferredTime = label;
    }

    public List<DayOfWeek> getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(List<DayOfWeek> dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Calendar getStart() {
        return start;
    }

    public void setStart(Calendar start) {
        this.start = start;
    }

    public Calendar getEnd() {
        return end;
    }

    public void setEnd(Calendar end) {
        this.end = end;
    }

    public String getInferredTime() {
        return inferredTime;
    }

    public void setInferredTime(String inferredTime) {
        this.inferredTime = inferredTime;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}