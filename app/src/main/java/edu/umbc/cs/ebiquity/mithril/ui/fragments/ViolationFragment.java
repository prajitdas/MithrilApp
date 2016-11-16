package edu.umbc.cs.ebiquity.mithril.ui.fragments;

import android.app.Fragment;
import android.content.Context;
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

import edu.umbc.cs.ebiquity.mithril.R;
import edu.umbc.cs.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.cs.ebiquity.mithril.data.model.Violation;
import edu.umbc.cs.ebiquity.mithril.ui.adapters.ViolationRecyclerViewAdapter;

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
        /**
         * Database creation and default data insertion, happens only once.
         */
        mithrilDBHelper = new MithrilDBHelper(view.getContext());
        mithrilDB = mithrilDBHelper.getWritableDatabase();
        violationItems = mithrilDBHelper.findAllViolations(mithrilDB);
        // Add some violation items.
        for (Violation item : violationItems)
            violationItemsMap.put(item.getId(), item);
        mithrilDB.close();
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
