package edu.umbc.ebiquity.mithril.data.model.rules.context;

import android.annotation.TargetApi;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.util.specialtasks.contextinstances.DayOfWeek;

@TargetApi(Build.VERSION_CODES.N)
public class SemanticTime extends SemanticUserContext implements Parcelable {
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
    private final String type = MithrilAC.getPrefKeyContextTypeTemporal();
    private List<DayOfWeek> dayOfWeek;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private String inferredTime;
    private boolean enabled;

    public SemanticTime(List<DayOfWeek> dayOfWeek, int startHour, int startMinute, int endHour, int endMinute, String inferredTime, boolean enabled) {
        this.dayOfWeek = dayOfWeek;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.inferredTime = inferredTime;
        this.enabled = enabled;
        this.dayOfWeek = dayOfWeek;
    }

    protected SemanticTime(Parcel in) {
        startHour = in.readInt();
        startMinute = in.readInt();
        endHour = in.readInt();
        endMinute = in.readInt();
        inferredTime = in.readString();
        enabled = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(startHour);
        dest.writeInt(startMinute);
        dest.writeInt(endHour);
        dest.writeInt(endMinute);
        dest.writeString(inferredTime);
        dest.writeByte((byte) (enabled ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Calendar getStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, startHour);
        calendar.set(Calendar.MINUTE, startMinute);
        return calendar;
    }

    public Calendar getEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, endHour);
        calendar.set(Calendar.MINUTE, endMinute);
        return calendar;
    }

    public String getDayOfWeekString() {
        StringBuffer stringBufferDayOfWeek = new StringBuffer();
        if (dayOfWeek == null)
            return stringBufferDayOfWeek.toString();
        for (DayOfWeek aDay : dayOfWeek) {
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

    public List<DayOfWeek> getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(List<DayOfWeek> dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
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