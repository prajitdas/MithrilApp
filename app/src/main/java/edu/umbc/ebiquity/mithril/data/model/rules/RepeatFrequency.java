package edu.umbc.ebiquity.mithril.data.model.rules;

import edu.umbc.ebiquity.mithril.MithrilAC;

public enum RepeatFrequency {
    NEVER_REPEATS(-1), ANYDAY(0), MONDAY(1), TUESDAY(2), WEDNESDAY(3), THURSDAY(4), FRIDAY(5), SATURDAY(6), SUNDAY(7), WEEKDAYS(8), WEEKEND(9);

    private int repFreqCode;
    private CharSequence repFreqCharSeq;

    RepeatFrequency(int repFreqCode) {
        this.repFreqCode = repFreqCode;
        if (repFreqCode == 0)
            repFreqCharSeq = MithrilAC.getPrefAnydayTemporalKey();
        else if (repFreqCode == 1)
            repFreqCharSeq = MithrilAC.getPrefMondayTemporalKey();
        else if (repFreqCode == 2)
            repFreqCharSeq = MithrilAC.getPrefTuesdayTemporalKey();
        else if (repFreqCode == 3)
            repFreqCharSeq = MithrilAC.getPrefWednesdayTemporalKey();
        else if (repFreqCode == 4)
            repFreqCharSeq = MithrilAC.getPrefThursdayTemporalKey();
        else if (repFreqCode == 5)
            repFreqCharSeq = MithrilAC.getPrefFridayTemporalKey();
        else if (repFreqCode == 6)
            repFreqCharSeq = MithrilAC.getPrefSaturdayTemporalKey();
        else if (repFreqCode == 7)
            repFreqCharSeq = MithrilAC.getPrefSundayTemporalKey();
        else if (repFreqCode == 8)
            repFreqCharSeq = MithrilAC.getPrefWeekdayTemporalKey();
        else if (repFreqCode == 9)
            repFreqCharSeq = MithrilAC.getPrefWeekendTemporalKey();
        else
            repFreqCharSeq = MithrilAC.getNeverRepeats();
    }

    public static RepeatFrequency charSeqToRepeatFrequency(CharSequence input) {
        if (input.equals(MithrilAC.getPrefAnydayTemporalKey()))
            return RepeatFrequency.ANYDAY;
        else if (input.equals(MithrilAC.getPrefMondayTemporalKey()))
            return RepeatFrequency.MONDAY;
        else if (input.equals(MithrilAC.getPrefTuesdayTemporalKey()))
            return RepeatFrequency.TUESDAY;
        else if (input.equals(MithrilAC.getPrefWednesdayTemporalKey()))
            return RepeatFrequency.WEDNESDAY;
        else if (input.equals(MithrilAC.getPrefThursdayTemporalKey()))
            return RepeatFrequency.THURSDAY;
        else if (input.equals(MithrilAC.getPrefFridayTemporalKey()))
            return RepeatFrequency.FRIDAY;
        else if (input.equals(MithrilAC.getPrefSaturdayTemporalKey()))
            return RepeatFrequency.SATURDAY;
        else if (input.equals(MithrilAC.getPrefSundayTemporalKey()))
            return RepeatFrequency.SUNDAY;
        else if (input.equals(MithrilAC.getPrefWeekdayTemporalKey()))
            return RepeatFrequency.WEEKDAYS;
        else if (input.equals(MithrilAC.getPrefWeekendTemporalKey()))
            return RepeatFrequency.WEEKEND;
        return RepeatFrequency.NEVER_REPEATS;
    }

    public int getRepFreqCode() {
        return repFreqCode;
    }

    public CharSequence getRepFreqCharSeq() {
        return repFreqCharSeq;
    }
}