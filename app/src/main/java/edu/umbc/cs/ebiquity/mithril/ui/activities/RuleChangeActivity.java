package edu.umbc.cs.ebiquity.mithril.ui.activities;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.R;
import edu.umbc.cs.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;

public class RuleChangeActivity extends AppCompatActivity {
    private MithrilDBHelper mithrilDBHelper;
    private SQLiteDatabase mithrilDB;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_change);
    }

    @Override
    public void onDestroy() {
        closeDB();
        super.onDestroy();
    }

    private void initDB() {
        try {
            // Let's get the DB instances loaded too
            mithrilDBHelper = new MithrilDBHelper(this);
            mithrilDB = mithrilDBHelper.getWritableDatabase();
        } catch (NullPointerException e) {
            Log.d(MithrilApplication.getDebugTag(), e.getMessage());
        }
    }

    private void closeDB() {
        if(mithrilDB != null)
            mithrilDB.close();
    }
}