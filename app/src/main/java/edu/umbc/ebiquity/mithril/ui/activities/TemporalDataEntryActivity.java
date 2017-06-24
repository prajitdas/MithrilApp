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
import java.util.List;
import java.util.Set;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.model.rules.RepeatFrequency;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticTime;
import edu.umbc.ebiquity.mithril.util.specialtasks.contextinstances.DayOfWeek;
import edu.umbc.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

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
        }
        else
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
                        semanticTime.getStart().toString());

        mEndTimeBtn = (Button) findViewById(R.id.endTimeBtn);
        mEndTimeBtn.setText(
                getResources().getString(
                        R.string.ending_time) +
                        semanticTime.getDayOfWeekString());
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
                                getStart(),
                                getEnd(),
                                label,
                                false));
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
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

    public Calendar getStart() {
        return semanticTime.getStart();
    }

    public Calendar getEnd() {
        return semanticTime.getEnd();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        if(view.equals(startTimePickerDialog))
            semanticTime.setStart(calendar);
        else
            semanticTime.setEnd(calendar);
    }
}