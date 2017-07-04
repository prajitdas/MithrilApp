package edu.umbc.ebiquity.mithril.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;

public class DisplayUploadDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_upload_data);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_rule_change);
        setSupportActionBar(toolbar);

        Bundle data = getIntent().getExtras();

        TextView textViewUploadDataTime = (TextView) findViewById(R.id.textViewUploadDataTime);
        textViewUploadDataTime.setText((String) data.get(MithrilAC.getFeedbackQuestionDataKey()));

        TextView textViewUploadData = (TextView) findViewById(R.id.textViewUploadData);
        textViewUploadData.setText((String) data.get(MithrilAC.getFeedbackQuestionDataTimeKey()));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}