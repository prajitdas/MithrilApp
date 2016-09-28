package edu.umbc.cs.ebiquity.mithril.util.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AppInstallBroadcastReceiver extends BroadcastReceiver {
    public AppInstallBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = new String();
        /**
         * Broadcast Action: A new application package has been installed on the device. The data contains the name of the package. Note that the newly installed package does not receive this broadcast.
         * May include the following extras:
         * EXTRA_UID containing the integer uid assigned to the new package.
         * EXTRA_REPLACING is set to true if this is following an ACTION_PACKAGE_REMOVED broadcast for the same package.
         * This is a protected intent that can only be sent by the system.
         * Constant Value: "android.intent.action.PACKAGE_ADDED"
         */
        if(intent.getAction() == "android.intent.action.PACKAGE_ADDED") {
            message = "App installed: " + webserviceSendDataHelper.findNewlyInstalledApp(Intent.EXTRA_UID);
//			Log.d(HeimdallApplication.getDebugTag(), "Installation complete!\n"+message);
            webserviceSendDataHelper.collectTheData();
            webserviceSendDataHelper.sendTheData();
            Notification(context, message);
        }
        /**
         * Broadcast Action: An existing application package has been changed (e.g. a component has been enabled or disabled). The data contains the name of the package.
         * EXTRA_UID containing the integer uid assigned to the package.
         * EXTRA_CHANGED_COMPONENT_NAME_LIST containing the class name of the changed components (or the package name itself).
         * EXTRA_DONT_KILL_APP containing boolean field to override the default action of restarting the application.
         * This is a protected intent that can only be sent by the system.
         * Constant Value: "android.intent.action.PACKAGE_CHANGED"
         */
        else if(intent.getAction() == "android.intent.action.PACKAGE_CHANGED") {
            /**
             * Don't send data on update for now
             */
//			Log.d(HeimdallApplication.getDebugTag(), "package changed, nothing to do");
//			message = "An existing application package has been changed (e.g. a component has been enabled or disabled): " + webserviceSendDataHelper.findPackageChanged(Intent.EXTRA_UID);
//			webserviceSendDataHelper.collectTheData();
//			webserviceSendDataHelper.sendTheData();
        }
        /**
         * Broadcast Action: An existing application package has been removed from the device. The data contains the name of the package. The package that is being installed does not receive this Intent.
         * EXTRA_UID containing the integer uid previously assigned to the package.
         * EXTRA_DATA_REMOVED is set to true if the entire application -- data and code -- is being removed.
         * EXTRA_REPLACING is set to true if this will be followed by an ACTION_PACKAGE_ADDED broadcast for the same package.
         * This is a protected intent that can only be sent by the system.
         * Constant Value: "android.intent.action.PACKAGE_REMOVED"
         */
        else if(intent.getAction() == "android.intent.action.PACKAGE_REMOVED") {
            /**
             * Don't send data on uninstall app for now
             */
            message = "App uninstalled: " + webserviceSendDataHelper.findPackageRemoved(Intent.EXTRA_UID);
//			Log.d(HeimdallApplication.getDebugTag(), "Uninstallation complete!\n"+message);
            webserviceSendDataHelper.collectTheData();
            webserviceSendDataHelper.sendTheData();
            Notification(context, message);
        }
        /**
         * Broadcast Action: A new version of an application package has been installed, replacing an existing version that was previously installed. The data contains the name of the package.
         * May include the following extras:
         * EXTRA_UID containing the integer uid assigned to the new package.
         * This is a protected intent that can only be sent by the system.
         * Constant Value: "android.intent.action.PACKAGE_REPLACED"
         */
        else if(intent.getAction() == "android.intent.action.PACKAGE_REPLACED") {
            /**
             * Don't send data on update for now
             */
//			Log.d(HeimdallApplication.getDebugTag(), "package replaced, nothing to do");
//			message = "New app installed is: " + webserviceSendDataHelper.findPackageReplaced(Intent.EXTRA_UID);
//			webserviceSendDataHelper.collectTheData();
//			webserviceSendDataHelper.sendTheData();
        }
    }
}
