package edu.umbc.cs.ebiquity.mithril.ui.activities;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.data.model.AppMetadata;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.ViolationFragment;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.appmanager.ShowAppsFragment;
import edu.umbc.cs.ebiquity.mithril.R;
import edu.umbc.cs.ebiquity.mithril.data.helpers.MithrilDBHelper;
import edu.umbc.cs.ebiquity.mithril.data.model.Violation;

public class MainActivity extends AppCompatActivity
        implements  NavigationView.OnNavigationItemSelectedListener,
                    ShowAppsFragment.OnListFragmentInteractionListener,
                    ShowAppsFragment.OnListFragmentLongInteractionListener{

    private static MithrilDBHelper mithrilDBHelper;
    private static SQLiteDatabase mithrilDB;
    private Violation violationItemSelected = null;

    private SharedPreferences sharedPreferences;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private AppMetadata appMetadataItemSelected = null;
    private List<AppMetadata> appMetadataItemsSelected = null;
    private FloatingActionButton fab;
    private TextView mAppCountTextView;

    private void loadAllAppsFragment() {
        Bundle data = new Bundle();
        data.putString(MithrilApplication.getAppDisplayTypeTag(), MithrilApplication.getAllAppsDisplayTag());

        ShowAppsFragment aShowappsFragment = new ShowAppsFragment();
        aShowappsFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, aShowappsFragment)
                .commit();
        mAppCountTextView.setText(mAppCountTextView.getText()
                + Integer.toString(sharedPreferences.getInt(MithrilApplication.getSharedPreferenceAppCount(),0)));
    }

    private void loadSystemAppsFragment() {
        Bundle data = new Bundle();
        data.putString(MithrilApplication.getAppDisplayTypeTag(), MithrilApplication.getSystemAppsDisplayTag());

        ShowAppsFragment aShowappsFragment = new ShowAppsFragment();
        aShowappsFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, aShowappsFragment)
                .commit();
        mAppCountTextView.setText(mAppCountTextView.getText()
                + Integer.toString(sharedPreferences.getInt(MithrilApplication.getSharedPreferenceAppCount(),0)));
    }

    private void loadUserAppsFragment() {
        Bundle data = new Bundle();
        data.putString(MithrilApplication.getAppDisplayTypeTag(), MithrilApplication.getUserAppsDisplayTag());

        ShowAppsFragment aShowappsFragment = new ShowAppsFragment();
        aShowappsFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, aShowappsFragment)
                .commit();
        mAppCountTextView.setText(mAppCountTextView.getText()
                + Integer.toString(sharedPreferences.getInt(MithrilApplication.getSharedPreferenceAppCount(),0)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            loadAllAppsFragment();
        } else if (id == R.id.nav_gallery) {
            loadSystemAppsFragment();
        } else if (id == R.id.nav_slideshow) {
            loadUserAppsFragment();
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onListFragmentInteraction(AppMetadata item) {
        //TODO Do something with the item selected
        appMetadataItemSelected = item;
    }

    @Override
    public void onListFragmentLongInteraction(List<AppMetadata> items) {
        //TODO Do something with the item selected
        appMetadataItemsSelected = items;
    }

    //
//    public void createShortCut(){
//        Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
//        shortcutintent.putExtra("duplicate", false);
//        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
//        Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher);
//        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
//        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(getApplicationContext(), EnterActivity.class));
//        sendBroadcast(shortcutintent);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        createShortCut();
        initViews();
        initData();
    }

    private void initData() {
        /**
         * Database creation and default data insertion, happens only once.
         */
        mithrilDBHelper = new MithrilDBHelper(this);
        mithrilDB = mithrilDBHelper.getWritableDatabase();
        sharedPreferences = this.getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE);
        defaultFragmentLoad();
    }

    private void initViews() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAppCountTextView = (TextView) findViewById(R.id.app_count);

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
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void defaultFragmentLoad() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container, new ViolationFragment())
                .commit();
        loadAllAppsFragment();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}