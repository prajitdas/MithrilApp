package edu.umbc.ebiquity.mithril.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

public class UserAgreementActivity extends AppCompatActivity {
    private Button mContinueToUserAgreementBtn;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        makeFullScreen();
        testUserAgreementAndLaunchNextActivity();
        initViews();
    }

    private void makeFullScreen() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void testUserAgreementAndLaunchNextActivity() {
        /*
         * If the user has already consented, we just go back tp the CoreActivity
         */
        sharedPreferences = getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE);
        if (sharedPreferences.contains(MithrilApplication.getPrefKeyUserConsent())) {
            if (sharedPreferences.getBoolean(MithrilApplication.getPrefKeyUserConsent(), false) == false)
                PermissionHelper.quitMithril(this, MithrilApplication.MITHRIL_BYE_BYE_MESSAGE);
            else {
                if (sharedPreferences.contains(MithrilApplication.getPrefKeyUserDeniedPermissions())) {
                    if (sharedPreferences.getBoolean(MithrilApplication.getPrefKeyUserDeniedPermissions(), true) != true)
                        startNextActivity(this, CoreActivity.class);
                    else
                        startNextActivity(this, PermissionAcquisitionActivity.class);
                } else
                    startNextActivity(this, ShowUserAgreementActivity.class);
            }
        } else
            startNextActivity(this, ShowUserAgreementActivity.class);
    }

    private void initViews() {
        setContentView(R.layout.activity_user_agreement);
        mContinueToUserAgreementBtn = (Button) findViewById(R.id.continueToUserAgreementBtn);

        setOnClickListeners();
    }

    private void setOnClickListeners() {
        mContinueToUserAgreementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNextActivity(v.getContext(), ShowUserAgreementActivity.class);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish(); // quit app
    }

    private void startNextActivity(Context context, Class activityClass) {
        Intent launchNextActivity = new Intent(context, activityClass);
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(launchNextActivity);
    }
}