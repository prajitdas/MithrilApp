package edu.umbc.ebiquity.mithril.util.specialtasks.execute;

import android.app.AppOpsManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Prajit on 4/13/2017.
 */

public class AppOps {
    private static AppOpsManager appOpsManager;
    private static Class appOpsManagerClass;

    public AppOps(AppOpsManager anAppOpsManager) {
        appOpsManager = anAppOpsManager;
        appOpsManagerClass = appOpsManager.getClass();
    }

    /**
     * See this: https://forums.xamarin.com/discussion/64456/c-java-reflection-access-private-methods-on-underlying-native-instance
     *
     * Original signature: public void setMode(int code, int uid, String packageName, int mode)
     * @param code
     * @param uid
     * @param mode
     * @return
     */
    public static boolean setMode(int code, int uid, String packageName, int mode) {
        try {
            Class[] types = new Class[4];
            types[0] = Integer.TYPE;
            types[1] = Integer.TYPE;
            types[2] = String.class;
            types[3] = Integer.TYPE;
            Method setModeMethod =
                    appOpsManagerClass.getMethod("setMode", types);

            Object[] args = new Object[4];
            args[0] = Integer.valueOf(code);
            args[1] = Integer.valueOf(uid);
            args[2] = packageName;
            args[3] = Integer.valueOf(mode);
            setModeMethod.invoke(appOpsManager, args);

            return true;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void appOps() {
//        PackageInfo mPackageInfo = null;
//        String mPackageName = null;
//        boolean newState = false;
//        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
//        mAppOps.setPrivacyGuardSettingForPackage(app.uid, app.packageName, app.privacyGuardEnabled);
/*
        mAppOps.setMode(AppOpsManager.OP_WRITE_SETTINGS,
                mPackageInfo.applicationInfo.uid, mPackageName, newState
                        ? AppOpsManager.MODE_ALLOWED : AppOpsManager.MODE_ERRORED);
        mCurSysAppOpMode = mAppOps.checkOp(AppOpsManager.OP_SYSTEM_ALERT_WINDOW, uid, pkg);
        mCurToastAppOpMode = mAppOps.checkOp(AppOpsManager.OP_TOAST_WINDOW, uid, pkg);
        mAppOps.setMode(AppOpsManager.OP_SYSTEM_ALERT_WINDOW, uid, pkg, AppOpsManager.MODE_IGNORED);
        mAppOps.setMode(AppOpsManager.OP_TOAST_WINDOW, uid, pkg, AppOpsManager.MODE_IGNORED);
        mAppOps.setMode(AppOpsManager.OP_SYSTEM_ALERT_WINDOW, uid, pkg, mCurSysAppOpMode);
        mAppOps.setMode(AppOpsManager.OP_TOAST_WINDOW, uid, pkg, mCurToastAppOpMode);
        final int switchOp = AppOpsManager.opToSwitch(firstOp.getOp());
        int mode = mAppOps.checkOp(switchOp, entry.getPackageOps().getUid(), entry.getPackageOps().getPackageName());
        mAppOps.setMode(switchOp, entry.getPackageOps().getUid(), entry.getPackageOps().getPackageName(), positionToMode(position));
        sw.setChecked(mAppOps.checkOp(switchOp, entry.getPackageOps()
                .getUid(), entry.getPackageOps().getPackageName()) == AppOpsManager.MODE_ALLOWED);
        sw.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                mAppOps.setMode(switchOp, entry.getPackageOps()
                                .getUid(), entry.getPackageOps()
                                .getPackageName(),
                        isChecked ? AppOpsManager.MODE_ALLOWED
                                : AppOpsManager.MODE_IGNORED);
            }
        });
        List<AppOpsManager.PackageOps> pkgs;
        if (packageName != null) {
            pkgs = mAppOps.getOpsForPackage(uid, packageName, tpl.ops);
        } else {
            pkgs = mAppOps.getPackagesForOps(tpl.ops);
        }
*/
    }
}