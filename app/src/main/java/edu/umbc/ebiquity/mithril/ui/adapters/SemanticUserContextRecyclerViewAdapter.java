package edu.umbc.ebiquity.mithril.ui.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticActivity;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticLocation;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticNearActor;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticTime;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticUserContext;
import edu.umbc.ebiquity.mithril.ui.fragments.rulechangeactivityfragments.RuleChangeFragment.OnListFragmentInteractionListener;
import edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions.ContextImplementationMissingException;

/**
 * {@link RecyclerView.Adapter} that can display a {@link SemanticUserContext} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class SemanticUserContextRecyclerViewAdapter extends RecyclerView.Adapter<SemanticUserContextRecyclerViewAdapter.ViewHolder> {

    private final List<SemanticUserContext> semanticUserContexts;
    private final OnListFragmentInteractionListener mListener;
    private View view;

    public SemanticUserContextRecyclerViewAdapter(List<SemanticUserContext> items, OnListFragmentInteractionListener listener) {
        semanticUserContexts = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_semanticusercontext, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = semanticUserContexts.get(position);
        try {
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                    view.getContext(),
                    R.layout.spinner_item,
                    getContexts(holder.mItem.getType())
            );
            arrayAdapter.getPosition(semanticUserContexts.get(position).getLabel());

            holder.mSpinner.setAdapter(arrayAdapter);
            holder.mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    String newLabelSelected = parent.getItemAtPosition(pos).toString();
                    mListener.onListFragmentInteraction(semanticUserContexts.get(position), newLabelSelected);
                    parent.setSelection(arrayAdapter.getPosition(parent.getItemAtPosition(pos).toString()));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            holder.mSpinner.setSelection(arrayAdapter.getPosition(semanticUserContexts.get(position).getLabel()));
            holder.mType.setText(semanticUserContexts.get(position).getType());
        } catch (ContextImplementationMissingException e) {
            Log.e(MithrilAC.getDebugTag(), e.getMessage());
        } catch (NullPointerException e) {
            Log.e(MithrilAC.getDebugTag(), e.getMessage());
        }
        holder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onListFragmentInteraction(semanticUserContexts.get(position), true);
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                }
            }
        });
    }

    private List<String> getContexts(String type) {
        List<String> possibleSemanticUserContexts = new ArrayList<>();
        SharedPreferences sharedPrefs = view.getContext().getSharedPreferences(MithrilAC.getSharedPreferencesName(), Context.MODE_PRIVATE);
        Gson retrieveDataGson = new Gson();
        String retrieveDataJson;
        Map<String, ?> allPrefs;
        allPrefs = sharedPrefs.getAll();
        for (Map.Entry<String, ?> aPref : allPrefs.entrySet()) {
            if (aPref.getKey().startsWith(type)) {
                retrieveDataJson = sharedPrefs.getString(aPref.getKey(), "");
                if (type.equals(MithrilAC.getPrefKeyContextTypeLocation())) {
                    SemanticLocation semanticLocation = retrieveDataGson.fromJson(retrieveDataJson, SemanticLocation.class);
                    possibleSemanticUserContexts.add(semanticLocation.getLabel());
                } else if (type.equals(MithrilAC.getPrefKeyContextTypeTemporal())) {
                    SemanticTime semanticTime = retrieveDataGson.fromJson(retrieveDataJson, SemanticTime.class);
                    possibleSemanticUserContexts.add(semanticTime.getLabel());
                } else if (type.equals(MithrilAC.getPrefKeyContextTypePresence())) {
                    SemanticNearActor semanticNearActor = retrieveDataGson.fromJson(retrieveDataJson, SemanticNearActor.class);
                    possibleSemanticUserContexts.add(semanticNearActor.getLabel());
                } else if (type.equals(MithrilAC.getPrefKeyContextTypeActivity())) {
                    SemanticActivity semanticActivity = retrieveDataGson.fromJson(retrieveDataJson, SemanticActivity.class);
                    possibleSemanticUserContexts.add(semanticActivity.getLabel());
                }
            }
        }
        return possibleSemanticUserContexts;
    }

    @Override
    public int getItemCount() {
        return semanticUserContexts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mType;
        public final ImageButton mDeleteBtn;
        public final Spinner mSpinner;
        public SemanticUserContext mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mType = (TextView) view.findViewById(R.id.semanticUserContextType);
            mDeleteBtn = (ImageButton) view.findViewById(R.id.deleteContextBtn);
            mSpinner = (Spinner) view.findViewById(R.id.spinner_rule_change);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mType.getText() + "'";
        }
    }
}