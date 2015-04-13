package edu.umbc.cs.ebiquity.mithril.androidperm.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import edu.umbc.cs.ebiquity.mithril.androidperm.R;
import edu.umbc.cs.ebiquity.mithril.androidperm.util.AppPermListAdapter;

public class ViewPermissionsActivity extends ListActivity {
	private TextView mPackageName;
	private String packageName;
	private PackageManager packageManager;
	private List<PermissionInfo> appPermList;
	private AppPermListAdapter appPermListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_permissions);
		packageManager = getApplicationContext().getPackageManager();
		appPermList = new ArrayList<PermissionInfo>();

		Intent intent = getIntent();
		packageName = intent.getStringExtra("app.packageName");
		
		initView();
	}
	
	private void initView() {
		mPackageName = (TextView) findViewById(R.id.appPackageNameTextViewId);
		mPackageName.setText(packageName);
		
		try {
			PackageInfo tempPackageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
			for(PermissionInfo permInfo : tempPackageInfo.permissions)
				appPermList.add(permInfo);
			appPermListAdapter = new AppPermListAdapter(ViewPermissionsActivity.this, R.layout.app_detail_list_item, appPermList);
			setListAdapter(appPermListAdapter);
			Log.v("AndroidPerm", Integer.toString(appPermList.size()));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}