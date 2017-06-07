package edu.umbc.ebiquity.mithril.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticLocation;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticTime;
import edu.umbc.ebiquity.mithril.ui.fragments.instancecreationactivityfragments.SemanticTimeFragment.OnListFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link SemanticTime} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class SemanticTimeRecyclerViewAdapter extends RecyclerView.Adapter<SemanticTimeRecyclerViewAdapter.ViewHolder> {

    private final List<SemanticTime> semanticTimes;
    private final OnListFragmentInteractionListener mListener;

    public SemanticTimeRecyclerViewAdapter(List<SemanticTime> items, OnListFragmentInteractionListener listener) {
        semanticTimes = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_semantic_time, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = semanticTimes.get(position);
        holder.mLabel.setText(semanticTimes.get(position).getInferredTime());
        holder.mDetail.setText(Long.toString(semanticTimes.get(position).getRawTime()));

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
        return semanticTimes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mLabel;
        public final TextView mDetail;
        public SemanticTime mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mLabel = (TextView) view.findViewById(R.id.semanticTimeLabel);
            mDetail = (TextView) view.findViewById(R.id.semanticTimeDetail);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mLabel.getText() + "'";
        }
    }
}