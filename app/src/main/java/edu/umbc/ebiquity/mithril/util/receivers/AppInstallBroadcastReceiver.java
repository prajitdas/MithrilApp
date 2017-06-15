package edu.umbc.ebiquity.mithril.util.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import java.util.HashMap;
import java.util.Map;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.data.model.components.AppData;

public class AppInstallBroadcastReceiver extends BroadcastReceiver {
    private PackageManager packageManager;
    private SQLiteDatabase mithrilDB;
    private int flags = PackageManager.GET_META_DATA |
            PackageManager.GET_SHARED_LIBRARY_FILES |
            PackageManager.GET_PERMISSIONS;

    public AppInstallBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        packageManager = context.getPackageManager();
        initDB(context);
//        Log.d(MithrilAC.getDebugTag(), "Action: "+intent.getAction());
//        Log.d(MithrilAC.getDebugTag(), "Uid: "+Integer.toString(intent.getIntExtra(Intent.EXTRA_UID, 0)));

        /**
         * Broadcast Action: A new application package has been installed on the device. The data contains the name of the package. Note that the newly installed package does not receive this broadcast.
         * May include the following extras:
         * EXTRA_UID containing the integer uid assigned to the new package.
         * EXTRA_REPLACING is set to true if this is following an ACTION_PACKAGE_REMOVED broadcast for the same package.
         * This is a protected intent that can only be sent by the system.
         * Constant Value: "android.intent.action.PACKAGE_ADDED"
         */
        if (intent.getAction() == "android.intent.action.PACKAGE_ADDED") {
            String[] packagesInstalled = packageManager.getPackagesForUid(intent.getIntExtra(Intent.EXTRA_UID, 0));
            for (String pkgName : packagesInstalled) {
                try {
//                    Log.d(MithrilAC.getDebugTag(), "Package: "+pkgName);
                    PackageInfo packageInfo = packageManager.getPackageInfo(pkgName, flags);
                    AppData tempAppData = new AppData();
                    if (packageInfo.packageName != null) {
                        if (packageInfo.applicationInfo.loadDescription(packageManager) != null)
                            tempAppData.setAppDescription(packageInfo.applicationInfo.loadDescription(packageManager).toString());
                        else
                            tempAppData.setAppDescription(MithrilAC.getDefaultDescription());
                        tempAppData.setAssociatedProcessName(packageInfo.applicationInfo.processName);
                        tempAppData.setTargetSdkVersion(packageInfo.applicationInfo.targetSdkVersion);
                        if (packageInfo.applicationInfo.loadIcon(packageManager) instanceof BitmapDrawable)
                            tempAppData.setIcon(((BitmapDrawable) packageInfo.applicationInfo.loadIcon(packageManager)).getBitmap());
                        else {
                            tempAppData.setIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
                        }
                        tempAppData.setAppName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
                        tempAppData.setPackageName(packageInfo.packageName);
                        tempAppData.setVersionInfo(packageInfo.versionName);
                        tempAppData.setInstalled(true);
                        if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1)
                            tempAppData.setAppType(MithrilAC.getPrefKeySystemAppsDisplay());
                        else
                            tempAppData.setAppType(MithrilAC.getPrefKeyUserAppsDisplay());
                        tempAppData.setUid(packageInfo.applicationInfo.uid);

                        //App permissions
                        if (packageInfo.requestedPermissions != null) {
                            Map<String, Boolean> requestedPermissions = new HashMap<String, Boolean>();
                            String[] packageUsesPermissions = packageInfo.requestedPermissions;
                            for (int permCount = 0; permCount < packageUsesPermissions.length; permCount++) {
                                //TODO fix this eventually because we are not getting all the permission information from the PackageInfo api
                                requestedPermissions.put(packageUsesPermissions[permCount], false);
                                /**
                                 * The following shell script may be used to extract exact permission data.
                                 * However, that will require root access and adb shell code execution.
                                 * Perhaps we should avoid that for now.
                                 * ---------------------------------------------------------------------------------------------------------------------------------------------
                                 findRequestedLineStart=`adb shell dumpsys package com.google.android.youtube | grep -n "requested permissions:" | cut -f1 -d ':'`
                                 findRequestedLineEnd=`adb shell dumpsys package com.google.android.youtube | grep -n "install permissions:" | cut -f1 -d ':'`
                                 findInstallLineStart=`adb shell dumpsys package com.google.android.youtube | grep -n "install permissions:" | cut -f1 -d ':'`
                                 findInstallLineEnd=`adb shell dumpsys package com.google.android.youtube | grep -n "installed=true" | cut -f1 -d ':'`

                                 numLinesRequestedPermission=$((findRequestedLineEnd-findRequestedLineStart-1))
                                 adb shell dumpsys package com.google.android.youtube | grep -A $numLinesRequestedPermission "requested permissions:" | tr -d ' '

                                 numLinesInstalledPermission=$((findInstallLineEnd-findInstallLineStart-1))
                                 adb shell dumpsys package com.google.android.youtube | grep -A $numLinesInstalledPermission "install permissions:" | cut -f1 -d"=" | tr -d ' '
                                 * ---------------------------------------------------------------------------------------------------------------------------------------------
                                 */
                                tempAppData.setPermissions(requestedPermissions);
                                //The following code should not be required. We are taking care of this in mithrilDBHelper.addAppPerm()
//                                for (int permIdx = 0; permIdx < requestedPermissions.length; permIdx++) {
//                                    String permissionName = requestedPermissions[permIdx];
//                                    long permId = mithrilDBHelper.findPermissionsByName(mithrilDB, permissionName);
//                                    if (permId == -1) {
//                                        try {
//                                            PermissionInfo permissionInfo = packageManager.getPermissionInfo(permissionName, PackageManager.GET_META_DATA);
//                                            mithrilDBHelper.addPermission(mithrilDB,
//                                                    mithrilDBHelper.getPermData(
//                                                            packageManager,
//                                                            permissionInfo.group,
//                                                            permissionInfo));
//                                        } catch (PackageManager.NameNotFoundException exception) {
//                                            Log.e(MithrilAC.getDebugTag(), "Some error due to " + exception.getMessage());
//                                            mithrilDBHelper.addPermission(mithrilDB, mithrilDBHelper.getPermData(packageManager, permissionName));
//                                        }
//                                    }
//                                }
                            }
                        }
                    }
                    //Insert app into database
                    long appId = MithrilDBHelper.getHelper(context).addApp(mithrilDB, tempAppData);

                    //Insert permissions for app into AppPerm
                    MithrilDBHelper.getHelper(context).addAppPerm(mithrilDB, tempAppData, appId);
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
        else if (intent.getAction() == "android.intent.action.PACKAGE_FULLY_REMOVED") {
            /**
             * When app is FULLY uninstalled delete it from table
             */
            MithrilDBHelper.getHelper(context).deleteAppByUID(mithrilDB, intent.getIntExtra(Intent.EXTRA_UID, 0));
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
        closeDB();
    }

    private void initDB(Context context) {
        // Let's get the DB instances loaded too
        mithrilDB = MithrilDBHelper.getHelper(context).getWritableDatabase();
    }

    private void closeDB() {
        if (mithrilDB != null)
            mithrilDB.close();
    }
}