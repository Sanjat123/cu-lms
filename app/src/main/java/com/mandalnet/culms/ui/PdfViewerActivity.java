package com.mandalnet.culms.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.github.barteksc.pdfviewer.PDFView;
import com.mandalnet.culms.R;
import com.mandalnet.culms.utils.LMSPrefs;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PdfViewerActivity extends AppCompatActivity {

    private PDFView pdfView;
    private ProgressBar loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        pdfView = findViewById(R.id.pdfView);
        loader = findViewById(R.id.pdfLoader);

        String fileUrl = getIntent().getStringExtra("PDF_URL");
        String title = getIntent().getStringExtra("TITLE");
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(title);

        // Step 7 Fix: PPT handling logic
        if (fileUrl != null && fileUrl.toLowerCase().endsWith(".pdf")) {
            new RetrievePDFStream().execute(fileUrl);
        } else {
            // Agar PPT hai toh browser ya external app mein kholne ka option dein
            Toast.makeText(this, "PPT detected. Opening in browser...", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl));
            startActivity(intent);
            finish();
        }
    }

    class RetrievePDFStream extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Cookie", LMSPrefs.getCookie(PdfViewerActivity.this));
                
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (Exception e) {
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            if (inputStream != null) {
                pdfView.fromStream(inputStream)
                        .onLoad(nbPages -> loader.setVisibility(View.GONE))
                        .onError(t -> Toast.makeText(PdfViewerActivity.this, "Failed to load PDF", Toast.LENGTH_SHORT).show())
                        .load();
            } else {
                loader.setVisibility(View.GONE);
                Toast.makeText(PdfViewerActivity.this, "Could not fetch file from LMS", Toast.LENGTH_SHORT).show();
            }
        }
    }
}