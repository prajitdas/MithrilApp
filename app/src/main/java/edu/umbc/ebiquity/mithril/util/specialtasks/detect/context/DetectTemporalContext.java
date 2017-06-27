package edu.umbc.ebiquity.mithril.util.specialtasks.detect.context;

import android.icu.util.Calendar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticTime;

public class DetectTemporalContext {
    public static List<SemanticTime> whatTimeIsItNow(Map<String, SemanticTime> semanticTimes) {
        List<SemanticTime> currentSemanticTimes = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int dow = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dow) {
            case Calendar.SUNDAY:
                currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefSundayTemporalKey()));
                currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefWeekendTemporalKey()));
                break;
            case Calendar.MONDAY:
                currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefMondayTemporalKey()));
                currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefWeekdayTemporalKey()));
                break;
            case Calendar.TUESDAY:
                currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefTuesdayTemporalKey()));
                currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefWeekdayTemporalKey()));
                break;
            case Calendar.WEDNESDAY:
                currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefWednesdayTemporalKey()));
                currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefWeekdayTemporalKey()));
                break;
            case Calendar.THURSDAY:
                currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefThursdayTemporalKey()));
                currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefWeekdayTemporalKey()));
                break;
            case Calendar.FRIDAY:
                currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefFridayTemporalKey()));
                currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefWeekdayTemporalKey()));
                break;
            case Calendar.SATURDAY:
                currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefSaturdayTemporalKey()));
                currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefWeekendTemporalKey()));
                break;
        }
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        if (isWithinTimePeriod(MithrilAC.getPrefBreakfastTemporalKey(), semanticTimes, hour, minute)) {
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefBreakfastTemporalKey()));
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefPersonalTemporalKey()));
        } else if (isWithinTimePeriod(MithrilAC.getPrefLunchTemporalKey(), semanticTimes, hour, minute)) {
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefLunchTemporalKey()));
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefPersonalTemporalKey()));
        } else if (isWithinTimePeriod(MithrilAC.getPrefDinnerTemporalKey(), semanticTimes, hour, minute)) {
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefDinnerTemporalKey()));
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefPersonalTemporalKey()));
        } else if (isWithinTimePeriod(MithrilAC.getPrefDndTemporalKey(), semanticTimes, hour, minute)) {
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefDndTemporalKey()));
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefPersonalTemporalKey()));
        } else if (isWithinTimePeriod(MithrilAC.getPrefFamilyTemporalKey(), semanticTimes, hour, minute)) {
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefFamilyTemporalKey()));
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefPersonalTemporalKey()));
        } else if (isWithinTimePeriod(MithrilAC.getPrefAloneTemporalKey(), semanticTimes, hour, minute)) {
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefAloneTemporalKey()));
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefPersonalTemporalKey()));
        } else if (isWithinTimePeriod(MithrilAC.getPrefWorkMorningTemporalKey(), semanticTimes, hour, minute)) {
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefWorkMorningTemporalKey()));
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefProfessionalTemporalKey()));
        } else if (isWithinTimePeriod(MithrilAC.getPrefWorkAfternoonTemporalKey(), semanticTimes, hour, minute)) {
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefWorkAfternoonTemporalKey()));
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefProfessionalTemporalKey()));
        }
        currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefAnydaytimeTemporalKey()));
        return currentSemanticTimes;
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