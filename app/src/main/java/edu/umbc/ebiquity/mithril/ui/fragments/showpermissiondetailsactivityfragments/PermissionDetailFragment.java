package edu.umbc.ebiquity.mithril.ui.fragments.showpermissiondetailsactivityfragments;

import android.app.Fragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.data.model.components.AppData;
import edu.umbc.ebiquity.mithril.ui.adapters.PermissionDetailRecyclerViewAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class PermissionDetailFragment extends Fragment {
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private MithrilDBHelper mithrilDBHelper;
    private SQLiteDatabase mithrilDB;
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private View view;

    /**
     * An array of apps
     */
    private List<AppData> apps = new ArrayList<>();
    private String mPermGroup;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PermissionDetailFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PermissionDetailFragment newInstance(int columnCount) {
        PermissionDetailFragment fragment = new PermissionDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mPermGroup = getArguments().getString(MithrilApplication.getPrefKeyPermGroupName());
        }
//        Log.d(MithrilApplication.getDebugTag(), mAppPackageName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_app_detail_list, container, false);
        initData();
        iniViews();
        return view;
    }

    private void iniViews() {
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new PermissionDetailRecyclerViewAdapter(apps, mListener));
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
    }

    private void initData() {
        initDB(view.getContext());
        apps = mithrilDBHelper.findAppsByPermGroups(mithrilDB, mPermGroup);
        closeDB();

        Collections.sort(apps);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void initDB(Context context) {
        // Let's get the DB instances loaded too
        mithrilDBHelper = new MithrilDBHelper(context);
        mithrilDB = mithrilDBHelper.getWritableDatabase();
    }

    private void closeDB() {
        if (mithrilDB != null)
            mithrilDB.close();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(AppData item);
    }
}
