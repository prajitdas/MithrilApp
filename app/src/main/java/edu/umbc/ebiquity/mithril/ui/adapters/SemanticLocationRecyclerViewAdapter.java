package edu.umbc.ebiquity.mithril.ui.adapters;

import android.location.Address;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticLocation;
import edu.umbc.ebiquity.mithril.ui.fragments.instancecreationactivityfragments.SemanticLocationFragment.OnListFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link SemanticLocation} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class SemanticLocationRecyclerViewAdapter extends RecyclerView.Adapter<SemanticLocationRecyclerViewAdapter.ViewHolder> {

    private final List<SemanticLocation> semanticLocations;
    private List<SemanticLocation> checkedLocations = new ArrayList<>();
    private final OnListFragmentInteractionListener mListener;
    private View view;

    public SemanticLocationRecyclerViewAdapter(List<SemanticLocation> items, OnListFragmentInteractionListener listener) {
        semanticLocations = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_semantic_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        SemanticLocation semanticLocation = semanticLocations.get(position);
        holder.mItem = semanticLocation;
        holder.mLabel.setText(semanticLocation.getInferredLocation());

        String addressLine = new String(
                semanticLocation.getAddress().getAddressLine(0) + ", " +
                semanticLocation.getAddress().getLocality() + ", " +
                semanticLocation.getAddress().getPostalCode());

        if(semanticLocation.getAddress().equals(new Address(Locale.getDefault()))) {
            if(semanticLocation.getDetails() != null)
                holder.mDetail.setText(semanticLocation.getDetails());
            else {
                StringBuffer latLng = new StringBuffer("Lat: ");
                latLng.append(Double.toString(semanticLocation.getLocation().getLatitude()));
                latLng.append(", Lng: ");
                latLng.append(Double.toString(semanticLocation.getLocation().getLongitude()));
                holder.mDetail.setText(latLng.toString());
            }
        } else {
            if(semanticLocation.getDetails() != null)
                holder.mDetail.setText(semanticLocation.getDetails());
            else
                holder.mDetail.setText(addressLine);
        }
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
        return semanticLocations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mLabel;
        public final TextView mDetail;
        public SemanticLocation mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mLabel = (TextView) view.findViewById(R.id.semanticLocationLabel);
            mDetail = (TextView) view.findViewById(R.id.semanticLocationDetail);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mLabel.getText() + "'";
        }
    }
}