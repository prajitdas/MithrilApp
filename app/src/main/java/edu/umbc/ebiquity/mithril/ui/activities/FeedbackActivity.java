package edu.umbc.ebiquity.mithril.ui.activities;

import android.annotation.NonNull;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.data.model.Feedback;
import edu.umbc.ebiquity.mithril.data.model.Upload;

public class FeedbackActivity extends AppCompatActivity {
    private RadioGroup fq1RadioGroup;
    private RadioGroup fq2RadioGroup;
    private RadioGroup fq3RadioGroup;
    private RadioGroup fq4RadioGroup;
    private RadioGroup fq5RadioGroup;
    private RadioGroup fq6RadioGroup;
    private RadioGroup fq7RadioGroup;
    private RadioGroup fq8RadioGroup;
    private RatingBar feedbackQ9SimplicityRatingBar;
    private EditText feedbackQ10EditText;

    private ImageButton uploadButton;
    private Switch feedbackSwitch;
    private ScrollView feedbackScrollview;

    private Map<String, Object> feedbackDataUploaderMap = new HashMap<>();

    private SQLiteDatabase mithrilDB;
    // Write a message to the database
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
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

        setOnClickListeners();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        if (mAuth.getCurrentUser() == null) {
            signInAnonymously();
            return null;
        } else
            return mAuth.getCurrentUser().getUid();
    }

    private FirebaseUser signInAnonymously() {
        showProgressDialog();
        // [START signin_anonymously]
        Task<AuthResult> authResultTask = mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(MithrilAC.getDebugTag(), "signInAnonymously:success");
                            user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(MithrilAC.getDebugTag(), "signInAnonymously:failure", task.getException());
                            Toast.makeText(FeedbackActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END signin_anonymously]
        return user;
    }

    private void signOut() {
        mAuth.signOut();
    }

    private void initData() {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null)
            signInAnonymously();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        mithrilDB = MithrilDBHelper.getHelper(this).getWritableDatabase();

        getDataFromDatabase();
    }

    private void getDataFromDatabase() {
        addToDataUploader(MithrilDBHelper.getHelper(this).findEveryViolation(mithrilDB), "violations");
        addToDataUploader(MithrilDBHelper.getHelper(this).findEveryPolicy(mithrilDB), "policies");
        addToDataUploader(MithrilDBHelper.getHelper(this).findAllApps(mithrilDB), "apps");
        addToDataUploader(MithrilDBHelper.getHelper(this).findAllContexts(mithrilDB), "contexts");
    }

    private void initViews() {
        uploadButton = (ImageButton) findViewById(R.id.simplyUploadBtn);

        feedbackSwitch = (Switch) findViewById(R.id.switchGiveFeedback);

        feedbackScrollview = (ScrollView) findViewById(R.id.feedbackScrollView);

        fq1RadioGroup = (RadioGroup) findViewById(R.id.fq1RadioGroup);
        fq1RadioGroup.check(R.id.fq1radio_strongly_agree);
        fq2RadioGroup = (RadioGroup) findViewById(R.id.fq2RadioGroup);
        fq2RadioGroup.check(R.id.fq2radio_strongly_agree);
        fq3RadioGroup = (RadioGroup) findViewById(R.id.fq3RadioGroup);
        fq3RadioGroup.check(R.id.fq3radio_strongly_agree);
        fq4RadioGroup = (RadioGroup) findViewById(R.id.fq4RadioGroup);
        fq4RadioGroup.check(R.id.fq4radio_strongly_agree);
        fq5RadioGroup = (RadioGroup) findViewById(R.id.fq5RadioGroup);
        fq5RadioGroup.check(R.id.fq5radio_strongly_agree);
        fq6RadioGroup = (RadioGroup) findViewById(R.id.fq6RadioGroup);
        fq6RadioGroup.check(R.id.fq6radio_strongly_agree);
        fq7RadioGroup = (RadioGroup) findViewById(R.id.fq7RadioGroup);
        fq7RadioGroup.check(R.id.fq7radio_strongly_agree);
        fq8RadioGroup = (RadioGroup) findViewById(R.id.fq8RadioGroup);
        fq8RadioGroup.check(R.id.fq8radio_strongly_agree);

        feedbackQ9SimplicityRatingBar = (RatingBar) findViewById(R.id.systemSimplicityRatingBar);

        feedbackQ10EditText = (EditText) findViewById(R.id.fq10EditText);
        feedbackQ10EditText.clearFocus();
        feedbackQ10EditText.setText("");
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
                    feedbackScrollview.setVisibility(View.VISIBLE);
                    addToDataUploader(getResources().getString(R.string.strongly_agree), MithrilAC.getFeedbackQuestion1());
                    addToDataUploader(getResources().getString(R.string.strongly_agree), MithrilAC.getFeedbackQuestion2());
                    addToDataUploader(getResources().getString(R.string.strongly_agree), MithrilAC.getFeedbackQuestion3());
                    addToDataUploader(getResources().getString(R.string.extremely_confident), MithrilAC.getFeedbackQuestion4());
                    addToDataUploader(getResources().getString(R.string.strongly_agree), MithrilAC.getFeedbackQuestion5());
                    addToDataUploader(getResources().getString(R.string.strongly_agree), MithrilAC.getFeedbackQuestion6());
                    addToDataUploader(getResources().getString(R.string.strongly_agree), MithrilAC.getFeedbackQuestion7());
                    addToDataUploader(getResources().getString(R.string.strongly_agree), MithrilAC.getFeedbackQuestion8());
                    addToDataUploader(feedbackQ9SimplicityRatingBar.getRating(), MithrilAC.getFeedbackQuestion9());
                    addToDataUploader(feedbackQ10EditText.getText().toString(), MithrilAC.getFeedbackQuestion10());
                } else {
                    feedbackScrollview.setVisibility(View.GONE);
                    addToDataUploader("", MithrilAC.getFeedbackQuestion1());
                    addToDataUploader("", MithrilAC.getFeedbackQuestion2());
                    addToDataUploader("", MithrilAC.getFeedbackQuestion3());
                    addToDataUploader("", MithrilAC.getFeedbackQuestion4());
                    addToDataUploader("", MithrilAC.getFeedbackQuestion5());
                    addToDataUploader("", MithrilAC.getFeedbackQuestion6());
                    addToDataUploader("", MithrilAC.getFeedbackQuestion7());
                    addToDataUploader("", MithrilAC.getFeedbackQuestion8());
                    addToDataUploader("", MithrilAC.getFeedbackQuestion9());
                    addToDataUploader("", MithrilAC.getFeedbackQuestion10());
                }
            }
        });

        fq1RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.fq1radio_strongly_agree)
                    addToDataUploader(getResources().getString(R.string.strongly_agree), MithrilAC.getFeedbackQuestion1());
                else if (checkedId == R.id.fq1radio_agree)
                    addToDataUploader(getResources().getString(R.string.agree), MithrilAC.getFeedbackQuestion1());
                else if (checkedId == R.id.fq1radio_neutral)
                    addToDataUploader(getResources().getString(R.string.neutral), MithrilAC.getFeedbackQuestion1());
                else if (checkedId == R.id.fq1radio_disagree)
                    addToDataUploader(getResources().getString(R.string.disagree), MithrilAC.getFeedbackQuestion1());
                else if (checkedId == R.id.fq1radio_strongly_disagree)
                    addToDataUploader(getResources().getString(R.string.strongly_disagree), MithrilAC.getFeedbackQuestion1());
            }
        });

        fq2RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.fq2radio_strongly_agree)
                    addToDataUploader(getResources().getString(R.string.strongly_agree), MithrilAC.getFeedbackQuestion2());
                else if (checkedId == R.id.fq2radio_agree)
                    addToDataUploader(getResources().getString(R.string.agree), MithrilAC.getFeedbackQuestion2());
                else if (checkedId == R.id.fq2radio_neutral)
                    addToDataUploader(getResources().getString(R.string.neutral), MithrilAC.getFeedbackQuestion2());
                else if (checkedId == R.id.fq2radio_disagree)
                    addToDataUploader(getResources().getString(R.string.disagree), MithrilAC.getFeedbackQuestion2());
                else if (checkedId == R.id.fq2radio_strongly_disagree)
                    addToDataUploader(getResources().getString(R.string.strongly_disagree), MithrilAC.getFeedbackQuestion2());
            }
        });

        fq3RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.fq3radio_strongly_agree)
                    addToDataUploader(getResources().getString(R.string.strongly_agree), MithrilAC.getFeedbackQuestion3());
                else if (checkedId == R.id.fq3radio_agree)
                    addToDataUploader(getResources().getString(R.string.agree), MithrilAC.getFeedbackQuestion3());
                else if (checkedId == R.id.fq3radio_neutral)
                    addToDataUploader(getResources().getString(R.string.neutral), MithrilAC.getFeedbackQuestion3());
                else if (checkedId == R.id.fq3radio_disagree)
                    addToDataUploader(getResources().getString(R.string.disagree), MithrilAC.getFeedbackQuestion3());
                else if (checkedId == R.id.fq3radio_strongly_disagree)
                    addToDataUploader(getResources().getString(R.string.strongly_disagree), MithrilAC.getFeedbackQuestion3());
            }
        });

        fq4RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.fq4radio_strongly_agree)
                    addToDataUploader(getResources().getString(R.string.extremely_confident), MithrilAC.getFeedbackQuestion4());
                else if (checkedId == R.id.fq4radio_agree)
                    addToDataUploader(getResources().getString(R.string.quite_confident), MithrilAC.getFeedbackQuestion4());
                else if (checkedId == R.id.fq4radio_neutral)
                    addToDataUploader(getResources().getString(R.string.somewhat_confident), MithrilAC.getFeedbackQuestion4());
                else if (checkedId == R.id.fq4radio_disagree)
                    addToDataUploader(getResources().getString(R.string.slightly_confident), MithrilAC.getFeedbackQuestion4());
                else if (checkedId == R.id.fq4radio_strongly_disagree)
                    addToDataUploader(getResources().getString(R.string.not_confident_at_all), MithrilAC.getFeedbackQuestion4());
            }
        });

        fq5RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.fq5radio_strongly_agree)
                    addToDataUploader(getResources().getString(R.string.strongly_agree), MithrilAC.getFeedbackQuestion5());
                else if (checkedId == R.id.fq5radio_agree)
                    addToDataUploader(getResources().getString(R.string.agree), MithrilAC.getFeedbackQuestion5());
                else if (checkedId == R.id.fq5radio_neutral)
                    addToDataUploader(getResources().getString(R.string.neutral), MithrilAC.getFeedbackQuestion5());
                else if (checkedId == R.id.fq5radio_disagree)
                    addToDataUploader(getResources().getString(R.string.disagree), MithrilAC.getFeedbackQuestion5());
                else if (checkedId == R.id.fq5radio_strongly_disagree)
                    addToDataUploader(getResources().getString(R.string.strongly_disagree), MithrilAC.getFeedbackQuestion5());
            }
        });

        fq6RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.fq6radio_strongly_agree)
                    addToDataUploader(getResources().getString(R.string.strongly_agree), MithrilAC.getFeedbackQuestion6());
                else if (checkedId == R.id.fq6radio_agree)
                    addToDataUploader(getResources().getString(R.string.agree), MithrilAC.getFeedbackQuestion6());
                else if (checkedId == R.id.fq6radio_neutral)
                    addToDataUploader(getResources().getString(R.string.neutral), MithrilAC.getFeedbackQuestion6());
                else if (checkedId == R.id.fq6radio_disagree)
                    addToDataUploader(getResources().getString(R.string.disagree), MithrilAC.getFeedbackQuestion6());
                else if (checkedId == R.id.fq6radio_strongly_disagree)
                    addToDataUploader(getResources().getString(R.string.strongly_disagree), MithrilAC.getFeedbackQuestion6());
            }
        });

        fq7RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.fq7radio_strongly_agree)
                    addToDataUploader(getResources().getString(R.string.strongly_agree), MithrilAC.getFeedbackQuestion7());
                else if (checkedId == R.id.fq7radio_agree)
                    addToDataUploader(getResources().getString(R.string.agree), MithrilAC.getFeedbackQuestion7());
                else if (checkedId == R.id.fq7radio_neutral)
                    addToDataUploader(getResources().getString(R.string.neutral), MithrilAC.getFeedbackQuestion7());
                else if (checkedId == R.id.fq7radio_disagree)
                    addToDataUploader(getResources().getString(R.string.disagree), MithrilAC.getFeedbackQuestion7());
                else if (checkedId == R.id.fq7radio_strongly_disagree)
                    addToDataUploader(getResources().getString(R.string.strongly_disagree), MithrilAC.getFeedbackQuestion7());
            }
        });

        fq8RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.fq8radio_strongly_agree)
                    addToDataUploader(getResources().getString(R.string.strongly_agree), MithrilAC.getFeedbackQuestion8());
                else if (checkedId == R.id.fq8radio_agree)
                    addToDataUploader(getResources().getString(R.string.agree), MithrilAC.getFeedbackQuestion8());
                else if (checkedId == R.id.fq8radio_neutral)
                    addToDataUploader(getResources().getString(R.string.neutral), MithrilAC.getFeedbackQuestion8());
                else if (checkedId == R.id.fq8radio_disagree)
                    addToDataUploader(getResources().getString(R.string.disagree), MithrilAC.getFeedbackQuestion8());
                else if (checkedId == R.id.fq8radio_strongly_disagree)
                    addToDataUploader(getResources().getString(R.string.strongly_disagree), MithrilAC.getFeedbackQuestion8());
            }
        });

        feedbackQ9SimplicityRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean isChecked) {
                addToDataUploader(v, MithrilAC.getFeedbackQuestion9());
            }
        });

        feedbackQ10EditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    addToDataUploader(feedbackQ10EditText.getText().toString(), MithrilAC.getFeedbackQuestion10());
                    hideKeyboardFrom(v.getContext(), v);
                    handled = true;
                    uploadButton.performClick();
                }
                return handled;
            }
        });
    }

    private void addToDataUploader(List<?> someData, String dataLabel) {
        feedbackDataUploaderMap.put(dataLabel, someData);
    }

    private void addToDataUploader(String userInput, String questionId) {
        if (userInput.equals(""))
            feedbackDataUploaderMap.remove(questionId);
        else
            feedbackDataUploaderMap.put(questionId, userInput);
    }

    private void addToDataUploader(float rating, String questionId) {
        feedbackDataUploaderMap.put(questionId, String.valueOf(rating));
    }


    private void startUpload() {
        if (getUid() != null) {
            Timestamp uploadTime = new Timestamp(System.currentTimeMillis());
            saveTheDataWeAreUploading(uploadTime);
            saveFeedbackInfo(uploadTime);

            addToDataUploader(MithrilDBHelper.getHelper(this).findAllFeedbacks(mithrilDB), "feedbackstats");
            databaseReference.child(MithrilAC.getMithrilFirebaseServerKeyUsers()).child(getUid()).child(String.valueOf(uploadTime)).setValue(feedbackDataUploaderMap);
        } else {
            signInAnonymously();//databaseReference.child(getUid()).child(String.valueOf(System.currentTimeMillis())).setValue(feedbackDataUploaderMap));
        }
    }

    private void saveFeedbackInfo(Timestamp uploadTime) {
        Timestamp afterTime = MithrilDBHelper.getHelper(this).findLastFeedbackTime(mithrilDB);
        Pair<Integer, Integer> violationTVFVCnts = MithrilDBHelper.getHelper(this).findAllViolationsCreatedAfterTime(mithrilDB, afterTime);
        MithrilDBHelper.getHelper(this).addFeedback(
                mithrilDB,
                new Feedback(
                        uploadTime,
                        violationTVFVCnts.first,
                        violationTVFVCnts.second,
                        MithrilDBHelper.getHelper(this).findEveryPolicy(mithrilDB).size()
                )
        );
    }

    private void saveTheDataWeAreUploading(Timestamp uploadTime) {
        //Store the uploaded data in the database
        try {
            JSONRequest feedbackJsonRequest = new JSONRequest(feedbackDataUploaderMap, getUid() + uploadTime);
            MithrilDBHelper.getHelper(this).addUpload(
                    mithrilDB,
                    new Upload(uploadTime,
                            feedbackJsonRequest.getRequest().toString()));
        } catch (JSONException aJSONException) {
            Log.e(MithrilAC.getDebugTag(), "Exception in creating JSON " + aJSONException.getMessage());
        }
    }

    public final class JSONRequest {
        private JSONObject request;

        public JSONRequest(Map<String, Object> feedback, String userId) throws JSONException {
            for (Map.Entry<String, Object> feedbackEntry : feedback.entrySet())
                Log.e(MithrilAC.getDebugTag(), "JSON data: " + feedbackEntry.getValue());
            request = new JSONObject();
            request.put(MithrilAC.getFeedbackQuestion1(), feedback.get(MithrilAC.getFeedbackQuestion1()));
            request.put(MithrilAC.getFeedbackQuestion2(), feedback.get(MithrilAC.getFeedbackQuestion2()));
            request.put(MithrilAC.getFeedbackQuestion3(), feedback.get(MithrilAC.getFeedbackQuestion3()));
            request.put(MithrilAC.getFeedbackQuestion4(), feedback.get(MithrilAC.getFeedbackQuestion4()));
            request.put(MithrilAC.getFeedbackQuestion5(), feedback.get(MithrilAC.getFeedbackQuestion5()));
            request.put(MithrilAC.getFeedbackQuestion6(), feedback.get(MithrilAC.getFeedbackQuestion6()));
            request.put(MithrilAC.getFeedbackQuestion7(), feedback.get(MithrilAC.getFeedbackQuestion7()));
            request.put(MithrilAC.getFeedbackQuestion8(), feedback.get(MithrilAC.getFeedbackQuestion8()));
            request.put(MithrilAC.getFeedbackQuestion9(), feedback.get(MithrilAC.getFeedbackQuestion9()));
            request.put(MithrilAC.getFeedbackQuestion10(), feedback.get(MithrilAC.getFeedbackQuestion10()));
            request.put(MithrilAC.getFeedbackQuestionDataKey(), feedback.get(MithrilAC.getFeedbackQuestionDataKey()));
            request.put(MithrilAC.getFeedbackQuestionDataTimeKey(), System.currentTimeMillis());
            request.put(MithrilAC.getRandomUserId(), userId);
        }

        public JSONObject getRequest() {
            return request;
        }
    }
}