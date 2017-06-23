package edu.umbc.ebiquity.mithril.ui.activities;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.icu.text.DateFormat;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.support.v7.widget.Toolbar;

import java.sql.Timestamp;
import java.util.Date;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.model.rules.RepeatFrequency;

public class TemporalDataEntryActivity extends AppCompatActivity {
    private Button mTemporalFirstBtn;
    private Button mTemporalStartTimeBtn;
    private Button mTemporalEndTimeBtn;
    private Button mChooseLabelBtn;
    private Spinner mSpinnerRepeatFrequency;

    private int mYear;
    private int mMonth;
    private int mDay;

    private final String type = MithrilAC.getPrefKeyContextTypeTemporal();
    private RepeatFrequency repeatFrequency;
    private Timestamp first;
    private int period;
    private String inferredTime;
    private boolean enabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temporal_data_entry);

        Bundle extras = getIntent().getExtras();
        String label = new String("Unknown");
        if (extras != null)
            label = extras.getString("label");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_view_temporal_data_entry);
        toolbar.setTitle(toolbar.getTitle() + label);
        setSupportActionBar(toolbar);

        mTemporalFirstBtn = (Button) findViewById(R.id.temporalFirstBtn);
        mTemporalStartTimeBtn = (Button) findViewById(R.id.temporalStartTimeBtn);
        mTemporalEndTimeBtn = (Button) findViewById(R.id.temporalEndTimeBtn);
        mChooseLabelBtn = (Button) findViewById(R.id.chooseLabelBtn);
        mSpinnerRepeatFrequency = (Spinner) findViewById(R.id.spinnerRepeatFrequency);

        setOnclickListeners();
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

        mChooseLabelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mSpinnerRepeatFrequency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @TargetApi(Build.VERSION_CODES.N)
        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            Calendar cal = new GregorianCalendar(year, monthOfYear, dayOfMonth);
            setFirst(new Timestamp(cal.getTimeInMillis()));
        }
    };

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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}