package aisa.dddqmmx.fuckclipboard;

import android.content.ClipData;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ClipboardBlocker implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        try {
            // 获取 ClipboardManager 类
            Class<?> clipboardManagerClass = XposedHelpers.findClass("android.content.ClipboardManager", lpparam.classLoader);
            // Hook getPrimaryClip 方法
            XposedHelpers.findAndHookMethod(clipboardManagerClass, "getPrimaryClip", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(ClipData.newPlainText("blocked", "")); // 返回空剪贴板内容
                }
            });
            // Hook getText 方法（部分旧版本 Android 可能使用此方法）
            XposedHelpers.findAndHookMethod(clipboardManagerClass, "getText", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(""); // 返回空字符串
                }
            });
        } catch (Throwable t) {
            XposedBridge.log("Xposed Hook 失败：" + t.getMessage());
        }
    }
}
