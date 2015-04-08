/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.umbc.cs.ebiquity.mithril.androidperm;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {
	private TextView mTextViewPermissionInfo;
	private PackageManager packageManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		init();
		initView();
	}
	
	private void initView() {
		mTextViewPermissionInfo = (TextView) findViewById(R.id.textViewPermissionInfo);
		mTextViewPermissionInfo.setText(getPermissions());
	}

	private void init() {
		packageManager = getApplicationContext().getPackageManager();
	}

	private String getPermissions() {
		StringBuffer tempAppInfo = new StringBuffer();
		int flags = PackageManager.GET_META_DATA | 
	            PackageManager.GET_SHARED_LIBRARY_FILES |     
	            PackageManager.GET_UNINSTALLED_PACKAGES | 
	            PackageManager.GET_PERMISSIONS;
		Log.v("AndroidPerm", Integer.toString(flags));
		for(PackageInfo pack : packageManager.getInstalledPackages(flags)) {
			if ((pack.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1) {
				tempAppInfo.append(packageManager.getApplicationLabel(pack.applicationInfo).toString()+", "+pack.packageName+", "+permissionsArrayToString(pack.packageName));
				tempAppInfo.append("\n");
			}
		}
		return tempAppInfo.toString();
	}
	
	private String permissionsArrayToString(String packageName) {
		StringBuffer tempPerm = new StringBuffer();
		// Get the permissions for the core android package
		PackageInfo packageInfo;
		try {
			packageInfo = getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
			if (packageInfo.permissions != null) {
				// For each defined permission
				for (PermissionInfo permission : packageInfo.permissions) {
					// Dump permission info
					String protectionLevel;
						switch(permission.protectionLevel) {
							case PermissionInfo.PROTECTION_NORMAL : protectionLevel = "normal"; break;
							case PermissionInfo.PROTECTION_DANGEROUS : protectionLevel = "dangerous"; break;
							case PermissionInfo.PROTECTION_SIGNATURE : protectionLevel = "signature"; break;
							case PermissionInfo.PROTECTION_SIGNATURE_OR_SYSTEM : protectionLevel = "signatureOrSystem"; break;
							default : protectionLevel = "<unknown>"; break;
					}
					String logInfo = "Permission: " + permission.name + " with protection level: " + protectionLevel;
					tempPerm.append(logInfo);
					Log.i("AndroidPerm", logInfo);
				}
			}
			return tempPerm.toString();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}
}