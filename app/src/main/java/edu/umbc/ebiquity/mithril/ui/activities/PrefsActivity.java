package edu.umbc.ebiquity.mithril.ui.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.ui.fragments.prefsactivityfragments.PrefsFragment;
import edu.umbc.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

public class PrefsActivity extends AppCompatActivity {
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_prefs);
        setSupportActionBar(toolbar);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(R.id.container_prefs, new PrefsFragment())
                .commit();

        fab = (FloatingActionButton) findViewById(R.id.fab_prefs);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSnackbar(view, view.getContext().getResources().getString(R.string.functionality_not_active_yet));
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

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

    @Override
    public void onBackPressed() {
        SharedPreferences sharedPreferences = getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE);
        if (!sharedPreferences.getBoolean(MithrilApplication.getPrefAllDoneKey(), false))
            PermissionHelper.toast(this, "Click on \"" + this.getResources().getString(R.string.pref_all_done_title) + "\" at the top, to go to app home...");
        else
            super.onBackPressed();
    }
}