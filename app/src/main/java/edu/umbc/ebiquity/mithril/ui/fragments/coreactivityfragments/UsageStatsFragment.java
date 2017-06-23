package edu.umbc.ebiquity.mithril.ui.fragments.coreactivityfragments;

import android.app.AppOpsManager;
import android.app.Fragment;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.model.rules.AppUsageStats;
import edu.umbc.ebiquity.mithril.data.model.rules.Resource;
import edu.umbc.ebiquity.mithril.ui.adapters.UsageStatsRecyclerViewAdapter;
import edu.umbc.ebiquity.mithril.util.specialtasks.appops.AppOpsState;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class UsageStatsFragment extends Fragment {
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // Constants defining order for display order
    private static final boolean localLOGV = false;
    private static final int _DISPLAY_ORDER_USAGE_TIME = 0;
    private static final int _DISPLAY_ORDER_LAST_TIME_USED = 1;
    private static final int _DISPLAY_ORDER_APP_NAME = 2;
    private final ArrayMap<String, String> mAppLabelMap = new ArrayMap<>();
    private final ArrayList<AppUsageStats> appUsageStats = new ArrayList<>();
    /**
     * An array of usage stats items.
     */
    public List<UsageStats> usageStats = new ArrayList<>();
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private View view;
    private int mDisplayOrder = _DISPLAY_ORDER_USAGE_TIME;
    private LastTimeUsedComparator mLastTimeUsedComparator = new LastTimeUsedComparator();
    private UsageTimeComparator mUsageTimeComparator = new UsageTimeComparator();
    private AppNameComparator mAppLabelComparator;
    private UsageStatsManager mUsageStatsManager;
    private PackageManager mPm;
    private Context context;
    private AppOpsState mState;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UsageStatsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static UsageStatsFragment newInstance(int columnCount) {
        UsageStatsFragment fragment = new UsageStatsFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_usagestats_list, container, false);
        context = view.getContext();
        mState = new AppOpsState(context);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
        initViews();
    }

    private void initViews() {
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new UsageStatsRecyclerViewAdapter(appUsageStats, mListener));
        }
    }

    private void initData() {
        mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        mPm = context.getPackageManager();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -5);
        final List<UsageStats> stats =
                mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, cal.getTimeInMillis(), System.currentTimeMillis());
        if (stats == null) {
            return;
        }
        ArrayMap<String, UsageStats> map = new ArrayMap<>();
        final int statCount = stats.size();
        for (int i = 0; i < statCount; i++) {
            final UsageStats pkgStats = stats.get(i);
            // load application labels for each application
            UsageStats existingStats = map.get(pkgStats.getPackageName());
            if (existingStats == null) {
                map.put(pkgStats.getPackageName(), pkgStats);
            } else {
                existingStats.add(pkgStats);
            }
        }
        List<UsageStats> someUsageStats = new ArrayList<>();
        someUsageStats.addAll(map.values());
        for (UsageStats usageStat : someUsageStats) {
            try {
                ApplicationInfo appInfo = mPm.getApplicationInfo(usageStat.getPackageName(), 0);
                String label = appInfo.loadLabel(mPm).toString();
                mAppLabelMap.put(usageStat.getPackageName(), label);

                AppUsageStats tempUsageStat = new AppUsageStats();
                tempUsageStat.setLastTimeUsed(usageStat.getLastTimeUsed());
                tempUsageStat.setTotalTimeInForeground(usageStat.getTotalTimeInForeground());
                tempUsageStat.setPackageName(usageStat.getPackageName());
                tempUsageStat.setLabel(label);
                tempUsageStat.setIcon(appInfo.loadIcon(mPm));

                /**
                 * TODO Right now we are unable to get the information of what operations were used.
                 * If we are able to do that we will handle that. For now let's work with app launches only!
                 */
                List<Resource> tempListOfResource = new ArrayList<>();
                List<AppOpsState.AppOpEntry> entries = mState.buildState(AppOpsState.ALL_OPS_TEMPLATE,
                        appInfo.uid, usageStat.getPackageName());
                Log.d(MithrilAC.getDebugTag(), "Whoa!" + Integer.toString(entries.size()));
                int position = 0;
                for (final AppOpsState.AppOpEntry entry : entries) {
                    AppOpsManager.OpEntry currEntry = entry.getOpEntry(position);
                    String appOpName = AppOpsManager.opToPermission(currEntry.getOp());
                    Log.d(MithrilAC.getDebugTag(), "Found usage of operation: " + appOpName);
                    Resource tempRes = null;
                    if (appOpName != null) {
                        try {
                            PermissionInfo pi = mPm.getPermissionInfo(appOpName, 0);
                            //                            tempRes.setResourceName(pi.packageName);
                            if (pi.group != null) {// && !lastPermGroup.equals(pi.group)) {
                                PermissionGroupInfo pgi = mPm.getPermissionGroupInfo(pi.group, 0);
                                // We care about the resource group because that tells us what was used!
                                if (pgi != null)
                                    tempRes = new Resource(
                                            pi.name,
                                            currEntry.getDuration(),
                                            currEntry.getOp(),
                                            currEntry.getTime(),
                                            entry.getTimeText(context, true).toString(),
                                            pgi.name,
                                            MithrilAC.getRiskForPerm(appOpName)
//                                        MithrilDBHelper.getHelper(context).findRiskLevelByPerm(mithrilDB, appOpName)
                                    );
                                else
                                    tempRes = new Resource(
                                            pi.name,
                                            currEntry.getDuration(),
                                            currEntry.getOp(),
                                            currEntry.getTime(),
                                            entry.getTimeText(context, true).toString(),
                                            MithrilAC.getNoPermissionGroupDesc(),
                                            MithrilAC.getRiskForPerm(appOpName)
//                                        MithrilDBHelper.getHelper(context).findRiskLevelByPerm(mithrilDB, appOpName)
                                    );
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            Log.e(MithrilAC.getDebugTag(), e.getMessage());
                        }
                    }
                    tempListOfResource.add(tempRes);
                }
                tempUsageStat.setResourcesUsed(tempListOfResource);
                appUsageStats.add(tempUsageStat);
            } catch (PackageManager.NameNotFoundException e) {
                // This package may be gone.
            }
        }
        // Sort list
        mAppLabelComparator = new AppNameComparator(mAppLabelMap);
        sortList(_DISPLAY_ORDER_LAST_TIME_USED);
    }

    public void sortList(int sortOrder) {
        if (mDisplayOrder == sortOrder) {
            // do nothing
            return;
        }
        mDisplayOrder = sortOrder;
        sortList();
    }

    public void sortList() {
        if (mDisplayOrder == _DISPLAY_ORDER_USAGE_TIME) {
            if (localLOGV) Log.i(MithrilAC.getDebugTag(), "Sorting by usage time");
            Collections.sort(usageStats, mUsageTimeComparator);
        } else if (mDisplayOrder == _DISPLAY_ORDER_LAST_TIME_USED) {
            if (localLOGV) Log.i(MithrilAC.getDebugTag(), "Sorting by last time used");
            Collections.sort(usageStats, mLastTimeUsedComparator);
        } else if (mDisplayOrder == _DISPLAY_ORDER_APP_NAME) {
            if (localLOGV) Log.i(MithrilAC.getDebugTag(), "Sorting by application name");
            Collections.sort(usageStats, mAppLabelComparator);
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(AppUsageStats item);
    }

    public static class AppNameComparator implements Comparator<UsageStats> {
        private Map<String, String> mAppLabelList;

        AppNameComparator(Map<String, String> appList) {
            mAppLabelList = appList;
        }

        @Override
        public final int compare(UsageStats a, UsageStats b) {
            String alabel = mAppLabelList.get(a.getPackageName());
            String blabel = mAppLabelList.get(b.getPackageName());
            return alabel.compareTo(blabel);
        }
    }

    public static class LastTimeUsedComparator implements Comparator<UsageStats> {
        @Override
        public final int compare(UsageStats a, UsageStats b) {
            // return by descending order
            return (int) (b.getLastTimeUsed() - a.getLastTimeUsed());
        }
    }

    public static class UsageTimeComparator implements Comparator<UsageStats> {
        @Override
        public final int compare(UsageStats a, UsageStats b) {
            return (int) (b.getTotalTimeInForeground() - a.getTotalTimeInForeground());
        }
    }
}