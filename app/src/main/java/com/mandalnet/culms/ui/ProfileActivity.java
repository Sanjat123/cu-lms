package com.mandalnet.culms.ui;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.mandalnet.culms.R;
import com.mandalnet.culms.utils.LMSPrefs;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("My Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> performLogout());
    }

    private void performLogout() {
        // 1. Clear Saved Cookies in Prefs
        LMSPrefs.saveCookie(this, "");

        // 2. Clear WebView Cookies (Taaki automatic login na ho)
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();

        // 3. Clear Database in background thread (Optional but recommended)
        new Thread(() -> {
            // AppDatabase.getInstance(this).subjectDao().deleteAll();
        }).start();

        // 4. Redirect to Login
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}