package edu.umbc.cs.ebiquity.mithril.ui.activities;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.R;
import edu.umbc.cs.ebiquity.mithril.data.model.AppData;
import edu.umbc.cs.ebiquity.mithril.data.model.Violation;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.AboutFragment;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.PrefsFragment;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.ReloadDefaultAppDataFragment;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.ShowAppsFragment;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.ViolationFragment;
import edu.umbc.cs.ebiquity.mithril.util.services.AppLaunchDetectorService;
import edu.umbc.cs.ebiquity.mithril.util.services.LocationUpdateService;
import edu.umbc.cs.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

public class MainActivity extends AppCompatActivity
        implements  NavigationView.OnNavigationItemSelectedListener,
                    ShowAppsFragment.OnListFragmentInteractionListener,
                    ShowAppsFragment.OnListFragmentLongInteractionListener,
        AboutFragment.OnFragmentInteractionListener,
        ViolationFragment.OnListFragmentInteractionListener,
        ReloadDefaultAppDataFragment.OnFragmentInteractionListener {

    private SharedPreferences sharedPref;
    private Violation violationItemSelected = null;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private List<AppData> appDataItemsSelected = null;
    private FloatingActionButton fab;

    private void loadAllAppsFragment() {
        Bundle data = new Bundle();
        data.putString(MithrilApplication.getPrefKeyAppDisplayType(), MithrilApplication.getPrefKeyAllAppsDisplay());

        ShowAppsFragment aShowappsFragment = new ShowAppsFragment();
        aShowappsFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_main, aShowappsFragment)
                .commit();
    }

    private void loadSystemAppsFragment() {
        Bundle data = new Bundle();
        data.putString(MithrilApplication.getPrefKeyAppDisplayType(), MithrilApplication.getPrefKeySystemAppsDisplay());

        ShowAppsFragment aShowappsFragment = new ShowAppsFragment();
        aShowappsFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_main, aShowappsFragment)
                .commit();
    }

    private void loadUserAppsFragment() {
        Bundle data = new Bundle();
        data.putString(MithrilApplication.getPrefKeyAppDisplayType(), MithrilApplication.getPrefKeyUserAppsDisplay());

        ShowAppsFragment aShowappsFragment = new ShowAppsFragment();
        aShowappsFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_main, aShowappsFragment)
                .commit();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_violations) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container_main, new ViolationFragment()).commit();
        } else if (id == R.id.nav_user) {
            loadUserAppsFragment();
        } else if (id == R.id.nav_system) {
            loadSystemAppsFragment();
        } else if (id == R.id.nav_all) {
            loadAllAppsFragment();
        } else if (id == R.id.nav_settings) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container_main, new PrefsFragment()).commit();
        } else if (id == R.id.nav_reload) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container_main, new ReloadDefaultAppDataFragment()).commit();
        } else if (id == R.id.nav_about) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container_main, new AboutFragment()).commit();
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initHousekeepingTasks();
    }

    private void initHousekeepingTasks() {
//        File locationFile = new File(getFilesDir(), "location.txt");
//        File logFile = new File(getFilesDir(), "log.txt");
//        sharedPref = getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPref.edit();
//        // Stores the lat / long pairs in a text file
//        editor.putString(MithrilApplication.getPrefKeyLocationFilename(), locationFile.getAbsolutePath());
//        // Stores the connect / disconnect data in a text file
//        editor.putString(MithrilApplication.getPrefKeyLogFilename(), logFile.getAbsolutePath());
//        editor.commit();

        if (PermissionHelper.isExplicitPermissionAcquisitionNecessary()) {
            PermissionHelper.requestAllNecessaryPermissions(this);
            if (PermissionHelper.getUsageStatsPermisison(this))
                startService(new Intent(this, AppLaunchDetectorService.class));
            if (PermissionHelper.isPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                startService(new Intent(this, LocationUpdateService.class));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        defaultFragmentLoad();
    }

    @SuppressWarnings("RestrictedApi")
    private void initViews() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab_main);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Once clicked, apps will be sent to server!", Snackbar.LENGTH_LONG)
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
    }

    private void defaultFragmentLoad() {
//        For ViolationFragment() we will have to manage in a different manner
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.container, new ViolationFragment()).commit();
//        If we are loading the app list we don't need the above two lines as we take care of that in the loadAllAppsFragment() method
        loadUserAppsFragment();
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
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MithrilApplication.ALL_PERMISSIONS_MITHRIL_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "You denied some permissions. This might disrupt some functionality!", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}