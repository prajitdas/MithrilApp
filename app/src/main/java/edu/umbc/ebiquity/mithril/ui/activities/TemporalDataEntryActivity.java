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

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.model.rules.RepeatFrequency;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticTime;
import edu.umbc.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

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

    private final String type = MithrilAC.getPrefKeyContextTypeTemporal();
    private RepeatFrequency repeatFrequency;
    private Timestamp first;
    private int period;
    private String inferredTime;

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temporal_data_entry);

        Bundle extras = getIntent().getExtras();
        if (extras != null)
            inferredTime = extras.getString("label");
        else
            failed();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_view_temporal_data_entry);
        toolbar.setTitle(toolbar.getTitle() + inferredTime);
        setSupportActionBar(toolbar);

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
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(getType() + inferredTime,
                            new SemanticTime(
                                    getRepeatFrequency(),
                                    getFirst(),
                                    getPeriod(),
                                    inferredTime,
                                    true));
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                } else
                    PermissionHelper.toast(v.getContext(), "Did you choose both the starting and ending timings?");
            }
        });
    }

    private void failed() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    private CharSequence getTimeText() {
        return DateUtils.getRelativeTimeSpanString(getFirst().getTime(),
                System.currentTimeMillis(),
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE);
    }

    public String getType() {
        return type;
    }

    public RepeatFrequency getRepeatFrequency() {
        return repeatFrequency;
    }

    public void setRepeatFrequency(RepeatFrequency repeatFrequency) {
        this.repeatFrequency = repeatFrequency;
    }

    public Timestamp getFirst() {
        return first;
    }

    public void setFirst(Timestamp first) {
        this.first = first;
    }

    public int getPeriod() {
        return period;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        setRepeatFrequency(RepeatFrequency.charSeqToRepeatFrequency((CharSequence) parent.getItemAtPosition(position)));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        PermissionHelper.toast(this, "Choose NEVER_REPEAT if this does not repeat...");
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar cal = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        mYear = year;
        mMonth = monthOfYear;
        mDay = dayOfMonth;
        setFirst(new Timestamp(cal.getTimeInMillis()));
        mTemporalFirstBtn.setText(getResources().getString(R.string.first_occurrence) + getTimeText().toString());
    }

    private boolean isStartClicked;

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