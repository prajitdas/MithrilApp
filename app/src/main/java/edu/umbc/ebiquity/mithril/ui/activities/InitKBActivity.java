package edu.umbc.ebiquity.mithril.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;

public class InitKBActivity extends AppCompatActivity {
    private static final int KBLOADED = 0;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(0, 0);
        makeFullScreen();
        handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                if(message.what == KBLOADED) {
                    startNextActivity(getApplicationContext(), InstanceCreationActivity.class);
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
        setContentView(R.layout.activity_load_kb);

        // Start lengthy operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                MithrilDBHelper mithrilDBHelper;
                SQLiteDatabase mithrilDB;
                // We have it here so that we can just load the animation running first time the db instances are loaded
                mithrilDBHelper = MithrilDBHelper.getHelper(getApplicationContext());
                mithrilDB = mithrilDBHelper.getWritableDatabase();
                if (mithrilDB != null)
                    mithrilDB.close();
                handler.sendEmptyMessage(KBLOADED);
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