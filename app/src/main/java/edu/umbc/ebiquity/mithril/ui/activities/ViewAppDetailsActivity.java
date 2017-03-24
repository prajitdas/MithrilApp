package edu.umbc.ebiquity.mithril.ui.activities;

import android.app.FragmentManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.model.components.PermData;
import edu.umbc.ebiquity.mithril.ui.fragments.viewappdetailsactivityfragments.AppDetailFragment;

public class ViewAppDetailsActivity extends AppCompatActivity
        implements AppDetailFragment.OnListFragmentInteractionListener {
    private PackageManager packageManager;
    //    private ImageButton mImgBtnLaunchApp;
//    private ImageButton mImgBtnAppIsGood;
//    private ImageButton mImgBtnAppIsBad;
    private TextView mTxtViewAppName;
    private String packageName;
    private Toolbar toolbar;

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
        toolbar = (Toolbar) findViewById(R.id.toolbar_view_app_details);
        setSupportActionBar(toolbar);

        packageManager = getApplicationContext().getPackageManager();

        packageName = getIntent().getStringExtra(MithrilApplication.getPrefKeyAppPkgName());

//        mImgBtnLaunchApp = (ImageButton) findViewById(R.id.launch_app_btn);
//        mImgBtnAppIsGood = (ImageButton) findViewById(R.id.app_is_good_btn);
//        mImgBtnAppIsBad = (ImageButton) findViewById(R.id.app_is_bad_btn);

        mTxtViewAppName = (TextView) findViewById(R.id.textViewAppDetails);
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, 0);
        } catch (final NameNotFoundException e) {
            applicationInfo = null;
        }
        mTxtViewAppName.setText(
                (
                        applicationInfo !=
                                null ? packageManager.getApplicationLabel(applicationInfo).toString() : MithrilApplication.getPermissionProtectionLevelUnknown()
                ) +
                        " (" +
                        packageName +
                        ")"
        );

        loadViewAppDetailsFragment();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setOnClickListeners();
    }

    /**
     * Add the fragment that will allow us to show and interact with the permissions for the current app
     */
    private void loadViewAppDetailsFragment() {
        Bundle data = new Bundle();
        data.putString(MithrilApplication.getPrefKeyAppPkgName(), packageName);

        AppDetailFragment appDetailFragment = new AppDetailFragment();
        appDetailFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_app_details, appDetailFragment)
                .commit();
    }

    private void setOnClickListeners() {
//        mImgBtnLaunchApp.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Intent intent = packageManager.getLaunchIntentForPackage(packageName);
//                if (intent != null) {
//                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(
//                            v.getContext(),
//                            "The application " + packageName + " was not launchable! Possibly because it's not meant to be!",
//                            Toast.LENGTH_LONG)
//                            .show();
//                }
//            }
//        });
//
//        mImgBtnAppIsGood.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(
//                        v.getContext(),
//                        "Thanks for your feedback on app: " + packageName + " but right now I will be doing nothing with it. Sorry!",
//                        Toast.LENGTH_SHORT)
//                        .show();
//            }
//        });
//
//        mImgBtnAppIsBad.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(
//                        v.getContext(),
//                        "Thanks for your feedback on app: " + packageName + " but right now I will be doing nothing with it. Sorry!",
//                        Toast.LENGTH_SHORT)
//                        .show();
//            }
//        });
    }

    @Override
    public void onListFragmentInteraction(PermData item) {
        //TODO Do something with the perm selected
    }
}