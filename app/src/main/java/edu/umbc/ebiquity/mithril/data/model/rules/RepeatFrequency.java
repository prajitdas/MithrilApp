package edu.umbc.ebiquity.mithril.data.model.rules;

import edu.umbc.ebiquity.mithril.MithrilAC;

public enum RepeatFrequency {
    NEVER_REPEATS(-1), SECOND(0), MINUTE(1), HOURLY(2), DAILY(3), MONDAY(4), TUESDAY(5), WEDNESDAY(6), THURSDAY(7), FRIDAY(8), SATURDAY(9), SUNDAY(10), WEEKDAYS(11), WEEKEND(12), MONTH(13), QUARTER(14), YEAR(15);

    private int repFreqCode;
    private CharSequence repFreqCharSeq;

    RepeatFrequency(int repFreqCode) {
        this.repFreqCode = repFreqCode;
        if (repFreqCode == 0)
            repFreqCharSeq = MithrilAC.getSecond();
        else if (repFreqCode == 1)
            repFreqCharSeq = MithrilAC.getMinute();
        else if (repFreqCode == 2)
            repFreqCharSeq = MithrilAC.getHour();
        else if (repFreqCode == 3)
            repFreqCharSeq = MithrilAC.getDay();
        else if (repFreqCode == 4)
            repFreqCharSeq = MithrilAC.getMonday();
        else if (repFreqCode == 5)
            repFreqCharSeq = MithrilAC.getTuesday();
        else if (repFreqCode == 6)
            repFreqCharSeq = MithrilAC.getWednesday();
        else if (repFreqCode == 7)
            repFreqCharSeq = MithrilAC.getThursday();
        else if (repFreqCode == 8)
            repFreqCharSeq = MithrilAC.getFriday();
        else if (repFreqCode == 9)
            repFreqCharSeq = MithrilAC.getSaturday();
        else if (repFreqCode == 10)
            repFreqCharSeq = MithrilAC.getSunday();
        else if (repFreqCode == 11)
            repFreqCharSeq = MithrilAC.getWeekday();
        else if (repFreqCode == 12)
            repFreqCharSeq = MithrilAC.getWeekend();
        else if (repFreqCode == 13)
            repFreqCharSeq = MithrilAC.getMonth();
        else if (repFreqCode == 14)
            repFreqCharSeq = MithrilAC.getQuarter();
        else if (repFreqCode == 15)
            repFreqCharSeq = MithrilAC.getYear();
        else
            repFreqCharSeq = MithrilAC.getNeverRepeats();
    }

    public static RepeatFrequency charSeqToRepeatFrequency(CharSequence input) {
        if (input.equals(MithrilAC.getSecond()))
            return RepeatFrequency.SECOND;
        else if (input.equals(MithrilAC.getMinute()))
            return RepeatFrequency.MINUTE;
        else if (input.equals(MithrilAC.getHour()))
            return RepeatFrequency.HOURLY;
        else if (input.equals(MithrilAC.getDay()))
            return RepeatFrequency.DAILY;
        else if (input.equals(MithrilAC.getMonday()))
            return RepeatFrequency.MONDAY;
        else if (input.equals(MithrilAC.getTuesday()))
            return RepeatFrequency.TUESDAY;
        else if (input.equals(MithrilAC.getWednesday()))
            return RepeatFrequency.WEDNESDAY;
        else if (input.equals(MithrilAC.getThursday()))
            return RepeatFrequency.THURSDAY;
        else if (input.equals(MithrilAC.getFriday()))
            return RepeatFrequency.FRIDAY;
        else if (input.equals(MithrilAC.getSaturday()))
            return RepeatFrequency.SATURDAY;
        else if (input.equals(MithrilAC.getSunday()))
            return RepeatFrequency.SUNDAY;
        else if (input.equals(MithrilAC.getWeekday()))
            return RepeatFrequency.WEEKDAYS;
        else if (input.equals(MithrilAC.getWeekend()))
            return RepeatFrequency.WEEKEND;
        else if (input.equals(MithrilAC.getMonth()))
            return RepeatFrequency.MONTH;
        else if (input.equals(MithrilAC.getQuarter()))
            return RepeatFrequency.QUARTER;
        else if (input.equals(MithrilAC.getYear()))
            return RepeatFrequency.YEAR;
        return RepeatFrequency.NEVER_REPEATS;
    }

    public int getRepFreqCode() {
        return repFreqCode;
    }

    public CharSequence getRepFreqCharSeq() {
        return repFreqCharSeq;
    }
}