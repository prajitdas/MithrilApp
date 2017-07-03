package edu.umbc.ebiquity.mithril.ui.adapters;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.data.model.components.AppData;
import edu.umbc.ebiquity.mithril.data.model.rules.PolicyRule;
import edu.umbc.ebiquity.mithril.ui.fragments.coreactivityfragments.PolicyRuleFragment.OnListFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PolicyRule} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class PolicyRuleRecyclerViewAdapter extends RecyclerView.Adapter<PolicyRuleRecyclerViewAdapter.ViewHolder> {

    private final List<PolicyRule> mValues;
    private final OnListFragmentInteractionListener mListener;
    private View view;
    private SQLiteDatabase mithrilDB;

    public PolicyRuleRecyclerViewAdapter(List<PolicyRule> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_policyrule, parent, false);
        mithrilDB = MithrilDBHelper.getHelper(view.getContext()).getWritableDatabase();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        AppData appData = MithrilDBHelper.getHelper(view.getContext()).findAppById(mithrilDB, mValues.get(position).getAppId());

        holder.mAppIcon.setImageBitmap(appData.getIcon());
        holder.mAppName.setText(appData.getAppName());
        holder.mPolicyDetails.setText(mValues.get(position).toString());

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
        private final ImageView mAppIcon;
        private final TextView mAppName;
        private final TextView mPolicyDetails;
        public PolicyRule mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mAppIcon = (ImageView) view.findViewById(R.id.policy_app_icon);
            mAppName = (TextView) view.findViewById(R.id.policy_app_name);
            mPolicyDetails = (TextView) view.findViewById(R.id.policy_app_usage);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mAppName.getText() + "'";
        }
    }
}
