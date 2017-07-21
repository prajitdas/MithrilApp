package edu.umbc.ebiquity.mithril.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

public class InstallAppsActivity extends AppCompatActivity {
    private Button mQuitAppBtn;
    private Button mDoneBtn;
    private Button mInstallApp1Btn;
    private Button mInstallApp2Btn;
    private Button mInstallApp3Btn;
    private Button mInstallApp4Btn;
    private Button mInstallApp5Btn;
    private Button mInstallApp6Btn;
    private Button mInstallApp7Btn;
    private Button mInstallApp8Btn;
    private Button mInstallApp9Btn;
    private Button mInstallApp10Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testAppInstalledAndLaunchNextActivity();
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

    private void testAppInstalledAndLaunchNextActivity() {
        if (areRequiredAppsInstalled()) {
            SharedPreferences.Editor editor = getSharedPreferences(MithrilAC.getSharedPreferencesName(), Context.MODE_PRIVATE).edit();
            editor.putBoolean(MithrilAC.getAppsInstalled(), true);
            editor.apply();
            // Apps installed, ask for permissions
            startNextActivity(this, PermissionAcquisitionActivity.class);
        }
    }

    private void initViews() {
        setContentView(R.layout.activity_install_apps);
        mQuitAppBtn = (Button) findViewById(R.id.quitAppButtonInInstallApps);

        mDoneBtn = (Button) findViewById(R.id.doneButtonInstallApps);

        mInstallApp1Btn = (Button) findViewById(R.id.installApp1Button);
        if (!isAppInstalled(MithrilAC.getAppPackageNames().get(0)))
            mInstallApp1Btn.setText(MithrilAC.getAppNames().get(0));
        else
            mInstallApp1Btn.setVisibility(View.GONE);

        mInstallApp2Btn = (Button) findViewById(R.id.installApp2Button);
        if (!isAppInstalled(MithrilAC.getAppPackageNames().get(1)))
            mInstallApp2Btn.setText(MithrilAC.getAppNames().get(1));
        else
            mInstallApp2Btn.setVisibility(View.GONE);

        mInstallApp3Btn = (Button) findViewById(R.id.installApp3Button);
        if (!isAppInstalled(MithrilAC.getAppPackageNames().get(2)))
            mInstallApp3Btn.setText(MithrilAC.getAppNames().get(2));
        else
            mInstallApp3Btn.setVisibility(View.GONE);

        mInstallApp4Btn = (Button) findViewById(R.id.installApp4Button);
        if (!isAppInstalled(MithrilAC.getAppPackageNames().get(3)))
            mInstallApp4Btn.setText(MithrilAC.getAppNames().get(3));
        else
            mInstallApp4Btn.setVisibility(View.GONE);

        mInstallApp5Btn = (Button) findViewById(R.id.installApp5Button);
        if (!isAppInstalled(MithrilAC.getAppPackageNames().get(4)))
            mInstallApp5Btn.setText(MithrilAC.getAppNames().get(4));
        else
            mInstallApp5Btn.setVisibility(View.GONE);

        mInstallApp6Btn = (Button) findViewById(R.id.installApp6Button);
        if (!isAppInstalled(MithrilAC.getAppPackageNames().get(5)))
            mInstallApp6Btn.setText(MithrilAC.getAppNames().get(5));
        else
            mInstallApp6Btn.setVisibility(View.GONE);

        mInstallApp7Btn = (Button) findViewById(R.id.installApp7Button);
        if (!isAppInstalled(MithrilAC.getAppPackageNames().get(6)))
            mInstallApp7Btn.setText(MithrilAC.getAppNames().get(6));
        else
            mInstallApp7Btn.setVisibility(View.GONE);

        mInstallApp8Btn = (Button) findViewById(R.id.installApp8Button);
        if (!isAppInstalled(MithrilAC.getAppPackageNames().get(7)))
            mInstallApp8Btn.setText(MithrilAC.getAppNames().get(7));
        else
            mInstallApp8Btn.setVisibility(View.GONE);

        mInstallApp9Btn = (Button) findViewById(R.id.installApp9Button);
        if (!isAppInstalled(MithrilAC.getAppPackageNames().get(8)))
            mInstallApp9Btn.setText(MithrilAC.getAppNames().get(8));
        else
            mInstallApp9Btn.setVisibility(View.GONE);

        mInstallApp10Btn = (Button) findViewById(R.id.installApp10Button);
        if (!isAppInstalled(MithrilAC.getAppPackageNames().get(9)))
            mInstallApp10Btn.setText(MithrilAC.getAppNames().get(9));
        else
            mInstallApp10Btn.setVisibility(View.GONE);

        setOnClickListeners();
    }

