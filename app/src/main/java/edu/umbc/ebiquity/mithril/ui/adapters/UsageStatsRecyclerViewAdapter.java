package edu.umbc.ebiquity.mithril.ui.adapters;

import android.content.Context;
import android.database.SQLException;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.model.UsageStats;
import edu.umbc.ebiquity.mithril.ui.fragments.coreactivityfragments.UsageStatsFragment.OnListFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link UsageStats} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class UsageStatsRecyclerViewAdapter extends RecyclerView.Adapter<UsageStatsRecyclerViewAdapter.ViewHolder> {

    private final List<UsageStats> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context context;
    private View view;

    public UsageStatsRecyclerViewAdapter(List<UsageStats> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_usagestats, parent, false);
        context = view.getContext();
        return new UsageStatsRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        String appPkgName = mValues.get(position).getPackageName();
        try {
            holder.mAppIcon.setImageDrawable(mValues.get(position).getIcon());
            holder.mAppLbl.setText(mValues.get(position).getLabel());
            holder.mAppUsageDetail.setText(getUsageDetails(appPkgName));
            holder.mAppLastUsedTime.setText(getStringForLastTimeUsed(mValues.get(position).getLastTimeUsed()));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onListFragmentInteraction(holder.mItem);
                    }
                }
            });
        } catch (SQLException e) {
            Log.e(MithrilApplication.getDebugTag(), "Could not find " + e);
        }
    }

    private CharSequence getStringForLastTimeUsed(long lastTimeUsed) {
        Calendar rightNow = Calendar.getInstance();

        Calendar lastTime = new GregorianCalendar();
        lastTime.setTimeInMillis(lastTimeUsed);
        if (lastTime.get(Calendar.YEAR) < rightNow.get(Calendar.YEAR))
            return MithrilApplication.NEVER_SEEN;

        return DateUtils.getRelativeTimeSpanString(lastTimeUsed,
                rightNow.getTimeInMillis(),
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE);
    }

    private String getUsageDetails(String appPkgName) {
        return "No usage data yet!";
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public static class AppNameComparator implements Comparator<android.app.usage.UsageStats> {
        private Map<String, String> mAppLabelList;
        AppNameComparator(Map<String, String> appList) {
            mAppLabelList = appList;
        }
        @Override
        public final int compare(android.app.usage.UsageStats a, android.app.usage.UsageStats b) {
            String alabel = mAppLabelList.get(a.getPackageName());
            String blabel = mAppLabelList.get(b.getPackageName());
            return alabel.compareTo(blabel);
        }
    }

    public static class LastTimeUsedComparator implements Comparator<android.app.usage.UsageStats> {
        @Override
        public final int compare(android.app.usage.UsageStats a, android.app.usage.UsageStats b) {
            // return by descending order
            return (int)(b.getLastTimeUsed() - a.getLastTimeUsed());
        }
    }

    public static class UsageTimeComparator implements Comparator<android.app.usage.UsageStats> {
        @Override
        public final int compare(android.app.usage.UsageStats a, android.app.usage.UsageStats b) {
            return (int)(b.getTotalTimeInForeground() - a.getTotalTimeInForeground());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mAppIcon;
        public final TextView mAppLbl;
        public final TextView mAppUsageDetail;
        public final TextView mAppLastUsedTime;
        public UsageStats mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mAppIcon = (ImageView) view.findViewById(R.id.usagestats_app_icon);
            mAppLbl = (TextView) view.findViewById(R.id.usagestats_app_name);
            mAppUsageDetail = (TextView) view.findViewById(R.id.usagestats_app_usage);
            mAppLastUsedTime = (TextView) view.findViewById(R.id.usagestats_app_last_launch);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mAppLbl.getText() + "'";
        }
    }
}
