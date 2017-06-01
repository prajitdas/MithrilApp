package edu.umbc.ebiquity.mithril.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

public class InstanceCreationActivity extends AppCompatActivity {
    private SharedPreferences.Editor editor;
    private BottomNavigationView navigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    private TextView mTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
        initViews();
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0, 0);
        super.onPause();
    }

    private void initViews() {
        setContentView(R.layout.activity_instance_creation);
        editor = getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE).edit();

        navigation = (BottomNavigationView) findViewById(R.id.navigation_menu);
        mTextMessage = (TextView) findViewById(R.id.message);

        setOnClickListeners();
    }

    private void setOnClickListeners() {
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_location:
                        mTextMessage.setText(R.string.text_instance_creation_location);
                        mTextMessage.setOnHoverListener(new View.OnHoverListener() {
                            @Override
                            public boolean onHover(View v, MotionEvent event) {
                                PermissionHelper.toast(v.getContext(), v.getContext().getResources().getString(R.string.tooltip_location));
                                return false;
                            }
                        });
                        return true;
                    case R.id.navigation_temporal:
                        mTextMessage.setText(R.string.text_instance_creation_temporal);
                        mTextMessage.setOnHoverListener(new View.OnHoverListener() {
                            @Override
                            public boolean onHover(View v, MotionEvent event) {
                                PermissionHelper.toast(v.getContext(), v.getContext().getResources().getString(R.string.tooltip_temporal));
                                return false;
                            }
                        });

                        return true;
                    case R.id.navigation_presence_related:
                        mTextMessage.setText(R.string.text_instance_creation_presence_related);
                        mTextMessage.setOnHoverListener(new View.OnHoverListener() {
                            @Override
                            public boolean onHover(View v, MotionEvent event) {
                                PermissionHelper.toast(v.getContext(), v.getContext().getResources().getString(R.string.tooltip_presence_related));
                                return false;
                            }
                        });
                        return true;
                }
                return false;
            }
        });
    }

    private void isInitialSetupTime() {
        startNextActivity(this, CoreActivity.class);
    }

    private boolean isPermissionAcquisitionComplete() {
        return PermissionHelper.isAllRequiredPermissionsGranted(this) && !PermissionHelper.needsUsageStatsPermission(this);
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