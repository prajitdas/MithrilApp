package edu.umbc.ebiquity.mithril.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions.SemanticInconsistencyException;

public class DownloadPoliciesActivity extends AppCompatActivity {
    private static final int POLICIESLOADED = 0;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(0, 0);
        makeFullScreen();
        handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                if (message.what == POLICIESLOADED) {
                    SharedPreferences.Editor editor = getSharedPreferences(MithrilAC.getSharedPreferencesName(), Context.MODE_PRIVATE).edit();
                    editor.putBoolean(MithrilAC.getPrefKeyPoliciesDownloaded(), true);
                    editor.apply();
                    startNextActivity(getApplicationContext(), CoreActivity.class);
                }
            }
        };
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

    private void initViews() {
        setContentView(R.layout.activity_download_policies);

        // Start lengthy operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                try {
                    // We have it here so that we can just load the animation running first time the db instances are loaded
                    SQLiteDatabase mithrilDB = MithrilDBHelper.getHelper(getApplicationContext()).getWritableDatabase();
                    /**
                     * This is where we will download policy data from the server but for now we will simply load the database with the policies manually
                     */
                    MithrilDBHelper.getHelper(getApplicationContext()).loadPoliciesForApps(mithrilDB);
                    if (mithrilDB != null)
                        mithrilDB.close();
                } catch (SQLException e) {
                    Log.e(MithrilAC.getDebugTag(), "Must have already inserted the policy!" + e.getMessage());
                } catch (SemanticInconsistencyException e) {
                    Log.e(MithrilAC.getDebugTag(), "Semantic inconsistency! " +
                            "We somehow created a policy with conflicting decisions for different contexts" + e.getMessage());
                }
                handler.sendEmptyMessageDelayed(POLICIESLOADED, MithrilAC.getMillisecondsPerSecond() * 5);
            }
        }).start();
    }

    private void startNextActivity(Context context, Class activityClass) {
        Intent launchNextActivity = new Intent(context, activityClass);
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(launchNextActivity);
    }
}