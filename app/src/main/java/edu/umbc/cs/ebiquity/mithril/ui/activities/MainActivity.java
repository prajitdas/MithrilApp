package edu.umbc.cs.ebiquity.mithril.ui.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.Calendar;
import java.util.List;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.R;
import edu.umbc.cs.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.cs.ebiquity.mithril.data.model.AppData;
import edu.umbc.cs.ebiquity.mithril.data.model.PermData;
import edu.umbc.cs.ebiquity.mithril.data.model.Violation;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.EmptyFragment;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.NothingHereFragment;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.mainactivityfragments.AboutFragment;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.mainactivityfragments.AppsFragment;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.mainactivityfragments.BroadcastReceiversFragment;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.mainactivityfragments.ContentProvidersFragment;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.mainactivityfragments.PermissionsFragment;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.mainactivityfragments.PrefsFragment;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.mainactivityfragments.ReloadDefaultDataFragment;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.mainactivityfragments.ServicesFragment;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.mainactivityfragments.ViolationFragment;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.mainactivityfragments.dummy.DummyContent;

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
                    ReloadDefaultDataFragment.OnFragmentInteractionListener,
        EmptyFragment.OnFragmentInteractionListener,
        NothingHereFragment.OnFragmentInteractionListener {

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
            loadViolationsFragment();
        } else if (id == R.id.nav_perm) {
            loadPermissionsFragment();
        } else if (id == R.id.nav_user) {
            loadUserAppsFragment();
        } else if (id == R.id.nav_system) {
            loadSystemAppsFragment();
        } else if (id == R.id.nav_all) {
            loadAllAppsFragment();
        } else if (id == R.id.nav_services) {
            loadServicesFragment();
        } else if (id == R.id.nav_bcastreceivers) {
            loadBroadcastReceiversFragment();
        } else if (id == R.id.nav_contentproviders) {
            loadContentProvidersFragment();
        } else if (id == R.id.nav_settings) {
            loadPrefsFragment();
        } else if (id == R.id.nav_reload) {
            loadReloadDefaultDataFragment();
        } else if (id == R.id.nav_about) {
            loadAboutFragment();
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        } else
            startMainActivityTasks();
    }

    private void startMainActivityTasks() {
        initViews();
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
        /**
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
        Log.d(MithrilApplication.getDebugTag(), Integer.toString(hourofday));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        if (hourofday < 12 && hourofday >= 6)
            headerView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.csee_morning, getTheme()));
        else if (hourofday < 18 && hourofday >= 12)
            headerView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.csee_afternoon, getTheme()));
        else
            headerView.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.csee_evening, getTheme()));
//        } else {
//            if (hourofday < 12 && hourofday >= 6)
//                headerView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.csee_morning));
//            else if (hourofday < 18 && hourofday >= 12)
//                headerView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.csee_afternoon));
//            else
//                headerView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.csee_evening));
//        }
    }

    private void defaultFragmentLoad() {
//        If we are loading the app list we don't need the above two lines as we take care of that in the loadAllAppsFragment() method
//        loadViolationsFragment();
        loadUserAppsFragment();
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

    private void loadReloadDefaultDataFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_main, new ReloadDefaultDataFragment())
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
        if(isViolationFragmentListEmpty())
            loadEmptyFragment();
        else {
//        For ViolationFragment() we will have to manage in a different manner
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container_main, new ViolationFragment())
                    .commit();
        }
    }

    private void loadBroadcastReceiversFragment(){
        if(isBroadcastReceiverListEmpty())
            loadNothingHereFragment();
        else {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container_main, new BroadcastReceiversFragment())
                    .commit();
        }
    }

    private void loadContentProvidersFragment(){
        if(isContentProvidersListEmpty())
            loadNothingHereFragment();
        else {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container_main, new ContentProvidersFragment())
                    .commit();
        }
    }

    private void loadServicesFragment(){
        if(isServicesListEmpty())
            loadNothingHereFragment();
        else {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container_main, new ServicesFragment())
                    .commit();
        }
    }

    private void loadPermissionsFragment(){
        if(isPermissionsListEmpty())
            loadNothingHereFragment();
        else {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.container_main, new PermissionsFragment())
                    .commit();
        }
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

    private boolean isViolationFragmentListEmpty() {
        /*
         * We find out how many violations are there in the database.
         * If there are none, we will load the EmptyFragment
         */
        violationItems = mithrilDBHelper.findAllViolations(mithrilDB);
        return !(violationItems == null || violationItems.size() <= 0);
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
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

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
}