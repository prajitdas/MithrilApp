package edu.umbc.ebiquity.mithril.ui.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.data.model.rules.AppUsageStats;
import edu.umbc.ebiquity.mithril.data.model.rules.Violation;
import edu.umbc.ebiquity.mithril.data.model.components.AppData;
import edu.umbc.ebiquity.mithril.data.model.components.BCastRecvData;
import edu.umbc.ebiquity.mithril.data.model.components.ContentProvData;
import edu.umbc.ebiquity.mithril.data.model.components.ServData;
import edu.umbc.ebiquity.mithril.ui.fragments.EmptyFragment;
import edu.umbc.ebiquity.mithril.ui.fragments.NothingHereFragment;
import edu.umbc.ebiquity.mithril.ui.fragments.coreactivityfragments.AboutFragment;
import edu.umbc.ebiquity.mithril.ui.fragments.coreactivityfragments.AppsFragment;
import edu.umbc.ebiquity.mithril.ui.fragments.coreactivityfragments.BroadcastReceiversFragment;
import edu.umbc.ebiquity.mithril.ui.fragments.coreactivityfragments.ContentProvidersFragment;
import edu.umbc.ebiquity.mithril.ui.fragments.coreactivityfragments.PermissionsFragment;
import edu.umbc.ebiquity.mithril.ui.fragments.coreactivityfragments.ServicesFragment;
import edu.umbc.ebiquity.mithril.ui.fragments.coreactivityfragments.UsageStatsFragment;
import edu.umbc.ebiquity.mithril.ui.fragments.coreactivityfragments.ViolationFragment;
import edu.umbc.ebiquity.mithril.util.services.AppLaunchDetectorService;
import edu.umbc.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

public class CoreActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AppsFragment.OnListFragmentInteractionListener,
        AppsFragment.OnListFragmentLongInteractionListener,
        PermissionsFragment.OnListFragmentInteractionListener,
        BroadcastReceiversFragment.OnListFragmentInteractionListener,
        ContentProvidersFragment.OnListFragmentInteractionListener,
        ServicesFragment.OnListFragmentInteractionListener,
        AboutFragment.OnFragmentInteractionListener,
        ViolationFragment.OnListFragmentInteractionListener,
        EmptyFragment.OnFragmentInteractionListener,
        NothingHereFragment.OnFragmentInteractionListener,
        UsageStatsFragment.OnListFragmentInteractionListener {
    private final File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    private final String agreementFile = MithrilApplication.getFlierPdfFileName();

    private Boolean exit = false;
    private MithrilDBHelper mithrilDBHelper;
    private SQLiteDatabase mithrilDB;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
//    private RootAccess rootAccess = null;

//    private Violation violationItemSelected = null;
//    private List<AppData> appDataItemsSelected = null;
    private List<Violation> violationItems;
    private FloatingActionButton fab;

    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private View headerView;

    /**
     * Following is a list of utility methods that ensure that the right fragments are launched
     * We are not launching the BCast receivers, Services and Content provider lists yet
     */
    private boolean isUserAppsListEmpty() {
        return false;
    }

    private boolean isSystemAppsListEmpty() {
        return false;
    }

    private boolean isAllAppsListEmpty() {
        return false;
    }

    private boolean isPermissionsListEmpty() {
        return false;
    }

    private boolean isServicesListEmpty() {
        return true;
    }

    private boolean isContentProvidersListEmpty() {
        return true;
    }

    private boolean isBroadcastReceiverListEmpty() {
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_violations) {
            /*
             * We find out how many violations are there in the database.
             * If there are none, we will load the EmptyFragment
             */
            if (isViolationListEmpty())
                loadEmptyFragment();
            else
                loadViolationsFragment();
        } else if (id == R.id.nav_usage) {
//            launchUsageStatsActivity();
            loadUsageStatsFragment();
        } else if (id == R.id.nav_perm) {
            if (isPermissionsListEmpty())
                loadNothingHereFragment("permissions");
            else
                loadPermissionsFragment();
        } else if (id == R.id.nav_user) {
            if (isUserAppsListEmpty())
                loadNothingHereFragment("user apps");
            else
                loadUserAppsFragment();
        } else if (id == R.id.nav_system) {
            if (isSystemAppsListEmpty())
                loadNothingHereFragment("system apps");
            else
                loadSystemAppsFragment();
        } else if (id == R.id.nav_all) {
            if (isAllAppsListEmpty())
                loadNothingHereFragment("apps");
            else
                loadAllAppsFragment();
        } else if (id == R.id.nav_services) {
            if (isServicesListEmpty())
                loadNothingHereFragment("services");
            else
                loadServicesFragment();
        } else if (id == R.id.nav_bcastreceivers) {
            if (isBroadcastReceiverListEmpty())
                loadNothingHereFragment("broadcast receivers");
            else
                loadBroadcastReceiversFragment();
        } else if (id == R.id.nav_contentproviders) {
            if (isContentProvidersListEmpty())
                loadNothingHereFragment("content providers");
            else
                loadContentProvidersFragment();
        } else if (id == R.id.nav_exit) {
            PermissionHelper.quitMithril(this, MithrilApplication.MITHRIL_BYE_BYE_MESSAGE);
        } else if (id == R.id.nav_settings) {
            launchInstanceCreationActivity();
        } else if (id == R.id.nav_about) {
            loadAboutFragment();
        } else if (id == R.id.nav_reset_app) {
            resetApp();
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
        initHouseKeepingTasks();
        initViews();
        defaultFragmentLoad();
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0, 0);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (exit) {
                finish(); // quit app
            } else {
                PermissionHelper.toast(this, "Press Back again to Exit.",
                        Toast.LENGTH_SHORT);
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 3 * 1000);

            }
        }
    }

    @Override
    public void onDestroy() {
        closeDB();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_core, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.capture_exec_settings) {
            executeRules();
            navigationView.getMenu().getItem(4).getSubMenu().getItem(3).setEnabled(true);
            SharedPreferences.Editor editor = this.getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE).edit();
            editor.putBoolean(MithrilApplication.getPrefKeyResetEnabled(), true);
            editor.apply();
