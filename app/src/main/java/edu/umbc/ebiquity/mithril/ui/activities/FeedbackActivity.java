package edu.umbc.ebiquity.mithril.ui.activities;

import android.annotation.NonNull;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.data.model.Upload;
import edu.umbc.ebiquity.mithril.data.model.components.AppData;
import edu.umbc.ebiquity.mithril.data.model.rules.PolicyRule;
import edu.umbc.ebiquity.mithril.data.model.rules.Violation;
import edu.umbc.ebiquity.mithril.data.model.rules.context.ContextForUpload;
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
    private ImageButton uploadButton;
    private Switch feedbackSwitch;
    private ScrollView feedbackScrollview;

    private Map<String, Object> feedbackDataUploaderMap = new HashMap<>();
    private JSONRequest feedbackJsonRequest;
    private String feedbackJsonResponse;
    private SharedPreferences sharedPreferences;

    private SQLiteDatabase mithrilDB;
    // Write a message to the database
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
//    private String userId;
    private FirebaseAuth mAuth;
    private TextView uploadingAsTextView;
    private ProgressDialog mProgressDialog;

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_feedback);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();

        initViews();
        initData();

        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);

        setOnClickListeners();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null)
            uploadingAsTextView.setText(getResources().getString(R.string.uploading_as).concat(user.getDisplayName()));
        else
            uploadingAsTextView.setText(getResources().getString(R.string.uploading_as).concat(getResources().getString(R.string.anonymous)));
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void signInAnonymously() {
        showProgressDialog();
        // [START signin_anonymously]
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(MithrilAC.getDebugTag(), "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(MithrilAC.getDebugTag(), "signInAnonymously:failure", task.getException());
                            Toast.makeText(FeedbackActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END signin_anonymously]
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private void initData() {
        sharedPreferences = getApplicationContext().getSharedPreferences(MithrilAC.getSharedPreferencesName(), Context.MODE_PRIVATE);
//        userId = sharedPreferences.getString(MithrilAC.getRandomUserId(), getResources().getString(R.string.anonymous));

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        mithrilDB = MithrilDBHelper.getHelper(this).getWritableDatabase();
        feedbackJsonResponse = new String();

        getDataFromDatabase();
//        addToDataUploader(extractDatabaseDataToUpload(), MithrilAC.getFeedbackQuestionDataKey());
    }

    private void getDataFromDatabase() {
        addToDataUploader(MithrilDBHelper.getHelper(this).findAllViolations(mithrilDB), "violations");
        addToDataUploader(MithrilDBHelper.getHelper(this).findAllPolicies(mithrilDB), "policies");
        addToDataUploader(MithrilDBHelper.getHelper(this).findAllApps(mithrilDB), "apps");
        addToDataUploader(MithrilDBHelper.getHelper(this).findAllContexts(mithrilDB), "contexts");
    }

    private String extractDatabaseDataToUpload() {
        try {
            JSONObject jsonObject = new JSONObject();

            List<ContextForUpload> contexts = MithrilDBHelper.getHelper(this).findAllContexts(mithrilDB);
            JSONArray contextForUploadJsonArray = new JSONArray();
            for (ContextForUpload contextForUpload : contexts)
                contextForUploadJsonArray.put(contextForUpload.uploadString());
            jsonObject.put("contexts", contextForUploadJsonArray);

            List<Violation> violations = MithrilDBHelper.getHelper(this).findAllViolations(mithrilDB);
            JSONArray violationJsonArray = new JSONArray();
            for (Violation violation : violations)
                violationJsonArray.put(violation.uploadString());
            jsonObject.put("violations", violationJsonArray);

            List<PolicyRule> policies = MithrilDBHelper.getHelper(this).findAllPolicies(mithrilDB);
            JSONArray policyJsonArray = new JSONArray();
            for (PolicyRule policyRule : policies)
                policyJsonArray.put(policyRule.uploadString());
            jsonObject.put("policies", policyJsonArray);

            List<AppData> apps = MithrilDBHelper.getHelper(this).findAllApps(mithrilDB);
            JSONArray appJsonArray = new JSONArray();
            for (AppData app : apps)
                appJsonArray.put(app.uploadString());
            jsonObject.put("apps", appJsonArray);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initViews() {
        uploadingAsTextView = (TextView) findViewById(R.id.uploadingAs);

        uploadButton = (ImageButton) findViewById(R.id.simplyUploadBtn);

        feedbackSwitch = (Switch) findViewById(R.id.switchGiveFeedback);

        feedbackScrollview = (ScrollView) findViewById(R.id.feedbackScrollView);

        feedbackQ1ToggleBtn = (ToggleButton) findViewById(R.id.fq1btn);
        feedbackQ1ToggleBtn.setChecked(false);

        feedbackQ2ToggleBtn = (ToggleButton) findViewById(R.id.fq2btn);
        feedbackQ2ToggleBtn.setChecked(false);

        feedbackQ3ToggleBtn = (ToggleButton) findViewById(R.id.fq3btn);
        feedbackQ3ToggleBtn.setChecked(false);

        feedbackQ4ConcernRatingBar = (RatingBar) findViewById(R.id.prisecConcernRatingBar);

        feedbackQ5OSRatingBar = (RatingBar) findViewById(R.id.currentOSRatingBar);

        feedbackQ6ToggleBtn = (ToggleButton) findViewById(R.id.fq6btn);
        feedbackQ6ToggleBtn.setChecked(false);

        feedbackQ7ToggleBtn = (ToggleButton) findViewById(R.id.fq7btn);
        feedbackQ7ToggleBtn.setChecked(false);

        feedbackQ8SimplicityRatingBar = (RatingBar) findViewById(R.id.systemSimplicityRatingBar);

        feedbackQ9EditText = (EditText) findViewById(R.id.fq9EditText);
        feedbackQ9EditText.clearFocus();
        feedbackQ9EditText.setText("");
    }

    private void setOnClickListeners() {
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUpload();
            }
        });

        feedbackSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
//                    feedbackSwitch.setChecked(false);
                    feedbackScrollview.setVisibility(View.VISIBLE);
                    uploadButton.setVisibility(View.GONE);
                    addToDataUploader(false, MithrilAC.getFeedbackQuestion1());
                    addToDataUploader(false, MithrilAC.getFeedbackQuestion2());
                    addToDataUploader(false, MithrilAC.getFeedbackQuestion3());
                    addToDataUploader(feedbackQ4ConcernRatingBar.getRating(), MithrilAC.getFeedbackQuestion4());
                    addToDataUploader(feedbackQ5OSRatingBar.getRating(), MithrilAC.getFeedbackQuestion5());
                    addToDataUploader(false, MithrilAC.getFeedbackQuestion6());
                    addToDataUploader(false, MithrilAC.getFeedbackQuestion7());
                    addToDataUploader(feedbackQ8SimplicityRatingBar.getRating(), MithrilAC.getFeedbackQuestion8());
                    addToDataUploader(feedbackQ9EditText.getText().toString(), MithrilAC.getFeedbackQuestion9());
                } else {
//                    feedbackSwitch.setChecked(true);
                    feedbackScrollview.setVisibility(View.GONE);
                    uploadButton.setVisibility(View.VISIBLE);
                    addToDataUploader("", MithrilAC.getFeedbackQuestion1());
                    addToDataUploader("", MithrilAC.getFeedbackQuestion2());
                    addToDataUploader("", MithrilAC.getFeedbackQuestion3());
                    addToDataUploader("", MithrilAC.getFeedbackQuestion4());
                    addToDataUploader("", MithrilAC.getFeedbackQuestion5());
                    addToDataUploader("", MithrilAC.getFeedbackQuestion6());
                    addToDataUploader("", MithrilAC.getFeedbackQuestion7());
                    addToDataUploader("", MithrilAC.getFeedbackQuestion8());
                    addToDataUploader("", MithrilAC.getFeedbackQuestion9());
                }
            }
        });

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

    private void addToDataUploader(List<?> someData, String dataLabel) {
        feedbackDataUploaderMap.put(dataLabel, someData);
    }

    private void addToDataUploader(boolean isChecked, String questionId) {
        feedbackDataUploaderMap.put(questionId, String.valueOf(isChecked));
    }

    private void addToDataUploader(String userInput, String questionId) {
        if (userInput == "")
            feedbackDataUploaderMap.remove(questionId);
        else
            feedbackDataUploaderMap.put(questionId, userInput);
    }

    private void addToDataUploader(float rating, String questionId) {
        feedbackDataUploaderMap.put(questionId, String.valueOf(rating));
    }

    private void startUpload() {
        signInAnonymously();
        //Store the uploaded data in the database
        MithrilDBHelper.getHelper(this).addUpload(
                mithrilDB,
                new Upload(
                        new Timestamp(System.currentTimeMillis()),
                        feedbackJsonRequest.getRequest().toString()
                )
        );
        databaseReference.child(getUid()).child(String.valueOf(System.currentTimeMillis())).setValue(feedbackDataUploaderMap);
    }

    private void oldStartUpload() {
        try {
            feedbackJsonRequest = new JSONRequest(feedbackDataUploaderMap,
                    sharedPreferences.getString(
                            MithrilAC.getRandomUserId(),
                            getResources().getString(R.string.anonymous)
                    )
            );
            //Store the uploaded data in the database
            MithrilDBHelper.getHelper(this).addUpload(
                    mithrilDB,
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
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra(MithrilAC.getFeedbackUploadResultKey(), feedbackJsonResponse);
                                setResult(Activity.RESULT_OK, resultIntent);
                                signOut();
                                finish();
                            } catch (JSONException aJSONException) {
                                Log.e(MithrilAC.getDebugTag(), "Exception in sending data using JSON " + aJSONException.getMessage());
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
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra(MithrilAC.getFeedbackUploadResultKey(), feedbackJsonResponse);
                            setResult(Activity.RESULT_CANCELED, resultIntent);
                            finish();
                        }
                    }
            );
            // Add a request (in this example, called jsObjRequest) to your RequestQueue.
            VolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
        } catch (JSONException aJSONException) {
            Log.e(MithrilAC.getDebugTag(), "Exception in sending data using JSON " + aJSONException.getMessage());
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