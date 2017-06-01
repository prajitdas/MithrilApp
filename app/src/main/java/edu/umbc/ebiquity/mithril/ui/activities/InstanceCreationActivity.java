package edu.umbc.ebiquity.mithril.ui.activities;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.ui.fragments.coreactivityfragments.ViolationFragment;
import edu.umbc.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

public class InstanceCreationActivity extends AppCompatActivity {
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

    private void initViews() {
        setContentView(R.layout.activity_instance_creation);

        navigation = (BottomNavigationView) findViewById(R.id.navigation_menu);
        mTextMessage = (TextView) findViewById(R.id.message);

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
                        loadSemanticLocationFragment();
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

    private void loadSemanticLocationFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_instance_creation, new ViolationFragment())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_instance_creation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.done_with_instances_settings)
            startNextActivity(this, CoreActivity.class);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0, 0);
        super.onPause();
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