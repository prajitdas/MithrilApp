package edu.umbc.ebiquity.mithril.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.ui.fragments.coreactivityfragments.PermissionsFragment.OnListFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link String} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class InstalledPermissionRecyclerViewAdapter extends RecyclerView.Adapter<InstalledPermissionRecyclerViewAdapter.ViewHolder> {

    private final List<?> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context context;
    private View view;

    public InstalledPermissionRecyclerViewAdapter(List<Pair<String, String>> items, OnListFragmentInteractionListener listener) {
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
        Pair<String, String> permPair = (Pair<String, String>) mValues.get(position);
        holder.mItem = permPair.first;

        if (permPair.first.equals(MithrilApplication.CONTACTS_PERMISSION_GROUP.first))
            holder.mPermIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.contacts, context.getTheme()));
        else if (permPair.first.equals(MithrilApplication.CALENDAR_PERMISSION_GROUP.first))
            holder.mPermIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.calendar, context.getTheme()));
        else if (permPair.first.equals(MithrilApplication.CAMERA_PERMISSION_GROUP.first))
            holder.mPermIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.camera, context.getTheme()));
        else if (permPair.first.equals(MithrilApplication.LOCATION_PERMISSION_GROUP.first))
            holder.mPermIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.map_marker, context.getTheme()));
        else if (permPair.first.equals(MithrilApplication.MICROPHONE_PERMISSION_GROUP.first))
            holder.mPermIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.microphone, context.getTheme()));
        else if (permPair.first.equals(MithrilApplication.PHONE_PERMISSION_GROUP.first))
            holder.mPermIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.phone, context.getTheme()));
        else if (permPair.first.equals(MithrilApplication.SENSORS_PERMISSION_GROUP.first))
            holder.mPermIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.thermometer_lines, context.getTheme()));
        else if (permPair.first.equals(MithrilApplication.SMS_PERMISSION_GROUP.first))
            holder.mPermIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.message_text_outline, context.getTheme()));
        else if (permPair.first.equals(MithrilApplication.STORAGE_PERMISSION_GROUP.first))
            holder.mPermIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.harddisk, context.getTheme()));
        else if (permPair.first.equals(MithrilApplication.SYSTEM_TOOLS_PERMISSION_GROUP.first))
            holder.mPermIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.security, context.getTheme()));
        else if (permPair.first.equals(MithrilApplication.CAR_INFORMATION_PERMISSION_GROUP.first))
            holder.mPermIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.car, context.getTheme()));
        else
            holder.mPermIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.help, context.getTheme()));

        //mValues.get(position).split("\\.")[mValues.get(position).split("\\.").length - 1];
        holder.mPermLabel.setText(permPair.second);
        holder.mPermString.setText(permPair.first);

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
        private final TextView mPermString;
        private String mItem;

        private ViewHolder(View view) {
            super(view);
            mView = view;
            mPermIcon = (ImageView) view.findViewById(R.id.protectionLvlImageView);
            mPermLabel = (TextView) view.findViewById(R.id.permissionLabelTextView);
            mPermString = (TextView) view.findViewById(R.id.permString);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mItem + "'";
        }
    }
}
