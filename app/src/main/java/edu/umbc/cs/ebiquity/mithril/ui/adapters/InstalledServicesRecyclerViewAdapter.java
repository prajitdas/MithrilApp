package edu.umbc.cs.ebiquity.mithril.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.R;
import edu.umbc.cs.ebiquity.mithril.data.model.components.ServData;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.mainactivityfragments.ServicesFragment.OnListFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ServData} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class InstalledServicesRecyclerViewAdapter extends RecyclerView.Adapter<InstalledServicesRecyclerViewAdapter.ViewHolder> {

    private final List<ServData> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context context;
    private View view;

    public InstalledServicesRecyclerViewAdapter(List<ServData> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_services, parent, false);
        context = view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        String servAppType = mValues.get(position).getAppType();
        if (servAppType.equals(MithrilApplication.getPrefKeySystemAppsDisplay())) {
            holder.mServIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.settings, context.getTheme()));
        } else if (servAppType.equals(MithrilApplication.getPrefKeyUserAppsDisplay())) {
            holder.mServIcon.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.account, view.getContext().getTheme()));
        }

        holder.mServLbl.setText(mValues.get(position).getLabel());
        holder.mServAppName.setText(mValues.get(position).getAppName());
        holder.mServName.setText(mValues.get(position).getName());

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
        private final ImageView mServIcon;
        private final TextView mServLbl;
        private final TextView mServAppName;
        private final TextView mServName;
        private ServData mItem;

        private ViewHolder(View view) {
            super(view);
            mView = view;
            mServIcon = (ImageView) view.findViewById(R.id.serv_icon);
            mServLbl = (TextView) view.findViewById(R.id.serv_lbl);
            mServAppName = (TextView) view.findViewById(R.id.serv_app_name);
            mServName = (TextView) view.findViewById(R.id.serv_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mServAppName.getText() + "'";
        }
    }
}
