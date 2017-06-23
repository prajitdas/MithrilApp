package edu.umbc.ebiquity.mithril.ui.adapters;

import android.app.usage.UsageStats;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.model.rules.AppUsageStats;
import edu.umbc.ebiquity.mithril.data.model.rules.Resource;
import edu.umbc.ebiquity.mithril.ui.fragments.coreactivityfragments.UsageStatsFragment.OnListFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link AppUsageStats} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class UsageStatsRecyclerViewAdapter extends RecyclerView.Adapter<UsageStatsRecyclerViewAdapter.ViewHolder> {
    private final List<AppUsageStats> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context context;
    private View view;

    public UsageStatsRecyclerViewAdapter(List<AppUsageStats> items, OnListFragmentInteractionListener listener) {
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
        try {
            holder.mAppIcon.setImageDrawable(context.getPackageManager().getApplicationIcon(mValues.get(position).getPackageName()));

            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(mValues.get(position).getPackageName(), 0);
            holder.mAppLbl.setText(applicationInfo != null ? context.getPackageManager().getApplicationLabel(applicationInfo) : "Some app");

            holder.mAppUsageDetail.setText(getStringForHowLongWasUsed(mValues.get(position).getTotalTimeInForeground()));
            holder.mAppLastUsedTime.setText(getStringForLastTimeUsed(mValues.get(position).getLastTimeUsed()));
            if(mValues.get(position).getResourcesUsed().size() > 0)
                holder.mResourceUsed.setText(mValues.get(position).getResourcesUsed().get(0).toString());
            else
                holder.mResourceUsed.setText("no resources used yet");

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
            Log.e(MithrilAC.getDebugTag(), "Could not find " + e);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(MithrilAC.getDebugTag(), "Could not find the package" + e);
        }
    }

    private CharSequence getStringForHowLongWasUsed(long totalTimeInForeground) {
        long second = (totalTimeInForeground / 1000) % 60;
        long minute = (totalTimeInForeground / (1000 * 60)) % 60;
        long hour = (totalTimeInForeground / (1000 * 60 * 60)) % 24;
        return "Used for: " + Long.toString(hour) + "h:" + Long.toString(minute) + "m:" + Long.toString(second) + "s";
    }

    private CharSequence getStringForLastTimeUsed(long lastTimeUsed) {
        Calendar rightNow = Calendar.getInstance();

        Calendar lastTime = new GregorianCalendar();
        lastTime.setTimeInMillis(lastTimeUsed);
        if (lastTime.get(Calendar.YEAR) < rightNow.get(Calendar.YEAR))
            return context.getResources().getText(R.string.app_ops_never_used);

        return DateUtils.getRelativeTimeSpanString(lastTimeUsed,
                rightNow.getTimeInMillis(),
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE);
    }

    private String getUsageDetails(List<Resource> resourcesUsed) {
        StringBuffer resBuffer = new StringBuffer();
        for (Resource res : resourcesUsed) {
            resBuffer.append(res.getResourceName());
            resBuffer.append(", ");
            resBuffer.append(res.getRelativeLastTimeUsed());
            resBuffer.append(", ");
            resBuffer.append(res.getGroup());
            resBuffer.append("; ");
        }
        return resBuffer.toString();
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mAppIcon;
        public final TextView mAppLbl;
        public final TextView mAppUsageDetail;
        public final TextView mAppLastUsedTime;
        public final TextView mResourceUsed;
        public AppUsageStats mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mAppIcon = (ImageView) view.findViewById(R.id.usagestats_app_icon);
            mAppLbl = (TextView) view.findViewById(R.id.usagestats_app_name);
            mAppUsageDetail = (TextView) view.findViewById(R.id.usagestats_app_usage);
            mAppLastUsedTime = (TextView) view.findViewById(R.id.usagestats_app_last_launch);
            mResourceUsed = (TextView) view.findViewById(R.id.resourceUsed);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mAppLbl.getText() + "'";
        }
    }
}
