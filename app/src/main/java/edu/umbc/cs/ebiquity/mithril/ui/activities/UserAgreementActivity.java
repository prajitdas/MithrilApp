package edu.umbc.cs.ebiquity.mithril.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.R;
import edu.umbc.cs.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.cs.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

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
        ifUserAgreesGoBackToMainApp();
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
                startActivityForResult(showUserAgreementIntent, MithrilApplication.USER_AGREEMENT_READ_REQUEST_CODE);
            }
        });

        mIAgreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Copy the agreement file to the external files directory. The user needs a copy of the agreement.
                File parent = getExternalFilesDir(null);
                String child = MithrilApplication.getFlierPdfFileName();
                copyAssets(parent, child);

                Toast.makeText(v.getContext(),
                        "Thanks! The agreement file has been copied to the "+parent.getAbsolutePath()+" directory, for your reference...",
                        Toast.LENGTH_LONG)
                        .show();

                SharedPreferences.Editor editor = v.getContext().getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE).edit();
                editor.putString(MithrilApplication.getPrefKeyUserConsent(), "agreed");
                editor.commit();
                // User has agreed, ask for the other permissions
                resultOkay();
            }
        });

        mIDisagreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri packageUri = Uri.parse("package:" + MithrilApplication.APP_PACKAGE_NAME_SELF);
                Intent uninstallIntent =
                        new Intent(Intent.ACTION_DELETE, packageUri);
                startActivity(uninstallIntent);
                //The following line should be unreachable
//                resultCanceled();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MithrilApplication.USER_AGREEMENT_READ_REQUEST_CODE) {
            /**
             * Do nothng in this case!
             * Some failure occurred obviously
             */isResultOkay = resultCode == Activity.RESULT_OK;
        }
    }

    private void ifUserAgreesGoBackToMainApp() {
        /**
         * If the user has already consented, we just go back tp the MainActivity, or else we are stuck here!
         */
        sharedPreferences = getApplicationContext().getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE);
        if (sharedPreferences.getString(MithrilApplication.getPrefKeyUserConsent(), null) != null)
            finish();
//            Intent startMainActivity = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(startMainActivity);
    }

    private void resultOkay() {
        initHousekeepingTasks();

//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //Do something after 100ms
//                //TODO show a window with a visual way os showing progress or better yet do the initHousekeepingTasks in a AsyncTask
//            }
//        }, 1000);

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void resultCanceled() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    private void initHousekeepingTasks() {
        if (PermissionHelper.isExplicitPermissionAcquisitionNecessary()) {
            PermissionHelper.requestAllNecessaryPermissions(this);
        }
        /**
         * Initiate database creation and default data insertion, happens only once.
         */
        mithrilDBHelper = new MithrilDBHelper(this);
        mithrilDB = mithrilDBHelper.getWritableDatabase();
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
//                } else {
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
                }
//                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void copyAssets(File parent, String child) {
        File file = new File(parent, child);

        if (!file.exists()) {
            try {
                writeFile(openFileOutput(file.getName(), Context.MODE_PRIVATE));
            } catch (FileNotFoundException e) {
                Log.e(MithrilApplication.getDebugTag(), e.getMessage());
            }
        } else {
            Log.d(MithrilApplication.getDebugTag(), "file already exists");
        }
    }

    private void writeFile(OutputStream destination) {
        AssetManager assetManager = getAssets();
        InputStream in = null;
        OutputStream out = destination;
        try {
            in = assetManager.open(MithrilApplication.getFlierPdfFileName());

            copyFile(in, out);

            in.close();
            out.flush();
            out.close();
        } catch (IOException iOException) {
            Log.e(MithrilApplication.getDebugTag(), iOException.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.d(MithrilApplication.getDebugTag(), "Filer file threw NullPointerException");
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.d(MithrilApplication.getDebugTag(), "output file threw NullPointerException");
                }
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}