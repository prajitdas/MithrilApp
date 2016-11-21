package edu.umbc.cs.ebiquity.mithril.ui.activities;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import edu.umbc.cs.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;
import edu.umbc.cs.ebiquity.mithril.util.specialtasks.usagestats.RunningAppInfo;

public class MainActivity extends AppCompatActivity
        implements  NavigationView.OnNavigationItemSelectedListener,
                    ShowAppsFragment.OnListFragmentInteractionListener,
                    ShowAppsFragment.OnListFragmentLongInteractionListener,
        AboutFragment.OnFragmentInteractionListener,
        ViolationFragment.OnListFragmentInteractionListener,
        ReloadDefaultAppDataFragment.OnFragmentInteractionListener {

    private Violation violationItemSelected = null;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private List<AppData> appDataItemsSelected = null;
    private FloatingActionButton fab;

    private void loadAllAppsFragment() {
        Bundle data = new Bundle();
        data.putString(MithrilApplication.getAppDisplayTypeTag(), MithrilApplication.getAllAppsDisplayTag());

        ShowAppsFragment aShowappsFragment = new ShowAppsFragment();
        aShowappsFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_main, aShowappsFragment)
                .commit();
    }

    private void loadSystemAppsFragment() {
        Bundle data = new Bundle();
        data.putString(MithrilApplication.getAppDisplayTypeTag(), MithrilApplication.getSystemAppsDisplayTag());

        ShowAppsFragment aShowappsFragment = new ShowAppsFragment();
        aShowappsFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_main, aShowappsFragment)
                .commit();
    }

    private void loadUserAppsFragment() {
        Bundle data = new Bundle();
        data.putString(MithrilApplication.getAppDisplayTypeTag(), MithrilApplication.getUserAppsDisplayTag());

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
        intent.putExtra(MithrilApplication.getAppPkgNameTag(), item.getPackageName());
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
//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_LOGS}, MithrilApplication.CONST_ALL_PERMISSIONS_MITHRIL_REQUEST_CODE);
//        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_LOGS);
//        if (result == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this, "GOT IT!", Toast.LENGTH_LONG).show();
//            Log.d(MithrilApplication.getDebugTag(), "GOT IT!");
//        } else {
//            Toast.makeText(this, "NOT GOT IT!", Toast.LENGTH_LONG).show();
//            Log.d(MithrilApplication.getDebugTag(), "NOT GOT IT!");
//        }
//        isPermitted();
        permCheck();
        initViews();
        defaultFragmentLoad();
        //TODO This is a test! Remove it later...
        Toast.makeText(this, RunningAppInfo.getForegroundProcess(this), Toast.LENGTH_LONG).show();
    }

    void permCheck() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.READ_LOGS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_LOGS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_LOGS},
                        MithrilApplication.CONST_ALL_PERMISSIONS_MITHRIL_REQUEST_CODE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
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
        //For ViolationFragment() we will have to manage in a different manner
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.container, new ViolationFragment()).commit();
        //If we are loading the app list we don't need the above two lines as we take care of that in the loadAllAppsFragment() method
        loadUserAppsFragment();
    }

    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
            case MithrilApplication.CONST_ALL_PERMISSIONS_MITHRIL_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d(MithrilApplication.getDebugTag(), "came to onRequestPermissionsResult");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    Log.d(MithrilApplication.getDebugTag(), "came to denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        Log.d(MithrilApplication.getDebugTag(), "Permission count: "+Integer.toString(grantResults[1]));
//        Log.d(MithrilApplication.getDebugTag(), "Permissions: "+permissions[1]);
    // If request is cancelled, the result arrays are empty.
//        if (grantResults.length > 0) {
//            for (int idx = 0; idx < PermissionHelper.getCountOfPermissionsToRequest(); idx++)
//                if (grantResults[idx] == PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(this, "thanks", Toast.LENGTH_LONG).show();
//                    MithrilApplication.addToPermissionsGranted(permissions[idx]);
//                } else {
//                    Toast.makeText(this, "You did not permit me to do things :(\nBye!", Toast.LENGTH_LONG).show();
//                    finish();
//                }
    // permission was granted, yay! Do the
    // contacts-related task you need to do.
//        } else {
//            Toast.makeText(this, "You did not permit me to do things :(\nBye!", Toast.LENGTH_LONG).show();
//            finish();
    // permission denied, boo! Disable the
    // functionality that depends on this permission.
//        }
//        Log.d(MithrilApplication.getDebugTag(), "Permission count: "+Integer.toString(grantResults.length));
//        Log.d(MithrilApplication.getDebugTag(), "Permission count: "+Integer.toString(MithrilApplication.getPermissionsGranted().size()));
    // We need all the permissions for this app
//        if(MithrilApplication.getPermissionsGranted().size() != PermissionHelper.getPermissionsRequiredCount()){
//            Toast.makeText(this, "You did not permit me to do things :(\nBye!", Toast.LENGTH_LONG).show();
//            finish();
//    }

    public void isPermitted() {
        if (PermissionHelper.isExplicitPermissionAcquisitionNecessary()) {
            PermissionHelper.requestAllPermissions(this);
        }
    }
}