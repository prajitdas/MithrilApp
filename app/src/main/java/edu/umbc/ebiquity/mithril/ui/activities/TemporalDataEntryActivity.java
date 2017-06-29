package edu.umbc.ebiquity.mithril.ui.activities;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.util.List;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticTime;
import edu.umbc.ebiquity.mithril.util.specialtasks.contextinstances.DayOfWeek;

public class TemporalDataEntryActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener {
    private Button mDaysOfWeekBtn;
    private Button mStartTimeBtn;
    private Button mEndTimeBtn;
    private ToggleButton mEnabledToggleBtn;
    private Button mDoneBtn;
    private ToggleButton mAllDayToggleBtn;

    private TimePickerDialog startTimePickerDialog;
    private TimePickerDialog endTimePickerDialog;

    private String label;
    private SemanticTime semanticTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temporal_data_entry);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            label = extras.getString(MithrilAC.getPrefKeyTemporalLabel());
            semanticTime = extras.getParcelable(MithrilAC.getPrefKeyContextTypeTemporal());
        } else
            failed();

        String activityBaseTitle = getResources().getString(R.string.title_activity_temporal_data_entry);
        setTitle(activityBaseTitle + semanticTime.getLabel());

        mDaysOfWeekBtn = (Button) findViewById(R.id.daysOfWeekBtn);
        mDaysOfWeekBtn.setText(
                getResources().getString(
                        R.string.days_of_week) +
                        semanticTime.getDayOfWeekString());

        mAllDayToggleBtn = (ToggleButton) findViewById(R.id.allDayBtn);
        mAllDayToggleBtn.setChecked(semanticTime.isAllDay());

        mStartTimeBtn = (Button) findViewById(R.id.startTimeBtn);
        mStartTimeBtn.setText(
                getResources().getString(
                        R.string.starting_time) +
                        semanticTime.getStartHour() +
                        ":" +
                        semanticTime.getStartMinute()
        );

        mEndTimeBtn = (Button) findViewById(R.id.endTimeBtn);
        mEndTimeBtn.setText(
                getResources().getString(
                        R.string.ending_time) +
                        semanticTime.getEndHour() +
                        ":" +
                        semanticTime.getEndMinute()
        );

        mEnabledToggleBtn = (ToggleButton) findViewById(R.id.enabledBtn);
        mEnabledToggleBtn.setChecked(semanticTime.isEnabled());

        mDoneBtn = (Button) findViewById(R.id.doneLabelBtn);

        // Create a new instance of TimePickerDialog
        startTimePickerDialog = new TimePickerDialog(
                this,
                this,
                semanticTime.getStartHour(),
                semanticTime.getStartMinute(),
                DateFormat.is24HourFormat(this));

        // Create a new instance of TimePickerDialog
        endTimePickerDialog = new TimePickerDialog(
                this,
                this,
                semanticTime.getEndHour(),
                semanticTime.getEndMinute(),
                DateFormat.is24HourFormat(this));

        setOnclickListeners();
    }

    private void setOnclickListeners() {
        mDaysOfWeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mAllDayToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    buttonView.setChecked(false);
                else
                    buttonView.setChecked(true);
                semanticTime.setAllDay(buttonView.isChecked());
            }
        });

        mStartTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTimePickerDialog.show();
            }
        });

        mEndTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTimePickerDialog.show();
            }
        });

        mEnabledToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    buttonView.setChecked(false);
                else
                    buttonView.setChecked(true);
                semanticTime.setEnabled(buttonView.isChecked());
            }
        });

        mDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(MithrilAC.getPrefKeyTemporalLabel(), label);
                resultIntent.putExtra(getType(),
                        new SemanticTime(
                                getDayOfWeek(),
                                getStartHour(),
                                getStartMinute(),
                                getEndHour(),
                                getEndMinute(),
                                getLabel(),
                                isEnabled(),
                                getLevel(),
                                isAllDay()
                        )
                );
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    private String getLabel() {
        return semanticTime.getLabel();
    }

    private boolean isEnabled() {
        return semanticTime.isEnabled();
    }

    private int getLevel() {
        return semanticTime.getLevel();
    }

    private boolean isAllDay() {
        return semanticTime.isAllDay();
    }

    private int getStartHour() {
        return semanticTime.getStartHour();
    }

    private int getStartMinute() {
        return semanticTime.getStartMinute();
    }

    private int getEndHour() {
        return semanticTime.getEndHour();
    }

    private int getEndMinute() {
        return semanticTime.getEndMinute();
    }

    private List<Integer> getDayOfWeek() {
        return semanticTime.getDayOfWeek();
    }

    private void failed() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    public String getType() {
        return semanticTime.getType();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        if (view.equals(startTimePickerDialog)) {
            semanticTime.setStartHour(hourOfDay);
            semanticTime.setStartMinute(minute);
        } else {
            semanticTime.setEndHour(hourOfDay);
            semanticTime.setEndMinute(minute);
        }
    }
}