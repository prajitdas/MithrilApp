package edu.umbc.cs.ebiquity.mithril.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.List;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.R;
import edu.umbc.cs.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.cs.ebiquity.mithril.data.model.Violation;
import edu.umbc.cs.ebiquity.mithril.data.model.components.AppData;
import edu.umbc.cs.ebiquity.mithril.data.model.components.BCastRecvData;
import edu.umbc.cs.ebiquity.mithril.data.model.components.ContentProvData;
import edu.umbc.cs.ebiquity.mithril.data.model.components.PermData;
import edu.umbc.cs.ebiquity.mithril.data.model.components.ServData;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.EmptyFragment;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.NothingHereFragment;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.mainactivityfragments.AboutFragment;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.mainactivityfragments.AdvancedPreferencesFragment;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.mainactivityfragments.AppsFragment;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.mainactivityfragments.BroadcastReceiversFragment;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.mainactivityfragments.ContentProvidersFragment;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.mainactivityfragments.PermissionsFragment;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.mainactivityfragments.PrefsFragment;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.mainactivityfragments.ServicesFragment;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.mainactivityfragments.ViolationFragment;
import edu.umbc.cs.ebiquity.mithril.util.services.AppLaunchDetectorService;
import edu.umbc.cs.ebiquity.mithril.util.services.LocationUpdateService;
import edu.umbc.cs.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