//            We do not need root access if we are a privileged app.
//            if (rootAccess == null) {
//                try {
//                    rootAccess = new RootAccess();
//                    if (!rootAccess.isRooted())
//                        rootAccess = null;
//                    else {
//                        rootAccess.runScript(new String[]{
//                                MithrilApplication.getCmdGrantUpdateAppOpsStats(),
//                                MithrilApplication.getCmdGrantGetAppOpsStats(),
//                                MithrilApplication.getCmdGrantManageAppOpsRestrictions()
//                        });
//                    }
//                } catch (PhoneNotRootedException phoneNotRootedException) {
//                    Log.d(MithrilApplication.getDebugTag(), "Phone is not rooted do non-root behavior" + phoneNotRootedException.getMessage());
//                    PermissionHelper.toast(this, "Phone is not rooted do non-root behavior" + phoneNotRootedException.getMessage(), Toast.LENGTH_SHORT);
//                }
//            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void executeRules() {
        AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);

        try {
            // Let's try to block contacts permission for YouTube!
            String packageName = "com.google.android.gm";
            PackageManager packageManager = this.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            int uid = applicationInfo.uid;

            // Without being a privileged app we cannot execute the next line!
//            int altMode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_READ_CONTACTS, uid, packageName);
            // Testing if we can do this for our own app. It works!
//            int altMode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_READ_CONTACTS, android.os.Process.myUid(), this.getPackageName());

            Log.d(MithrilApplication.getDebugTag(), "Mode allowed: " + Integer.toString(AppOpsManager.MODE_ALLOWED));
            Log.d(MithrilApplication.getDebugTag(), "Mode default: " + Integer.toString(AppOpsManager.MODE_DEFAULT));
            Log.d(MithrilApplication.getDebugTag(), "Mode error: " + Integer.toString(AppOpsManager.MODE_ERRORED));
            Log.d(MithrilApplication.getDebugTag(), "Mode ignored: " + Integer.toString(AppOpsManager.MODE_IGNORED));

//            Log.d(MithrilApplication.getDebugTag(), "Alt mode: " + Integer.toString(altMode));

            /*
             * Check and block read contacts for Gmail app
             */
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_READ_CONTACTS, uid, packageName);
            PermissionHelper.toast(this, "Executing: " + packageName + " got mode: " + Integer.toString(mode), Toast.LENGTH_SHORT);
            Log.d(MithrilApplication.getDebugTag(), "Executing: " + packageName + " got mode: " + Integer.toString(mode));

            int modeToSet = AppOpsManager.MODE_ALLOWED;
            if(mode == AppOpsManager.MODE_ALLOWED)
                modeToSet = AppOpsManager.MODE_IGNORED;
            appOpsManager.setMode(AppOpsManager.OP_READ_CONTACTS, uid, packageName, modeToSet);

            mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_READ_CONTACTS, uid, packageName);
            PermissionHelper.toast(this, "Executing: " + packageName + " got mode: " + Integer.toString(mode), Toast.LENGTH_SHORT);
            Log.d(MithrilApplication.getDebugTag(), "Executing: " + packageName + " got mode: " + Integer.toString(mode));

            /*
             * Check and block write contacts for Gmail app
             */
            mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_WRITE_CONTACTS, uid, packageName);
            PermissionHelper.toast(this, "Executing: " + packageName + " got mode: " + Integer.toString(mode), Toast.LENGTH_SHORT);
            Log.d(MithrilApplication.getDebugTag(), "Executing: " + packageName + " got mode: " + Integer.toString(mode));

            modeToSet = AppOpsManager.MODE_ALLOWED;
            if (mode == AppOpsManager.MODE_ALLOWED)
                modeToSet = AppOpsManager.MODE_IGNORED;
            appOpsManager.setMode(AppOpsManager.OP_WRITE_CONTACTS, uid, packageName, modeToSet);

            mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_WRITE_CONTACTS, uid, packageName);
            PermissionHelper.toast(this, "Executing: " + packageName + " got mode: " + Integer.toString(mode), Toast.LENGTH_SHORT);
            Log.d(MithrilApplication.getDebugTag(), "Executing: " + packageName + " got mode: " + Integer.toString(mode));
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(MithrilApplication.getDebugTag(), "AppOpsManager execute: " + e.getMessage());
        } catch (Exception e) {
            PermissionHelper.toast(this, "Mithril does not seem to have UPDATE_APP_OPS_STATS permission. Cannot execute any rules. Sorry!", Toast.LENGTH_SHORT);
            Log.d(MithrilApplication.getDebugTag(), "Mithril does not seem to have UPDATE_APP_OPS_STATS permission. Cannot execute any rules. Sorry! " + e.getMessage());
        }
    }

    private void initHouseKeepingTasks() {
        if (PermissionHelper.isPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                !PermissionHelper.needsUsageStatsPermission(this)) {
//            startService(new Intent(this, LocationUpdateService.class));
            startService(new Intent(this, AppLaunchDetectorService.class));
        } else
            PermissionHelper.quitMithril(this, MithrilApplication.MITHRIL_BYE_BYE_MESSAGE);
        initDB(this);
        sharedPreferences = getApplicationContext().getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE);
        editor = getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE).edit();
        //Agreement has not been copied to downloads folder yet, do it now
        if (!isAgreementDownloaded())
            copyAssets(downloadsDirectory, agreementFile);
    }

    private void initViews() {
        setContentView(R.layout.activity_core);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_core);
        setSupportActionBar(toolbar);

        //If we are here, we have usage stats permission which means all asynchronous tasks are complete. User is on main screen and we may show the snackbar, once.
        if (!PermissionHelper.needsUsageStatsPermission(this))
            showAgreementDownloadedSnackbar();

        fab = (FloatingActionButton) findViewById(R.id.fab_core);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSnackbar(view, view.getContext().getResources().getString(R.string.functionality_not_active_yet));
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (sharedPreferences.getBoolean(MithrilApplication.getPrefKeyResetEnabled(), false))
            navigationView.getMenu().getItem(3).getSubMenu().getItem(3).setEnabled(true);

        applyHeaderView();
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

    private void showAgreementDownloadedSnackbar() {
        if (sharedPreferences.getBoolean(MithrilApplication.getPrefKeyShouldShowAgreementSnackbar(), true)) {
            if (isAgreementDownloaded()) {
                showSnackbar(findViewById(R.id.main_coordinator_layout),
                        getResources().getString(R.string.agreement_copied));

                SharedPreferences.Editor editor = this.getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE).edit();
                editor.putBoolean(MithrilApplication.getPrefKeyShouldShowAgreementSnackbar(), false);
                editor.apply();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view,
                message,
                Snackbar.LENGTH_INDEFINITE);

        snackbar.setActionTextColor(getResources().getColor(R.color.white, this.getTheme()));

        // get snackbar view
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary, this.getTheme()));

        snackbar.setAction(R.string.okay,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                }).show();
    }

    private void applyHeaderView() {
        /*
         * We wanted to show different banner at different times during the day. The following sub-section of the method takes care of that.
         * http://stackoverflow.com/questions/33560219/in-android-how-to-set-navigation-drawer-header-image-and-name-programmatically-i
         * As mentioned in the bug 190226, Since version 23.1.0 getting header layout view with: navigationView.findViewById(R.id.navigation_header_text) no longer works.
         * A workaround is to inflate the headerview programmatically and find view by ID from the inflated header view.
         * mNavHeaderMain = (LinearLayout) findViewById(R.id.drawer_view);
         */
        headerView = navigationView.inflateHeaderView(R.layout.nav_header_main);
        headerView.findViewById(R.id.drawer_view);
        Calendar cal = Calendar.getInstance();
        int hourofday = cal.get(Calendar.HOUR_OF_DAY);
        if (hourofday < 12 && hourofday >= 6)
            headerView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.csee_morning, getTheme()));
        else if (hourofday < 18 && hourofday >= 12)
            headerView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.csee_afternoon, getTheme()));
        else
            headerView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.csee_evening, getTheme()));
    }

    private void defaultFragmentLoad() {
//        if (!isContextInfoSet())
//            launchPrefsActivity();
//        else {
        /*
         * If we are loading the app list we don't need the above two lines
         * as we take care of that in the loadAllAppsFragment() method
         */
        if (isViolationListEmpty())
            loadEmptyFragment();
        else
            loadViolationsFragment();
    }
