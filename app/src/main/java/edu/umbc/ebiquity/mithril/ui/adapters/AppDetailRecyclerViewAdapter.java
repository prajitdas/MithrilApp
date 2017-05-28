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
import java.util.regex.Pattern;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.model.components.PermData;
import edu.umbc.ebiquity.mithril.ui.fragments.showppdetailsactivityfragments.AppDetailFragment.OnListFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PermData} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class AppDetailRecyclerViewAdapter extends RecyclerView.Adapter<AppDetailRecyclerViewAdapter.ViewHolder> {

    private final List<PermData> mValues;
    private final OnListFragmentInteractionListener mListener;
    private CardView cardView;
    private View view;
    private Context context;

    public AppDetailRecyclerViewAdapter(List<PermData> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_app_detail, parent, false);
        cardView = (CardView) view.findViewById(R.id.card_view_perm);
        context = view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String permProtLvl = mValues.get(position).getPermissionProtectionLevel();

        holder.mItem = mValues.get(position);
        holder.mPermissionLabel.setText(mValues.get(position).getPermissionLabel());
        holder.mPermissionName.setText(mValues.get(position).getPermissionName());

        if (permProtLvl.equals(MithrilApplication.getPermissionProtectionLevelNormal())) {
            holder.mPermissionProtectionLevel.setImageDrawable(context.getResources().getDrawable(R.drawable.comment_check_outline, context.getTheme()));
//            cardView.setCardBackgroundColor(Color.GREEN);
        } else if (permProtLvl.equals(MithrilApplication.getPermissionProtectionLevelDangerous())) {
            holder.mPermissionProtectionLevel.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.comment_alert_outline, view.getContext().getTheme()));
//            cardView.setCardBackgroundColor(Color.RED);
        } else if (permProtLvl.equals(MithrilApplication.getPermissionProtectionLevelSignature())) {
            holder.mPermissionProtectionLevel.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.comment_processing_outline, view.getContext().getTheme()));
//            cardView.setCardBackgroundColor(Color.YELLOW);
        } else if (permProtLvl.equals(MithrilApplication.getPermissionProtectionLevelPrivileged())) {
            holder.mPermissionProtectionLevel.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.comment_remove_outline, view.getContext().getTheme()));
//            cardView.setCardBackgroundColor(Color.BLUE);
        } else {
            holder.mPermissionProtectionLevel.setImageDrawable(view.getContext().getResources().getDrawable(R.drawable.comment_question_outline, view.getContext().getTheme()));
//            cardView.setCardBackgroundColor(Color.GRAY);
        }

        if (!mValues.get(position).getPermissionGroup().equals(MithrilApplication.NO_PERMISSION_GROUP)) {
            String[] words = mValues.get(position).getPermissionGroup().split(Pattern.quote("."));
            //In a group, the last word is most important for group identification, so use that I guess!
            holder.mPermissionGroup.setText(words[words.length - 1]);
        } else
            holder.mPermissionGroup.setText(mValues.get(position).getPermissionGroup());

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
        private final ImageView mPermissionProtectionLevel;
        private final TextView mPermissionLabel;
        private final TextView mPermissionName;
        private final TextView mPermissionGroup;
        private PermData mItem;

        private ViewHolder(View view) {
            super(view);
            mView = view;

            mPermissionProtectionLevel = (ImageView) view.findViewById(R.id.perm_protection_lvl);
            mPermissionLabel = (TextView) view.findViewById(R.id.perm_lbl);
            mPermissionName = (TextView) view.findViewById(R.id.perm_name);
            mPermissionGroup = (TextView) view.findViewById(R.id.perm_group);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mPermissionLabel.getText() + "'";
        }
    }
}