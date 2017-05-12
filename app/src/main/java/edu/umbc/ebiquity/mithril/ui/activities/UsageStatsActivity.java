package edu.umbc.ebiquity.mithril.ui.activities;

/**
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

import android.app.Activity;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.ui.adapters.UsageStatsAdapter;

/**
 * Activity to display package usage statistics.
 */

public class UsageStatsActivity extends Activity implements OnItemSelectedListener {
    private static final boolean localLOGV = false;
    private UsageStatsManager mUsageStatsManager;
    private LayoutInflater mInflater;
    private PackageManager mPm;
    private UsageStatsAdapter mAdapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_usage_stats);
        mUsageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPm = getPackageManager();
        Spinner typeSpinner = (Spinner) findViewById(R.id.typeSpinner);
        typeSpinner.setOnItemSelectedListener(this);
        ListView listView = (ListView) findViewById(R.id.pkg_list);
        mAdapter = new UsageStatsAdapter();
        listView.setAdapter(mAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mAdapter.sortList(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

    // View Holder used when displaying views
    public static class AppViewHolder {
        public TextView pkgName;
        public TextView lastTimeUsed;
        public TextView usageTime;
    }
}