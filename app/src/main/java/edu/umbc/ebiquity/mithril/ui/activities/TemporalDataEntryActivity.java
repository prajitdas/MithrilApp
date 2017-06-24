package edu.umbc.ebiquity.mithril.ui.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.support.v7.widget.Toolbar;
import android.widget.TimePicker;

import java.sql.Timestamp;
import java.util.Date;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.model.rules.RepeatFrequency;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticTime;
import edu.umbc.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

@TargetApi(Build.VERSION_CODES.N)
public class TemporalDataEntryActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener,
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
        }
        else
            failed();

        String activityBaseTitle = getResources().getString(R.string.title_activity_temporal_data_entry);
        setTitle(activityBaseTitle + semanticTime.getLabel());

        mDaysOfWeekBtn = (Button) findViewById(R.id.daysOfWeekBtn);
        mStartTimeBtn = (Button) findViewById(R.id.startTimeBtn);
        mEndTimeBtn = (Button) findViewById(R.id.endTimeBtn);
        mEnabledBtn = (Button) findViewById(R.id.enabledBtn);
        mDoneBtn = (Button) findViewById(R.id.doneLabelBtn);

        // Create a new instance of TimePickerDialog
        startTimePickerDialog = new TimePickerDialog(
                this,
                this,
                semanticTime.getStart().get(Calendar.HOUR_OF_DAY),
                semanticTime.getStart().get(Calendar.MINUTE),
                DateFormat.is24HourFormat(this));

        // Create a new instance of TimePickerDialog
        endTimePickerDialog = new TimePickerDialog(
                this,
                this,
                semanticTime.getEnd().get(Calendar.HOUR_OF_DAY),
                semanticTime.getEnd().get(Calendar.MINUTE),
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
                isStartClicked = true;
                startTimePickerDialog.show();
            }
        });

        mEndTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartClicked = false;
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
                if(semanticTime.getStart() > -1 && endHour > -1) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(MithrilAC.getPrefKeyTemporalLabel(), label);
                    resultIntent.putExtra(getType(),
                            new SemanticTime(
                                    getDayOfWeek(),
                                    getStart(),
                                    getEnd(),
                                    label,
                                    false));
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else
                    PermissionHelper.toast(v.getContext(), "Did you choose both the starting and ending timings?");
            }
        });
    }

    private int manageNextDayEnding(Calendar start, Calendar end) {
        int period = 0;
        final Calendar dayEnd = Calendar.getInstance();
        dayEnd.set(Calendar.HOUR, 23);
        dayEnd.set(Calendar.MINUTE, 59);
        period = (int) (dayEnd.getTimeInMillis() - start.getTimeInMillis());
        period += 60000; // Adding 1 minute for the last minute of the day
        final Calendar dayStart = Calendar.getInstance();
        dayStart.set(Calendar.HOUR, 0);
        dayStart.set(Calendar.MINUTE, 0);
        period += (int) (end.getTimeInMillis() - dayStart.getTimeInMillis());
        return period;
    }

    private void failed() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    public String getType() {
        return semanticTime.getType();
    }

    public Calendar getStart() {
        return semanticTime.getStart();
    }

    public Calendar getEnd() {
        return semanticTime.getEnd();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        semanticTime.setRepeatFrequency(RepeatFrequency.charSeqToRepeatFrequency((CharSequence) parent.getItemAtPosition(position)));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        PermissionHelper.toast(this, "Choose NEVER_REPEAT if this does not repeat...");
    }

    private boolean isStartClicked;

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(isStartClicked) {
            startHour = hourOfDay;
            startMinute = minute;
        } else {
            endHour = hourOfDay;
            endMinute = minute;
        }
    }
}