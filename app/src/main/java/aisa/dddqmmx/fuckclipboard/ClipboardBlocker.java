package aisa.dddqmmx.fuckclipboard;

import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ClipboardBlocker implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!"android".equals(lpparam.packageName)) {
            return; // Only hook in system server
        }

        try {
            Class<?> clipboardService = XposedHelpers.findClass("com.android.server.clipboard.ClipboardService", lpparam.classLoader);

            try {
                XposedHelpers.findAndHookMethod(
                        clipboardService,
                        "getClipboardLocked",
                        int.class, // userId
                        int.class, // deviceId
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                param.setResult(null);
                            }
                        });
            } catch (Exception e) {
                XposedBridge.log("setPrimaryClipInternalLocked not found: " + e);
            }

            XposedBridge.log("ClipboardBlocker hooked successfully");
        } catch (Throwable t) {
            XposedBridge.log("Hook failed: " + t + " 包名: " + lpparam.packageName + " 进程名: " + lpparam.processName);
        }
    }
}