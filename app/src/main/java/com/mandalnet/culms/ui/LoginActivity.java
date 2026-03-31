package com.mandalnet.culms.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.mandalnet.culms.R;
import com.mandalnet.culms.utils.LMSPrefs;

/**
 * LoginActivity: WebView based login to bypass security blocks.
 */
public class LoginActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar = findViewById(R.id.progressBar);
        webView = new WebView(this); // Background engine logic or show it in a FrameLayout if needed
        
        // Agar aap chahte hain ki user ko login page dikhe (Browser ki tarah)
        // toh XML mein ek FrameLayout add karke webView wahan add kar sakte hain.
        // Filhaal main ise background sync mode mein de raha hoon.
        
        setupWebView();
        startLoginFlow();
    }

    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setUserAgentString("Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36");
        
        CookieManager.getInstance().setAcceptCookie(true);
    }

    private void startLoginFlow() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Dashboard par pahunchte hi cookie capture karein
                if (url.contains("/my/") || url.contains("dashboard") || url.contains("courses.php")) {
                    String cookies = CookieManager.getInstance().getCookie(url);
                    if (cookies != null && cookies.contains("MoodleSession")) {
                        captureAndContinue(cookies);
                    }
                }
            }
        });

        // Seedha login page load karein taaki user wahan details bhar sake
        // Isse "Security Block" nahi aayega kyunki user khud browser use kar raha hai
        setContentView(webView); 
        webView.loadUrl("https://lms.cuchd.in/login/index.php");
    }

    private void captureAndContinue(String cookies) {
        String moodleCookie = "";
        for (String c : cookies.split(";")) {
            if (c.trim().startsWith("MoodleSession")) {
                moodleCookie = c.trim();
                break;
            }
        }

        if (!moodleCookie.isEmpty()) {
            LMSPrefs.saveCookie(this, moodleCookie);
            runOnUiThread(() -> {
                Toast.makeText(this, "MandalNet: Login Successful!", Toast.LENGTH_SHORT).show();
                // Login ke baad aapka puraana MainActivity logic start ho jayega
                startActivity(new Intent(this, MainActivity.class));
                finish();
            });
        }
    }
}
