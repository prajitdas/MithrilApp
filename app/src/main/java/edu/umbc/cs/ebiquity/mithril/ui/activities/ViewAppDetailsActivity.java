package edu.umbc.cs.ebiquity.mithril.ui.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.umbc.cs.ebiquity.mithril.R;
import edu.umbc.cs.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.cs.ebiquity.mithril.ui.adapters.AppPermListAdapter;

public class ViewAppDetailsActivity extends ListActivity {
    private PackageManager packageManager;
    private List<PermissionInfo> appPermList;
    private AppPermListAdapter appPermListAdapter;
    private ImageButton mImgBtnLaunchApp;
    private ImageButton mImgBtnAppIsGood;
    private ImageButton mImgBtnAppIsBad;
    private String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * ListActivity has a default layout that consists of a single, full-screen list in the center of the screen.
         * However, if you desire, you can customize the screen layout by setting your own view layout with setContentView() in onCreate().
         * To do this, your own view MUST contain a ListView object with the id "@android:id/list" (or list if it's in code)
         */
        setContentView(R.layout.activity_view_app_details);

        initView();
    }

    private void initView() {
        packageManager = getApplicationContext().getPackageManager();

        mImgBtnLaunchApp = (ImageButton) findViewById(R.id.launch_app_btn);
        mImgBtnAppIsGood = (ImageButton) findViewById(R.id.app_is_good_btn);
        mImgBtnAppIsBad = (ImageButton) findViewById(R.id.app_is_bad_btn);

        MithrilDBHelper dbHelper = new MithrilDBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        setAppPermList(dbHelper.getAppPermissions());

        appPermListAdapter = new AppPermListAdapter(ViewAppDetailsActivity.this, R.layout.app_list_item, getAppPermList());
        // Bind to new adapter.
        if (appPermListAdapter.getCount() == 0)
            appPermListAdapter = null;
        setListAdapter(appPermListAdapter);

        setOnClickListeners();
    }

    private void setOnClickListeners() {
        mImgBtnLaunchApp.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    Intent intent = packageManager.getLaunchIntentForPackage(packageName);
                    if (intent == null) {
                        throw new NameNotFoundException();
                    }
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    startActivity(intent);
                } catch (NameNotFoundException e) {
                    Toast.makeText(
                            v.getContext(),
                            "The application " + packageName + " was not found! Possibly due to an exception: " + e.getMessage(),
                            Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        mImgBtnAppIsGood.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(
                        v.getContext(),
                        "Thanks for your feedback on app: " + packageName,
                        Toast.LENGTH_LONG)
                        .show();
            }
        });

        mImgBtnAppIsBad.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(
                        v.getContext(),
                        "Thanks for your feedback on app: " + packageName,
                        Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    public List<PermissionInfo> getAppPermList() {
        return appPermList;
    }

    public void setAppPermList(PermissionInfo[] requestedPermissions) {
        appPermList = new ArrayList<PermissionInfo>();
        for(int index = 0; index < requestedPermissions.length; index++)
            appPermList.add(requestedPermissions[index]);
    }
}