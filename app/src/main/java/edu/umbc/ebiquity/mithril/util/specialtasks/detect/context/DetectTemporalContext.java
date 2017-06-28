package edu.umbc.ebiquity.mithril.util.specialtasks.detect.context;

import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.text.format.Time;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticTime;

public class DetectTemporalContext {
    public static List<SemanticTime> whatTimeIsItNow(Map<String, SemanticTime> semanticTimes) {
        List<SemanticTime> currentSemanticTimes = new ArrayList<>();
        Calendar currCalendar = Calendar.getInstance();
        Log.d(MithrilAC.getDebugTag(), "current time is: "+currCalendar.get(Calendar.HOUR_OF_DAY) + ":" + currCalendar.get(Calendar.MINUTE));
        int dow = currCalendar.get(Calendar.DAY_OF_WEEK);
        switch (dow) {
            case Calendar.SUNDAY:
                currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefSundayTemporalKey()));
                currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefWeekendTemporalKey()));
                Log.d(MithrilAC.getDebugTag(), "times 1size"+currentSemanticTimes.size());
                break;
            case Calendar.MONDAY:
                currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefMondayTemporalKey()));
                currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefWeekdayTemporalKey()));
                Log.d(MithrilAC.getDebugTag(), "times 2size"+currentSemanticTimes.size());
                break;
            case Calendar.TUESDAY:
                currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefTuesdayTemporalKey()));
                currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefWeekdayTemporalKey()));
                Log.d(MithrilAC.getDebugTag(), "times 3size"+currentSemanticTimes.size());
                break;
            case Calendar.WEDNESDAY:
                currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefWednesdayTemporalKey()));
                currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefWeekdayTemporalKey()));
                Log.d(MithrilAC.getDebugTag(), "times 4size"+currentSemanticTimes.size());
                break;
            case Calendar.THURSDAY:
                currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefThursdayTemporalKey()));
                currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefWeekdayTemporalKey()));
                Log.d(MithrilAC.getDebugTag(), "times 5size"+currentSemanticTimes.size());
                break;
            case Calendar.FRIDAY:
                currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefFridayTemporalKey()));
                currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefWeekdayTemporalKey()));
                Log.d(MithrilAC.getDebugTag(), "times 6size"+currentSemanticTimes.size());
                break;
            case Calendar.SATURDAY:
                currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefSaturdayTemporalKey()));
                currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefWeekendTemporalKey()));
                Log.d(MithrilAC.getDebugTag(), "times 7size"+currentSemanticTimes.size());
                break;
        }

        if (isWithinTimePeriod(semanticTimes.get(MithrilAC.getPrefBreakfastTemporalKey()))) {
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefBreakfastTemporalKey()));
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefPersonalTemporalKey()));
            Log.d(MithrilAC.getDebugTag(), "times 8size"+currentSemanticTimes.size());
        }
        if (isWithinTimePeriod(semanticTimes.get(MithrilAC.getPrefLunchTemporalKey()))) {
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefLunchTemporalKey()));
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefPersonalTemporalKey()));
            Log.d(MithrilAC.getDebugTag(), "times 9size"+currentSemanticTimes.size());
        }
        if (isWithinTimePeriod(semanticTimes.get(MithrilAC.getPrefDinnerTemporalKey()))) {
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefDinnerTemporalKey()));
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefPersonalTemporalKey()));
            Log.d(MithrilAC.getDebugTag(), "times 10size"+currentSemanticTimes.size());
        }
        if (isWithinTimePeriod(semanticTimes.get(MithrilAC.getPrefDndTemporalKey()))) {
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefDndTemporalKey()));
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefPersonalTemporalKey()));
            Log.d(MithrilAC.getDebugTag(), "times 11size"+currentSemanticTimes.size());
        }
        if (isWithinTimePeriod(semanticTimes.get(MithrilAC.getPrefFamilyTemporalKey()))) {
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefFamilyTemporalKey()));
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefPersonalTemporalKey()));
            Log.d(MithrilAC.getDebugTag(), "times 12size"+currentSemanticTimes.size());
        }
        if (isWithinTimePeriod(semanticTimes.get(MithrilAC.getPrefAloneTemporalKey()))) {
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefAloneTemporalKey()));
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefPersonalTemporalKey()));
            Log.d(MithrilAC.getDebugTag(), "times 13size"+currentSemanticTimes.size());
        }
        if (isWithinTimePeriod(semanticTimes.get(MithrilAC.getPrefWorkMorningTemporalKey()))) {
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefWorkMorningTemporalKey()));
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefProfessionalTemporalKey()));
            Log.d(MithrilAC.getDebugTag(), "times 14size"+currCalendar.get(Calendar.HOUR_OF_DAY));
            Log.d(MithrilAC.getDebugTag(), "times 14size"+currCalendar.get(Calendar.MINUTE));
            Log.d(MithrilAC.getDebugTag(), "times 14size"+semanticTimes.get(MithrilAC.getPrefWorkMorningTemporalKey()).getStartHour());
            Log.d(MithrilAC.getDebugTag(), "times 14size"+semanticTimes.get(MithrilAC.getPrefWorkMorningTemporalKey()).getEndHour());
        }
        if (isWithinTimePeriod(semanticTimes.get(MithrilAC.getPrefWorkAfternoonTemporalKey()))) {
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefWorkAfternoonTemporalKey()));
            currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefProfessionalTemporalKey()));
            Log.d(MithrilAC.getDebugTag(), "times 15size"+currCalendar.get(Calendar.HOUR_OF_DAY));
            Log.d(MithrilAC.getDebugTag(), "times 15size"+currCalendar.get(Calendar.MINUTE));
            Log.d(MithrilAC.getDebugTag(), "times 15size"+semanticTimes.get(MithrilAC.getPrefWorkMorningTemporalKey()).getStartHour());
            Log.d(MithrilAC.getDebugTag(), "times 15size"+semanticTimes.get(MithrilAC.getPrefWorkMorningTemporalKey()).getEndHour());
        }
        currentSemanticTimes.add(semanticTimes.get(MithrilAC.getPrefAnydaytimeTemporalKey()));
        Log.d(MithrilAC.getDebugTag(), "times 16size"+currentSemanticTimes.size());
        return currentSemanticTimes;
    }

    private static boolean isWithinTimePeriod(SemanticTime semanticTime) {
        Calendar currCalendar = Calendar.getInstance();
        Calendar knownCalStart = Calendar.getInstance();
        knownCalStart.set(Calendar.HOUR_OF_DAY, semanticTime.getStartHour());
        knownCalStart.set(Calendar.MINUTE, semanticTime.getStartMinute());
        Calendar knownCalEnd = Calendar.getInstance();
        knownCalEnd.set(Calendar.HOUR_OF_DAY, semanticTime.getEndHour());
        knownCalEnd.set(Calendar.MINUTE, semanticTime.getEndMinute());
        //We have a next day end time situation
        if (knownCalStart.compareTo(knownCalEnd) == 0) {
            return false;
        }
        if (knownCalStart.compareTo(knownCalEnd) == 1) {
            int diffHour = 24 - knownCalStart.get(Calendar.HOUR_OF_DAY);
            knownCalStart.set(Calendar.HOUR_OF_DAY, 0);
            knownCalEnd.set(Calendar.HOUR_OF_DAY, knownCalEnd.get(Calendar.HOUR_OF_DAY) + diffHour);
            currCalendar.set(Calendar.HOUR_OF_DAY, currCalendar.get(Calendar.HOUR_OF_DAY) + diffHour);
            if (currCalendar.compareTo(knownCalStart) == 1 && currCalendar.compareTo(knownCalEnd) == -1)
                return true;
            return false;
        }
        if (currCalendar.compareTo(knownCalStart) == 1 && currCalendar.compareTo(knownCalEnd) == -1)
            return true;
        return false;
    }
}