package edu.umbc.cs.ebiquity.mithril.mithrilappmanager.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import edu.umbc.cs.ebiquity.mithril.mithrilappmanager.R;
import edu.umbc.cs.ebiquity.mithril.mithrilappmanager.data.model.AppMetadata;
import edu.umbc.cs.ebiquity.mithril.mithrilappmanager.ui.ShowAppsFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link AppMetadata} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class InstalledAppsRecyclerViewAdapter extends RecyclerView.Adapter<InstalledAppsRecyclerViewAdapter.ViewHolder> {

    private final List<AppMetadata> mValues;
    private final OnListFragmentInteractionListener mListener;

    public InstalledAppsRecyclerViewAdapter(List<AppMetadata> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_apps, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mAppIcon.setImageBitmap(mValues.get(position).getIcon());
        holder.mAppName.setText(mValues.get(position).getAppName());
        holder.mAppVersion.setText(mValues.get(position).getVersionInfo());
        holder.mAppSelected.setChecked(false);

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
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mAppIcon;
        public final TextView mAppName;
        public final TextView mAppVersion;
        public final CheckBox mAppSelected;
        public AppMetadata mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mAppIcon = (ImageView) view.findViewById(R.id.app_icon);
            mAppName = (TextView) view.findViewById(R.id.app_name);
            mAppVersion = (TextView) view.findViewById(R.id.app_version);
            mAppSelected = (CheckBox) view.findViewById(R.id.app_selected);

            mAppSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //TODO When checked the app should be sent
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mAppName.getText() + "'";
        }
    }
}
