package com.mandalnet.culms.ui;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.bumptech.glide.Glide;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.mandalnet.culms.R;
import com.mandalnet.culms.api.LMSFetcher;
import com.mandalnet.culms.utils.LMSPrefs;

/**
 * ProfileActivity: Handles User Profile display, Theme switching, and Logout.
 * Part of the MandalNet LMS Native Experience.
 */
public class ProfileActivity extends AppCompatActivity {

    private ImageView ivProfile;
    private TextView tvStudentName;
    private SwitchMaterial themeSwitch;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 1. Toolbar Setup
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("MandalNet Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 2. Initialize UI Components
        ivProfile = findViewById(R.id.ivProfile);
        tvStudentName = findViewById(R.id.tvStudentName);
        themeSwitch = findViewById(R.id.themeSwitch);
        btnLogout = findViewById(R.id.btnLogout);

        // 3. Load Real Profile Data from LMS
        loadProfileData();

        // 4. Dark Mode Switch Logic
        setupThemeSwitch();

        // 5. Logout Button Logic
        btnLogout.setOnClickListener(v -> performLogout());
    }

    /**
     * Calls LMSFetcher to scrape Name and Profile Picture from /user/profile.php
     */
    private void loadProfileData() {
        LMSFetcher.fetchUserProfile(this, new LMSFetcher.ProfileCallback() {
            @Override
            public void onSuccess(String name, String imageUrl, String email) {
                runOnUiThread(() -> {
                    tvStudentName.setText(name);
                    
                    // Load Profile Image using Glide for a smooth native feel
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Glide.with(ProfileActivity.this)
                                .load(imageUrl)
                                .placeholder(android.R.drawable.ic_menu_myplaces)
                                .circleCrop()
                                .into(ivProfile);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> 
                    Toast.makeText(ProfileActivity.this, "Profile Sync Failed: " + error, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    /**
     * Handles switching between Light and Dark mode using AppCompatDelegate.
     */
    private void setupThemeSwitch() {
        // Sync switch state with current system mode
        int currentMode = AppCompatDelegate.getDefaultNightMode();
        themeSwitch.setChecked(currentMode == AppCompatDelegate.MODE_NIGHT_YES);

        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                Toast.makeText(this, "Dark Mode Enabled", Toast.LENGTH_SHORT).show();
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                Toast.makeText(this, "Light Mode Enabled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Clears all session data and redirects the user to the Login screen.
     */
    private void performLogout() {
        // 1. Clear SharedPreferences Cookies
        LMSPrefs.saveCookie(this, "");

        // 2. Clear System WebView Cookies (Important for re-login)
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();

        // 3. Redirect to Login and Clear Activity Stack
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        
        Toast.makeText(this, "MandalNet: Logged out successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Handle Back button on Toolbar
        onBackPressed();
        return true;
    }
}