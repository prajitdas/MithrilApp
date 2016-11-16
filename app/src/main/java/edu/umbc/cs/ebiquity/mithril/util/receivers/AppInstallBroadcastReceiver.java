package edu.umbc.cs.ebiquity.mithril.util.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.R;
import edu.umbc.cs.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.cs.ebiquity.mithril.data.model.AppData;

public class AppInstallBroadcastReceiver extends BroadcastReceiver {
    private PackageManager packageManager;
    private MithrilDBHelper mithrilDBHelper;
    private SQLiteDatabase mithrilDB;
    private int flags = PackageManager.GET_META_DATA |
            PackageManager.GET_SHARED_LIBRARY_FILES |
            PackageManager.GET_PERMISSIONS;

    public AppInstallBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        packageManager = context.getPackageManager();
        mithrilDBHelper = new MithrilDBHelper(context);
        mithrilDB = mithrilDBHelper.getWritableDatabase();
        Log.d(MithrilApplication.getDebugTag(), "Action: "+intent.getAction());
        Log.d(MithrilApplication.getDebugTag(), "Uid: "+Integer.toString(intent.getIntExtra(Intent.EXTRA_UID, 0)));

        /**
         * Broadcast Action: A new application package has been installed on the device. The data contains the name of the package. Note that the newly installed package does not receive this broadcast.
         * May include the following extras:
         * EXTRA_UID containing the integer uid assigned to the new package.
         * EXTRA_REPLACING is set to true if this is following an ACTION_PACKAGE_REMOVED broadcast for the same package.
         * This is a protected intent that can only be sent by the system.
         * Constant Value: "android.intent.action.PACKAGE_ADDED"
         */
        if(intent.getAction() == "android.intent.action.PACKAGE_ADDED") {
            String[] packagesInstalled = packageManager.getPackagesForUid(intent.getIntExtra(Intent.EXTRA_UID, 0));
            for (String pkgName : packagesInstalled) {
                try {
                    Log.d(MithrilApplication.getDebugTag(), "Package: "+pkgName);
                    PackageInfo packageInfo = packageManager.getPackageInfo(pkgName, flags);
                    AppData tempAppData = new AppData();
                    if (packageInfo.packageName != null) {
                        if(packageInfo.applicationInfo.loadDescription(packageManager) != null)
                            tempAppData.setAppDescription(packageInfo.applicationInfo.loadDescription(packageManager).toString());
                        else
                            tempAppData.setAppDescription(MithrilApplication.getConstDefaultDescription());
                        tempAppData.setAssociatedProcessName(packageInfo.applicationInfo.processName);
                        tempAppData.setTargetSdkVersion(packageInfo.applicationInfo.targetSdkVersion);
                        if(packageInfo.applicationInfo.loadIcon(packageManager) instanceof BitmapDrawable)
                            tempAppData.setIcon(((BitmapDrawable) packageInfo.applicationInfo.loadIcon(packageManager)).getBitmap());
                        else {
                            tempAppData.setIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
                        }
                        tempAppData.setAppName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
                        tempAppData.setPackageName(packageInfo.packageName);
                        tempAppData.setVersionInfo(packageInfo.versionName);
                        tempAppData.setInstalled(true);
                        if((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1)
                            tempAppData.setAppType(MithrilApplication.getSystemAppsDisplayTag());
                        else
                            tempAppData.setAppType(MithrilApplication.getUserAppsDisplayTag());
                        tempAppData.setUid(packageInfo.applicationInfo.uid);
                    }
                    //Find all apps and insert into database
                    mithrilDBHelper.addAppData(mithrilDB, tempAppData);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        /**
         * Broadcast Action: An existing application package has been changed (e.g. a component has been enabled or disabled). The data contains the name of the package.
         * EXTRA_UID containing the integer uid assigned to the package.
         * EXTRA_CHANGED_COMPONENT_NAME_LIST containing the class name of the changed components (or the package name itself).
         * EXTRA_DONT_KILL_APP containing boolean field to override the default action of restarting the application.
         * This is a protected intent that can only be sent by the system.
         * Constant Value: "android.intent.action.PACKAGE_CHANGED"
         */
//        else if(intent.getAction() == "android.intent.action.PACKAGE_CHANGED") {
            /**
             * Don't send data on update for now
             */
//        }
        /**
         * Broadcast Action: An existing application package has been completely removed from the device.
         * The data contains the name of the package. This is like ACTION_PACKAGE_REMOVED, but only set when EXTRA_DATA_REMOVED is true and EXTRA_REPLACING is false of that broadcast.
         * EXTRA_UID containing the integer uid previously assigned to the package.
         * This is a protected intent that can only be sent by the system.
         * Constant Value: "android.intent.action.PACKAGE_FULLY_REMOVED"
         */
        else if(intent.getAction() == "android.intent.action.PACKAGE_FULLY_REMOVED") {
            /**
             * When app is FULLY uninstalled delete it from table
             */
            mithrilDBHelper.deleteAppByUID(mithrilDB, intent.getIntExtra(Intent.EXTRA_UID, 0));
        }
        /**
         * Broadcast Action: A new version of an application package has been installed, replacing an existing version that was previously installed. The data contains the name of the package.
         * May include the following extras:
         * EXTRA_UID containing the integer uid assigned to the new package.
         * This is a protected intent that can only be sent by the system.
         * Constant Value: "android.intent.action.PACKAGE_REPLACED"
         */
//        else if(intent.getAction() == "android.intent.action.PACKAGE_REPLACED") {
            /**
             * Don't send data on update for now
             */
//        }
    }
}
