package edu.umbc.ebiquity.mithril.ui.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.List;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.data.model.components.AppData;
import edu.umbc.ebiquity.mithril.data.model.rules.Violation;
import edu.umbc.ebiquity.mithril.ui.fragments.coreactivityfragments.ViolationFragment.OnListFragmentInteractionListener;
import edu.umbc.ebiquity.mithril.util.specialtasks.permissions.PermissionHelper;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Violation} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ViolationRecyclerViewAdapter extends RecyclerView.Adapter<ViolationRecyclerViewAdapter.ViewHolder> {

    private final List<Violation> mValues;
    private final OnListFragmentInteractionListener mListener;
    private View view;
    private Context context;

    public ViolationRecyclerViewAdapter(List<Violation> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_violations, parent, false);
        context = view.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final SQLiteDatabase mithrilDB = MithrilDBHelper.getHelper(view.getContext()).getWritableDatabase();
        final AppData violatingApp = MithrilDBHelper.getHelper(view.getContext()).findAppById(mithrilDB, mValues.get(position).getAppId());
        final long rowid = MithrilDBHelper.getHelper(view.getContext()).findViolationRowIdByPolicyAppOpId(mithrilDB, mValues.get(position));

        holder.mItem = mValues.get(position);
        holder.mViolatingAppIcon.setImageBitmap(violatingApp.getIcon());
        holder.mViolationAppLaunch.setText(violatingApp.getAppName());

        holder.mViolationOpDetail.setText("Used: " + mValues.get(position).getOpStr().replace("_", " ") + " " +
                MithrilAC.getTimeText(true, mValues.get(position).getDetectedAtTime()));

        holder.mViolationContext.setText("In context: " +
                mValues.get(position).getContextsString(context));

        holder.mViolationResponseYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionHelper.toast(view.getContext(), "Will do...");
                mValues.get(position).setAsked(true);
                mValues.get(position).setFeedbackTime(new Timestamp(System.currentTimeMillis()));
                MithrilDBHelper.getHelper(view.getContext()).updateViolationForRowId(mithrilDB, mValues.get(position), rowid);
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem, true);
                }
            }
        });

        holder.mViolationResponseNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionHelper.toast(view.getContext(), "Let's add/modify rules");
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem, false);
                }
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionHelper.toast(view.getContext(), "Let's add/modify rules");
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem, false);
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
        private final ImageView mViolatingAppIcon;
        private final TextView mViolationAppLaunch;
        private final TextView mViolationOpDetail;
        private final TextView mViolationContext;
        private final Button mViolationResponseYesButton;
        private final Button mViolationResponseNoButton;

        private Violation mItem;

        private ViewHolder(View view) {
            super(view);
            mView = view;
            mViolatingAppIcon = (ImageView) view.findViewById(R.id.violatingAppIcon);
            mViolationAppLaunch = (TextView) view.findViewById(R.id.violationAppLaunch);
            mViolationOpDetail = (TextView) view.findViewById(R.id.violationOpDetail);
            mViolationContext = (TextView) view.findViewById(R.id.violationContext);
            mViolationResponseYesButton = (Button) view.findViewById(R.id.trueViolationButton);
            mViolationResponseNoButton = (Button) view.findViewById(R.id.falseViolationButton);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mItem.toString() + "'";
        }
    }
}
