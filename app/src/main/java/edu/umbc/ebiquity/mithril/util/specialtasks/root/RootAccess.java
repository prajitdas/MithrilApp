package edu.umbc.ebiquity.mithril.util.specialtasks.root;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions.PhoneNotRootedException;

/*
 * Created by prajit on 8/26/2016.
 * Original source: http://www.stealthcopter.com/blog/2010/01/android-requesting-root-access-in-your-app/
 */

public class RootAccess {
    private Process rootProcess;

    public RootAccess() throws PhoneNotRootedException {
        if (!isRooted())
            throw new PhoneNotRootedException();
    }

    public boolean isRoot() {
        try {
            // Preform su to get root privileges
            rootProcess = Runtime.getRuntime().exec("su");

            // Attempt to write a file to a root-only
            DataOutputStream os = new DataOutputStream(rootProcess.getOutputStream());
            os.writeBytes("echo \"Do I have root?\" >/system/sd/temporary.txt\n");
            os.writeBytes("rm /system/sd/temporary.txt\n");

//             Close the terminal
            os.writeBytes("exit\n");
            os.flush();
            try {
                rootProcess.waitFor();
                if (rootProcess.exitValue() != 255) {
                    // TODO Code to run on success
                    Log.d(MithrilApplication.getDebugTag(), "root");
                } else {
                    // TODO Code to run on unsuccessful
                    Log.d(MithrilApplication.getDebugTag(), "can't root");
                    return false;
                }
            } catch (InterruptedException e) {
                // TODO Code to run in interrupted exception
                Log.d(MithrilApplication.getDebugTag(), "can't root");
                return false;
            }
        } catch (IOException e) {
            // TODO Code to run in input/output exception
            Log.d(MithrilApplication.getDebugTag(), "can't root");
            return false;
        }
        return true;
    }

    public boolean runScript(String[] statementsToRun) {
        if (!isRoot())
            return false;
        try {
            // Preform su to get root privileges
            rootProcess = Runtime.getRuntime().exec("su");

            // Attempt to write a file to a root-only
            DataOutputStream os = new DataOutputStream(rootProcess.getOutputStream());
            for (int commandCount = 0; commandCount < statementsToRun.length; commandCount++) {
                String command = statementsToRun[commandCount] + "\n";
                os.writeBytes(command);
            }

//             Close the terminal
            os.writeBytes("exit\n");
            os.flush();
            try {
                rootProcess.waitFor();
                if (rootProcess.exitValue() != 255) {
                    // TODO Code to run on success
                    Log.d(MithrilApplication.getDebugTag(), "root");
                } else {
                    // TODO Code to run on unsuccessful
                    Log.d(MithrilApplication.getDebugTag(), "can't root");
                    return false;
                }
            } catch (InterruptedException e) {
                // TODO Code to run in interrupted exception
                Log.d(MithrilApplication.getDebugTag(), "can't root");
                return false;
            }
        } catch (IOException e) {
            // TODO Code to run in input/output exception
            Log.d(MithrilApplication.getDebugTag(), "can't root");
            return false;
        }
        return true;
    }


    /**
     * Checks if the device is rooted.
     *
     * @return <code>true</code> if the device is rooted, <code>false</code> otherwise.
     */
    public boolean isRooted() {

        // get from build info
        String buildTags = android.os.Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }

        // check if /system/app/Superuser.apk is present
        try {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists()) {
                return true;
            }
        } catch (Exception e1) {
            // ignore
        }

        // try executing commands
        return canExecuteCommand("/system/xbin/which su")
                || canExecuteCommand("/system/bin/which su") || canExecuteCommand("which su");
    }

    // executes a command on the system
    private boolean canExecuteCommand(String command) {
        boolean executedSuccessfully;
        try {
            Runtime.getRuntime().exec(command);
            executedSuccessfully = true;
        } catch (Exception e) {
            executedSuccessfully = false;
        }

        return executedSuccessfully;
    }
}