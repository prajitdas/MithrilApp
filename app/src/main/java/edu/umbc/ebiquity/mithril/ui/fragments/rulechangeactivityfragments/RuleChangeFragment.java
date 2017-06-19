package edu.umbc.ebiquity.mithril.ui.fragments.rulechangeactivityfragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.data.model.rules.PolicyRule;
import edu.umbc.ebiquity.mithril.data.model.rules.Violation;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticActivity;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticLocation;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticNearActor;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticTime;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticUserContext;
import edu.umbc.ebiquity.mithril.ui.adapters.SemanticUserContextRecyclerViewAdapter;

/**
 * Created by prajit on 6/16/17.
 */

public class RuleChangeFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    /**
     * An array of violation items.
     */
    private List<SemanticUserContext> contextPieces = new ArrayList<>();
    //    /**
//     * A map of violation items, by ID.
//     */
//    public Map<String, Violation> violationItemsMap = new HashMap<>();
    private SQLiteDatabase mithrilDB;
    private View view;
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private RuleChangeFragment.OnListFragmentInteractionListener mListener;
    private Violation violation;
    private SharedPreferences sharedPreferences;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RuleChangeFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RuleChangeFragment newInstance(int columnCount, Violation violation) {
        RuleChangeFragment fragment = new RuleChangeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putParcelable("rule", violation);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            violation = getArguments().getParcelable("rule");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_semanticusercontext_list, container, false);
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
            recyclerView.setAdapter(new SemanticUserContextRecyclerViewAdapter(contextPieces, mListener));
        }
        return view;
    }

    private void initData() {
        initDB(view.getContext());

        Gson retrieveDataGson = new Gson();
        String retrieveDataJson;
        SemanticUserContext semanticUserContext = null;
        List<PolicyRule> policyRules = MithrilDBHelper.getHelper(getActivity()).findAllPoliciesForAppWhenPerformingOp(
                mithrilDB,
                MithrilDBHelper.getHelper(getActivity()).findAppById(
                        mithrilDB,
                        violation.getAppId()).getPackageName(),
                violation.getOprId());
        for (PolicyRule policyRule : policyRules) {
            Pair<String, String> contextPiece = MithrilDBHelper.getHelper(getActivity()).findContextByID(mithrilDB, policyRule.getId());
            sharedPreferences = getActivity().getSharedPreferences(MithrilAC.getSharedPreferencesName(), Context.MODE_PRIVATE);
            retrieveDataJson = sharedPreferences.getString(contextPiece.first + contextPiece.second, "");
            if (contextPiece.first.equals(MithrilAC.getPrefKeyContextTypeLocation()))
                semanticUserContext = retrieveDataGson.fromJson(retrieveDataJson, SemanticLocation.class);
            else if (contextPiece.first.equals(MithrilAC.getPrefKeyContextTypeActivity()))
                semanticUserContext = retrieveDataGson.fromJson(retrieveDataJson, SemanticActivity.class);
            else if (contextPiece.first.equals(MithrilAC.getPrefKeyContextTypePresence()))
                semanticUserContext = retrieveDataGson.fromJson(retrieveDataJson, SemanticNearActor.class);
            else if (contextPiece.first.equals(MithrilAC.getPrefKeyContextTypeTemporal()))
                semanticUserContext = retrieveDataGson.fromJson(retrieveDataJson, SemanticTime.class);

            contextPieces.add(semanticUserContext);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RuleChangeFragment.OnListFragmentInteractionListener) {
            mListener = (RuleChangeFragment.OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        closeDB();
    }

    private void initDB(Context context) {
        // Let's get the DB instances loaded too
        mithrilDB = MithrilDBHelper.getHelper(context).getWritableDatabase();
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
        void onListFragmentInteraction(SemanticUserContext item);
    }
}