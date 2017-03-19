package edu.umbc.ebiquity.mithril.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ToggleButton;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

public class PermissionAcquisitionActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private ToggleButton mGenericPermToggleButton;
    private ToggleButton mSpecialPermToggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
    }

    @Override
    public void onResume() {
        super.onResume();
        makeFullScreen();
//        testUserAgreementAndLaunchNextActivity();
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

    private void initViews() {
        setContentView(R.layout.activity_permission_acquisition);
        sharedPreferences = getApplicationContext().getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE);

        mGenericPermToggleButton = (ToggleButton) findViewById(R.id.genericPermToggleButton);
        mSpecialPermToggleButton = (ToggleButton) findViewById(R.id.specialPermToggleButton);

        setOnClickListeners();
    }

    private void setOnClickListeners() {
        mGenericPermToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        mSpecialPermToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void testUserAgreementAndLaunchNextActivity() {
        /*
         * If the user has already consented, we just go back tp the CoreActivity, or else we are going to make them uninstall the app!
         */
        sharedPreferences = getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE);
        if (sharedPreferences.contains(MithrilApplication.getPrefKeyUserConsent())) {
            if (sharedPreferences.getBoolean(MithrilApplication.getPrefKeyUserConsent(), false) != false)
                startNextActivity(this, CoreActivity.class);
            else
                PermissionHelper.quitMithril(this, MithrilApplication.MITHRIL_BYE_BYE_MESSAGE);
        }
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