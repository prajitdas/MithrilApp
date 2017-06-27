package edu.umbc.ebiquity.mithril.util.specialtasks.detect.context;

import android.icu.util.Calendar;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticTime;

public class DetectTemporalContext {
    public static List<SemanticTime> whatTimeIsItNow(Map<String, SemanticTime> semanticTimes) {
        List<SemanticTime> currentSemanticTimes = new ArrayList<>();
        Calendar currCalendar = Calendar.getInstance();
        int dow = currCalendar.get(Calendar.DAY_OF_WEEK);
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

        if (isWithinTimePeriod(semanticTimes.get(MithrilAC.getPrefBreakfastTemporalKey()), currCalendar)) {
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefBreakfastTemporalKey()));
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefPersonalTemporalKey()));
        } else if (isWithinTimePeriod(semanticTimes.get(MithrilAC.getPrefLunchTemporalKey()), currCalendar)) {
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefLunchTemporalKey()));
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefPersonalTemporalKey()));
        } else if (isWithinTimePeriod(semanticTimes.get(MithrilAC.getPrefDinnerTemporalKey()), currCalendar)) {
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefDinnerTemporalKey()));
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefPersonalTemporalKey()));
        } else if (isWithinTimePeriod(semanticTimes.get(MithrilAC.getPrefDndTemporalKey()), currCalendar)) {
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefDndTemporalKey()));
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefPersonalTemporalKey()));
        } else if (isWithinTimePeriod(semanticTimes.get(MithrilAC.getPrefFamilyTemporalKey()), currCalendar)) {
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefFamilyTemporalKey()));
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefPersonalTemporalKey()));
        } else if (isWithinTimePeriod(semanticTimes.get(MithrilAC.getPrefAloneTemporalKey()), currCalendar)) {
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefAloneTemporalKey()));
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefPersonalTemporalKey()));
        } else if (isWithinTimePeriod(semanticTimes.get(MithrilAC.getPrefWorkMorningTemporalKey()), currCalendar)) {
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefWorkMorningTemporalKey()));
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefProfessionalTemporalKey()));
        } else if (isWithinTimePeriod(semanticTimes.get(MithrilAC.getPrefWorkAfternoonTemporalKey()), currCalendar)) {
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefWorkAfternoonTemporalKey()));
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefProfessionalTemporalKey()));
        }
        currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefAnydaytimeTemporalKey()));
        return currentSemanticTimes;
    }

    private static boolean isWithinTimePeriod(SemanticTime semanticTime, Calendar currCalendar) {
        Calendar knownCalStart = Calendar.getInstance();
        knownCalStart.set(Calendar.HOUR_OF_DAY, semanticTime.getStartHour());
        knownCalStart.set(Calendar.MINUTE, semanticTime.getStartMinute());
        Calendar knownCalEnd = Calendar.getInstance();
        knownCalEnd.set(Calendar.HOUR_OF_DAY, semanticTime.getEndHour());
        knownCalEnd.set(Calendar.MINUTE, semanticTime.getEndMinute());
        //We have a next day end time situation
        if(knownCalStart.compareTo(knownCalEnd) == 0)
            return false;
        if(knownCalStart.compareTo(knownCalEnd) > 0) {
            int diffHour = 24 - knownCalStart.get(Calendar.HOUR_OF_DAY);
            knownCalStart.set(Calendar.HOUR_OF_DAY, knownCalStart.get(Calendar.HOUR_OF_DAY)+diffHour);
            knownCalEnd.set(Calendar.HOUR_OF_DAY, knownCalEnd.get(Calendar.HOUR_OF_DAY)+diffHour);
            currCalendar.set(Calendar.HOUR_OF_DAY, currCalendar.get(Calendar.HOUR_OF_DAY)+diffHour);
            if(currCalendar.compareTo(knownCalStart) > 0 && currCalendar.compareTo(knownCalEnd) < 0)
                return true;
        }
        if(currCalendar.compareTo(knownCalStart) > 0 && currCalendar.compareTo(knownCalEnd) < 0)
            return true;
        return false;
    }
}