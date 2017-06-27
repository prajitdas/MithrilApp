package edu.umbc.ebiquity.mithril.util.specialtasks.detect.context;

import android.annotation.TargetApi;
import android.icu.util.Calendar;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticTime;

/**
 * Created by prajit on 6/27/17.
 */

public class DetectTemporalContext {
    public static List<String> whatTimeIsItNow(Map<String, SemanticTime> semanticTimes) {
        List<String> labels = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int dow = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dow) {
            case Calendar.SUNDAY:
                labels.add(MithrilAC.getPrefSundayTemporalKey());
                labels.add(MithrilAC.getPrefWeekendTemporalKey());
                break;
            case Calendar.MONDAY:
                labels.add(MithrilAC.getPrefMondayTemporalKey());
                labels.add(MithrilAC.getPrefWeekdayTemporalKey());
                break;
            case Calendar.TUESDAY:
                labels.add(MithrilAC.getPrefTuesdayTemporalKey());
                labels.add(MithrilAC.getPrefWeekdayTemporalKey());
                break;
            case Calendar.WEDNESDAY:
                labels.add(MithrilAC.getPrefWednesdayTemporalKey());
                labels.add(MithrilAC.getPrefWeekdayTemporalKey());
                break;
            case Calendar.THURSDAY:
                labels.add(MithrilAC.getPrefThursdayTemporalKey());
                labels.add(MithrilAC.getPrefWeekdayTemporalKey());
                break;
            case Calendar.FRIDAY:
                labels.add(MithrilAC.getPrefFridayTemporalKey());
                labels.add(MithrilAC.getPrefWeekdayTemporalKey());
                break;
            case Calendar.SATURDAY:
                labels.add(MithrilAC.getPrefSaturdayTemporalKey());
                labels.add(MithrilAC.getPrefWeekendTemporalKey());
                break;
        }
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        if (isWithinTimePeriod(MithrilAC.getPrefBreakfastTemporalKey(), semanticTimes, hour, minute)) {
            labels.add(MithrilAC.getPrefBreakfastTemporalKey());
            labels.add(MithrilAC.getPrefPersonalTemporalKey());
        } else if (isWithinTimePeriod(MithrilAC.getPrefLunchTemporalKey(), semanticTimes, hour, minute)) {
            labels.add(MithrilAC.getPrefLunchTemporalKey());
            labels.add(MithrilAC.getPrefPersonalTemporalKey());
        } else if (isWithinTimePeriod(MithrilAC.getPrefDinnerTemporalKey(), semanticTimes, hour, minute)) {
            labels.add(MithrilAC.getPrefDinnerTemporalKey());
            labels.add(MithrilAC.getPrefPersonalTemporalKey());
        } else if (isWithinTimePeriod(MithrilAC.getPrefDndTemporalKey(), semanticTimes, hour, minute)) {
            labels.add(MithrilAC.getPrefDndTemporalKey());
            labels.add(MithrilAC.getPrefPersonalTemporalKey());
        } else if (isWithinTimePeriod(MithrilAC.getPrefFamilyTemporalKey(), semanticTimes, hour, minute)) {
            labels.add(MithrilAC.getPrefFamilyTemporalKey());
            labels.add(MithrilAC.getPrefPersonalTemporalKey());
        } else if (isWithinTimePeriod(MithrilAC.getPrefAloneTemporalKey(), semanticTimes, hour, minute)) {
            labels.add(MithrilAC.getPrefAloneTemporalKey());
            labels.add(MithrilAC.getPrefPersonalTemporalKey());
        } else if (isWithinTimePeriod(MithrilAC.getPrefWorkMorningTemporalKey(), semanticTimes, hour, minute)) {
            labels.add(MithrilAC.getPrefWorkMorningTemporalKey());
            labels.add(MithrilAC.getPrefProfessionalTemporalKey());
        } else if (isWithinTimePeriod(MithrilAC.getPrefWorkAfternoonTemporalKey(), semanticTimes, hour, minute)) {
            labels.add(MithrilAC.getPrefWorkAfternoonTemporalKey());
            labels.add(MithrilAC.getPrefProfessionalTemporalKey());
        }
        labels.add(MithrilAC.getPrefAnydaytimeTemporalKey());
        return labels;
    }

    private static boolean isWithinTimePeriod(String key, Map<String, SemanticTime> semanticTimes, int hour, int minute) {
        if (semanticTimes.containsKey(key)) {
            SemanticTime semanticTime = semanticTimes.get(key);
            if (hour > semanticTime.getStartHour() && hour < semanticTime.getEndHour())
                if (minute > semanticTime.getStartMinute() && minute < semanticTime.getEndMinute())
                    return true;
        }
        return false;
    }
}