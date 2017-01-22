package edu.umbc.cs.ebiquity.mithril.ui.fragments.mainactivityfragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.R;
import edu.umbc.cs.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.cs.ebiquity.mithril.data.model.AppData;
import edu.umbc.cs.ebiquity.mithril.ui.adapters.InstalledAppsRecyclerViewAdapter;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class AppsFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private MithrilDBHelper mithrilDBHelper;
    private SQLiteDatabase mithrilDB;
    private SharedPreferences sharedPreferences;
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private OnListFragmentLongInteractionListener mListenerLongInteraction;
    private String mAppDisplayType;

    /**
     * An array of app items.
     */
    private List<AppData> allAppDataItems = new ArrayList<>();
    private List<AppData> systemAppDataItems = new ArrayList<>();
    private List<AppData> userAppDataItems = new ArrayList<>();
    private Map<String, AppData> appMetadataMap = new HashMap<>();
    private View view;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AppsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static AppsFragment newInstance(int columnCount, String appDisplayType) {
        AppsFragment fragment = new AppsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(MithrilApplication.getPrefKeyAppDisplayType(), appDisplayType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mAppDisplayType = getArguments().getString(MithrilApplication.getPrefKeyAppDisplayType());
        }
//        Log.d(MithrilApplication.getDebugTag(), mAppDisplayType);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_apps_list, container, false);
        initData();
        initViews();
        return view;
    }

    private void initViews() {
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;

            if (mColumnCount <= 1)
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            else
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));

            if (mAppDisplayType.equals(MithrilApplication.getPrefKeyAllAppsDisplay()))
                recyclerView.setAdapter(new InstalledAppsRecyclerViewAdapter(allAppDataItems, mListener, mListenerLongInteraction));
            else if (mAppDisplayType.equals(MithrilApplication.getPrefKeySystemAppsDisplay()))
                recyclerView.setAdapter(new InstalledAppsRecyclerViewAdapter(systemAppDataItems, mListener, mListenerLongInteraction));
            else if (mAppDisplayType.equals(MithrilApplication.getPrefKeyUserAppsDisplay()))
                recyclerView.setAdapter(new InstalledAppsRecyclerViewAdapter(userAppDataItems, mListener, mListenerLongInteraction));

            recyclerView.setHasFixedSize(true);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
    }

    /**
     * Finds all the applications on the phone and stores them in a database accessible to the whole app
     */
    private void initData() {
        mithrilDBHelper = new MithrilDBHelper(view.getContext());
        mithrilDB = mithrilDBHelper.getWritableDatabase();
        sharedPreferences = view.getContext().getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE);

        if (mAppDisplayType.equals(MithrilApplication.getPrefKeyAllAppsDisplay())) {
            /**
             * Data loading: get all apps
             */
            getAllApps();

            for (Map.Entry<String, AppData> entry : appMetadataMap.entrySet()) {
//            Log.d("MithrilAppManager", entry.toString());
                allAppDataItems.add(entry.getValue());
            }
            Collections.sort(allAppDataItems);
            appMetadataMap.clear();
        } else if (mAppDisplayType.equals(MithrilApplication.getPrefKeySystemAppsDisplay())) {

            /**
             * Data loading: get all system apps
             */
            getSystemApps();

            for (Map.Entry<String, AppData> entry : appMetadataMap.entrySet()) {
//            Log.d("MithrilAppManager", entry.toString());
                systemAppDataItems.add(entry.getValue());
            }
            Collections.sort(systemAppDataItems);
            appMetadataMap.clear();
        } else if (mAppDisplayType.equals(MithrilApplication.getPrefKeyUserAppsDisplay())) {
            /**
             * Data loading: get all user apps
             */
            getUserApps();

            for (Map.Entry<String, AppData> entry : appMetadataMap.entrySet()) {
//            Log.d("MithrilAppManager", entry.toString());
                userAppDataItems.add(entry.getValue());
            }
            Collections.sort(userAppDataItems);
            appMetadataMap.clear();
        }

        mithrilDB.close();
    }

    /**
     * Get all apps that are installed on the device by reading the MithrilDB
     */
    private void getAllApps() {
        List<AppData> tempList = mithrilDBHelper.findAllApps(mithrilDB);
        SharedPreferences.Editor editor = view.getContext().getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE).edit();
        editor.putInt(MithrilApplication.getPrefKeyAppCount(), tempList.size());
        editor.commit();
        for (AppData app : tempList)
            appMetadataMap.put(app.getPackageName(), app);
    }

    /**
     * Get system apps that are installed on the device by reading the MithrilDB
     */
    private void getSystemApps() {
        for (AppData app : mithrilDBHelper.findAllApps(mithrilDB))
            if (app.getAppType().equals(MithrilApplication.getPrefKeySystemAppsDisplay()))
                appMetadataMap.put(app.getPackageName(), app);
    }

    /**
     * Get user apps that are installed on the device by reading the MithrilDB
     */
    private void getUserApps() {
        for (AppData app : mithrilDBHelper.findAllApps(mithrilDB))
            if (app.getAppType().equals(MithrilApplication.getPrefKeyUserAppsDisplay()))
                appMetadataMap.put(app.getPackageName(), app);
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
        void onListFragmentInteraction(AppData item);
    }

    public interface OnListFragmentLongInteractionListener {
        void onListFragmentLongInteraction(List<AppData> items);
    }
}