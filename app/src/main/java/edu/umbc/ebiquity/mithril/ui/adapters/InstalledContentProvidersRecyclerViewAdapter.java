package edu.umbc.ebiquity.mithril.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.model.components.ContentProvData;
import edu.umbc.ebiquity.mithril.ui.fragments.coreactivityfragments.ContentProvidersFragment.OnListFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ContentProvData} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class InstalledContentProvidersRecyclerViewAdapter extends RecyclerView.Adapter<InstalledContentProvidersRecyclerViewAdapter.ViewHolder> {

    private final List<ContentProvData> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context context;
    private View view;

    public InstalledContentProvidersRecyclerViewAdapter(List<ContentProvData> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contentproviders, parent, false);
        context = view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        boolean contentProvExported = mValues.get(position).isExported();
        if (contentProvExported)
            holder.mContentProvExported.setImageDrawable(context.getResources().getDrawable(R.drawable.comment_check_outline, context.getTheme()));
        else
            holder.mContentProvExported.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.comment_remove_outline, view.getContext().getTheme()));
        holder.mContentProvName.setText(mValues.get(position).getName());
        holder.mContentProvProcessName.setText(mValues.get(position).getProcessName());

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
        private final ImageView mContentProvExported;
        private final TextView mContentProvName;
        private final TextView mContentProvProcessName;
        private ContentProvData mItem;

        private ViewHolder(View view) {
            super(view);
            mView = view;
            mContentProvExported = (ImageView) view.findViewById(R.id.contentProvExportedImageView);
            mContentProvName = (TextView) view.findViewById(R.id.contentProvNameTextView);
            mContentProvProcessName = (TextView) view.findViewById(R.id.contentProvProcessNameTextView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentProvName.getText() + "'";
        }
    }
}
