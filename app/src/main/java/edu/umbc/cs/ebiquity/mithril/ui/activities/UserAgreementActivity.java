package edu.umbc.cs.ebiquity.mithril.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.R;
import edu.umbc.cs.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;

public class UserAgreementActivity extends AppCompatActivity {
//    private final Handler handler = new Handler();
    private Button mShowUserAgreementBtn;
    private Button mIAgreeBtn;
    private Button mIDisagreeBtn;
    private SharedPreferences sharedPreferences;
    private boolean isResultOkay = false;
    private MithrilDBHelper mithrilDBHelper;
    private SQLiteDatabase mithrilDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ifUserConsentedGoBackToMainApp();
        initViews();
    }

    private void initViews() {
        setContentView(R.layout.activity_user_agreement);
        mShowUserAgreementBtn = (Button) findViewById(R.id.showUserAgreementBtn);
        mIAgreeBtn = (Button) findViewById(R.id.iAgreeBtn);
        mIDisagreeBtn = (Button) findViewById(R.id.iDisagreeBtn);

        if (!isResultOkay)
            mIAgreeBtn.setVisibility(View.GONE);
        else
            mIAgreeBtn.setVisibility(View.VISIBLE);

        makeFullScreen();
        setOnClickListeners();
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

    private void setOnClickListeners() {
        mShowUserAgreementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showUserAgreementIntent = new Intent(v.getContext(), ShowUserAgreementActivity.class);
                startActivityForResult(showUserAgreementIntent, MithrilApplication.ACTIVITY_RESULT_CODE_USER_AGREEMENT_READ);
            }
        });

        mIAgreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = v.getContext().getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE).edit();
                editor.putString(MithrilApplication.getPrefKeyUserConsent(), "agreed");
                editor.commit();
                // User has agreed, ask for the other permissions
                initHousekeepingTasks();
            }
        });

        mIDisagreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri packageUri = Uri.parse("package:" + MithrilApplication.APP_PACKAGE_NAME_SELF);
                Intent uninstallIntent =
                        new Intent(Intent.ACTION_DELETE, packageUri);
                startActivity(uninstallIntent);
//                The following line should be unreachable.
//                resultCanceled();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MithrilApplication.ACTIVITY_RESULT_CODE_USER_AGREEMENT_READ) {
            /**
             * Do nothing in this case!
             * Some failure occurred obviously
             */
            isResultOkay = resultCode == Activity.RESULT_OK;
        }
    }

    private void ifUserConsentedGoBackToMainApp() {
        /**
         * If the user has already consented, we just go back tp the MainActivity, or else we are stuck here!
         */
        sharedPreferences = getApplicationContext().getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE);
        if (sharedPreferences.getString(MithrilApplication.getPrefKeyUserConsent(), null) != null)
            finish();
    }

    private void resultOkay() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void initHousekeepingTasks() {
        /**
         * Initiate database creation and default data insertion, happens only once.
         */
        mithrilDBHelper = new MithrilDBHelper(this);
        mithrilDB = mithrilDBHelper.getWritableDatabase();

        //And close this instance
        mithrilDB.close();

        resultOkay();
    }

//    Potentially will never be used. Left for future purposes.
//    private void resultCanceled() {
//        Intent returnIntent = new Intent();
//        setResult(Activity.RESULT_CANCELED, returnIntent);
//        finish();
//    }

    /*
    Following are permission request and handling sample code
        if (PermissionHelper.isExplicitPermissionAcquisitionNecessary()) {
            PermissionHelper.requestPermissionIfAllowed(this, Manifest.permission.PACKAGE_USAGE_STATS, MithrilApplication.PERMISSION_REQUEST_CODE_PACKAGE_USAGE_STATS);
            PermissionHelper.requestPermissionIfAllowed(this, Manifest.permission.ACCESS_FINE_LOCATION, MithrilApplication.PERMISSION_REQUEST_CODE_ACCESS_FINE_LOCATION);
            PermissionHelper.requestPermissionIfAllowed(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, MithrilApplication.PERMISSION_REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
            PermissionHelper.requestPermissionIfAllowed(this, Manifest.permission.READ_EXTERNAL_STORAGE, MithrilApplication.PERMISSION_REQUEST_CODE_READ_EXTERNAL_STORAGE);
            PermissionHelper.requestPermissionIfAllowed(this, Manifest.permission.RECEIVE_BOOT_COMPLETED, MithrilApplication.PERMISSION_REQUEST_CODE_RECEIVE_BOOT_COMPLETED);
            PermissionHelper.requestPermissionIfAllowed(this, Manifest.permission.INTERNET, MithrilApplication.PERMISSION_REQUEST_CODE_INTERNET);
            PermissionHelper.requestPermissionIfAllowed(this, Manifest.permission.NFC, MithrilApplication.PERMISSION_REQUEST_CODE_NFC);
            PermissionHelper.requestPermissionIfAllowed(this, Manifest.permission.GET_TASKS, MithrilApplication.PERMISSION_REQUEST_CODE_GET_TASKS);
            PermissionHelper.requestPermissionIfAllowed(this, Manifest.permission.READ_LOGS, MithrilApplication.PERMISSION_REQUEST_CODE_READ_LOGS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MithrilApplication.ALL_PERMISSIONS_MITHRIL_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length < 0
                        && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "You denied some permissions. This might disrupt some functionality!", Toast.LENGTH_SHORT).show();
                    resultCanceled();
                } else {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
     */
}