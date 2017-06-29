package edu.umbc.ebiquity.mithril.data.model.rules.context;

import android.icu.util.Calendar;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.android.gms.awareness.fence.TimeFence;

import java.util.ArrayList;
import java.util.List;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.util.specialtasks.collections.MithrilCollections;

public class SemanticTime extends SemanticUserContext implements Parcelable, Comparable<SemanticTime> {
    private final String type = MithrilAC.getPrefKeyContextTypeTemporal();
    private List<Integer> dayOfWeek = new ArrayList<>();
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private String inferredTime;
    private boolean enabled;
    private int level;
    private boolean allDay = false;

    public SemanticTime(List<Integer> dayOfWeek, int startHour, int startMinute, int endHour, int endMinute, String inferredTime, boolean enabled, int level, boolean allDay) {
        this.dayOfWeek = dayOfWeek;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.inferredTime = inferredTime;
        this.enabled = enabled;
        this.level = level;
        this.allDay = allDay;
    }

    protected SemanticTime(Parcel in) {
        startHour = in.readInt();
        startMinute = in.readInt();
        endHour = in.readInt();
        endMinute = in.readInt();
        inferredTime = in.readString();
        enabled = in.readByte() != 0;
        level = in.readInt();
        allDay = in.readByte() != 0;
        in.readList(dayOfWeek, Integer.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(startHour);
        dest.writeInt(startMinute);
        dest.writeInt(endHour);
        dest.writeInt(endMinute);
        dest.writeString(inferredTime);
        dest.writeByte((byte) (enabled ? 1 : 0));
        dest.writeInt(level);
        dest.writeByte((byte) (allDay ? 1 : 0));
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
        if (dayOfWeek == null)
            return new String();

        List<Integer> allWeek = new ArrayList<>();
        allWeek.add(TimeFence.DAY_OF_WEEK_SATURDAY);
        allWeek.add(TimeFence.DAY_OF_WEEK_SUNDAY);
        allWeek.add(TimeFence.DAY_OF_WEEK_MONDAY);
        allWeek.add(TimeFence.DAY_OF_WEEK_TUESDAY);
        allWeek.add(TimeFence.DAY_OF_WEEK_WEDNESDAY);
        allWeek.add(TimeFence.DAY_OF_WEEK_THURSDAY);
        allWeek.add(TimeFence.DAY_OF_WEEK_FRIDAY);

        if (dayOfWeek.size() == 7 && MithrilCollections.isExactMatchList(dayOfWeek, allWeek))
            return MithrilAC.getPrefAnydaytimeTemporalKey();

        List<Integer> weekend = new ArrayList<>();
        weekend.add(TimeFence.DAY_OF_WEEK_SATURDAY);
        weekend.add(TimeFence.DAY_OF_WEEK_SUNDAY);

        if (dayOfWeek.size() == 5 && MithrilCollections.isExactMatchList(dayOfWeek, weekend))
            return MithrilAC.getPrefWeekendTemporalKey();

        List<Integer> workweek = new ArrayList<>();
        workweek.add(TimeFence.DAY_OF_WEEK_MONDAY);
        workweek.add(TimeFence.DAY_OF_WEEK_TUESDAY);
        workweek.add(TimeFence.DAY_OF_WEEK_WEDNESDAY);
        workweek.add(TimeFence.DAY_OF_WEEK_THURSDAY);
        workweek.add(TimeFence.DAY_OF_WEEK_FRIDAY);

        if (dayOfWeek.size() == 2 && MithrilCollections.isExactMatchList(dayOfWeek, workweek))
            return MithrilAC.getPrefWeekdayTemporalKey();

        StringBuffer stringBufferDayOfWeek = new StringBuffer();
        for (Integer aDay : dayOfWeek) {
            if (aDay.equals(TimeFence.DAY_OF_WEEK_MONDAY))
                stringBufferDayOfWeek.append(MithrilAC.getPrefMondayTemporalKey());
            else if (aDay.equals(TimeFence.DAY_OF_WEEK_TUESDAY))
                stringBufferDayOfWeek.append(MithrilAC.getPrefTuesdayTemporalKey());
            else if (aDay.equals(TimeFence.DAY_OF_WEEK_WEDNESDAY))
                stringBufferDayOfWeek.append(MithrilAC.getPrefWednesdayTemporalKey());
            else if (aDay.equals(TimeFence.DAY_OF_WEEK_THURSDAY))
                stringBufferDayOfWeek.append(MithrilAC.getPrefThursdayTemporalKey());
            else if (aDay.equals(TimeFence.DAY_OF_WEEK_FRIDAY))
                stringBufferDayOfWeek.append(MithrilAC.getPrefFridayTemporalKey());
            else if (aDay.equals(TimeFence.DAY_OF_WEEK_SATURDAY))
                stringBufferDayOfWeek.append(MithrilAC.getPrefSaturdayTemporalKey());
            else if (aDay.equals(TimeFence.DAY_OF_WEEK_SUNDAY))
                stringBufferDayOfWeek.append(MithrilAC.getPrefSundayTemporalKey());
            stringBufferDayOfWeek.append(", ");
        }
        return stringBufferDayOfWeek.toString();
    }

    @Override
    public String toString() {
        if(this.getDayOfWeekString().equals(MithrilAC.getPrefAnydaytimeTemporalKey()) && isAllDay())
            return "Always";
        if(isAllDay())
            return getDayOfWeekString();
        return "From: "+getTimeString(startHour)+getTimeString(startMinute)+"hrs to "+getTimeString(endHour)+getTimeString(endMinute)+"hrs on "+getDayOfWeekString();
    }

    public static String getTimeString(int time) {
        if(time < 10)
            return "0"+String.valueOf(time);
        return String.valueOf(time);
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

    public List<Integer> getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(List<Integer> dayOfWeek) {
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

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    @Override
    public int compareTo(@NonNull SemanticTime o) {
        int levelComparison = ((Integer) this.getLevel()).compareTo(o.getLevel());
        if(levelComparison == 0) {
            Calendar aCal = Calendar.getInstance();
            aCal.set(Calendar.HOUR_OF_DAY, this.getStartHour());
            aCal.set(android.icu.util.Calendar.MINUTE, this.getStartMinute());
            Calendar anotherCal = Calendar.getInstance();
            anotherCal.set(android.icu.util.Calendar.HOUR_OF_DAY, o.getStartHour());
            anotherCal.set(android.icu.util.Calendar.MINUTE, o.getStartMinute());
            return aCal.compareTo(anotherCal);
        } else
            return levelComparison;
    }
}