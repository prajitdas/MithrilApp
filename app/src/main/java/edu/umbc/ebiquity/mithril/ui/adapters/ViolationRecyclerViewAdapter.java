package edu.umbc.ebiquity.mithril.ui.adapters;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

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

    public ViolationRecyclerViewAdapter(List<Violation> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_violations, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
//        Resources res = view.getContext().getResources();
//        Drawable drawable;
//        drawable = res.getDrawable(R.drawable.ic_launcher, view.getContext().getTheme());

        SQLiteDatabase mithrilDB = MithrilDBHelper.getHelper(view.getContext()).getWritableDatabase();
        AppData violatingApp = MithrilDBHelper.getHelper(view.getContext()).findAppById(mithrilDB, mValues.get(position).getAppId());

        holder.mItem = mValues.get(position);
        holder.mViolatingAppIcon.setImageBitmap(violatingApp.getIcon());
        holder.mViolationText.setText(violatingApp.getAppName() + " " + mValues.get(position).getDesc());
        holder.mResponseYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionHelper.toast(view.getContext(), "Yes!!!");
            }
        });
        holder.mResponseNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionHelper.toast(view.getContext(), "No!!");
            }
        });

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
        private final ImageView mViolatingAppIcon;
        private final TextView mViolationText;
        private final ImageButton mResponseYesButton;
        private final ImageButton mResponseNoButton;

        private Violation mItem;

        private ViewHolder(View view) {
            super(view);
            mView = view;
            mViolatingAppIcon = (ImageView) view.findViewById(R.id.violatingAppIcon);
            mViolationText = (TextView) view.findViewById(R.id.violationText);
            mResponseYesButton = (ImageButton) view.findViewById(R.id.mResponseYesButton);
            mResponseNoButton = (ImageButton) view.findViewById(R.id.responseNoButton);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mItem.toString() + "'";
        }
    }
}
