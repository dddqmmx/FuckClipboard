package aisa.dddqmmx.fuckclipboard;

import android.content.ClipboardManager;
import android.content.ClipData;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView clipboardContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clipboardContent = findViewById(R.id.clipboardContent);

        // 给 TextView 添加焦点监听，模拟焦点获取
        clipboardContent.setFocusable(true);
        clipboardContent.setFocusableInTouchMode(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 获取焦点后再尝试读取剪贴板内容
        readClipboardContent();
    }

    // 获取剪贴板内容
    private void readClipboardContent() {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        if (clipboardManager != null && clipboardManager.hasPrimaryClip()) {
            ClipData clipData = clipboardManager.getPrimaryClip();
            String clipboardText = clipData.getItemAt(0).getText().toString();
            clipboardContent.setText("剪贴板内容: " + clipboardText);
        } else {
            clipboardContent.setText("剪贴板为空或不可访问");
        }
    }
}
