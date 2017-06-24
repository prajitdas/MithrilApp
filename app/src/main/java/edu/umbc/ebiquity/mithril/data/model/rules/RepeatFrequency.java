package edu.umbc.ebiquity.mithril.data.model.rules;

import edu.umbc.ebiquity.mithril.MithrilAC;

public enum RepeatFrequency {
    NEVER_REPEATS(-1), SECOND(0), MINUTE(1), HOURLY(2), DAILY(3), MONDAY(4), TUESDAY(5), WEDNESDAY(6), THURSDAY(7), FRIDAY(8), SATURDAY(9), SUNDAY(10), WEEKDAYS(11), WEEKEND(12), MONTH(13), QUARTER(14), YEAR(15);

    private int repFreqCode;
    private CharSequence repFreqCharSeq;

    RepeatFrequency(int repFreqCode) {
        this.repFreqCode = repFreqCode;
        if (repFreqCode == 0)
            repFreqCharSeq = MithrilAC.getEverySecond();
        else if (repFreqCode == 1)
            repFreqCharSeq = MithrilAC.getEveryMinute();
        else if (repFreqCode == 2)
            repFreqCharSeq = MithrilAC.getEveryHour();
        else if (repFreqCode == 3)
            repFreqCharSeq = MithrilAC.getEveryDay();
        else if (repFreqCode == 4)
            repFreqCharSeq = MithrilAC.getEveryMonday();
        else if (repFreqCode == 5)
            repFreqCharSeq = MithrilAC.getEveryTuesday();
        else if (repFreqCode == 6)
            repFreqCharSeq = MithrilAC.getEveryWednesday();
        else if (repFreqCode == 7)
            repFreqCharSeq = MithrilAC.getEveryThursday();
        else if (repFreqCode == 8)
            repFreqCharSeq = MithrilAC.getEveryFriday();
        else if (repFreqCode == 9)
            repFreqCharSeq = MithrilAC.getEverySaturday();
        else if (repFreqCode == 10)
            repFreqCharSeq = MithrilAC.getEverySunday();
        else if (repFreqCode == 11)
            repFreqCharSeq = MithrilAC.getEveryWeekday();
        else if (repFreqCode == 12)
            repFreqCharSeq = MithrilAC.getEveryWeekend();
        else if (repFreqCode == 13)
            repFreqCharSeq = MithrilAC.getEveryMonth();
        else if (repFreqCode == 14)
            repFreqCharSeq = MithrilAC.getEveryQuarter();
        else if (repFreqCode == 15)
            repFreqCharSeq = MithrilAC.getEveryYear();
        else
            repFreqCharSeq = MithrilAC.getNeverRepeats();
    }

    public static RepeatFrequency charSeqToRepeatFrequency(CharSequence input) {
        if (input.equals(MithrilAC.getEverySecond()))
            return RepeatFrequency.SECOND;
        else if (input.equals(MithrilAC.getEveryMinute()))
            return RepeatFrequency.MINUTE;
        else if (input.equals(MithrilAC.getEveryHour()))
            return RepeatFrequency.HOURLY;
        else if (input.equals(MithrilAC.getEveryDay()))
            return RepeatFrequency.DAILY;
        else if (input.equals(MithrilAC.getEveryMonday()))
            return RepeatFrequency.MONDAY;
        else if (input.equals(MithrilAC.getEveryTuesday()))
            return RepeatFrequency.TUESDAY;
        else if (input.equals(MithrilAC.getEveryWednesday()))
            return RepeatFrequency.WEDNESDAY;
        else if (input.equals(MithrilAC.getEveryThursday()))
            return RepeatFrequency.THURSDAY;
        else if (input.equals(MithrilAC.getEveryFriday()))
            return RepeatFrequency.FRIDAY;
        else if (input.equals(MithrilAC.getEverySaturday()))
            return RepeatFrequency.SATURDAY;
        else if (input.equals(MithrilAC.getEverySunday()))
            return RepeatFrequency.SUNDAY;
        else if (input.equals(MithrilAC.getEveryWeekday()))
            return RepeatFrequency.WEEKDAYS;
        else if (input.equals(MithrilAC.getEveryWeekend()))
            return RepeatFrequency.WEEKEND;
        else if (input.equals(MithrilAC.getEveryMonth()))
            return RepeatFrequency.MONTH;
        else if (input.equals(MithrilAC.getEveryQuarter()))
            return RepeatFrequency.QUARTER;
        else if (input.equals(MithrilAC.getEveryYear()))
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