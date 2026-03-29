package com.mandalnet.culms.ui;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import com.mandalnet.culms.utils.LMSPrefs;

public class LoginActivity extends AppCompatActivity {

    private WebView webView;
    // Naya Moodle LMS Link
    private String loginUrl = "https://lms.cuchd.in/login/index.php"; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        webView = new WebView(this);
        setContentView(webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        
        // Purana cache clean karein taaki 404 na aaye
        webView.clearCache(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Moodle mein login ke baad aksar URL mein "/my/" ya "dashboard" aata hai
                if (url.contains("/my/") || url.contains("dashboard")) {
                    
                    // Yahan se hum cookies nikalte hain
                    String cookies = CookieManager.getInstance().getCookie(url);
                    
                    if (cookies != null && cookies.contains("MoodleSession")) {
                        LMSPrefs.saveCookie(LoginActivity.this, cookies);
                        
                        // Login successful, Dashboard par bhejein
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish(); 
                    }
                }
            }
        });

        webView.loadUrl(loginUrl);
    }
}