    private void setOnClickListeners() {
        mQuitAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionHelper.quitMithril(v.getContext(), MithrilAC.MITHRIL_BYE_BYE_MESSAGE);
            }
        });

        mDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testAppInstalledAndLaunchNextActivity();
            }
        });

        mInstallApp1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + MithrilAC.getAppPackageNames().get(0)));
                startActivityForResult(intent, MithrilAC.APP_INSTALL_REQUEST_CODES_1);
            }
        });

        mInstallApp2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + MithrilAC.getAppPackageNames().get(1)));
                startActivityForResult(intent, MithrilAC.APP_INSTALL_REQUEST_CODES_1);
            }
        });

        mInstallApp3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + MithrilAC.getAppPackageNames().get(2)));
                startActivityForResult(intent, MithrilAC.APP_INSTALL_REQUEST_CODES_1);
            }
        });

        mInstallApp4Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + MithrilAC.getAppPackageNames().get(3)));
                startActivityForResult(intent, MithrilAC.APP_INSTALL_REQUEST_CODES_1);
            }
        });

        mInstallApp5Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + MithrilAC.getAppPackageNames().get(4)));
                startActivityForResult(intent, MithrilAC.APP_INSTALL_REQUEST_CODES_1);
            }
        });

        mInstallApp6Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + MithrilAC.getAppPackageNames().get(5)));
                startActivityForResult(intent, MithrilAC.APP_INSTALL_REQUEST_CODES_1);
            }
        });

        mInstallApp7Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + MithrilAC.getAppPackageNames().get(6)));
                startActivityForResult(intent, MithrilAC.APP_INSTALL_REQUEST_CODES_1);
            }
        });

        mInstallApp8Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + MithrilAC.getAppPackageNames().get(7)));
                startActivityForResult(intent, MithrilAC.APP_INSTALL_REQUEST_CODES_1);
            }
        });

        mInstallApp9Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + MithrilAC.getAppPackageNames().get(8)));
                startActivityForResult(intent, MithrilAC.APP_INSTALL_REQUEST_CODES_1);
            }
        });

        mInstallApp10Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("market://details?id=" + MithrilAC.getAppPackageNames().get(9)));
                startActivityForResult(intent, MithrilAC.APP_INSTALL_REQUEST_CODES_1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MithrilAC.APP_INSTALL_REQUEST_CODES_1: {
                if (resultCode == Activity.RESULT_OK)
                    if (isAppInstalled(MithrilAC.getAppPackageNames().get(0)))
                        mInstallApp1Btn.setVisibility(View.GONE);
                break;
            }
            case MithrilAC.APP_INSTALL_REQUEST_CODES_2: {
                if (resultCode == Activity.RESULT_OK)
                    if (isAppInstalled(MithrilAC.getAppPackageNames().get(1)))
                        mInstallApp2Btn.setVisibility(View.GONE);
                break;
            }
            case MithrilAC.APP_INSTALL_REQUEST_CODES_3: {
                if (resultCode == Activity.RESULT_OK)
                    if (isAppInstalled(MithrilAC.getAppPackageNames().get(2)))
                        mInstallApp3Btn.setVisibility(View.GONE);
                break;
            }
            case MithrilAC.APP_INSTALL_REQUEST_CODES_4: {
                if (resultCode == Activity.RESULT_OK)
                    if (isAppInstalled(MithrilAC.getAppPackageNames().get(3)))
                        mInstallApp4Btn.setVisibility(View.GONE);
                break;
            }
            case MithrilAC.APP_INSTALL_REQUEST_CODES_5: {
                if (resultCode == Activity.RESULT_OK)
                    if (isAppInstalled(MithrilAC.getAppPackageNames().get(4)))
                        mInstallApp5Btn.setVisibility(View.GONE);
                break;
            }
            case MithrilAC.APP_INSTALL_REQUEST_CODES_6: {
                if (resultCode == Activity.RESULT_OK)
                    if (isAppInstalled(MithrilAC.getAppPackageNames().get(5)))
                        mInstallApp6Btn.setVisibility(View.GONE);
                break;
            }
            case MithrilAC.APP_INSTALL_REQUEST_CODES_7: {
                if (resultCode == Activity.RESULT_OK)
                    if (isAppInstalled(MithrilAC.getAppPackageNames().get(6)))
                        mInstallApp7Btn.setVisibility(View.GONE);
                break;
            }
            case MithrilAC.APP_INSTALL_REQUEST_CODES_8: {
                if (resultCode == Activity.RESULT_OK)
                    if (isAppInstalled(MithrilAC.getAppPackageNames().get(7)))
                        mInstallApp8Btn.setVisibility(View.GONE);
                break;
            }
            case MithrilAC.APP_INSTALL_REQUEST_CODES_9: {
                if (resultCode == Activity.RESULT_OK)
                    if (isAppInstalled(MithrilAC.getAppPackageNames().get(8)))
                        mInstallApp9Btn.setVisibility(View.GONE);
                break;
            }
            case MithrilAC.APP_INSTALL_REQUEST_CODES_10: {
                if (resultCode == Activity.RESULT_OK)
                    if (isAppInstalled(MithrilAC.getAppPackageNames().get(9)))
                        mInstallApp10Btn.setVisibility(View.GONE);
                break;
            }
        }
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

    public boolean areRequiredAppsInstalled() {
        for (String appPkgName : MithrilAC.getAppPackageNames())
            if (!isAppInstalled(appPkgName))
                return false;
        return true;
    }

    public boolean isAppInstalled(String pkgName) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(pkgName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}