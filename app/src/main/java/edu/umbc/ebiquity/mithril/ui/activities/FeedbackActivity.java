package edu.umbc.ebiquity.mithril.ui.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import edu.umbc.ebiquity.mithril.R;

public class FeedbackActivity extends AppCompatActivity {
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab_feedback);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSnackbar(view, view.getContext().getResources().getString(R.string.functionality_not_active_yet));
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
}