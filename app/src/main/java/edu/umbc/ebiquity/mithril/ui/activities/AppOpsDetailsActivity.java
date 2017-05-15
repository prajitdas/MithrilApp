package edu.umbc.ebiquity.mithril.ui.activities;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.android.internal.logging.MetricsProto;

import java.util.List;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.util.specialtasks.appops.AppOpsState;

public class AppOpsDetailsActivity extends AppCompatActivity {
    private AppOpsState mState;
    private PackageManager mPm;
    private AppOpsManager mAppOps;
    private PackageInfo mPackageInfo;
    private LayoutInflater mInflater;
    private View mRootView;
    private LinearLayout mOperationsSection;

    // Utility method to set application label and icon.
    private void setAppLabelAndIcon(PackageInfo pkgInfo) {
        final View appSnippet = mRootView.findViewById(R.id.app_snippet);
        CharSequence label = mPm.getApplicationLabel(pkgInfo.applicationInfo);
        Drawable icon = mPm.getApplicationIcon(pkgInfo.applicationInfo);
        setupAppSnippet(appSnippet, label, icon,
                pkgInfo != null ? pkgInfo.versionName : null);
    }

    private void setupAppSnippet(View appSnippet, CharSequence label, Drawable icon,
                                 CharSequence versionName) {
        LayoutInflater.from(appSnippet.getContext()).inflate(R.layout.widget_text_views,
                (ViewGroup) appSnippet.findViewById(android.R.id.widget_frame));
        ImageView iconView = (ImageView) appSnippet.findViewById(android.R.id.icon);
        iconView.setImageDrawable(icon);
        // Set application name.
        TextView labelView = (TextView) appSnippet.findViewById(android.R.id.title);
        labelView.setText(label);
        // Version number of application
        TextView appVersion = (TextView) appSnippet.findViewById(R.id.widget_text1);
        if (!TextUtils.isEmpty(versionName)) {
            appVersion.setSelected(true);
            appVersion.setVisibility(View.VISIBLE);
            appVersion.setText(appSnippet.getContext().getString(R.string.version_text,
                    String.valueOf(versionName)));
        } else {
            appVersion.setVisibility(View.INVISIBLE);
        }
    }

    private String retrieveAppEntry() {
        String args = getIntent().getExtras().getString("package");
        String packageName = args;
        if (packageName == null) {
            Intent intent = (args == null) ?
                    getIntent() : null;
            if (intent != null) {
                packageName = intent.getData().getSchemeSpecificPart();
            }
        }
        try {
            mPackageInfo = mPm.getPackageInfo(packageName,
                    PackageManager.GET_DISABLED_COMPONENTS |
                            PackageManager.GET_UNINSTALLED_PACKAGES);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(MithrilApplication.getDebugTag(), "Exception when retrieving package:" + packageName, e);
            mPackageInfo = null;
        }
        return packageName;
    }

    private boolean refreshUi() {
        if (mPackageInfo == null) {
            return false;
        }
        setAppLabelAndIcon(mPackageInfo);
        mOperationsSection.removeAllViews();
        String lastPermGroup = "";
        for (AppOpsState.OpsTemplate tpl : AppOpsState.ALL_TEMPLATES) {
            List<AppOpsState.AppOpEntry> entries = mState.buildState(tpl,
                    mPackageInfo.applicationInfo.uid, mPackageInfo.packageName);
            for (final AppOpsState.AppOpEntry entry : entries) {
                final AppOpsManager.OpEntry firstOp = entry.getOpEntry(0);
                final View view = mInflater.inflate(R.layout.app_ops_details_item,
                        mOperationsSection, false);
                mOperationsSection.addView(view);
                String perm = AppOpsManager.opToPermission(firstOp.getOp());
                if (perm != null) {
                    try {
                        PermissionInfo pi = mPm.getPermissionInfo(perm, 0);
                        if (pi.group != null && !lastPermGroup.equals(pi.group)) {
                            lastPermGroup = pi.group;
                            PermissionGroupInfo pgi = mPm.getPermissionGroupInfo(pi.group, 0);
                            if (pgi.icon != 0) {
                                ((ImageView) view.findViewById(R.id.op_icon)).setImageDrawable(
                                        pgi.loadIcon(mPm));
                            }
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                    }
                }
                ((TextView) view.findViewById(R.id.op_name)).setText(
                        entry.getSwitchText(mState));
                ((TextView) view.findViewById(R.id.op_time)).setText(
                        entry.getTimeText(this, true));
                Switch sw = (Switch) view.findViewById(R.id.switchWidget);
                final int switchOp = AppOpsManager.opToSwitch(firstOp.getOp());
                try {
                    int checkedVal = mAppOps.checkOp(switchOp, entry.getPackageOps().getUid(), entry.getPackageOps().getPackageName());
                    sw.setChecked(checkedVal == AppOpsManager.MODE_ALLOWED);
                    sw.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            try {
                                mAppOps.setMode(switchOp, entry.getPackageOps().getUid(),
                                        entry.getPackageOps().getPackageName(), isChecked
                                                ? AppOpsManager.MODE_ALLOWED : AppOpsManager.MODE_IGNORED);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

//    private void setIntentAndFinish(boolean finish, boolean appChanged) {
//        Intent intent = new Intent();
//        intent.putExtra(ManageApplications.APP_CHG, appChanged);
//        SettingsActivity sa = (SettingsActivity) getActivity();
//        sa.finishPreferencePanel(this, Activity.RESULT_OK, intent);
//    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        mState = new AppOpsState(this);
        mPm = getPackageManager();
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mAppOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        retrieveAppEntry();
        refreshUi();
//        setHasOptionsMenu(true);
    }

//    @Override
//    public View onCreateView(
//            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        final View view = inflater.inflate(R.layout.app_ops_details, container, false);
////        Utils.prepareCustomPreferencesList(container, view, view, false);
//        mRootView = view;
//        mOperationsSection = (LinearLayout) view.findViewById(R.id.operations_section);
//        return view;
//    }

    protected int getMetricsCategory() {
        return MetricsProto.MetricsEvent.APP_OPS_DETAILS;
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (!refreshUi()) {
//            setIntentAndFinish(true, true);
//        }
    }
}

/*
// Copyright (C) 2016 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

syntax = "proto2";

option java_package = "com.android.internal.logging";
option java_outer_classname = "MetricsProto";

package com_android_internal_logging;

// Wrapper for System UI log events
message MetricsEvent {

  // Known visual elements: views or controls.
  enum View {
  }
}

 */