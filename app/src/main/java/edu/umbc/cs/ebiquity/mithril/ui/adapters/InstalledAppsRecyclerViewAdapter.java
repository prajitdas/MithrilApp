package edu.umbc.cs.ebiquity.mithril.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.umbc.cs.ebiquity.mithril.R;
import edu.umbc.cs.ebiquity.mithril.data.model.components.AppData;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.mainactivityfragments.AppsFragment.OnListFragmentInteractionListener;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.mainactivityfragments.AppsFragment.OnListFragmentLongInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link AppData} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class InstalledAppsRecyclerViewAdapter extends RecyclerView.Adapter<InstalledAppsRecyclerViewAdapter.ViewHolder> {

    private final List<AppData> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final OnListFragmentLongInteractionListener mListenerLongInteraction;
    private List<AppData> appsSelected = new ArrayList<>();

    public InstalledAppsRecyclerViewAdapter(List<AppData> items, OnListFragmentInteractionListener listener, OnListFragmentLongInteractionListener listenerLongInteraction) {
        mValues = items;
        mListener = listener;
        mListenerLongInteraction = listenerLongInteraction;
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
//        holder.mAppSelected.setChecked(false);

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

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null != mListenerLongInteraction) {
                    appsSelected.add(holder.mItem);
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListenerLongInteraction.onListFragmentLongInteraction(appsSelected);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final ImageView mAppIcon;
        private final TextView mAppName;
        private final TextView mAppVersion;
        //        private final CheckBox mAppSelected;
        private AppData mItem;

        private ViewHolder(View view) {
            super(view);
            mView = view;
            mAppIcon = (ImageView) view.findViewById(R.id.app_icon);
            mAppName = (TextView) view.findViewById(R.id.app_name);
            mAppVersion = (TextView) view.findViewById(R.id.app_version);
//            mAppSelected = (CheckBox) view.findViewById(R.id.app_selected);
//
//            mAppSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    //TODO When checked the app should be sent
//                }
//            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mAppName.getText() + "'";
        }
    }
}
