package com.mandalnet.culms.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mandalnet.culms.R;
import com.mandalnet.culms.api.LMSFetcher;
import com.mandalnet.culms.models.Subject;
import com.mandalnet.culms.repository.DataRepository;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SubjectAdapter adapter;
    private DataRepository repository;
    private List<Subject> subjectList = new ArrayList<>();
    private ProgressBar progressBar;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); 

        // 1. UI Initialization
        progressBar = findViewById(R.id.progressBar); // Layout mein add kar lena
        recyclerView = findViewById(R.id.subjectRecyclerView);
        bottomNav = findViewById(R.id.bottomNav);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 2. Repository & Adapter Setup
        repository = new DataRepository(this);
        adapter = new SubjectAdapter(subjectList, this::onSubjectClicked);
        recyclerView.setAdapter(adapter);

        // 3. Bottom Navigation Logic
        setupBottomNavigation();

        // 4. Data Loading Flow
        loadOfflineData();
        syncWithLMS();
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // Already on Home, maybe scroll to top
                recyclerView.smoothScrollToPosition(0);
                return true;
            } else if (id == R.id.nav_profile) {
                // Profile Screen par bhejein
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            } else if (id == R.id.nav_resources) {
                Toast.makeText(this, "Select a subject to view study material", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    private void loadOfflineData() {
        new Thread(() -> {
            List<Subject> offlineSubjects = repository.getOfflineSubjects();
            if (!offlineSubjects.isEmpty()) {
                runOnUiThread(() -> {
                    updateUI(offlineSubjects);
                });
            }
        }).start();
    }

    private void syncWithLMS() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        
        repository.refreshSubjects(new LMSFetcher.LMSCallback() {
            @Override
            public void onSuccess(List<Subject> subjects) {
                runOnUiThread(() -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    updateUI(subjects);
                    Toast.makeText(MainActivity.this, "MandalNet: Data Synced", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Offline Mode: Connection Error", Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void updateUI(List<Subject> subjects) {
        subjectList.clear();
        subjectList.addAll(subjects);
        adapter.notifyDataSetChanged();
    }

    public void onSubjectClicked(Subject subject) {
        Intent intent = new Intent(this, ResourceActivity.class);
        intent.putExtra("SUBJECT_URL", subject.getLmsLink()); 
        intent.putExtra("SUBJECT_NAME", subject.getName());
        startActivity(intent);
    }
}