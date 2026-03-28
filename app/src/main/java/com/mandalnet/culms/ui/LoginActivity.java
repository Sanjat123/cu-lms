package com.mandalnet.culms.ui;

import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import com.mandalnet.culms.utils.LMSPrefs;

public class LoginActivity extends AppCompatActivity {

    private WebView webView;
    // Yahan CU ka actual LMS login URL daalein
    private String loginUrl = "https://uims.cuchd.in/uims/"; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        webView = new WebView(this);
        setContentView(webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Agar URL mein "Dashboard" ya "Home" aa jaye, matlab login success!
                if (url.contains("Dashboard") || url.contains("Index")) {
                    String cookies = CookieManager.getInstance().getCookie(url);
                    LMSPrefs.saveCookie(LoginActivity.this, cookies);
                    
                    // Yahan se Sync start karein ya Dashboard Activity par jayein
                    finish(); 
                }
            }
        });

        webView.loadUrl(loginUrl);
    }
}