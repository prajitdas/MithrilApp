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

import android.util.Log;

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
    public static StringBuilder getReadLogsPermission() {
        StringBuilder logBuilder = new StringBuilder();
        try {
            /**
             * This is more complicated than I thought it would be
             * https://developer.android.com/guide/components/processes-and-threads.html
             */
            Process process = Runtime.getRuntime().exec(MithrilApplication.getReadLogsPermissionForAppCmd());
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                logBuilder.append(line + "\n");
            }
        } catch (IOException e) {
            Log.d(MithrilApplication.getDebugTag(), "Some exception occurred: " + e.getMessage());
        }
        return logBuilder;
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
