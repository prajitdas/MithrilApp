package edu.umbc.ebiquity.mithril.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticUserContext;
import edu.umbc.ebiquity.mithril.ui.fragments.rulechangeactivityfragments.RuleChangeFragment.OnListFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link SemanticUserContext} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class SemanticUserContextRecyclerViewAdapter extends RecyclerView.Adapter<SemanticUserContextRecyclerViewAdapter.ViewHolder> {

    private final List<SemanticUserContext> semanticUserContexts;
    private final OnListFragmentInteractionListener mListener;

    public SemanticUserContextRecyclerViewAdapter(List<SemanticUserContext> items, OnListFragmentInteractionListener listener) {
        semanticUserContexts = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_semanticusercontext, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = semanticUserContexts.get(position);
        holder.mLabel.setText(semanticUserContexts.get(position).getType());
        if(semanticUserContexts.get(position).isEnabled())
            holder.mDetail.setText(semanticUserContexts.get(position).getLabel());
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
        return semanticUserContexts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mLabel;
        public final TextView mDetail;
        public SemanticUserContext mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mLabel = (TextView) view.findViewById(R.id.semanticUserContextLabel);
            mDetail = (TextView) view.findViewById(R.id.semanticUserContextDetail);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mLabel.getText() + "'";
        }
    }
}