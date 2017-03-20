package edu.umbc.ebiquity.mithril.util.specialtasks.root;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions.PhoneNotRootedException;

/*
 * Created by prajit on 8/26/2016.
 * Original source: http://www.stealthcopter.com/blog/2010/01/android-requesting-root-access-in-your-app/
 */

public class RootAccess {
    private Process rootProcess;
    private Context appContext;

    public RootAccess(Context context) throws PhoneNotRootedException {
        if (!isRooted())
            throw new PhoneNotRootedException();
        appContext = context;
    }

    public boolean runScript(String[] statementsToRun) {
        try {
            // Preform su to get root privileges
            rootProcess = Runtime.getRuntime().exec(statementsToRun);

//            // Attempt to write a file to a root-only
//            DataOutputStream os = new DataOutputStream(rootProcess.getOutputStream());
//            os.writeBytes("echo \"Do I have root?\" >/system/sd/temporary.txt\n");

            // Close the terminal
//            os.writeBytes("exit\n");
//            os.flush();
            try {
                rootProcess.waitFor();
                if (rootProcess.exitValue() != 255) {
                    // TODO Code to run on success
                    Toast.makeText(appContext, "root", Toast.LENGTH_LONG).show();
                } else {
                    // TODO Code to run on unsuccessful
                    Toast.makeText(appContext, "can't root", Toast.LENGTH_LONG).show();
                    return false;
                }
            } catch (InterruptedException e) {
                // TODO Code to run in interrupted exception
                Toast.makeText(appContext, "can't root", Toast.LENGTH_LONG).show();
                return false;
            }
        } catch (IOException e) {
            // TODO Code to run in input/output exception
            Toast.makeText(appContext, "can't root", Toast.LENGTH_LONG).show();
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