package edu.umbc.ebiquity.mithril.ui.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.List;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticTime;
import edu.umbc.ebiquity.mithril.util.specialtasks.contextinstances.DayOfWeek;

@TargetApi(Build.VERSION_CODES.N)
public class TemporalDataEntryActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener {
    private Button mDaysOfWeekBtn;
    private Button mStartTimeBtn;
    private Button mEndTimeBtn;
    private Button mEnabledBtn;
    private Button mDoneBtn;

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
                        R.string.first_occurrence) +
                        semanticTime.getDayOfWeekString());

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

        mEnabledBtn = (Button) findViewById(R.id.enabledBtn);
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

        mEnabledBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                                label,
                                false,
                                0
                        )
                );
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
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

    private List<DayOfWeek> getDayOfWeek() {
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