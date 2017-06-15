package edu.umbc.ebiquity.mithril.util.specialtasks.root;

import android.util.Log;

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
    private Process rootProcess;
    private boolean rooted;

    public RootAccess() throws PhoneNotRootedException {
        testRooted();
        if (!isRooted())
            throw new PhoneNotRootedException();
    }

    public Process getRootProcess() {
        return rootProcess;
    }

    public void setRootProcess(Process rootProcess) {
        this.rootProcess = rootProcess;
    }

    public boolean isRooted() {
        return rooted;
    }

    public void setRooted(boolean rooted) {
        this.rooted = rooted;
    }

    public boolean runScript(String[] statementsToRun) throws PhoneNotRootedException {
        if (!isRooted())
            throw new PhoneNotRootedException();
        try {
            // Preform su to get root privileges
            rootProcess = Runtime.getRuntime().exec("su");
//            // Attempt to write a file to a root-only
//            DataOutputStream os = new DataOutputStream(rootProcess.getOutputStream());
//            os.writeBytes("echo \"Do I have root?\" >/system/temporary.txt\n");
//            os.writeBytes("rm /system/temporary.txt\n");
            runOnSU(statementsToRun);
        } catch (IOException e) {
            // TODO Code to run in input/output exception
            Log.d(MithrilAC.getDebugTag(), "Can't root, I/O exception: " + e.getMessage());
            throw new PhoneNotRootedException();
        }
        return true;
    }

    private void runOnSU(String[] statementsToRun) throws PhoneNotRootedException {
        StringBuffer output = new StringBuffer();
        try {
            DataOutputStream os = new DataOutputStream(rootProcess.getOutputStream());
            for (int commandCount = 0; commandCount < statementsToRun.length; commandCount++) {
                String command = statementsToRun[commandCount] + "\n";
                os.writeBytes(command);
            }

//            // Reads stdout.
//            // NOTE: You can write to stdin of the command using
//            //       process.getOutputStream().
//            BufferedReader reader = new BufferedReader(
//                    new InputStreamReader(rootProcess.getInputStream()));
//            int read;
//            char[] buffer = new char[4096];
//            while ((read = reader.read(buffer)) > 0) {
//                output.append(buffer, 0, read);
//            }
//            reader.close();

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
//        return output.toString();
    }


    /**
     * Checks if the device is rooted.
     *
     * @return <code>true</code> if the device is rooted, <code>false</code> otherwise.
     */
    public void testRooted() throws PhoneNotRootedException {

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
        boolean executedSuccessfully;
        try {
            rootProcess = Runtime.getRuntime().exec("su");
            runOnSU(new String[]{command});
            executedSuccessfully = true;
        } catch (Exception e) {
            executedSuccessfully = false;
        }

        return executedSuccessfully;
    }
}