package edu.umbc.cs.ebiquity.mithril.util;

import android.content.Context;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by prajit on 8/26/2016.
 */

public class RootAccess {
    private Process rootProcess;
    private Context appContext;

    public RootAccess(Context context) {
        appContext = context;
    }

    public void checkAccess() {
        try {
            // Preform su to get root privileges
            rootProcess = Runtime.getRuntime().exec("su");

            // Attempt to write a file to a root-only
            DataOutputStream os = new DataOutputStream(rootProcess.getOutputStream());
            os.writeBytes("echo \"Do I have root?\" >/system/sd/temporary.txt\n");

            // Close the terminal
            os.writeBytes("exit\n");
            os.flush();
            try {
                rootProcess.waitFor();
                if (rootProcess.exitValue() != 255) {
                    // TODO Code to run on success
                    Toast.makeText(appContext,"root",Toast.LENGTH_LONG).show();
                }
                else {
                    // TODO Code to run on unsuccessful
                    Toast.makeText(appContext,"can't root",Toast.LENGTH_LONG).show();
                }
            } catch (InterruptedException e) {
                // TODO Code to run in interrupted exception
                Toast.makeText(appContext,"can't root",Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            // TODO Code to run in input/output exception
            Toast.makeText(appContext,"can't root",Toast.LENGTH_LONG).show();
        }
    }
}