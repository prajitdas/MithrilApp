package edu.umbc.cs.ebiquity.mithril.util.specialtasks.context;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.HashSet;
import java.util.Set;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.R;

/**
 * Created by prajit on 2/19/17.
 * http://stackoverflow.com/questions/4505845/concise-way-of-writing-new-dialogpreference-classes
 */

public class DayOfWeekPreference extends DialogPreference {
    private Set<String> daysOfWeek = new HashSet<String>();

    private CheckBox mMondayCheckbox;
    private CheckBox mTuesdayCheckbox;
    private CheckBox mWednesdayCheckbox;
    private CheckBox mThursdayCheckbox;
    private CheckBox mFridayCheckbox;
    private CheckBox mSaturdayCheckbox;
    private CheckBox mSundayCheckbox;

    private String empty = "None!";

    private View view;

    public DayOfWeekPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        setPersistent(true);
        setPositiveButtonText(R.string.pref_days_set);
        setNegativeButtonText(R.string.pref_days_cancel);
        setDialogLayoutResource(R.layout.day_of_week_dialog_preferences);
    }

    @Override
    protected void onBindDialogView(View v) {
        view = v;
        initViews(view.getContext());
        super.onBindDialogView(v);
    }

    @Override
    protected void onDialogClosed(boolean result) {
        super.onDialogClosed(result);

        if (result) {
            StringBuilder temp = new StringBuilder();
            if (daysOfWeek.size() > 0) {
                boolean first = true;
                for (String day : daysOfWeek) {
                    if (!day.equals("")) {
                        if (first) {
                            temp.append(day);
                            first = false;
                        } else {
                            temp.append(",");
                            temp.append(day);
                        }
                    }
                }
            } else
                temp.append(empty);
            if (callChangeListener(temp.toString())) {
                persistString(temp.toString());
            }
        }
    }

    private void initViews(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE);
        setDaysOfWeekForPref(sharedPrefs.getString(this.getKey(), empty));

        mMondayCheckbox = (CheckBox) view.findViewById(R.id.monday);
        mTuesdayCheckbox = (CheckBox) view.findViewById(R.id.tuesday);
        mWednesdayCheckbox = (CheckBox) view.findViewById(R.id.wednesday);
        mThursdayCheckbox = (CheckBox) view.findViewById(R.id.thursday);
        mFridayCheckbox = (CheckBox) view.findViewById(R.id.friday);
        mSaturdayCheckbox = (CheckBox) view.findViewById(R.id.saturday);
        mSundayCheckbox = (CheckBox) view.findViewById(R.id.sunday);

        if (daysOfWeek.contains(MithrilApplication.getPrefMonday()))
            mMondayCheckbox.setChecked(true);
        if (daysOfWeek.contains(MithrilApplication.getPrefTuesday()))
            mTuesdayCheckbox.setChecked(true);
        if (daysOfWeek.contains(MithrilApplication.getPrefWednesday()))
            mWednesdayCheckbox.setChecked(true);
        if (daysOfWeek.contains(MithrilApplication.getPrefThursday()))
            mThursdayCheckbox.setChecked(true);
        if (daysOfWeek.contains(MithrilApplication.getPrefFriday()))
            mFridayCheckbox.setChecked(true);
        if (daysOfWeek.contains(MithrilApplication.getPrefSaturday()))
            mSaturdayCheckbox.setChecked(true);
        if (daysOfWeek.contains(MithrilApplication.getPrefSunday()))
            mSundayCheckbox.setChecked(true);

        setOnClickListeners();
    }

    private void setDaysOfWeekForPref(String string) {
        Log.d(MithrilApplication.getDebugTag() + " What did we persist?", string);
        daysOfWeek.clear();
        if (!string.equals(empty)) {
            for (String day : string.split(",")) {
                if (!day.equals(""))
                    daysOfWeek.add(day);
            }
        }
    }

    private void setOnClickListeners() {
        mMondayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!daysOfWeek.contains(MithrilApplication.getPrefMonday()))
                        daysOfWeek.add(MithrilApplication.getPrefMonday());
                } else
                    daysOfWeek.remove(MithrilApplication.getPrefMonday());
            }
        });

        mTuesdayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!daysOfWeek.contains(MithrilApplication.getPrefTuesday()))
                        daysOfWeek.add(MithrilApplication.getPrefTuesday());
                } else
                    daysOfWeek.remove(MithrilApplication.getPrefTuesday());
            }
        });

        mWednesdayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!daysOfWeek.contains(MithrilApplication.getPrefWednesday()))
                        daysOfWeek.add(MithrilApplication.getPrefWednesday());
                } else
                    daysOfWeek.remove(MithrilApplication.getPrefWednesday());
            }
        });

        mThursdayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!daysOfWeek.contains(MithrilApplication.getPrefThursday()))
                        daysOfWeek.add(MithrilApplication.getPrefThursday());
                } else
                    daysOfWeek.remove(MithrilApplication.getPrefThursday());
            }
        });

        mFridayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!daysOfWeek.contains(MithrilApplication.getPrefFriday()))
                        daysOfWeek.add(MithrilApplication.getPrefFriday());
                } else
                    daysOfWeek.remove(MithrilApplication.getPrefFriday());
            }
        });

        mSaturdayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!daysOfWeek.contains(MithrilApplication.getPrefSaturday()))
                        daysOfWeek.add(MithrilApplication.getPrefSaturday());
                } else
                    daysOfWeek.remove(MithrilApplication.getPrefSaturday());
            }
        });

        mSundayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!daysOfWeek.contains(MithrilApplication.getPrefSunday()))
                        daysOfWeek.add(MithrilApplication.getPrefSunday());
                } else
                    daysOfWeek.remove(MithrilApplication.getPrefSunday());
            }
        });
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        if (restoreValue) {
            if (defaultValue == null)
                setDaysOfWeekForPref(getPersistedString("Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday"));
            else
                setDaysOfWeekForPref(getPersistedString(defaultValue.toString()));
        } else
            setDaysOfWeekForPref(defaultValue.toString());
    }
}