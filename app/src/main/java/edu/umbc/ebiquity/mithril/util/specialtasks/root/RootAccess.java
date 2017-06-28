package edu.umbc.ebiquity.mithril.util.specialtasks.root;

import android.util.Log;
import android.widget.ProgressBar;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions.PhoneNotRootedException;

/*
 * Created by prajit on 8/26/2016.
 * Original source: http://www.stealthcopter.com/blog/2010/01/android-requesting-root-access-in-your-app/
 */

public class RootAccess {
    private boolean rooted;

    public RootAccess() throws PhoneNotRootedException {
        testRooted();
        if (!isRooted())
            throw new PhoneNotRootedException();
    }

    public boolean isRooted() throws PhoneNotRootedException {
        return rooted;
    }

    private void setRooted(boolean rooted) {
        this.rooted = rooted;
    }

    public boolean runScript(String[] statementsToRun) throws PhoneNotRootedException {
        if (!isRooted())
            throw new PhoneNotRootedException();
        try {
            // Preform su to get root privileges
            Process rootProcess = Runtime.getRuntime().exec("su");
            // Attempt to write a file to a root-only
            StringBuffer output = new StringBuffer();
            try {
                DataOutputStream os = new DataOutputStream(rootProcess.getOutputStream());
                for (int commandCount = 0; commandCount < statementsToRun.length; commandCount++) {
                    String command = statementsToRun[commandCount] + "\n";
                    os.writeBytes(command);
                }
                // Close the terminal
                os.writeBytes("exit\n");
                os.flush();
                try {
                    // Waits for the command to finish.
                    rootProcess.waitFor();
                    if (rootProcess.exitValue() != 255) {
                        // TODO Code to run on success
                        Log.d(MithrilAC.getDebugTag(), "Got root!");
                    } else {
                        // TODO Code to run on unsuccessful
                        Log.d(MithrilAC.getDebugTag(), "Can't root, exit value = " + Integer.toString(rootProcess.exitValue()));
                        throw new PhoneNotRootedException();
                    }
                } catch (InterruptedException e) {
                    // TODO Code to run in interrupted exception
                    Log.d(MithrilAC.getDebugTag(), "Can't root, interrupted exception: " + e.getMessage());
                    throw new PhoneNotRootedException();
                }
            } catch (IOException e) {
                // TODO Code to run in input/output exception
                Log.d(MithrilAC.getDebugTag(), "Can't root, I/O exception: " + e.getMessage());
                throw new PhoneNotRootedException();
            }
        } catch (IOException e) {
            // TODO Code to run in input/output exception
            Log.d(MithrilAC.getDebugTag(), "Can't root, I/O exception: " + e.getMessage());
            throw new PhoneNotRootedException();
        }
        return true;
    }

    /**
     * Checks if the device is rooted.
     *
     * @return <code>true</code> if the device is rooted, <code>false</code> otherwise.
     */
    private void testRooted() throws PhoneNotRootedException {
        // get from build info
        String buildTags = android.os.Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys")) {
            setRooted(true);
            return;
        }
        // check if /system/app/Superuser.apk is present
        try {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists()) {
                setRooted(true);
                return;
            }
        } catch (Exception e1) {
            // ignore
        }
        // try executing commands
        if (canExecuteCommand("/system/xbin/which su | tr -d '\\n'") || canExecuteCommand("/system/bin/which su | tr -d '\\n'") || canExecuteCommand("which su | tr -d '\\n'")) {
            setRooted(true);
            return;
        }
        setRooted(false);
        //Phone is not rooted
        throw new PhoneNotRootedException();
    }

    // executes a command on the system
    private boolean canExecuteCommand(String command) {
        try {
            runScript(new String[]{command});
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}