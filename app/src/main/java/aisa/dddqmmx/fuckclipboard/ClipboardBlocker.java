package aisa.dddqmmx.fuckclipboard;

import android.app.AppOpsManager;

import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ClipboardBlocker implements IXposedHookLoadPackage {

    private static int opWriteClipboard;

    static {
        try {
            // 缓存反射字段
            Field opWriteClipboardField = XposedHelpers.findField(AppOpsManager.class, "OP_WRITE_CLIPBOARD");
            opWriteClipboard = (int) opWriteClipboardField.get(null);
        } catch (Exception e) {
            XposedBridge.log("Error initializing AppOpsManager field: " + e);
        }
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!"android".equals(lpparam.packageName)) {
            return;
        }

        try {
            Class<?> clipboardService = XposedHelpers.findClass("com.android.server.clipboard.ClipboardService", lpparam.classLoader);

            hookClipboardAccessMethod(clipboardService);

            XposedBridge.log("ClipboardBlocker hooked successfully");
        } catch (Throwable t) {
            XposedBridge.log("Hook failed: " + t + " 包名: " + lpparam.packageName + " 进程名: " + lpparam.processName);
        }
    }

    private void hookClipboardAccessMethod(Class<?> clipboardService) {
        try {
            XposedHelpers.findAndHookMethod(clipboardService, "clipboardAccessAllowed", int.class, String.class, String.class, int.class, int.class, int.class, boolean.class, boolean.class, boolean.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            int op = (int) param.args[0]; // 第一个参数
                            if (op == opWriteClipboard) {
                                return;
                            }
                            String callingPackage = (String) param.args[1]; // 第二个参数
                            if ("com.google.android.inputmethod.latin".equals(callingPackage)) {
                                return;
                            }
                            param.setResult(false); // 拒绝访问剪贴板
                        }
                    });
        } catch (Exception e) {
            XposedBridge.log("Error hooking clipboardAccessAllowed: " + e);
        }
    }
}
