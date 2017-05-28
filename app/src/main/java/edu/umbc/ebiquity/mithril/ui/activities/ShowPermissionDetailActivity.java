package edu.umbc.ebiquity.mithril.ui.activities;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.model.components.AppData;
import edu.umbc.ebiquity.mithril.ui.fragments.showpermissiondetailsactivityfragments.PermissionDetailFragment;

public class ShowPermissionDetailActivity extends AppCompatActivity
        implements PermissionDetailFragment.OnListFragmentInteractionListener {

    private String permGroupName;
    private String permGroupLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * ListActivity has a default layout that consists of a single, full-screen list in the center of the screen.
         * However, if you desire, you can customize the screen layout by setting your own view layout with setContentView() in onCreate().
         * To do this, your own view MUST contain a ListView object with the id "@android:id/list" (or list if it's in code)
         */
        setContentView(R.layout.activity_show_permission_detail);
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_view_permission_details);
        setSupportActionBar(toolbar);

        permGroupName = getIntent().getStringExtra(MithrilApplication.getPrefKeyPermGroupName());
        permGroupLabel = getIntent().getStringExtra(MithrilApplication.getPrefKeyPermGroupLabel());

        TextView mTxtViewPermGroup = (TextView) findViewById(R.id.textViewPermissionDetails);
        mTxtViewPermGroup.setText(permGroupLabel);

        loadPermissionDetailFragment();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Add the fragment that will allow us to show and interact with the permissions for the current app
     */
    private void loadPermissionDetailFragment() {
        Bundle data = new Bundle();
        data.putString(MithrilApplication.getPrefKeyPermGroupName(), permGroupName);

        PermissionDetailFragment permissionDetailFragment = new PermissionDetailFragment();
        permissionDetailFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_permission_details, permissionDetailFragment)
                .commit();
    }

    @Override
    public void onListFragmentInteraction(AppData item) {
        //TODO Do something with the perm selected
    }
}
