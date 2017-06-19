package edu.umbc.ebiquity.mithril.data.model.rules;

import android.util.Log;

import edu.umbc.ebiquity.mithril.MithrilAC;

public enum Action {
    ALLOW(1), ALLOW_WITH_CAVEAT(2), DENY(0);

    private int value;
    private String actionString;

    Action(int actionInt) {
        value = actionInt;
        if (actionInt == 0)
            actionString = "Deny";
        else if (actionInt == 1)
            actionString = "Allow";
        else if (actionInt == 2)
            actionString = "Allow_with_caveat";
        else
            Log.e(MithrilAC.getDebugTag(), "Illegal action");
    }

    public int getValue() {
        return value;
    }

    public String getActionString() {
        return actionString;
    }
}