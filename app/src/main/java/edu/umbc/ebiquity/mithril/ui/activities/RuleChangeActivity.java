package edu.umbc.ebiquity.mithril.ui.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;

public class RuleChangeActivity extends AppCompatActivity {
    private MithrilDBHelper mithrilDBHelper;
    private SQLiteDatabase mithrilDB;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_change);

        initDB(this);
    }

    @Override
    public void onDestroy() {
        closeDB();
        super.onDestroy();
    }

    private void initDB(Context context) {
        // Let's get the DB instances loaded too
        mithrilDBHelper = MithrilDBHelper.getHelper(context);
        mithrilDB = mithrilDBHelper.getWritableDatabase();
    }

    private void closeDB() {
        if (mithrilDB != null)
            mithrilDB.close();
    }
}