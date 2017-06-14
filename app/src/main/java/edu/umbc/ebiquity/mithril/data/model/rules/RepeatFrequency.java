package edu.umbc.ebiquity.mithril.data.model.rules;

/**
 * Created by prajit on 6/11/17.
 */

public enum RepeatFrequency {
    DOES_NOT_REPEAT(-1), SECOND(0), MINUTE(1), HOURLY(2), DAILY(3), WEEKLY(4), WEEKDAYS(5), WEEKENDS(6), MONTHLY(7), QUARTERLY(8), YEARLY(9);

    private int repFreqCode;
    private CharSequence repFreqCharSeq;

    RepeatFrequency(int repFreqCode) {
        this.repFreqCode = repFreqCode;
        if(repFreqCode == 0)
            repFreqCharSeq = "Second";
        else if(repFreqCode == 1)
            repFreqCharSeq = "Minute";
        else if(repFreqCode == 2)
            repFreqCharSeq = "Hourly";
        else if(repFreqCode == 3)
            repFreqCharSeq = "Daily";
        else if(repFreqCode == 4)
            repFreqCharSeq = "Weekly";
        else if(repFreqCode == 5)
            repFreqCharSeq = "Weekdays";
        else if(repFreqCode == 6)
            repFreqCharSeq = "Weekends";
        else if(repFreqCode == 7)
            repFreqCharSeq = "Monthly";
        else if(repFreqCode == 8)
            repFreqCharSeq = "Quarterly";
        else if(repFreqCode == 9)
            repFreqCharSeq = "Yearly";
        else
            repFreqCharSeq = "Does not repeat";
    }

    public int getRepFreqCode() {
        return repFreqCode;
    }

    public CharSequence getRepFreqCharSeq() {
        return repFreqCharSeq;
    }

    public static RepeatFrequency charSeqToRepeatFrequency(CharSequence input) {
        if(input.equals("Second"))
            return RepeatFrequency.SECOND;
        else if(input.equals("Minute"))
            return RepeatFrequency.MINUTE;
        else if(input.equals("Hourly"))
            return RepeatFrequency.HOURLY;
        else if(input.equals("Daily"))
            return RepeatFrequency.DAILY;
        else if(input.equals("Weekly"))
            return RepeatFrequency.WEEKLY;
        else if(input.equals("Weekdays"))
            return RepeatFrequency.WEEKDAYS;
        else if(input.equals("Weekends"))
            return RepeatFrequency.WEEKENDS;
        else if(input.equals("Monthly"))
            return RepeatFrequency.MONTHLY;
        else if(input.equals("Quarterly"))
            return RepeatFrequency.QUARTERLY;
        else if(input.equals("Yearly"))
            return RepeatFrequency.YEARLY;
        return  RepeatFrequency.DOES_NOT_REPEAT;
    }
}