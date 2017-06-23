package edu.umbc.ebiquity.mithril.ui.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.support.v7.widget.Toolbar;

import java.sql.Timestamp;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.model.rules.RepeatFrequency;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticTime;

public class TemporalDataEntryActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {
    private Button mTemporalFirstBtn;
    private Button mTemporalStartTimeBtn;
    private Button mTemporalEndTimeBtn;
    private Button mDoneBtn;
    private Spinner mSpinnerRepeatFrequency;

    private int mYear;
    private int mMonth;
    private int mDay;

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
        String label = new String("Unknown");
        if (extras != null)
            label = extras.getString("label");
        else
            failed();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_view_temporal_data_entry);
        toolbar.setTitle(toolbar.getTitle() + label);
        setSupportActionBar(toolbar);

        mTemporalFirstBtn = (Button) findViewById(R.id.temporalFirstBtn);
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        mTemporalStartTimeBtn = (Button) findViewById(R.id.temporalStartTimeBtn);
        mTemporalEndTimeBtn = (Button) findViewById(R.id.temporalEndTimeBtn);

        mDoneBtn = (Button) findViewById(R.id.doneLabelBtn);

        mSpinnerRepeatFrequency = (Spinner) findViewById(R.id.spinnerRepeatFrequency);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.freq_array, android.R.layout.simple_spinner_item);
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
                new DatePickerDialog(v.getContext(),
                        mDateSetListener,
                        mYear, mMonth, mDay).show();
            }
        });

        mTemporalStartTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mTemporalEndTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(getType()+getInferredTime(),
                        new SemanticTime(
                                getRepeatFrequency(),
                                getFirst(),
                                getPeriod(),
                                getInferredTime(),
                                true));
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    private void failed() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @TargetApi(Build.VERSION_CODES.N)
        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            Calendar cal = new GregorianCalendar(year, monthOfYear, dayOfMonth);
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            setFirst(new Timestamp(cal.getTimeInMillis()));
            mTemporalFirstBtn.setText(mTemporalFirstBtn.getText() + getTimeText().toString());
        }
    };

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

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getInferredTime() {
        return inferredTime;
    }

    public void setInferredTime(String inferredTime) {
        this.inferredTime = inferredTime;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        setRepeatFrequency(RepeatFrequency.charSeqToRepeatFrequency((CharSequence) parent.getItemAtPosition(position)));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        setRepeatFrequency(RepeatFrequency.NEVER_REPEATS);
    }

    public int getmYear() {
        return mYear;
    }

    public void setmYear(int mYear) {
        this.mYear = mYear;
    }

    public int getmMonth() {
        return mMonth;
    }

    public void setmMonth(int mMonth) {
        this.mMonth = mMonth;
    }

    public int getmDay() {
        return mDay;
    }

    public void setmDay(int mDay) {
        this.mDay = mDay;
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user

        }
    }
}