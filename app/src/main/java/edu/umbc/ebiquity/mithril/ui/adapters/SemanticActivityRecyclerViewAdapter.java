package edu.umbc.ebiquity.mithril.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticActivity;
import edu.umbc.ebiquity.mithril.ui.fragments.instancecreationactivityfragments.SemanticActivityFragment.OnListFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link SemanticActivity} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class SemanticActivityRecyclerViewAdapter extends RecyclerView.Adapter<SemanticActivityRecyclerViewAdapter.ViewHolder> {

    private final List<SemanticActivity> semanticActivities;
    private final OnListFragmentInteractionListener mListener;

    public SemanticActivityRecyclerViewAdapter(List<SemanticActivity> items, OnListFragmentInteractionListener listener) {
        semanticActivities = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_semantic_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = semanticActivities.get(position);
        holder.mLabel.setText(semanticActivities.get(position).getInferredActivity());
        if(semanticActivities.get(position).isEnabled())
            holder.mDetail.setText(semanticActivities.get(position).getInferredActivity());
        else
            holder.mDetail.setText(R.string.click_save_button_to_enable_context);

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
        return semanticActivities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mLabel;
        public final TextView mDetail;
        public SemanticActivity mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mLabel = (TextView) view.findViewById(R.id.semanticActivityLabel);
            mDetail = (TextView) view.findViewById(R.id.semanticActivityDetail);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mLabel.getText() + "'";
        }
    }
}