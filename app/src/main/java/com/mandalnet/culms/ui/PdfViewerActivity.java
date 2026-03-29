package com.mandalnet.culms.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
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

        String pdfUrl = getIntent().getStringExtra("PDF_URL");
        new RetrievePDFStream().execute(pdfUrl);
    }

    class RetrievePDFStream extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                
                // IMPORTANT: CU LMS ke liye cookie bhejna zaroori hai warna file download nahi hogi
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
            pdfView.fromStream(inputStream)
                    .onLoad(nbPages -> loader.setVisibility(View.GONE))
                    .load();
        }
    }
}