//    }

    private void resetApp() {
        // Use the Builder class for convenient dialog construction
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_reload_data)
                .setPositiveButton(R.string.dialog_resp_delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
                        try {
                            appOpsManager.setMode(AppOpsManager.OP_GET_USAGE_STATS, android.os.Process.myUid(), getApplicationContext().getPackageName(), AppOpsManager.MODE_DEFAULT);
                        } catch (Exception e) {
                            Log.e(MithrilApplication.getDebugTag(), e.getMessage());
                        }
                        ((ActivityManager) builder.getContext().getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();
                        PermissionHelper.toast(builder.getContext(), "App was reset!", Toast.LENGTH_SHORT);
                    }
                })
                .setNegativeButton(R.string.dialog_resp_NO, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        PermissionHelper.toast(builder.getContext(), "App was not reset!", Toast.LENGTH_SHORT);
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = builder.create();

        // show it
        alertDialog.show();
    }

    private void loadNothingHereFragment(String what) {
        Bundle data = new Bundle();
        data.putString(MithrilApplication.getPrefWhatFragmentKey(), what);

        NothingHereFragment aNothingHereFragment = new NothingHereFragment();
        aNothingHereFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_core, aNothingHereFragment)
                .commit();
    }

    private void loadEmptyFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_core, new EmptyFragment())
                .commit();
    }

    private void loadAboutFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_core, new AboutFragment())
                .commit();
    }

    private void loadViolationsFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_core, new ViolationFragment())
                .commit();
    }

    private void loadBroadcastReceiversFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_core, new BroadcastReceiversFragment())
                .commit();
    }

    private void loadContentProvidersFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_core, new ContentProvidersFragment())
                .commit();
    }

    private void loadServicesFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_core, new ServicesFragment())
                .commit();
    }

    private void loadPermissionsFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_core, new PermissionsFragment())
                .commit();
    }

    private void loadUsageStatsFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_core, new UsageStatsFragment())
                .commit();
    }
    private void loadAllAppsFragment() {
        Bundle data = new Bundle();
        data.putString(MithrilApplication.getPrefKeyAppDisplayType(), MithrilApplication.getPrefKeyAllAppsDisplay());

        AppsFragment aShowappsFragment = new AppsFragment();
        aShowappsFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_core, aShowappsFragment)
                .commit();
    }

    private void loadSystemAppsFragment() {
        Bundle data = new Bundle();
        data.putString(MithrilApplication.getPrefKeyAppDisplayType(), MithrilApplication.getPrefKeySystemAppsDisplay());

        AppsFragment aShowappsFragment = new AppsFragment();
        aShowappsFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_core, aShowappsFragment)
                .commit();
    }

    private void loadUserAppsFragment() {
        Bundle data = new Bundle();
        data.putString(MithrilApplication.getPrefKeyAppDisplayType(), MithrilApplication.getPrefKeyUserAppsDisplay());

        AppsFragment aShowappsFragment = new AppsFragment();
        aShowappsFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_core, aShowappsFragment)
                .commit();
    }

