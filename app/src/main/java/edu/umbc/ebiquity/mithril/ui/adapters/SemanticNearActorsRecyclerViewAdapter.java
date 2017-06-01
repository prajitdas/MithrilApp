package edu.umbc.ebiquity.mithril.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.ui.fragments.instancecreationactivityfragments.SemanticNearActorsFragment.OnListFragmentInteractionListener;
import edu.umbc.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticNearActors;

/**
 * {@link RecyclerView.Adapter} that can display a {@link SemanticNearActors} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class SemanticNearActorsRecyclerViewAdapter extends RecyclerView.Adapter<SemanticNearActorsRecyclerViewAdapter.ViewHolder> {

    private final List<SemanticNearActors> semanticNearActors;
    private final OnListFragmentInteractionListener mListener;

    public SemanticNearActorsRecyclerViewAdapter(List<SemanticNearActors> items, OnListFragmentInteractionListener listener) {
        semanticNearActors = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_semantic_near_actors, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = semanticNearActors.get(position);
        holder.mIdView.setText(semanticNearActors.get(position).getInferredActorRelationship());
        holder.mContentView.setText(semanticNearActors.get(position).toString());

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
        return semanticNearActors.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public SemanticNearActors mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
