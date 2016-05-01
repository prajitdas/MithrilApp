package edu.umbc.cs.ebiquity.mithril.mithrilappmanager.ui;

import android.app.Fragment;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.umbc.cs.ebiquity.mithril.mithrilappmanager.R;
import edu.umbc.cs.ebiquity.mithril.mithrilappmanager.data.model.AppMetadata;
import edu.umbc.cs.ebiquity.mithril.mithrilappmanager.ui.adapters.InstalledAppsRecyclerViewAdapter;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ShowAppsFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

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
    public ShowAppsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ShowAppsFragment newInstance(int columnCount) {
        ShowAppsFragment fragment = new ShowAppsFragment();
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

        for(AppMetadata info : appMetadataItems)
            Log.d("AppItems: ",info.toString());

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new InstalledAppsRecyclerViewAdapter(appMetadataItems, mListener));
        }
        return view;
    }

    /**
     * Finds all the applications on the phone and stores them in a database accessible to the whole app
     */
    private void initData() {
        /**
         * Data loading
         */
        int flags = PackageManager.GET_META_DATA |
                PackageManager.GET_SHARED_LIBRARY_FILES |
//                PackageManager.GET_UNINSTALLED_PACKAGES |
                PackageManager.GET_PERMISSIONS;
        for(PackageInfo pack : packageManager.getInstalledPackages(flags)) {
            if ((pack.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1) {
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

        for(Map.Entry<String, AppMetadata> entry : appMetadataMap.entrySet()) {
            Log.d("MithrilAppManager", entry.toString());
            appMetadataItems.add(entry.getValue());
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
}
