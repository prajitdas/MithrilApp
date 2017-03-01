package edu.umbc.ebiquity.mithril.util.specialtasks.detect.runningapps;

/*
 * Created by Prajit on 10/3/2016.
 * READ_LOGS
 * Added in API level 1
 * String READ_LOGS
 * Allows an application to read the low-level system log files.
 * Not for use by third-party applications, because Log entries can contain the user's private information.
 * Constant Value: "android.permission.READ_LOGS"
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import edu.umbc.ebiquity.mithril.MithrilApplication;

/*
 * This class will provide utility to read logs.
 *
 * @author Chintan Rathod (http://chintanrathod.com)
 *         from: http://chintanrathod.com/read-logs-programmatically-in-android/
 */
public class ReadLogs {
    /*
     * Method returns a LogBuilder object with all the log info on LAUNCHER intent. We will extract the component part from it and know what app was launched!
     *
     * @return LogBuilder
     */
    public static String readLogs() {
        StringBuilder logBuilder = new StringBuilder();
        try {
            //logcat -d dumps and exits the process! Won't work for me :(
            Process process = Runtime.getRuntime().exec(MithrilApplication.getCmdDetectAppLaunch());
            BufferedReader appLaunchData = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = appLaunchData.readLine()) != null) {
                if (line.contains(MithrilApplication.getLogIntent())) {
//                    Log.d(MithrilApplication.getDebugTag(), "another app launch: " + line);
                    String date = line.substring(0, 4);
                    String time = line.substring(7, 12);
                    int appLuanchedIdx = line.indexOf("cmp=");
                    String appLaunched = line.substring(appLuanchedIdx);
                    int endIdx = appLaunched.indexOf("/");
                    appLaunched = appLaunched.substring(0, endIdx);
                    logBuilder.append(date + ";" + time + ";" + appLaunched + "\n");
                }
            }
        } catch (IOException e) {
        }
//        Log.d(MithrilApplication.getDebugTag(), "ReadLogLog app launch: " + logBuilder);
        return logBuilder.toString();
    }
}
