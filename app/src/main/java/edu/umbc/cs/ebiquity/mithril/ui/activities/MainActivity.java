package edu.umbc.cs.ebiquity.mithril.ui.activities;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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

import java.util.List;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.R;
import edu.umbc.cs.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.cs.ebiquity.mithril.data.model.AppData;
import edu.umbc.cs.ebiquity.mithril.data.model.Violation;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.PrefsFragment;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.ShowAppsFragment;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.ViolationFragment;

public class MainActivity extends AppCompatActivity
        implements  NavigationView.OnNavigationItemSelectedListener,
                    ShowAppsFragment.OnListFragmentInteractionListener,
                    ShowAppsFragment.OnListFragmentLongInteractionListener,
                    ViolationFragment.OnListFragmentInteractionListener {

    private static MithrilDBHelper mithrilDBHelper;
    private static SQLiteDatabase mithrilDB;
    private Violation violationItemSelected = null;

    private SharedPreferences sharedPreferences;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private AppData appDataItemSelected = null;
    private List<AppData> appDataItemsSelected = null;
    private FloatingActionButton fab;

    private void loadAllAppsFragment() {
        Bundle data = new Bundle();
        data.putString(MithrilApplication.getAppDisplayTypeTag(), MithrilApplication.getAllAppsDisplayTag());

        ShowAppsFragment aShowappsFragment = new ShowAppsFragment();
        aShowappsFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, aShowappsFragment)
                .commit();
    }

    private void loadSystemAppsFragment() {
        Bundle data = new Bundle();
        data.putString(MithrilApplication.getAppDisplayTypeTag(), MithrilApplication.getSystemAppsDisplayTag());

        ShowAppsFragment aShowappsFragment = new ShowAppsFragment();
        aShowappsFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, aShowappsFragment)
                .commit();
    }

    private void loadUserAppsFragment() {
        Bundle data = new Bundle();
        data.putString(MithrilApplication.getAppDisplayTypeTag(), MithrilApplication.getUserAppsDisplayTag());

        ShowAppsFragment aShowappsFragment = new ShowAppsFragment();
        aShowappsFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, aShowappsFragment)
                .commit();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_violations) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, new ViolationFragment()).commit();
        } else if (id == R.id.nav_user) {
            loadUserAppsFragment();
        } else if (id == R.id.nav_system) {
            loadSystemAppsFragment();
        } else if (id == R.id.nav_all) {
            loadAllAppsFragment();
        } else if (id == R.id.nav_settings) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container, new PrefsFragment()).commit();
        } else if (id == R.id.nav_about) {
            Intent intent= new Intent(this, AboutActivity.class);
            startActivity(intent);
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListFragmentInteraction(AppData item) {
        //TODO Do something with the item selected
        appDataItemSelected = item;
    }

    @Override
    public void onListFragmentInteraction(Violation item) {
        //TODO Do something with the item selected
        violationItemSelected = item;
    }

    @Override
    public void onListFragmentLongInteraction(List<AppData> items) {
        //TODO Do something with the item selected
        appDataItemsSelected = items;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initViews();
        defaultFragmentLoad();
    }

    @Override
    public void onResume() {
        super.onResume();
        defaultFragmentLoad();
    }

    private void initViews() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
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

    private void initData() {
        /**
         * Database creation and default data insertion, happens only once.
         */
        mithrilDBHelper = new MithrilDBHelper(this);
        mithrilDB = mithrilDBHelper.getWritableDatabase();
        sharedPreferences = getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE);
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
}