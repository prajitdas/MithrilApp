package edu.umbc.ebiquity.mithril.ui.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.model.components.AppData;
import edu.umbc.ebiquity.mithril.ui.fragments.showpermissiondetailsactivityfragments.PermissionDetailFragment;

/**
 * Created by Prajit on 5/28/2017.
 */

/**
 * {@link RecyclerView.Adapter} that can display a {@link AppData} and makes a call to the
 * specified {@link PermissionDetailFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class PermissionDetailRecyclerViewAdapter extends RecyclerView.Adapter<PermissionDetailRecyclerViewAdapter.ViewHolder> {

    private final List<AppData> mValues;
    private final PermissionDetailFragment.OnListFragmentInteractionListener mListener;
    private CardView cardView;
    private View view;
    private Context context;

    public PermissionDetailRecyclerViewAdapter(List<AppData> items, PermissionDetailFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_permission_detail, parent, false);
        cardView = (CardView) view.findViewById(R.id.card_view_perm);
        context = view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mAppIcon.setImageBitmap(mValues.get(position).getIcon());
        holder.mAppName.setText(mValues.get(position).getAppName());
        holder.mAppPerms.setText(mValues.get(position).getGrantedPermissions());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final ImageView mAppIcon;
        private final TextView mAppName;
        private final TextView mAppPerms;
        private AppData mItem;

        private ViewHolder(View view) {
            super(view);
            mView = view;

            mAppIcon = (ImageView) view.findViewById(R.id.app_icon_perm_detail);
            mAppName = (TextView) view.findViewById(R.id.app_name_perm_detail);
            mAppPerms = (TextView) view.findViewById(R.id.app_permissions_used);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mAppName.getText() + "'";
        }
    }
}