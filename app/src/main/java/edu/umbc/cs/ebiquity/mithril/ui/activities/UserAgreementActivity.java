package edu.umbc.cs.ebiquity.mithril.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.R;

public class UserAgreementActivity extends AppCompatActivity {
    private Button mShowUserAgreementBtn;
    private Button mIAgreeBtn;
    private Button mIDisagreeBtn;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * If the user has already consented, we just go to the MainActivity, or else we are stuck here!
         */
        sharedPreferences = getApplicationContext().getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE);
        if (sharedPreferences.getString(MithrilApplication.getPrefKeyUserConsent(), null) != null)
            userAgreesStartMainApp();
    }

    @Override
    public void onResume() {
        super.onResume();
        initViews();
    }

    private void initViews() {
        setContentView(R.layout.activity_user_agreement);
        mShowUserAgreementBtn = (Button) findViewById(R.id.showUserAgreementBtn);
        mIAgreeBtn = (Button) findViewById(R.id.iAgreeBtn);
        mIDisagreeBtn = (Button) findViewById(R.id.iDisagreeBtn);

        mIAgreeBtn.setVisibility(View.GONE);

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

                SharedPreferences.Editor editor = v.getContext().getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE).edit();
                editor.putString(MithrilApplication.getPrefKeyUserConsent(), "agreed");
                editor.commit();
                userAgreesStartMainApp();
            }
        });

        mIDisagreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri packageUri = Uri.parse("package:" + MithrilApplication.APP_PACKAGE_NAME_SELF);
                Intent uninstallIntent =
                        new Intent(Intent.ACTION_DELETE, packageUri);
                startActivity(uninstallIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MithrilApplication.USER_AGREEMENT_READ_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                mIAgreeBtn.setVisibility(View.VISIBLE);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                /**
                 * Do nothng in this case!
                 * Some failure occurred obviously
                 */
            }
        }
    }

    private void userAgreesStartMainApp() {
        Intent startMainActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(startMainActivity);
    }
}