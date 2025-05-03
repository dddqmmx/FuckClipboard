package aisa.dddqmmx.fuckclipboard;

import android.app.AppOpsManager;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ClipboardBlocker implements IXposedHookLoadPackage{

    private static int opWriteClipboard;
    private static final String WHITELIST_PREFS = "clipboard_whitelist_prefs";
    private static final String WHITELIST_KEY = "whitelist";
    public static Set<String> WHITELIST = Collections.emptySet();

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
            // 加载白名单
            WHITELIST = loadWhitelist();
            XposedBridge.log("Whitelist loaded: " + WHITELIST);

            Class<?> clipboardService = XposedHelpers.findClass("com.android.server.clipboard.ClipboardService", lpparam.classLoader);
            hookClipboardAccessMethod(clipboardService);
            XposedBridge.log("ClipboardBlocker hooked successfully");
        } catch (Throwable t) {
            XposedBridge.log("Hook failed: " + t + " 包名: " + lpparam.packageName + " 进程名: " + lpparam.processName);
        }
    }

    private static XSharedPreferences getPref() {
        XSharedPreferences pref = new XSharedPreferences(BuildConfig.APPLICATION_ID, ClipboardBlocker.WHITELIST_PREFS);
        return pref.getFile().canRead() ? pref : null;
    }

    private static Set<String> loadWhitelist() {
        XSharedPreferences pref = getPref();
        if (pref != null) {
            pref.reload();
            Set<String> whitelist = new HashSet<>();
            Set<String> savedWhitelist = pref.getStringSet(WHITELIST_KEY, null);
            if (savedWhitelist != null) {
                whitelist.addAll(savedWhitelist);
            }
            return whitelist;
        }
        return Collections.emptySet();
    }

//    public static void refreshWhitelist() {
//        WHITELIST = loadWhitelist();
//        XposedBridge.log("Whitelist refreshed: " + WHITELIST);
//    }

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
                            if (WHITELIST.contains(callingPackage)) {
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