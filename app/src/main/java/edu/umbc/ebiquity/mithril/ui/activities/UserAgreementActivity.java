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
        testUserAgreementAndLaunchCoreActivity();
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

    private void testUserAgreementAndLaunchCoreActivity() {
        /**
         * If the user has already consented, we just go back tp the CoreActivity, or else we are going to make them uninstall the app!
         */
        sharedPreferences = getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE);
        if (sharedPreferences.contains(MithrilApplication.getPrefKeyUserConsent())) {
            if (sharedPreferences.getBoolean(MithrilApplication.getPrefKeyUserConsent(), false) != false)
                startActivity(new Intent(this, CoreActivity.class));
            else
                PermissionHelper.quitMithril(this, "You disagreed with the agreement, please uninstall app...");
        }
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
                startActivity(new Intent(v.getContext(), ShowUserAgreementActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}