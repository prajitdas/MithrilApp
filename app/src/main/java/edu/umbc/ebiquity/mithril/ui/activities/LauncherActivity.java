package edu.umbc.ebiquity.mithril.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

public class LauncherActivity extends AppCompatActivity {
    private Button mContinueToUserAgreementBtn;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testUserAgreementAndLaunchNextActivity();
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
        makeFullScreen();
        initViews();
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0, 0);
        super.onPause();
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
        sharedPreferences = getSharedPreferences(MithrilAC.getSharedPreferencesName(), Context.MODE_PRIVATE);
        if (PermissionHelper.isAllRequiredPermissionsGranted(this) &&
                !PermissionHelper.needsUsageStatsPermission(this) &&
                !PermissionHelper.needsWriteSettingsPermission(this) &&
                !PermissionHelper.needsSystemAlertWindowPermission(this) &&
                !PermissionHelper.needsRootPrivileges(this)) {
            if (sharedPreferences.contains(MithrilAC.getPrefKeyLocaInstancesCreated()) &&
                    sharedPreferences.contains(MithrilAC.getPrefKeyPresInstancesCreated()) &&
                    sharedPreferences.contains(MithrilAC.getPrefKeyActiInstancesCreated()) &&
                    sharedPreferences.contains(MithrilAC.getPrefKeyTimeInstancesCreated()) &&
                    sharedPreferences.contains(MithrilAC.getAppsInstalled()) &&
                    sharedPreferences.getBoolean(MithrilAC.getPrefKeyLocaInstancesCreated(), false) &&
                    sharedPreferences.getBoolean(MithrilAC.getPrefKeyPresInstancesCreated(), false) &&
                    sharedPreferences.getBoolean(MithrilAC.getPrefKeyActiInstancesCreated(), false) &&
                    sharedPreferences.getBoolean(MithrilAC.getPrefKeyTimeInstancesCreated(), false) &&
                    sharedPreferences.getBoolean(MithrilAC.getAppsInstalled(), false))
                startNextActivity(this, CoreActivity.class);
            else
                startNextActivity(this, InstanceCreationActivity.class);
        } else if (sharedPreferences.contains(MithrilAC.getAppsInstalled()) &&
                sharedPreferences.getBoolean(MithrilAC.getAppsInstalled(), false))
            startNextActivity(this, PermissionAcquisitionActivity.class);
        else if (sharedPreferences.contains(MithrilAC.getPrefKeyUserConsent()) &&
                sharedPreferences.getBoolean(MithrilAC.getPrefKeyUserConsent(), false))
            startNextActivity(this, InstallAppsActivity.class);
        else if (sharedPreferences.contains(MithrilAC.getPrefKeyUserContinueClicked()) &&
                sharedPreferences.getBoolean(MithrilAC.getPrefKeyUserContinueClicked(), false))
            startNextActivity(this, ShowUserAgreementActivity.class);
    }

    private void initViews() {
        setContentView(R.layout.activity_launcher);
        mContinueToUserAgreementBtn = (Button) findViewById(R.id.continueToUserAgreementBtn);

        setOnClickListeners();
    }

    private void setOnClickListeners() {
        mContinueToUserAgreementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences(MithrilAC.getSharedPreferencesName(), Context.MODE_PRIVATE).edit();
                editor.putBoolean(MithrilAC.getPrefKeyUserContinueClicked(), true);
                editor.apply();
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
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(launchNextActivity);
    }
}