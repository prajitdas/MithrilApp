package edu.umbc.cs.ebiquity.mithril.androidperm.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import edu.umbc.cs.ebiquity.mithril.androidperm.R;
import edu.umbc.cs.ebiquity.mithril.androidperm.util.AppsAdapter;

public class MainActivity extends ListActivity {
//	private TextView mTextViewPermissionInfo;
	private PackageManager packageManager;
	private List<ApplicationInfo> appsList;
	private AppsAdapter listAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		packageManager = getApplicationContext().getPackageManager();
		appsList = new ArrayList<ApplicationInfo>();
		/**
		 * If you want to load the apps this is what you do
		new LoadApps().execute();
		 */
		initView();
	}
	
	private void initView() {
		List<ApplicationInfo> tempAppsList = new ArrayList<ApplicationInfo>();
		tempAppsList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
		for(ApplicationInfo appInfo : tempAppsList) {
			try {
				if(appInfo.packageName != null) {
					appsList.add(appInfo);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		listAdapter = new AppsAdapter(MainActivity.this, R.layout.app_list_item, appsList);
		setListAdapter(listAdapter);
		Log.v("AndroidPerm", Integer.toString(appsList.size()));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		ApplicationInfo app = appsList.get(position);
		try {
			Intent intent = packageManager.getLaunchIntentForPackage(app.packageName);
			if(intent != null)
				startActivity(intent);
		} catch(ActivityNotFoundException e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		} catch(Exception e) {
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
//	private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
//		ArrayList<ApplicationInfo> appsList = new ArrayList<ApplicationInfo>();
//		for(ApplicationInfo appInfo : list) {
//			try {
//				if(packageManager.getLaunchIntentForPackage(appInfo.packageName) != null) {
//					appsList.add(appInfo);
//				}
//			} catch(Exception e) {
//				e.printStackTrace();
//			}
//		}
//		return appsList;
//	}
	
/**
 * 
 * If you want to load the apps this is what you do
 * 	private class LoadApps extends AsyncTask<Void, Void, Void> {

		private ProgressDialog progress = null;
		@Override
		protected Void doInBackground(Void... params) {
			appsList = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
			listAdapter = new AppsAdapter(MainActivity.this, R.layout.list_item, appsList);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			setListAdapter(listAdapter);
			progress.dismiss();
			super.onPostExecute(result);
		}
		
		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(MainActivity.this, null, "Loading apps info...");
			super.onPreExecute();
		}
	}
*/
//	private String getPermissions() {
//		StringBuffer tempAppInfo = new StringBuffer();
//		int flags = PackageManager.GET_META_DATA | 
//	            PackageManager.GET_SHARED_LIBRARY_FILES |     
//	            PackageManager.GET_UNINSTALLED_PACKAGES | 
//	            PackageManager.GET_PERMISSIONS;
//		Log.v("AndroidPerm", Integer.toString(flags));
//		for(PackageInfo pack : packageManager.getInstalledPackages(flags)) {
//			if ((pack.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1) {
//				tempAppInfo.append(packageManager.getApplicationLabel(pack.applicationInfo).toString()+", "+pack.packageName+", "+permissionsArrayToString(pack.packageName));
//				tempAppInfo.append("\n");
//			}
//		}
//		return tempAppInfo.toString();
//	}
//	
//	private String permissionsArrayToString(String packageName) {
//		StringBuffer tempPerm = new StringBuffer();
//		// Get the permissions for the core android package
//		PackageInfo packageInfo;
//		try {
//			packageInfo = getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
//			if (packageInfo.permissions != null) {
//				// For each defined permission
//				for (PermissionInfo permission : packageInfo.permissions) {
//					// Dump permission info
//					String protectionLevel;
//						switch(permission.protectionLevel) {
//							case PermissionInfo.PROTECTION_NORMAL : protectionLevel = "normal"; break;
//							case PermissionInfo.PROTECTION_DANGEROUS : protectionLevel = "dangerous"; break;
//							case PermissionInfo.PROTECTION_SIGNATURE : protectionLevel = "signature"; break;
//							case PermissionInfo.PROTECTION_SIGNATURE_OR_SYSTEM : protectionLevel = "signatureOrSystem"; break;
//							default : protectionLevel = "<unknown>"; break;
//					}
//					String logInfo = permission.name + "," + protectionLevel + "\n";
//					tempPerm.append(logInfo);
//					Log.i("AndroidPerm", logInfo);
//				}
//			}
//			return tempPerm.toString();
//		} catch (NameNotFoundException e) {
//			e.printStackTrace();
//			return "";
//		}
//	}
}