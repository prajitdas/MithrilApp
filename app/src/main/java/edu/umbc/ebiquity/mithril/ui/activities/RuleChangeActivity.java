package edu.umbc.ebiquity.mithril.ui.activities;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.data.model.rules.Violation;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticUserContext;
import edu.umbc.ebiquity.mithril.ui.fragments.coreactivityfragments.AppsFragment;
import edu.umbc.ebiquity.mithril.ui.fragments.rulechangeactivityfragments.RuleChangeFragment;

public class RuleChangeActivity extends AppCompatActivity  implements RuleChangeFragment.OnListFragmentInteractionListener {
    private Violation currentViolation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rule_change);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_rule_change);
        setSupportActionBar(toolbar);

        currentViolation = getIntent().getParcelableExtra("rule");
        loadRuleChangeFragment();
    }

    private void loadRuleChangeFragment() {
        Bundle data = new Bundle();
        data.putParcelable("rule", currentViolation);

        RuleChangeFragment aRuleChangeFragment = new RuleChangeFragment();
        aRuleChangeFragment.setArguments(data);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.container_rule_change, aRuleChangeFragment)
                .commit();
    }

    @Override
    public void onListFragmentInteraction(SemanticUserContext item) {

    }
}