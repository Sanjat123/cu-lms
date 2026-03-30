package com.mandalnet.culms.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.mandalnet.culms.R;
import com.mandalnet.culms.utils.LMSPrefs;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private WebView hiddenWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Fixed: Standard call
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);

        setupWebView();

        btnLogin.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            if (!user.isEmpty() && !pass.isEmpty()) {
                startSecureLogin(user, pass);
            } else {
                Toast.makeText(this, "Enter Credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupWebView() {
        hiddenWebView = new WebView(this);
        WebSettings settings = hiddenWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadsImagesAutomatically(false);
        settings.setUserAgentString("Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Mobile Safari/537.36");
        
        CookieManager.getInstance().removeAllCookies(null);
    }

    private void startSecureLogin(String username, String password) {
        btnLogin.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        hiddenWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (url.contains("login/index.php")) {
                    view.loadUrl("javascript:(function() {" +
                            "document.getElementById('username').value='" + username + "';" +
                            "document.getElementById('password').value='" + password + "';" +
                            "var btn = document.getElementById('loginbtn') || document.querySelector('button[type=submit]');" +
                            "if(btn) btn.click();" +
                            "})()");
                }

                if (url.contains("/my/") || url.contains("dashboard")) {
                    String cookies = CookieManager.getInstance().getCookie(url);
                    if (cookies != null && cookies.contains("MoodleSession")) {
                        handleLoginSuccess(cookies);
                    }
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                resetUI("Network Error. Check Internet.");
            }
        });

        hiddenWebView.loadUrl("https://lms.cuchd.in/login/index.php");
        
        // Timeout check
        btnLogin.postDelayed(() -> {
            if (progressBar.getVisibility() == View.VISIBLE) {
                resetUI("LMS Server Timeout. Try again.");
            }
        }, 25000);
    }

    private void handleLoginSuccess(String cookies) {
        String moodleCookie = "";
        for (String c : cookies.split(";")) {
            if (c.trim().startsWith("MoodleSession")) {
                moodleCookie = c.trim();
                break;
            }
        }
        LMSPrefs.saveCookie(this, moodleCookie);
        runOnUiThread(() -> {
            Toast.makeText(this, "MandalNet: Access Granted!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
    }

    private void resetUI(String message) {
        runOnUiThread(() -> {
            btnLogin.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            if (message != null) Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }
}