//    private void launchPrefsActivity() {
//        startActivity(new Intent(this, PrefsActivity.class));
//    }

    private void launchInstanceCreationActivity() {
        editor.putBoolean(MithrilApplication.getPrefKeyInstancesCreated(), false);
        editor.apply();
        startActivity(new Intent(this, InstanceCreationActivity.class));
    }

    private void launchUsageStatsActivity() {
        startActivity(new Intent(this, UsageStatsActivity.class));
//        Intent intent = new Intent(this, AppOpsDetailsActivity.class);
//        Bundle b = new Bundle();
//        b.putString("package", "com.google.android.youtube"); //Your id
//        intent.putExtras(b); //Put your id to your next Intent
//        startActivity(intent);
    }

    private boolean isViolationListEmpty() {
        /*
         * We find out how many violations are there in the database.
         * If there are none, we will load the EmptyFragment
         */
        violationItems = mithrilDBHelper.findAllViolations(mithrilDB);
        if (violationItems != null)
            Log.d(MithrilApplication.getDebugTag(), "Number of violations" + Integer.toString(violationItems.size()));
        else
            Log.d(MithrilApplication.getDebugTag(), "Null");
        return !(violationItems == null || violationItems.size() > 0);
    }

    private void copyAssets(File parent, String child) {
        File file = new File(parent, child);

        try {
            writeFile(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            Log.e(MithrilApplication.getDebugTag(), e.getMessage());
        }
    }

    private void writeFile(OutputStream destination) {
        AssetManager assetManager = getAssets();
        InputStream in = null;
        try {
            in = assetManager.open(MithrilApplication.getFlierPdfFileName());

            copyFile(in, destination);

            in.close();
            destination.flush();
            destination.close();
        } catch (IOException iOException) {
            Log.e(MithrilApplication.getDebugTag(), iOException.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.d(MithrilApplication.getDebugTag(), "Flier file threw NullPointerException");
                }
            }
            if (destination != null) {
                try {
                    destination.close();
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
        SharedPreferences.Editor editor = this.getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE).edit();
        editor.putBoolean(MithrilApplication.getPrefKeyUserAgreementCopied(), true);
        editor.apply();
    }

    private boolean isAgreementDownloaded() {
        return sharedPreferences.getBoolean(MithrilApplication.getPrefKeyUserAgreementCopied(), false) && new File(downloadsDirectory, agreementFile).exists();
    }

    private boolean isContextInfoSet() {
        return sharedPreferences.getBoolean(MithrilApplication.getPrefAllDoneKey(), false);
    }

    @Override
    public void onListFragmentInteraction(AppData item) {
        //TODO Do something with the App selected
        Intent intent = new Intent(this, ShowAppDetailsActivity.class);
        intent.putExtra(MithrilApplication.getPrefKeyAppPkgName(), item.getPackageName());
        startActivity(intent);
    }

    @Override
    public void onListFragmentInteraction(Violation item) {
        //TODO Do something with the Violation selected
//        violationItemSelected = item;
    }

    @Override
    public void onListFragmentLongInteraction(List<AppData> items) {
        //TODO Do something with the Apps selected
//        appDataItemsSelected = items;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //TODO do something when the reload data fragment is interacted with
    }

    @Override
    public void onListFragmentInteraction(String item) {
        //TODO Do something with the Perm group selected
        Intent intent = new Intent(this, ShowPermissionDetailActivity.class);
        intent.putExtra(MithrilApplication.getPrefKeyPermGroupName(), item);
        intent.putExtra(MithrilApplication.getPrefKeyPermGroupLabel(), item.split("\\.")[item.split("\\.").length - 1]);
        startActivity(intent);
    }

    @Override
    public void onListFragmentInteraction(ServData item) {
        //TODO do something when the service data is requested - I have an idea. Why don't you launch a list of services that are being used by apps.
    }

    @Override
    public void onListFragmentInteraction(ContentProvData item) {
        //TODO do something when the content provider data is requested
    }

    @Override
    public void onListFragmentInteraction(BCastRecvData item) {
        //TODO do something when the broadcast receiver data is requested
    }

    @Override
    public void onListFragmentInteraction(AppUsageStats item) {
        //TODO do something when the usage stats data is requested
    }
}