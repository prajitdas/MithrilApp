package edu.umbc.ebiquity.mithril.data.model.rules;

import android.util.Log;

import edu.umbc.ebiquity.mithril.MithrilApplication;

/**
 * Created by prajit on 6/11/17.
 */

public enum RepeatFrequency {
    SECOND(0), MINUTE(1), HOURLY(2), DAILY(3), WEEKLY(4), WEEKDAYS(5), WEEKENDS(6), MONTHLY(7), YEARLY(8);

    private int repeatCode;
    private String repeatString;

    RepeatFrequency(int repeatCode) {
        this.repeatCode = repeatCode;
        if(repeatCode == 0)
            repeatString = "Second";
        else if(repeatCode == 1)
            repeatString = "Minute";
        else if(repeatCode == 2)
            repeatString = "Hourly";
        else if(repeatCode == 3)
            repeatString = "Daily";
        else if(repeatCode == 4)
            repeatString = "Weekly";
        else if(repeatCode == 5)
            repeatString = "Weekdays";
        else if(repeatCode == 6)
            repeatString = "Weekends";
        else if(repeatCode == 7)
            repeatString = "Monthly";
        else if(repeatCode == 8)
            repeatString = "Yearly";
        else
            Log.e(MithrilApplication.getDebugTag(), "Illegal frequency");
    }

    public int getRepeatCode() {
        return repeatCode;
    }

    public String getRepeatString() {
        return repeatString;
    }
}