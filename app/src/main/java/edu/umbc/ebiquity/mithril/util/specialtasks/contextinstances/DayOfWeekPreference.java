package edu.umbc.ebiquity.mithril.util.specialtasks.contextinstances;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.HashSet;
import java.util.Set;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.R;

/**
 * Created by prajit on 2/19/17.
 * http://stackoverflow.com/questions/4505845/concise-way-of-writing-new-dialogpreference-classes
 */

public class DayOfWeekPreference extends DialogPreference {
    private Set<DayOfWeek> daysOfWeek = new HashSet<DayOfWeek>();
    private String empty = "None!";
    private String delimiter = ", ";

    private CheckBox mMondayCheckbox;
    private CheckBox mTuesdayCheckbox;
    private CheckBox mWednesdayCheckbox;
    private CheckBox mThursdayCheckbox;
    private CheckBox mFridayCheckbox;
    private CheckBox mSaturdayCheckbox;
    private CheckBox mSundayCheckbox;

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
                if (daysOfWeek.contains(DayOfWeek.Monday)) {
                    temp.append(DayOfWeek.Monday);
                    temp.append(delimiter);
                }
                if (daysOfWeek.contains(DayOfWeek.Tuesday)) {
                    temp.append(DayOfWeek.Tuesday);
                    temp.append(delimiter);
                }
                if (daysOfWeek.contains(DayOfWeek.Wednesday)) {
                    temp.append(DayOfWeek.Wednesday);
                    temp.append(delimiter);
                }
                if (daysOfWeek.contains(DayOfWeek.Thursday)) {
                    temp.append(DayOfWeek.Thursday);
                    temp.append(delimiter);
                }
                if (daysOfWeek.contains(DayOfWeek.Friday)) {
                    temp.append(DayOfWeek.Friday);
                    temp.append(delimiter);
                }
                if (daysOfWeek.contains(DayOfWeek.Saturday)) {
                    temp.append(DayOfWeek.Saturday);
                    temp.append(delimiter);
                }
                if (daysOfWeek.contains(DayOfWeek.Sunday)) {
                    temp.append(DayOfWeek.Sunday);
                    temp.append(delimiter);
                }
                temp.deleteCharAt(temp.length() - 1);
                temp.deleteCharAt(temp.length() - 1);
            } else
                temp.append(empty);
            if (callChangeListener(temp.toString())) {
                persistString(temp.toString());
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return (a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        if (restoreValue) {
            if (defaultValue == null)
                setDaysOfWeekForPref(getPersistedString("Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday"));
            else
                setDaysOfWeekForPref(getPersistedString(defaultValue.toString()));
        } else
            setDaysOfWeekForPref(defaultValue.toString());
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

        if (daysOfWeek.contains(DayOfWeek.Monday))
            mMondayCheckbox.setChecked(true);
        if (daysOfWeek.contains(DayOfWeek.Tuesday))
            mTuesdayCheckbox.setChecked(true);
        if (daysOfWeek.contains(DayOfWeek.Wednesday))
            mWednesdayCheckbox.setChecked(true);
        if (daysOfWeek.contains(DayOfWeek.Thursday))
            mThursdayCheckbox.setChecked(true);
        if (daysOfWeek.contains(DayOfWeek.Friday))
            mFridayCheckbox.setChecked(true);
        if (daysOfWeek.contains(DayOfWeek.Saturday))
            mSaturdayCheckbox.setChecked(true);
        if (daysOfWeek.contains(DayOfWeek.Sunday))
            mSundayCheckbox.setChecked(true);

        setOnClickListeners();
    }

    private void setDaysOfWeekForPref(String string) {
        daysOfWeek.clear();
        if (!string.equals(empty)) {
            for (String day : string.split(delimiter)) {
                if (day.equals(MithrilApplication.getPrefMonday()))
                    daysOfWeek.add(DayOfWeek.Monday);
                else if (day.equals(MithrilApplication.getPrefTuesday()))
                    daysOfWeek.add(DayOfWeek.Tuesday);
                else if (day.equals(MithrilApplication.getPrefWednesday()))
                    daysOfWeek.add(DayOfWeek.Wednesday);
                else if (day.equals(MithrilApplication.getPrefThursday()))
                    daysOfWeek.add(DayOfWeek.Thursday);
                else if (day.equals(MithrilApplication.getPrefFriday()))
                    daysOfWeek.add(DayOfWeek.Friday);
                else if (day.equals(MithrilApplication.getPrefSaturday()))
                    daysOfWeek.add(DayOfWeek.Saturday);
                else
                    daysOfWeek.add(DayOfWeek.Sunday);
            }
        }
    }

    private void setOnClickListeners() {
        mMondayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!daysOfWeek.contains(MithrilApplication.getPrefMonday()))
                        daysOfWeek.add(DayOfWeek.Monday);
                } else
                    daysOfWeek.remove(DayOfWeek.Monday);
            }
        });

        mTuesdayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!daysOfWeek.contains(MithrilApplication.getPrefTuesday()))
                        daysOfWeek.add(DayOfWeek.Tuesday);
                } else
                    daysOfWeek.remove(DayOfWeek.Tuesday);
            }
        });

        mWednesdayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!daysOfWeek.contains(MithrilApplication.getPrefWednesday()))
                        daysOfWeek.add(DayOfWeek.Wednesday);
                } else
                    daysOfWeek.remove(DayOfWeek.Wednesday);
            }
        });

        mThursdayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!daysOfWeek.contains(MithrilApplication.getPrefThursday()))
                        daysOfWeek.add(DayOfWeek.Thursday);
                } else
                    daysOfWeek.remove(DayOfWeek.Thursday);
            }
        });

        mFridayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!daysOfWeek.contains(MithrilApplication.getPrefFriday()))
                        daysOfWeek.add(DayOfWeek.Friday);
                } else
                    daysOfWeek.remove(DayOfWeek.Friday);
            }
        });

        mSaturdayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!daysOfWeek.contains(MithrilApplication.getPrefSaturday()))
                        daysOfWeek.add(DayOfWeek.Saturday);
                } else
                    daysOfWeek.remove(DayOfWeek.Saturday);
            }
        });

        mSundayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (!daysOfWeek.contains(MithrilApplication.getPrefSunday()))
                        daysOfWeek.add(DayOfWeek.Sunday);
                } else
                    daysOfWeek.remove(DayOfWeek.Sunday);
            }
        });
    }
}