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
import edu.umbc.cs.ebiquity.mithril.data.model.PermData;
import edu.umbc.cs.ebiquity.mithril.ui.fragments.ShowPermissionsFragment.OnListFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PermData} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class InstalledPermissionRecyclerViewAdapter extends RecyclerView.Adapter<InstalledPermissionRecyclerViewAdapter.ViewHolder> {

    private final List<PermData> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Context context;
    private View view;

    public InstalledPermissionRecyclerViewAdapter(List<PermData> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_permissions, parent, false);
        context = view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        String permProtLvl = mValues.get(position).getPermissionProtectionLevel();
        if (permProtLvl.equals(MithrilApplication.getPermissionProtectionLevelNormal())) {
            holder.mPermIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.comment_check_outline, context.getTheme()));
//            cardView.setCardBackgroundColor(Color.GREEN);
        } else if (permProtLvl.equals(MithrilApplication.getPermissionProtectionLevelDangerous())) {
            holder.mPermIcon.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.comment_alert_outline, view.getContext().getTheme()));
//            cardView.setCardBackgroundColor(Color.RED);
        } else if (permProtLvl.equals(MithrilApplication.getPermissionProtectionLevelSignature())) {
            holder.mPermIcon.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.comment_processing_outline, view.getContext().getTheme()));
//            cardView.setCardBackgroundColor(Color.YELLOW);
        } else if (permProtLvl.equals(MithrilApplication.getPermissionProtectionLevelPrivileged())) {
            holder.mPermIcon.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.comment_remove_outline, view.getContext().getTheme()));
//            cardView.setCardBackgroundColor(Color.BLUE);
        } else {
            holder.mPermIcon.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.comment_question_outline, view.getContext().getTheme()));
//            cardView.setCardBackgroundColor(Color.GRAY);
        }
        holder.mPermLabel.setText(mValues.get(position).getPermissionLabel());
        holder.mAppsUsingPerm.setText(mValues.get(position).findAppsUsingPermission());

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
        public final ImageView mPermIcon;
        public final TextView mPermLabel;
        public final TextView mAppsUsingPerm;
        public PermData mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mPermIcon = (ImageView) view.findViewById(R.id.protectionLvlImageView);
            mPermLabel = (TextView) view.findViewById(R.id.permissionLabelTextView);
            mAppsUsingPerm = (TextView) view.findViewById(R.id.appsUsingPermTextView);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mAppsUsingPerm.getText() + "'";
        }
    }
}
