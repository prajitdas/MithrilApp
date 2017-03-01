package edu.umbc.ebiquity.mithril.ui.fragments.mainactivityfragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.data.model.Violation;
import edu.umbc.ebiquity.mithril.ui.adapters.ViolationRecyclerViewAdapter;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ViolationFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    /**
     * An array of violation items.
     */
    public List<Violation> violationItems = new ArrayList<Violation>();
    /**
     * A map of violation items, by ID.
     */
    public Map<Integer, Violation> violationItemsMap = new HashMap<Integer, Violation>();
    private MithrilDBHelper mithrilDBHelper;
    private SQLiteDatabase mithrilDB;
    private View view;
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ViolationFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ViolationFragment newInstance(int columnCount) {
        ViolationFragment fragment = new ViolationFragment();
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_violation_list, container, false);

        initData();

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new ViolationRecyclerViewAdapter(violationItems, mListener));
        }
        return view;
    }

    private void initData() {
        initDB(view.getContext());
        violationItems = mithrilDBHelper.findAllViolations(mithrilDB);

        //TODO remove this later. This is for demo purposes.
        SharedPreferences sharedPref = getActivity().getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE);
        String appPkgName = sharedPref.getString(MithrilApplication.getPrefKeyAppPkgName(), "Youtube");
        if (appPkgName.equals("com.google.android.youtube") &&
                sharedPref.contains(MithrilApplication.getPrefKeyCurrentTime()))
            violationItems.add(new Violation("App package name: " +
                    sharedPref.getString(MithrilApplication.getPrefKeyAppPkgName(), "Youtube") +
                    "Location context: " +
                    sharedPref.getString(MithrilApplication.getPrefKeyCurrentLocation(), "location") +
                    "Temporal context: " +
                    sharedPref.getString(MithrilApplication.getPrefKeyCurrentTime(), "time"), 1, 1, true));
        else if (appPkgName.equals("com.google.android.youtube") &&
                !sharedPref.contains(MithrilApplication.getPrefKeyCurrentTime()))
            violationItems.add(new Violation("App package name: " +
                    sharedPref.getString(MithrilApplication.getPrefKeyAppPkgName(), "Youtube") +
                    "Location context: " +
                    sharedPref.getString(MithrilApplication.getPrefKeyCurrentLocation(), "location"), 1, 1, true));
        else
            violationItems.clear();

        // Add some violation items.
        for (Violation item : violationItems)
            violationItemsMap.put(item.getId(), item);
        closeDB();
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Violation item);
    }
}