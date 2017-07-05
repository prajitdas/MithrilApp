package edu.umbc.ebiquity.mithril.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.data.model.Upload;
import edu.umbc.ebiquity.mithril.util.networking.JSONRequest;
import edu.umbc.ebiquity.mithril.util.networking.VolleySingleton;
import edu.umbc.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

public class FeedbackActivity extends AppCompatActivity {
    private ToggleButton feedbackQ1ToggleBtn;
    private ToggleButton feedbackQ2ToggleBtn;
    private ToggleButton feedbackQ3ToggleBtn;
    private RatingBar feedbackQ4ConcernRatingBar;
    private RatingBar feedbackQ5OSRatingBar;
    private ToggleButton feedbackQ6ToggleBtn;
    private ToggleButton feedbackQ7ToggleBtn;
    private RatingBar feedbackQ8SimplicityRatingBar;
    private EditText feedbackQ9EditText;

    private Map<String, String> feedbackDataMap = new HashMap<>();
    private JSONRequest feedbackJsonRequest;
    private String feedbackJsonResponse;

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

        initData();
        initViews();
        setOnClickListeners();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initData() {
        feedbackJsonResponse = new String();
        addToDataUploader(extractDatabaseDataToUpload(), MithrilAC.getFeedbackQuestionDataKey());
    }

    private String extractDatabaseDataToUpload() {
        return "";
    }

    private void initViews() {
        feedbackQ1ToggleBtn = (ToggleButton) findViewById(R.id.fq1btn);
        feedbackQ1ToggleBtn.setChecked(false);
        addToDataUploader(false, MithrilAC.getFeedbackQuestion1());

        feedbackQ2ToggleBtn = (ToggleButton) findViewById(R.id.fq2btn);
        feedbackQ2ToggleBtn.setChecked(false);
        addToDataUploader(false, MithrilAC.getFeedbackQuestion2());

        feedbackQ3ToggleBtn = (ToggleButton) findViewById(R.id.fq3btn);
        feedbackQ3ToggleBtn.setChecked(false);
        addToDataUploader(false, MithrilAC.getFeedbackQuestion3());

        feedbackQ4ConcernRatingBar = (RatingBar) findViewById(R.id.prisecConcernRatingBar);
        addToDataUploader(feedbackQ4ConcernRatingBar.getRating(), MithrilAC.getFeedbackQuestion4());

        feedbackQ5OSRatingBar = (RatingBar) findViewById(R.id.currentOSRatingBar);
        addToDataUploader(feedbackQ5OSRatingBar.getRating(), MithrilAC.getFeedbackQuestion5());

        feedbackQ6ToggleBtn = (ToggleButton) findViewById(R.id.fq6btn);
        feedbackQ6ToggleBtn.setChecked(false);
        addToDataUploader(false, MithrilAC.getFeedbackQuestion6());

        feedbackQ7ToggleBtn = (ToggleButton) findViewById(R.id.fq7btn);
        feedbackQ7ToggleBtn.setChecked(false);
        addToDataUploader(false, MithrilAC.getFeedbackQuestion7());

        feedbackQ8SimplicityRatingBar = (RatingBar) findViewById(R.id.systemSimplicityRatingBar);
        addToDataUploader(feedbackQ8SimplicityRatingBar.getRating(), MithrilAC.getFeedbackQuestion8());

        feedbackQ9EditText = (EditText) findViewById(R.id.fq9EditText);
        feedbackQ9EditText.clearFocus();
        feedbackQ9EditText.setText("");
        addToDataUploader(feedbackQ9EditText.getText().toString(), MithrilAC.getFeedbackQuestion9());
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
                if (!buttonView.isChecked())
                    buttonView.setChecked(true);
                else
                    buttonView.setChecked(false);
                feedbackQ3ToggleBtn.toggle();
                addToDataUploader(buttonView.isChecked(), MithrilAC.getFeedbackQuestion3());
            }
        });

        feedbackQ4ConcernRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean isChecked) {
                addToDataUploader(v, MithrilAC.getFeedbackQuestion4());
            }
        });

        feedbackQ5OSRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean isChecked) {
                addToDataUploader(v, MithrilAC.getFeedbackQuestion5());
            }
        });

        feedbackQ6ToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked())
                    buttonView.setChecked(false);
                else
                    buttonView.setChecked(true);
                feedbackQ6ToggleBtn.toggle();
                addToDataUploader(buttonView.isChecked(), MithrilAC.getFeedbackQuestion6());
            }
        });

        feedbackQ7ToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked())
                    buttonView.setChecked(false);
                else
                    buttonView.setChecked(true);
                feedbackQ7ToggleBtn.toggle();
                addToDataUploader(buttonView.isChecked(), MithrilAC.getFeedbackQuestion7());
            }
        });

        feedbackQ8SimplicityRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean isChecked) {
                addToDataUploader(v, MithrilAC.getFeedbackQuestion8());
            }
        });

        feedbackQ9EditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addToDataUploader(feedbackQ9EditText.getText().toString(), MithrilAC.getFeedbackQuestion9());
                    hideKeyboardFrom(v.getContext(), v);
                    handled = true;
                }
                return handled;
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
        try {
            SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(MithrilAC.getSharedPreferencesName(), Context.MODE_PRIVATE);
            feedbackJsonRequest = new JSONRequest(feedbackDataMap,
                    sharedPreferences.getString(
                            MithrilAC.getRandomUserId(),
                            getResources().getString(R.string.pref_user_id_default_value)
                    )
            );
            //Store the uploaded data in the database
            MithrilDBHelper.getHelper(this).addUpload(
                    MithrilDBHelper.getHelper(this).getWritableDatabase(),
                    new Upload(
                            new Timestamp(System.currentTimeMillis()),
                            feedbackJsonRequest.getRequest().toString()
                    )
            );
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
                                PermissionHelper.toast(getApplicationContext(), feedbackJsonResponse);
                            } catch (JSONException aJSONException) {
                                Log.e(MithrilAC.getDebugTag(), "Exception in sending data using JSON "+aJSONException.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String statusCode;
                            try {
                                statusCode = String.valueOf(error.networkResponse.statusCode);
                                PermissionHelper.toast(getApplicationContext(), feedbackJsonResponse);
                            } catch (NullPointerException e) {
                                statusCode = "fatal error! Error code not received";
                            }
                            feedbackJsonResponse = "Getting an error code: " + statusCode + " from the server\n";
                        }
                    }
            );
            // Add a request (in this example, called jsObjRequest) to your RequestQueue.
            VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
        } catch (JSONException aJSONException) {
            Log.e(MithrilAC.getDebugTag(), "Exception in sending data using JSON "+aJSONException.getMessage());
        }
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