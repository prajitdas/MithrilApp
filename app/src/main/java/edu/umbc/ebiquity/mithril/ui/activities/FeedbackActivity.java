package edu.umbc.ebiquity.mithril.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.util.networking.JSONRequest;
import edu.umbc.ebiquity.mithril.util.networking.VolleySingleton;

public class FeedbackActivity extends AppCompatActivity {
    private ToggleButton feedbackQ1ToggleBtn;
    private ToggleButton feedbackQ2ToggleBtn;
    private ToggleButton feedbackQ3ToggleBtn;
    private ToggleButton feedbackQ4ToggleBtn;
    private RatingBar feedbackQ5SimplicityRatingBar;
    private EditText feedbackQ6EditText;
    private ToggleButton feedbackQ7ToggleBtn;
    private RatingBar feedbackQ8ConcernRatingBar;
    private RatingBar feedbackQ9OSRatingBar;

    private Map<String, String> feedbackDataMap = new HashMap<>();
    private JSONRequest feedbackJsonRequest;
    private String feedbackJsonResponse;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_feedback);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_feedback);
        setSupportActionBar(toolbar);

        feedbackJsonResponse = new String();

        initViews();
        setOnClickListeners();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initViews() {
        feedbackQ1ToggleBtn = (ToggleButton) findViewById(R.id.fq1btn);
        feedbackQ1ToggleBtn.setChecked(false);

        feedbackQ2ToggleBtn = (ToggleButton) findViewById(R.id.fq2btn);
        feedbackQ2ToggleBtn.setChecked(false);

        feedbackQ3ToggleBtn = (ToggleButton) findViewById(R.id.fq3btn);
        feedbackQ3ToggleBtn.setChecked(false);

        feedbackQ4ToggleBtn = (ToggleButton) findViewById(R.id.fq4btn);
        feedbackQ4ToggleBtn.setChecked(false);

        feedbackQ5SimplicityRatingBar = (RatingBar) findViewById(R.id.systemSimplicityRatingBar);

        feedbackQ6EditText = (EditText) findViewById(R.id.fq6EditText);
        feedbackQ6EditText.clearFocus();
        feedbackQ6EditText.setText("");

        feedbackQ7ToggleBtn = (ToggleButton) findViewById(R.id.fq7btn);
        feedbackQ7ToggleBtn.setChecked(false);

        feedbackQ8ConcernRatingBar = (RatingBar) findViewById(R.id.prisecConcernRatingBar);

        feedbackQ9OSRatingBar = (RatingBar) findViewById(R.id.currentOSRatingBar);
    }

    private void setOnClickListeners() {
        feedbackQ1ToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked())
                    buttonView.setChecked(false);
                else
                    buttonView.setChecked(true);
                feedbackQ1ToggleBtn.toggle();
                addToDataUploader(buttonView.isChecked(), MithrilAC.getFeedbackQuestion1());
            }
        });

        feedbackQ2ToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked())
                    buttonView.setChecked(false);
                else
                    buttonView.setChecked(true);
                feedbackQ2ToggleBtn.toggle();
                addToDataUploader(buttonView.isChecked(), MithrilAC.getFeedbackQuestion2());
            }
        });

        feedbackQ3ToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked())
                    buttonView.setChecked(false);
                else
                    buttonView.setChecked(true);
                feedbackQ3ToggleBtn.toggle();
                addToDataUploader(buttonView.isChecked(), MithrilAC.getFeedbackQuestion4());
            }
        });

        feedbackQ4ToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked())
                    buttonView.setChecked(false);
                else
                    buttonView.setChecked(true);
                feedbackQ4ToggleBtn.toggle();
                addToDataUploader(buttonView.isChecked(), MithrilAC.getFeedbackQuestion5());
            }
        });

        feedbackQ5SimplicityRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean isChecked) {
                addToDataUploader(v, MithrilAC.getFeedbackQuestion6());
            }
        });

        feedbackQ6EditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addToDataUploader(v.getText().toString(), MithrilAC.getFeedbackQuestion7());
                    hideKeyboardFrom(v.getContext(), v);
                    handled = true;
                }
                return handled;
            }
        });

        feedbackQ7ToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!buttonView.isChecked())
                    buttonView.setChecked(true);
                else
                    buttonView.setChecked(false);
                feedbackQ7ToggleBtn.toggle();
                addToDataUploader(buttonView.isChecked(), MithrilAC.getFeedbackQuestion3());
            }
        });

        feedbackQ8ConcernRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean isChecked) {
                addToDataUploader(v, MithrilAC.getFeedbackQuestion8());
            }
        });

        feedbackQ9OSRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean isChecked) {
                addToDataUploader(v, MithrilAC.getFeedbackQuestion9());
            }
        });
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void addToDataUploader(boolean isChecked, String questionId) {
        feedbackDataMap.put(questionId, String.valueOf(isChecked));
    }

    private void addToDataUploader(String userInput, String questionId) {
        feedbackDataMap.put(questionId, userInput);
    }

    private void addToDataUploader(float rating, String questionId) {
        feedbackDataMap.put(questionId, String.valueOf(rating));
    }

    private void startUpload() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(MithrilAC.getSharedPreferencesName(), Context.MODE_PRIVATE);
        try {
            feedbackJsonRequest = new JSONRequest(feedbackDataMap, sharedPreferences.getString(MithrilAC.getRandomUserId(), getResources().getString(R.string.pref_user_id_default_value)));
        } catch (JSONException aJSONException) {
        }
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST,
                MithrilAC.getFeedbackUrl(),
                feedbackJsonRequest.getRequest(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            final String status = response.getString("status");
                            feedbackJsonResponse = "Status: " + status;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String statusCode;
                        try {
                            statusCode = String.valueOf(error.networkResponse.statusCode);
                        } catch (NullPointerException e) {
                            statusCode = "fatal error! Error code not received";
                        }
                        feedbackJsonResponse = "Getting an error code: " + statusCode + " from the server\n";
                    }
                }
        );
        // Add a request (in this example, called jsObjRequest) to your RequestQueue.
        VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
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
}