package edu.umbc.ebiquity.mithril.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.ToggleButton;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;

public class FeedbackActivity extends AppCompatActivity {
    private ToggleButton feedbackQ1ToggleBtn;
    private ToggleButton feedbackQ2ToggleBtn;
    private ToggleButton feedbackQ3ToggleBtn;
    private ToggleButton feedbackQ4ToggleBtn;
    private ToggleButton feedbackQ5ToggleBtn;
    private RatingBar feedbackQ6SimplicityRatingBar;
    private EditText feedbackQ7EditText;
    private RatingBar feedbackQ8ConcernRatingBar;
    private RatingBar feedbackQ9OSRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_feedback);
        setSupportActionBar(toolbar);

        feedbackQ1ToggleBtn = (ToggleButton) findViewById(R.id.fq1btn);
        feedbackQ2ToggleBtn = (ToggleButton) findViewById(R.id.fq2btn);
        feedbackQ3ToggleBtn = (ToggleButton) findViewById(R.id.fq3btn);
        feedbackQ4ToggleBtn = (ToggleButton) findViewById(R.id.fq4btn);
        feedbackQ5ToggleBtn = (ToggleButton) findViewById(R.id.fq5btn);
        feedbackQ6SimplicityRatingBar = (RatingBar) findViewById(R.id.systemSimplicityRatingBar);
        feedbackQ7EditText = (EditText) findViewById(R.id.fq7EditText);
        feedbackQ8ConcernRatingBar = (RatingBar) findViewById(R.id.prisecConcernRatingBar);
        feedbackQ9OSRatingBar = (RatingBar) findViewById(R.id.currentOSRatingBar);

        setOnClickListeners();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setOnClickListeners() {
        feedbackQ1ToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                if (!buttonView.isChecked())
                    buttonView.setChecked(true);
                else
                    buttonView.setChecked(false);
                addToDataUploader(buttonView.isChecked(), MithrilAC.getFeedbackQuestion1());
            }
        });

        feedbackQ2ToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                if (!buttonView.isChecked())
                    buttonView.setChecked(true);
                else
                    buttonView.setChecked(false);
                addToDataUploader(buttonView.isChecked(), MithrilAC.getFeedbackQuestion2());
            }
        });

        feedbackQ3ToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                if (!buttonView.isChecked())
                    buttonView.setChecked(true);
                else
                    buttonView.setChecked(false);
                addToDataUploader(buttonView.isChecked(), MithrilAC.getFeedbackQuestion3());
            }
        });

        feedbackQ4ToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                if (!buttonView.isChecked())
                    buttonView.setChecked(true);
                else
                    buttonView.setChecked(false);
                addToDataUploader(buttonView.isChecked(), MithrilAC.getFeedbackQuestion4());
            }
        });

        feedbackQ5ToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                if (!buttonView.isChecked())
                    buttonView.setChecked(true);
                else
                    buttonView.setChecked(false);
                addToDataUploader(buttonView.isChecked(), MithrilAC.getFeedbackQuestion5());
            }
        });

        feedbackQ6SimplicityRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                addToDataUploader(v, MithrilAC.getFeedbackQuestion6());
            }
        });

        feedbackQ7EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                addToDataUploader(charSequence.toString(), MithrilAC.getFeedbackQuestion7());
            }
        });

        feedbackQ8ConcernRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                addToDataUploader(v, MithrilAC.getFeedbackQuestion8());
            }
        });

        feedbackQ9OSRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                addToDataUploader(v, MithrilAC.getFeedbackQuestion9());
            }
        });

    }

    private void addToDataUploader(boolean isChecked, String questionId) {

    }

    private void addToDataUploader(String userInput, String questionId) {

    }

    private void addToDataUploader(float rating, String questionId) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.upload_feedback) {
            startUpload();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startUpload() {

    }
}