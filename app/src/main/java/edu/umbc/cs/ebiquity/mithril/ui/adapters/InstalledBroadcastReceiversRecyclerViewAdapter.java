package edu.umbc.cs.ebiquity.mithril.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.umbc.cs.ebiquity.mithril.R;
import edu.umbc.cs.ebiquity.mithril.data.model.components.BCastRecvData;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.mainactivityfragments.BroadcastReceiversFragment.OnListFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link BCastRecvData} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class InstalledBroadcastReceiversRecyclerViewAdapter extends RecyclerView.Adapter<InstalledBroadcastReceiversRecyclerViewAdapter.ViewHolder> {

    private final List<BCastRecvData> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context context;
    private View view;

    public InstalledBroadcastReceiversRecyclerViewAdapter(List<BCastRecvData> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_broadcastreceivers, parent, false);
        context = view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mBcastIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.comment_check_outline, context.getTheme()));
        holder.mBcastLabel.setText(mValues.get(position).getLabel());
        holder.mBcastName.setText(mValues.get(position).getName());

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
        public final ImageView mBcastIcon;
        public final TextView mBcastLabel;
        public final TextView mBcastName;
        public BCastRecvData mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mBcastIcon = (ImageView) view.findViewById(R.id.bcast_icon);
            mBcastLabel = (TextView) view.findViewById(R.id.bcast_lbl);
            mBcastName = (TextView) view.findViewById(R.id.bcast_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mBcastName.getText() + "'";
        }
    }
}
