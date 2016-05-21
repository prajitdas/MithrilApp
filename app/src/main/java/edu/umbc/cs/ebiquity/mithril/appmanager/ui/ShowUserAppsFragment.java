package edu.umbc.cs.ebiquity.mithril.appmanager.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
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

import edu.umbc.cs.ebiquity.mithril.appmanager.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.appmanager.ui.specialFeatures.DividerItemDecoration;
import edu.umbc.cs.ebiquity.mithril.mithrilappmanager.R;
import edu.umbc.cs.ebiquity.mithril.appmanager.data.model.AppMetadata;
import edu.umbc.cs.ebiquity.mithril.appmanager.ui.adapters.InstalledAppsRecyclerViewAdapter;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ShowUserAppsFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private OnListFragmentLongInteractionListener mListenerLongInteraction;

    /**
     * An array of violation items.
     */
    private List<AppMetadata> appMetadataItems = new ArrayList<>();
    private Map<String, AppMetadata> appMetadataMap = new HashMap<>();
    private PackageManager packageManager;
    private View view;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ShowUserAppsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ShowUserAppsFragment newInstance(int columnCount) {
        ShowUserAppsFragment fragment = new ShowUserAppsFragment();
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
        view = inflater.inflate(R.layout.fragment_apps_list, container, false);
        packageManager = view.getContext().getPackageManager();
        initData();
        initView();
        sharedPreferences.edit().putInt(MithrilApplication.getSharedPreferenceAppCount(), appMetadataItems.size());
        return view;
    }

    private void initView() {
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new InstalledAppsRecyclerViewAdapter(appMetadataItems, mListener, mListenerLongInteraction));

            /**
             * Item decoration added
             */
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

            recyclerView.setHasFixedSize(true);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
    }

    /**
     * Finds all the applications on the phone and stores them in a database accessible to the whole app
     */
    private void initData() {
        sharedPreferences = view.getContext().getSharedPreferences(MithrilApplication.getSharedPreferencesName(), Context.MODE_PRIVATE);
        /**
         * Data loading: get all apps
         */

        getAllApps();

        for(Map.Entry<String, AppMetadata> entry : appMetadataMap.entrySet()) {
//            Log.d("MithrilAppManager", entry.toString());
            appMetadataItems.add(entry.getValue());
        }
        Collections.sort(appMetadataItems);
    }

    private void getAllApps() {
        int flags = PackageManager.GET_META_DATA |
                PackageManager.GET_SHARED_LIBRARY_FILES |
                PackageManager.GET_PERMISSIONS;
        for(PackageInfo pack : packageManager.getInstalledPackages(flags)) {
//            if ((pack.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1) {
            if ((pack.applicationInfo.flags) != 1) {
                try {
                    AppMetadata tempAppMetaData = new AppMetadata("dummyApp");
                    if (pack.packageName != null) {
                        tempAppMetaData.setPackageName(pack.packageName);
                        tempAppMetaData.setAppName(pack.applicationInfo.loadLabel(packageManager).toString());
                        tempAppMetaData.setVersionInfo(pack.versionName);
                        tempAppMetaData.setIcon(((BitmapDrawable) pack.applicationInfo.loadIcon(packageManager)).getBitmap());
                    }
                    appMetadataMap.put(tempAppMetaData.getPackageName(), tempAppMetaData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

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
        void onListFragmentInteraction(AppMetadata item);
    }
    public interface OnListFragmentLongInteractionListener {
        void onListFragmentLongInteraction(List<AppMetadata> items);
    }
}