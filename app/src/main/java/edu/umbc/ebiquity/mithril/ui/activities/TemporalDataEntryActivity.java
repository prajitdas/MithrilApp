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
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {
    private Button mTemporalFirstBtn;
    private Button mTemporalStartTimeBtn;
    private Button mTemporalEndTimeBtn;
    private Button mDoneBtn;
    private Spinner mSpinnerRepeatFrequency;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog startTimePickerDialog;
    private TimePickerDialog endTimePickerDialog;

    private int mYear = -1;
    private int mMonth = -1;
    private int mDay = -1;

    private int startHour = -1;
    private int startMinute = -1;
    private int endHour = -1;
    private int endMinute = -1;

    String label;
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

        isStartClicked = false;

        mTemporalFirstBtn = (Button) findViewById(R.id.temporalFirstBtn);
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(this, TemporalDataEntryActivity.this, mYear, mMonth, mDay);

        // Use the current time as the default values for the picker
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog
        startTimePickerDialog = new TimePickerDialog(
                this,
                this,
                hour,
                minute,
                DateFormat.is24HourFormat(this));

        // Create a new instance of TimePickerDialog
        endTimePickerDialog = new TimePickerDialog(
                this,
                this,
                hour,
                minute,
                DateFormat.is24HourFormat(this));

        mTemporalStartTimeBtn = (Button) findViewById(R.id.temporalStartTimeBtn);
        mTemporalEndTimeBtn = (Button) findViewById(R.id.temporalEndTimeBtn);

        mDoneBtn = (Button) findViewById(R.id.doneLabelBtn);

        mSpinnerRepeatFrequency = (Spinner) findViewById(R.id.spinnerRepeatFrequency);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.freq_array,
                android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSpinnerRepeatFrequency.setAdapter(adapter);

        setOnclickListeners();
        mSpinnerRepeatFrequency.setOnItemSelectedListener(this);
    }

    private void setOnclickListeners() {
        mTemporalFirstBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        mTemporalStartTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartClicked = true;
                startTimePickerDialog.show();
            }
        });

        mTemporalEndTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStartClicked = false;
                endTimePickerDialog.show();
            }
        });

        mDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startHour > -1 && endHour > -1) {
                    final Calendar start = Calendar.getInstance();
                    start.set(Calendar.HOUR, startHour);
                    start.set(Calendar.MINUTE, startMinute);

                    final Calendar end = Calendar.getInstance();
                    end.set(Calendar.HOUR, endHour);
                    end.set(Calendar.MINUTE, endMinute);

                    int period;
                    if ((end.getTimeInMillis() - start.getTimeInMillis()) < 0)
                        period = manageNextDayEnding(start, end);
                    else
                        period = (int) (end.getTimeInMillis() - start.getTimeInMillis());

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(MithrilAC.getPrefKeyTemporalLabel(), label);
                    resultIntent.putExtra(getType(),
                            new SemanticTime(
                                    getRepeatFrequency(),
                                    getFirst(),
                                    period,
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

    public RepeatFrequency getRepeatFrequency() {
        return semanticTime.getRepeatFrequency();
    }

    public Timestamp getFirst() {
        return semanticTime.getFirst();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        semanticTime.setRepeatFrequency(RepeatFrequency.charSeqToRepeatFrequency((CharSequence) parent.getItemAtPosition(position)));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        PermissionHelper.toast(this, "Choose NEVER_REPEAT if this does not repeat...");
    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        mYear = year;
        mMonth = monthOfYear;
        mDay = dayOfMonth;
        semanticTime.setFirst(new Timestamp(cal.getTimeInMillis()));
        mTemporalFirstBtn.setText(getResources().getString(R.string.first_occurrence) + semanticTime.toString());
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