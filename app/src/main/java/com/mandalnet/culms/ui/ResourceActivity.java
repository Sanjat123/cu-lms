package com.mandalnet.culms.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mandalnet.culms.R;
import com.mandalnet.culms.api.LMSFetcher;
import com.mandalnet.culms.models.Unit;
import java.util.ArrayList;
import java.util.List;

/**
 * ResourceActivity displays the list of Units/Files (PDFs, PPTs) 
 * for a specific subject in a native list view.
 */
public class ResourceActivity extends AppCompatActivity {

    private RecyclerView rvUnits;
    private UnitAdapter adapter;
    private List<Unit> unitList = new ArrayList<>();
    private ProgressBar progressBar;
    private String courseUrl;
    private String courseName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource);

        // 1. Get Course details from the previous screen (MainActivity)
        courseUrl = getIntent().getStringExtra("SUBJECT_URL");
        courseName = getIntent().getStringExtra("SUBJECT_NAME");

        // 2. Configure Action Bar / Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(courseName != null ? courseName : "Subject Resources");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 3. Initialize UI components
        progressBar = findViewById(R.id.progressBar);
        rvUnits = findViewById(R.id.rvUnits);
        
        // 4. Setup RecyclerView with LinearLayoutManager
        rvUnits.setLayoutManager(new LinearLayoutManager(this));

        // 5. Initialize Adapter with a click listener for units
        adapter = new UnitAdapter(unitList, this::onUnitClicked);
        rvUnits.setAdapter(adapter);

        // 6. Fetch units from the LMS via Scraper
        if (courseUrl != null) {
            loadUnits();
        } else {
            Toast.makeText(this, "Error: Course link not found", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Calls the LMSFetcher to scrape resources in a background thread.
     */
    private void loadUnits() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        LMSFetcher.fetchUnits(this, courseUrl, new LMSFetcher.UnitCallback() {
            @Override
            public void onSuccess(List<Unit> units) {
                // UI updates must be on the Main Thread
                runOnUiThread(() -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    unitList.clear();
                    unitList.addAll(units);
                    adapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    Toast.makeText(ResourceActivity.this, "MandalNet Error: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * Handles clicking on a Unit card to view/download the file.
     */
    private void onUnitClicked(Unit unit) {
        try {
            // Open the LMS resource link in the system browser or default PDF viewer
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(unit.getLmsLink()));
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "No app found to open this file", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles the Back button on the Toolbar.
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}