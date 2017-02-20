package edu.umbc.cs.ebiquity.mithril.util.specialtasks.context;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.R;

/**
 * Created by prajit on 2/19/17.
 * http://stackoverflow.com/questions/4505845/concise-way-of-writing-new-dialogpreference-classes
 */

public class DayOfWeekPreference extends DialogPreference {
    private List<String> daysOfWeek;

    private Context context;

    private CheckBox mMondayCheckbox;
    private CheckBox mTuesdayCheckbox;
    private CheckBox mWednesdayCheckbox;
    private CheckBox mThursdayCheckbox;
    private CheckBox mFridayCheckbox;
    private CheckBox mSaturdayCheckbox;
    private CheckBox mSundayCheckbox;

    private Button mSetDaysButton;
    private Button mCancelDaysButton;

    private View view;
    private String key;
    private SharedPreferences sharedPrefs;

    public DayOfWeekPreference(Context context, AttributeSet attributeSet, String prefKey) {
        super(context, attributeSet);

        key = prefKey;
        setDialogLayoutResource(R.layout.day_of_week_dialog_preferences);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        builder.setTitle(R.string.pref_choose_days);
        builder.setPositiveButton(R.string.pref_days_set, null);
        builder.setNegativeButton(R.string.pref_days_cancel, null);
        super.onPrepareDialogBuilder(builder);
    }

    @Override
    protected void onBindDialogView(View v) {
        view = v;
        context = view.getContext();
        initViews();
        super.onBindDialogView(v);
    }

    private void initViews() {
        sharedPrefs = context.getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE);

        daysOfWeek = new ArrayList<String>();

        getDaysOfWeekForPref(sharedPrefs.getString(key, ""));

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

        mSetDaysButton = (Button) view.findViewById(R.id.daysSetBtn);
        mCancelDaysButton = (Button) view.findViewById(R.id.daysCancelBtn);

        setOnClickListeners();
    }

    private void getDaysOfWeekForPref(String string) {
        for (String day : string.split(",")) {
            daysOfWeek.add(day);
        }
    }

    private void setOnClickListeners() {
        mSetDaysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder temp = new StringBuilder();
                boolean first = true;
                for (String day : daysOfWeek) {
                    if (first)
                        temp.append(day);
                    else {
                        temp.append(",");
                        temp.append(day);
                    }
                }
                SharedPreferences.Editor editor = context.getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE).edit();
                editor.putString(key, temp.toString());
                editor.commit();
            }
        });

        mCancelDaysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        mMondayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                daysOfWeek.add(MithrilApplication.getPrefMonday());
            }
        });

        mTuesdayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                daysOfWeek.add(MithrilApplication.getPrefTuesday());
            }
        });

        mWednesdayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                daysOfWeek.add(MithrilApplication.getPrefWednesday());
            }
        });

        mThursdayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                daysOfWeek.add(MithrilApplication.getPrefThursday());
            }
        });

        mFridayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                daysOfWeek.add(MithrilApplication.getPrefFriday());
            }
        });

        mSaturdayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                daysOfWeek.add(MithrilApplication.getPrefSaturday());
            }
        });

        mSundayCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                daysOfWeek.add(MithrilApplication.getPrefSunday());
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
                getDaysOfWeekForPref(getPersistedString("Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday"));
            else
                getDaysOfWeekForPref(getPersistedString(defaultValue.toString()));
        } else
            getDaysOfWeekForPref(defaultValue.toString());
    }
}