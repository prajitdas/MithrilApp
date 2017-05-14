package edu.umbc.ebiquity.mithril.ui.fragments.coreactivityfragments;

import android.app.AppOpsManager;
import android.app.Fragment;
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
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.model.Resource;
import edu.umbc.ebiquity.mithril.data.model.UsageStats;
import edu.umbc.ebiquity.mithril.ui.adapters.UsageStatsRecyclerViewAdapter;
import edu.umbc.ebiquity.mithril.util.specialtasks.appops.AppOpsState;
import edu.umbc.ebiquity.mithril.util.specialtasks.appops.MithrilAppOpsManager;
import edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions.AppOpsException;

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
    private final ArrayList<android.app.usage.UsageStats> mPackageStats = new ArrayList<>();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
            recyclerView.setAdapter(new UsageStatsRecyclerViewAdapter(usageStats, mListener));
        }
    }

    private void initData() {
        mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        mPm = context.getPackageManager();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -5);
        final List<android.app.usage.UsageStats> stats =
                mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, cal.getTimeInMillis(), System.currentTimeMillis());
        if (stats == null) {
            return;
        }
        ArrayMap<String, android.app.usage.UsageStats> map = new ArrayMap<>();
        final int statCount = stats.size();
        for (int i = 0; i < statCount; i++) {
            final android.app.usage.UsageStats pkgStats = stats.get(i);
            // load application labels for each application
            android.app.usage.UsageStats existingStats = map.get(pkgStats.getPackageName());
            if (existingStats == null) {
                map.put(pkgStats.getPackageName(), pkgStats);
            } else {
                existingStats.add(pkgStats);
            }
        }
        mPackageStats.addAll(map.values());
        for(android.app.usage.UsageStats usageStat : mPackageStats) {
            try {
                ApplicationInfo appInfo = mPm.getApplicationInfo(usageStat.getPackageName(), 0);
                String label = appInfo.loadLabel(mPm).toString();
                mAppLabelMap.put(usageStat.getPackageName(), label);

                UsageStats tempUsageStat = new UsageStats();
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
                String lastPermGroup = "";
                List<AppOpsState.AppOpEntry> entries = mState.buildState(AppOpsState.LOCATION_TEMPLATE,
                        appInfo.uid, usageStat.getPackageName());
                Log.d(MithrilApplication.getDebugTag(), "Whoa!"+Integer.toString(entries.size()));
                for (final AppOpsState.AppOpEntry entry : entries) {
                    Resource tempRes = new Resource();
                    final MithrilAppOpsManager.OpEntry firstOp = entry.getOpEntry(0);
                    String perm = MithrilAppOpsManager.opToPermission(firstOp.getOp());
                    if (perm != null) {
                        try {
                            PermissionInfo pi = mPm.getPermissionInfo(perm, 0);
                            tempRes.setResourceName(pi.packageName);
                            if (pi.group != null && !lastPermGroup.equals(pi.group)) {
                                lastPermGroup = pi.group;
                                PermissionGroupInfo pgi = mPm.getPermissionGroupInfo(pi.group, 0);
                                if (pgi.icon != 0) {
                                    tempRes.setIcon(pgi.loadIcon(mPm));
                                }
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            Log.e(MithrilApplication.getDebugTag(), e.getMessage());
                        }
                    }
                    tempRes.setLabel(entry.getSwitchText(mState).toString());
                    tempRes.setRelativeLastTimeUsed(entry.getTimeText(context, true).toString());
                    /**
                     * Code to change operations will not be used right now
                    final MithrilAppOpsManager appOps = new MithrilAppOpsManager((AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE));
                    Switch sw = (Switch) view.findViewById(R.id.switchWidget);
                    final int switchOp = MithrilAppOpsManager.opToSwitch(firstOp.getOp());
                    try {
                        int checkedVal = appOps.checkOp(switchOp, entry.getPackageOps().getUid(), entry.getPackageOps().getPackageName());
                        sw.setChecked(checkedVal == AppOpsManager.MODE_ALLOWED);
                        sw.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
                            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                try {
                                    appOps.setMode(switchOp, entry.getPackageOps().getUid(),
                                    entry.getPackageOps().getPackageName(), isChecked
                                    ? AppOpsManager.MODE_ALLOWED : AppOpsManager.MODE_IGNORED);
                                } catch (AppOpsException e) {
                                    Log.e(MithrilApplication.getDebugTag(), e.getMessage());
                                }
                            }
                        });
                    } catch (AppOpsException e) {
                        Log.e(MithrilApplication.getDebugTag(), e.getMessage());
                    }
                    */
                    tempListOfResource.add(tempRes);
                }
                tempUsageStat.setResourcesUsed(tempListOfResource);
                usageStats.add(tempUsageStat);
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
        mDisplayOrder= sortOrder;
        sortList();
    }

    public void sortList() {
        if (mDisplayOrder == _DISPLAY_ORDER_USAGE_TIME) {
            if (localLOGV) Log.i(MithrilApplication.getDebugTag(), "Sorting by usage time");
            Collections.sort(usageStats, mUsageTimeComparator);
        } else if (mDisplayOrder == _DISPLAY_ORDER_LAST_TIME_USED) {
            if (localLOGV) Log.i(MithrilApplication.getDebugTag(), "Sorting by last time used");
            Collections.sort(usageStats, mLastTimeUsedComparator);
        } else if (mDisplayOrder == _DISPLAY_ORDER_APP_NAME) {
            if (localLOGV) Log.i(MithrilApplication.getDebugTag(), "Sorting by application name");
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
        void onListFragmentInteraction(UsageStats item);
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
            return (int)(b.getLastTimeUsed() - a.getLastTimeUsed());
        }
    }

    public static class UsageTimeComparator implements Comparator<UsageStats> {
        @Override
        public final int compare(UsageStats a, UsageStats b) {
            return (int)(b.getTotalTimeInForeground() - a.getTotalTimeInForeground());
        }
    }
}
