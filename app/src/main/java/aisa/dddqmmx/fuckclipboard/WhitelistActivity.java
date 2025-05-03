package aisa.dddqmmx.fuckclipboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

public class WhitelistActivity extends AppCompatActivity {

    private EditText editPackage;
    private RecyclerView recyclerView;
    private WhitelistAdapter adapter;
    private SharedPreferences prefs;
    private Set<String> whitelist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editPackage = findViewById(R.id.edit_package);
        recyclerView = findViewById(R.id.whitelist_recycler);
        Button buttonAdd = findViewById(R.id.button_add);

        prefs = getSharedPreferences("clipboard_whitelist_prefs", Context.MODE_WORLD_READABLE);
        whitelist = new HashSet<>(prefs.getStringSet("whitelist", new HashSet<>()));

        adapter = new WhitelistAdapter(new ArrayList<>(whitelist), this::removePackage);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        buttonAdd.setOnClickListener(v -> {
            String pkg = editPackage.getText().toString().trim();
            if (!pkg.isEmpty() && whitelist.add(pkg)) {
                adapter.addPackage(pkg);
                saveWhitelist();
                editPackage.setText("");
            } else {
                Toast.makeText(this, "包名为空或已存在", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removePackage(String pkg) {
        if (whitelist.remove(pkg)) {
            saveWhitelist();
        }
    }

    private void saveWhitelist() {
        prefs.edit().putStringSet("whitelist", new HashSet<>(whitelist)).apply();
    }
}
