package edu.umbc.cs.ebiquity.mithril.util.specialtasks.context;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;

import edu.umbc.cs.ebiquity.mithril.R;

/**
 * Created by prajit on 2/19/17.
 * http://stackoverflow.com/questions/4505845/concise-way-of-writing-new-dialogpreference-classes
 */

public class DayOfWeekPreference extends DialogPreference {
    private String daysOfWeek = null;
    private String[] selectedDaysOfWeek;
    private View view;
    private String key;

    public DayOfWeekPreference(Context context, AttributeSet attributeSet, String prefKey) {
        super(context, attributeSet);

        key = prefKey;
        setDialogLayoutResource(R.layout.day_of_week_dialog_preferences);
    }

    public String[] getDaysOfWeek(String time) {
        return daysOfWeek.split(",");
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        view = v;

        daysOfWeek = getStringFormDaysOfWeek();
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {

        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        if (restoreValue) {
            if (defaultValue == null) {
                daysOfWeek = getPersistedString("M,Tu,W,Th,F,Sa,Su");
            } else {
                daysOfWeek = getPersistedString(defaultValue.toString());
            }
        } else {
            daysOfWeek = defaultValue.toString();
        }
    }

    public String getStringFormDaysOfWeek() {
        StringBuilder temp = new StringBuilder();
        boolean first = true;
        for (String day : selectedDaysOfWeek) {
            if (first)
                temp.append(day);
            else {
                temp.append(",");
                temp.append(day);
            }
        }
        return temp.toString();
    }
}