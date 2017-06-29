package edu.umbc.ebiquity.mithril.data.model.rules.context;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.util.specialtasks.collections.MithrilCollections;
import edu.umbc.ebiquity.mithril.util.specialtasks.contextinstances.DayOfWeek;

public class SemanticTime extends SemanticUserContext implements Parcelable {
    private final String type = MithrilAC.getPrefKeyContextTypeTemporal();
    private List<DayOfWeek> dayOfWeek;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private String inferredTime;
    private boolean enabled;
    private int level;
    private boolean allDay = false;

    public SemanticTime(List<DayOfWeek> dayOfWeek, int startHour, int startMinute, int endHour, int endMinute, String inferredTime, boolean enabled, int level, boolean allDay) {
        this.dayOfWeek = dayOfWeek;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.inferredTime = inferredTime;
        this.enabled = enabled;
        this.level = level;
        this.allDay = true;
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
        in.readList(dayOfWeek, DayOfWeek.class.getClassLoader());
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

        List<DayOfWeek> week = new ArrayList<>();
        week.add(DayOfWeek.Saturday);
        week.add(DayOfWeek.Sunday);
        week.add(DayOfWeek.Monday);
        week.add(DayOfWeek.Tuesday);
        week.add(DayOfWeek.Wednesday);
        week.add(DayOfWeek.Thursday);
        week.add(DayOfWeek.Friday);

        if (dayOfWeek.size() == 7 && MithrilCollections.isExactMatchList(dayOfWeek, week))
            return MithrilAC.getPrefAnydaytimeTemporalKey();

        week.remove(DayOfWeek.Saturday);
        week.remove(DayOfWeek.Sunday);

        if (dayOfWeek.size() == 5 && MithrilCollections.isExactMatchList(dayOfWeek, week))
            return MithrilAC.getPrefWeekdayTemporalKey();

        week.remove(DayOfWeek.Monday);
        week.remove(DayOfWeek.Tuesday);
        week.remove(DayOfWeek.Wednesday);
        week.remove(DayOfWeek.Thursday);
        week.remove(DayOfWeek.Friday);

        if (dayOfWeek.size() == 2 && MithrilCollections.isExactMatchList(dayOfWeek, week))
            return MithrilAC.getPrefWeekendTemporalKey();

        StringBuffer stringBufferDayOfWeek = new StringBuffer();
        for (DayOfWeek aDay : dayOfWeek) {
            if (aDay.equals(DayOfWeek.Monday))
                stringBufferDayOfWeek.append(MithrilAC.getPrefMondayTemporalKey());
            else if (aDay.equals(DayOfWeek.Tuesday))
                stringBufferDayOfWeek.append(MithrilAC.getPrefTuesdayTemporalKey());
            else if (aDay.equals(DayOfWeek.Wednesday))
                stringBufferDayOfWeek.append(MithrilAC.getPrefWednesdayTemporalKey());
            else if (aDay.equals(DayOfWeek.Thursday))
                stringBufferDayOfWeek.append(MithrilAC.getPrefThursdayTemporalKey());
            else if (aDay.equals(DayOfWeek.Friday))
                stringBufferDayOfWeek.append(MithrilAC.getPrefFridayTemporalKey());
            else if (aDay.equals(DayOfWeek.Saturday))
                stringBufferDayOfWeek.append(MithrilAC.getPrefSaturdayTemporalKey());
            else if (aDay.equals(DayOfWeek.Sunday))
                stringBufferDayOfWeek.append(MithrilAC.getPrefSundayTemporalKey());
            stringBufferDayOfWeek.append(", ");
        }
        return stringBufferDayOfWeek.toString();
    }

    @Override
    public String toString() {
        if(getLabel().equals(MithrilAC.getPrefAnydaytimeTemporalKey()))
            return "Always";
        if(allDay)
            return "Occurs on: "+getDayOfWeekString();
        return "From:"+startHour+":"+startMinute+" to "+endHour+":"+endMinute+" on "+getDayOfWeekString();
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
}