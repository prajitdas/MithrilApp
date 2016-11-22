package edu.umbc.cs.ebiquity.mithril.util.specialtasks.root;

/**
 * Created by Prajit on 10/3/2016.
 * READ_LOGS
 * Added in API level 1
 * String READ_LOGS
 * Allows an application to read the low-level system log files.
 * Not for use by third-party applications, because Log entries can contain the user's private information.
 * Constant Value: "android.permission.READ_LOGS"
 */

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;

/**
 * This class will provide utility to read logs.
 *
 * @author Chintan Rathod (http://chintanrathod.com)
 * from: http://chintanrathod.com/read-logs-programmatically-in-android/
 */
public class LogsUtil {
    /**
     * <uses-permission android:name="android.permission.READ_LOGS" />
     * read logs needs the above permission. We are unable to trigger it from inside the app. Something special needs to be done in this case?
     * We need to execute
     * adb shell pm grant edu.umbc.cs.ebiquity.mithril android.permission.READ_LOGS
     * as per the instructions here: http://stackoverflow.com/a/11517421/1816861
     * from terminal and just the shell command from this class as below:
     * pm grant edu.umbc.cs.ebiquity.mithril android.permission.READ_LOGS
     */
    public static boolean getReadLogsPermission(Context context) {
        String pname = context.getPackageName();
        RootAccess rootAccess = new RootAccess(context);
        String[] CMDLINE_GRANTPERMS = {"su", "-c", null};
        if (context.getPackageManager().checkPermission(android.Manifest.permission.READ_LOGS, pname) != 0) {
            Log.d(MithrilApplication.getDebugTag(), "we do not have the READ_LOGS permission!");
            if (android.os.Build.VERSION.SDK_INT >= 16) {
                Log.d(MithrilApplication.getDebugTag(), "Working around JellyBeans 'feature'...");
                try {
                    CMDLINE_GRANTPERMS[2] = MithrilApplication.getReadLogsPermissionForAppCmd();
                    boolean result = rootAccess.runScript(CMDLINE_GRANTPERMS);
                    if (!result)
                        throw new Exception("failed to become root");
                } catch (Exception e) {
                    Log.d(MithrilApplication.getDebugTag(), "exec(): " + e);
                    Toast.makeText(context, "Failed to obtain READ_LOGS permission", Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        } else
            Log.d(MithrilApplication.getDebugTag(), "we have the READ_LOGS permission already!");
        return true;
    }

    public static StringBuilder readLogs() {
        StringBuilder logBuilder = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(MithrilApplication.getConstLogLaunchIntentTxt()))
                    logBuilder.append(line + "\n");
            }
        } catch (IOException e) {
        }
        return logBuilder;
    }
}
