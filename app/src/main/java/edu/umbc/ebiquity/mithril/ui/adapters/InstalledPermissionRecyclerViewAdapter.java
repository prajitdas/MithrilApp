package edu.umbc.ebiquity.mithril.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.ui.fragments.coreactivityfragments.PermissionsFragment.OnListFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link String} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class InstalledPermissionRecyclerViewAdapter extends RecyclerView.Adapter<InstalledPermissionRecyclerViewAdapter.ViewHolder> {

    private final List<String> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context context;
    private View view;

    public InstalledPermissionRecyclerViewAdapter(List<String> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_permissions, parent, false);
        context = view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        if (mValues.get(position).equals(MithrilApplication.CONTACTS_PERMISSION_GROUP))
            holder.mPermIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.contacts, context.getTheme()));
        else if (mValues.get(position).equals(MithrilApplication.CALENDAR_PERMISSION_GROUP))
            holder.mPermIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.calendar, context.getTheme()));
        else if (mValues.get(position).equals(MithrilApplication.CAMERA_PERMISSION_GROUP))
            holder.mPermIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.camera, context.getTheme()));
        else if (mValues.get(position).equals(MithrilApplication.CONTACTS_PERMISSION_GROUP))
            holder.mPermIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.contacts, context.getTheme()));
        else if (mValues.get(position).equals(MithrilApplication.LOCATION_PERMISSION_GROUP))
            holder.mPermIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.map_marker, context.getTheme()));
        else if (mValues.get(position).equals(MithrilApplication.MICROPHONE_PERMISSION_GROUP))
            holder.mPermIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.microphone, context.getTheme()));
        else if (mValues.get(position).equals(MithrilApplication.PHONE_PERMISSION_GROUP))
            holder.mPermIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.phone, context.getTheme()));
        else if (mValues.get(position).equals(MithrilApplication.SENSORS_PERMISSION_GROUP))
            holder.mPermIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.thermometer_lines, context.getTheme()));
        else if (mValues.get(position).equals(MithrilApplication.SMS_PERMISSION_GROUP))
            holder.mPermIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.message_text_outline, context.getTheme()));
        else if (mValues.get(position).equals(MithrilApplication.STORAGE_PERMISSION_GROUP))
            holder.mPermIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.harddisk, context.getTheme()));
        else if (mValues.get(position).equals(MithrilApplication.SYSTEM_TOOLS_PERMISSION_GROUP))
            holder.mPermIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.security, context.getTheme()));
        else if (mValues.get(position).equals(MithrilApplication.CAR_INFORMATION_PERMISSION_GROUP))
            holder.mPermIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.car, context.getTheme()));
        else
            holder.mPermIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.help, context.getTheme()));

        holder.mPermLabel.setText(mValues.get(position).split(".")[mValues.get(position).split(".").length].toLowerCase());
        holder.mAppsUsingPerm.setText(mValues.get(position));

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
        private final View mView;
        private final ImageView mPermIcon;
        private final TextView mPermLabel;
        private final TextView mAppsUsingPerm;
        private String mItem;

        private ViewHolder(View view) {
            super(view);
            mView = view;
            mPermIcon = (ImageView) view.findViewById(R.id.protectionLvlImageView);
            mPermLabel = (TextView) view.findViewById(R.id.permissionLabelTextView);
            mAppsUsingPerm = (TextView) view.findViewById(R.id.appsUsingPermTextView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mAppsUsingPerm.getText() + "'";
        }
    }
}
