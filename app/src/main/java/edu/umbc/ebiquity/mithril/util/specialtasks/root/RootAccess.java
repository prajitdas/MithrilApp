package edu.umbc.ebiquity.mithril.util.specialtasks.root;

import android.content.Context;
import android.util.Log;

import com.jaredrummler.android.shell.CommandResult;
import com.jaredrummler.android.shell.Shell;
import com.scottyab.rootbeer.RootBeer;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import edu.umbc.ebiquity.mithril.MithrilAC;
import edu.umbc.ebiquity.mithril.util.specialtasks.errorsnexceptions.PhoneNotRootedException;

/**
 * Source: https://github.com/scottyab/rootbeer
 * and: https://github.com/jaredrummler/AndroidShell
 */
public class RootAccess {
    public RootAccess(Context context) throws PhoneNotRootedException {
        if (!isRooted(context))
            throw new PhoneNotRootedException();
    }

    public static boolean isRooted(Context context) {
        RootBeer rootBeer = new RootBeer(context);
        if (rootBeer.isRooted()) {
            //we found indication of root
            return true;
        } else {
            //we didn't find indication of root
            return false;
        }
    }

    public static void exec(String[] statementsToRun) {
        CommandResult result = Shell.SU.run(statementsToRun);
        if (result.isSuccessful()) {
            Log.d(MithrilAC.getDebugTag(), result.getStdout());
            /** Example output on a rooted device:
             *  uid=0(root) gid=0(root) groups=0(root) context=u:r:init:s0
             *  CommandResult Shell.run(String shell, String... commands)
             *  CommandResult Shell.SH.run(String... commands)
             *  CommandResult Shell.SU.run(String... commands)
             */
        }
    }
}