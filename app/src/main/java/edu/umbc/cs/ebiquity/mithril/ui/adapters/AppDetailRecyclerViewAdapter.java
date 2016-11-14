package edu.umbc.cs.ebiquity.mithril.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import edu.umbc.cs.ebiquity.mithril.R;
import edu.umbc.cs.ebiquity.mithril.data.model.PermData;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.AppDetailFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PermData} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class AppDetailRecyclerViewAdapter extends RecyclerView.Adapter<AppDetailRecyclerViewAdapter.ViewHolder> {

    private final List<PermData> mValues;
    private final OnListFragmentInteractionListener mListener;

    public AppDetailRecyclerViewAdapter(List<PermData> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_app_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mPermissionIcon.setImageBitmap(mValues.get(position).getPermissionIcon());
        holder.mPermissionLabel.setText(mValues.get(position).getPermissionLabel());
        holder.mPermissionDescription.setText(mValues.get(position).getPermissionDescription());
        holder.mProtectedResource.setText(mValues.get(position).getResource().getResourceName());

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
        public final ImageView mPermissionIcon;
        public final TextView mPermissionLabel;
        public final TextView mPermissionDescription;
        public final TextView mProtectedResource;
        public PermData mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPermissionIcon = (ImageView) view.findViewById(R.id.perm_icon);
            mPermissionLabel = (TextView) view.findViewById(R.id.perm_lbl);
            mPermissionDescription = (TextView) view.findViewById(R.id.perm_desc);
            mProtectedResource = (TextView) view.findViewById(R.id.protected_rsrc);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mPermissionLabel.getText() + "'";
        }
    }
}
