package edu.umbc.cs.ebiquity.mithril.util.specialtasks.detectrunningapps;

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
public class ReadLogs {
    /**
     * Method returns a LogBuilder object with all the log info on LAUNCHER intent. We will extract the component part from it and know what app was launched!
     *
     * @return LogBuilder
     */
    public static StringBuilder readLogs() {
        StringBuilder logBuilder = new StringBuilder();
        try {
            //logcat -d dumps and exits the process! Won't work for me :(
            Process process = Runtime.getRuntime().exec(MithrilApplication.getDetectAppLaunchCmd());
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(MithrilApplication.getConstLogLaunchIntentTxt()))
                    logBuilder.append(line + "\n");
            }
        } catch (IOException e) {
        }
        Log.d(MithrilApplication.getDebugTag(), "Log: " + logBuilder);
        return logBuilder;
    }
}