public class MainActivity extends AppCompatActivity
        implements  NavigationView.OnNavigationItemSelectedListener,
                    AppsFragment.OnListFragmentInteractionListener,
                    AppsFragment.OnListFragmentLongInteractionListener,
                    PermissionsFragment.OnListFragmentInteractionListener,
                    BroadcastReceiversFragment.OnListFragmentInteractionListener,
                    ContentProvidersFragment.OnListFragmentInteractionListener,
                    ServicesFragment.OnListFragmentInteractionListener,
                    AboutFragment.OnFragmentInteractionListener,
                    ViolationFragment.OnListFragmentInteractionListener,
        AdvancedPreferencesFragment.OnFragmentInteractionListener,
        EmptyFragment.OnFragmentInteractionListener,
        NothingHereFragment.OnFragmentInteractionListener {

    private final File downloadsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    private final String agreementFile = MithrilApplication.getFlierPdfFileName();
    private MithrilDBHelper mithrilDBHelper;
    private SQLiteDatabase mithrilDB;
    private SharedPreferences sharedPreferences;
    private Violation violationItemSelected = null;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private List<AppData> appDataItemsSelected = null;
    private List<Violation> violationItems;
    private FloatingActionButton fab;
    private View headerView;

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
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
        } else if (id == R.id.nav_perm) {
            if (isPermissionsListEmpty())
                loadNothingHereFragment();
            else
                loadPermissionsFragment();
        } else if (id == R.id.nav_user) {
            if (isUserAppsListEmpty())
                loadNothingHereFragment();
            else
                loadUserAppsFragment();
        } else if (id == R.id.nav_system) {
            if (isSystemAppsListEmpty())
                loadNothingHereFragment();
            else
                loadSystemAppsFragment();
        } else if (id == R.id.nav_all) {
            if (isAllAppsListEmpty())
                loadNothingHereFragment();
            else
                loadAllAppsFragment();
        } else if (id == R.id.nav_services) {
            if (isServicesListEmpty())
                loadNothingHereFragment();
            else
                loadServicesFragment();
        } else if (id == R.id.nav_bcastreceivers) {
            if (isBroadcastReceiverListEmpty())
                loadNothingHereFragment();
            else
                loadBroadcastReceiversFragment();
        } else if (id == R.id.nav_contentproviders) {
            if (isContentProvidersListEmpty())
                loadNothingHereFragment();
            else
                loadContentProvidersFragment();
        } else if (id == R.id.nav_exit) {
            exitTheApp();
        } else if (id == R.id.nav_settings) {
            loadPrefsFragment();
        } else if (id == R.id.nav_about) {
            loadAboutFragment();
        } else if (id == R.id.nav_advanced_settings) {
            loadAdvancedPreferencesFragment();
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void exitTheApp() {
        Uri packageUri = Uri.parse("package:" + MithrilApplication.APP_PACKAGE_NAME_SELF);
        Intent uninstallIntent =
                new Intent(Intent.ACTION_DELETE, packageUri);
        startActivity(uninstallIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getUserConsent();
    }

    private void getUserConsent() {
        /*
         * If the user has already consented, we just go to the MainActivity, or else we are stuck here!
         */
        sharedPreferences = getApplicationContext().getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE);
        if (sharedPreferences.getString(MithrilApplication.getPrefKeyUserConsent(), null) == null) {
            Intent consentActivity = new Intent(getApplicationContext(), UserAgreementActivity.class);
            startActivityForResult(consentActivity, MithrilApplication.USER_CONSENT_RECEIVED_REQUEST_CODE);
        } else {
            //Agreement has not been copied to downloads folder yet, do it now
            if (!isAgreementDownloaded())
                copyAgreement();
            startMainActivityTasks();
        }
    }

    private void startMainActivityTasks() {
        initHouseKeepingTasks();

        initViews();
        defaultFragmentLoad();
    }

    private void initHouseKeepingTasks() {
        if (PermissionHelper.getUsageStatsPermisison(this))
            startService(new Intent(this, AppLaunchDetectorService.class));
        if (PermissionHelper.isPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            startService(new Intent(this, LocationUpdateService.class));
//            if (PermissionHelper.isPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                boolean updatesRequested = false;
//                    /*
//                    * Get any previous setting for location updates
//                    * Gets "false" if an error occurs
//                    */
//                if (sharedPref.contains(MithrilApplication.getPrefKeyLocationUpdateServiceState())) {
//                    updatesRequested = sharedPref.getBoolean(MithrilApplication.getPrefKeyLocationUpdateServiceState(), false);
//                }
//                if (updatesRequested) {
//                    startService(new Intent(this, LocationUpdateService.class));
//                }
//            }
    }

    private void copyAgreement() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("The agreement doc has been copied to the "
                + downloadsDirectory.getAbsolutePath()
                + " directory, for your reference...")
                .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Copy the agreement file to the external files directory. The user needs a copy of the agreement.
                        copyAssets(downloadsDirectory, agreementFile);
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = builder.create();

        // show it
        alertDialog.show();
    }

    private void initViews() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab_main);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Functionality not active right now. Once clicked, apps will be sent to server!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        applyHeaderView();
        
        // Let's get the DB instances loaded too
        mithrilDBHelper = new MithrilDBHelper(this);
        mithrilDB = mithrilDBHelper.getWritableDatabase();
    }

    private void applyHeaderView() {
        /*
         * We wanted to show different banner at different times during the day. The following sub-section of the method takes care of that.
         * http://stackoverflow.com/questions/33560219/in-android-how-to-set-navigation-drawer-header-image-and-name-programmatically-i
         * As mentioned in the bug 190226, Since version 23.1.0 getting header layout view with: navigationView.findViewById(R.id.navigation_header_text) no longer works.
         * A workaround is to inflate the headerview programatically and find view by ID from the inflated header view.
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
        if (!isContextInfoSet())
            loadPrefsFragment();
        else {
        /*
         * If we are loading the app list we don't need the above two lines
         * as we take care of that in the loadAllAppsFragment() method
         */
            if (isViolationListEmpty())
                loadEmptyFragment();
            else
                loadViolationsFragment();
//                loadUserAppsFragment();
        }
    }

    private void loadNothingHereFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_main, new NothingHereFragment())
                .commit();
    }

    private void loadEmptyFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_main, new EmptyFragment())
                .commit();
    }

    private void loadAdvancedPreferencesFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_main, new AdvancedPreferencesFragment())
                .commit();
    }

    private void loadPrefsFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_main, new PrefsFragment())
                .commit();
    }

    private void loadAboutFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_main, new AboutFragment())
                .commit();
    }

    private void loadViolationsFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_main, new ViolationFragment())
                .commit();
    }

    private void loadBroadcastReceiversFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_main, new BroadcastReceiversFragment())
                .commit();
    }

    private void loadContentProvidersFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_main, new ContentProvidersFragment())
                .commit();
    }

    private void loadServicesFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_main, new ServicesFragment())
                .commit();
    }

    private void loadPermissionsFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_main, new PermissionsFragment())
                .commit();
    }

    private void loadAllAppsFragment() {
        Bundle data = new Bundle();
        data.putString(MithrilApplication.getPrefKeyAppDisplayType(), MithrilApplication.getPrefKeyAllAppsDisplay());

        AppsFragment aShowappsFragment = new AppsFragment();
        aShowappsFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_main, aShowappsFragment)
                .commit();
    }

    private void loadSystemAppsFragment() {
        Bundle data = new Bundle();
        data.putString(MithrilApplication.getPrefKeyAppDisplayType(), MithrilApplication.getPrefKeySystemAppsDisplay());

        AppsFragment aShowappsFragment = new AppsFragment();
        aShowappsFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_main, aShowappsFragment)
                .commit();
    }

    private void loadUserAppsFragment() {
        Bundle data = new Bundle();
        data.putString(MithrilApplication.getPrefKeyAppDisplayType(), MithrilApplication.getPrefKeyUserAppsDisplay());

        AppsFragment aShowappsFragment = new AppsFragment();
        aShowappsFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_main, aShowappsFragment)
                .commit();
    }

    private boolean isViolationListEmpty() {
        return true;
        /*
         * We find out how many violations are there in the database.
         * If there are none, we will load the EmptyFragment
         */
//        violationItems = mithrilDBHelper.findAllViolations(mithrilDB);
//        if (violationItems != null)
//            Log.d(MithrilApplication.getDebugTag(), "Number of violations" + Integer.toString(violationItems.size()));
//        else
//            Log.d(MithrilApplication.getDebugTag(), "Null");
//        return !(violationItems == null || violationItems.size() > 0);
    }

    private boolean isBroadcastReceiverListEmpty() {
        return true;
    }

    private boolean isUserAppsListEmpty() {
        return false;
    }

    private boolean isSystemAppsListEmpty() {
        return false;
    }

    private boolean isAllAppsListEmpty() {
        return false;
    }

    private boolean isServicesListEmpty() {
        return true;
    }

    private boolean isContentProvidersListEmpty() {
        return true;
    }

    private boolean isPermissionsListEmpty() {
        return true;
    }

    @Override
    public void onListFragmentInteraction(AppData item) {
        //TODO Do something with the App selected
        Intent intent = new Intent(this, ViewAppDetailsActivity.class);
        intent.putExtra(MithrilApplication.getPrefKeyAppPkgName(), item.getPackageName());
        startActivity(intent);
    }

    @Override
    public void onListFragmentInteraction(Violation item) {
        //TODO Do something with the Violation selected
        violationItemSelected = item;
    }

    @Override
    public void onListFragmentLongInteraction(List<AppData> items) {
        //TODO Do something with the Apps selected
        appDataItemsSelected = items;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //TODO do something when the reload data fragment is interacted with
    }

    @Override
    public void onListFragmentInteraction(PermData item) {
        //TODO do something when the permission data is requested - I have an idea. Why don't you launch a list of permissions that are being used by apps.
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
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MithrilApplication.USER_CONSENT_RECEIVED_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                startMainActivityTasks();
            } else {
                finish();
                /*
                 * We did not get the consent, perhaps we should finish?
                 * Something is obviously wrong!
                 * We should never reach this state, ever...
                 */
            }
        }
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
        editor.commit();
    }

    private boolean isAgreementDownloaded() {
        return sharedPreferences.getBoolean(MithrilApplication.getPrefKeyUserAgreementCopied(), false) && new File(downloadsDirectory, agreementFile).exists();
    }

    private boolean isContextInfoSet() {
        return sharedPreferences.getBoolean(MithrilApplication.getPrefAllDoneKey(), false);
    }
}