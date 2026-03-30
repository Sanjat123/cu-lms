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

/**
 * MainActivity: The heart of MandalNet. 
 * Handles course listing, background attendance syncing, and offline storage.
 */
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
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.subjectRecyclerView);
        bottomNav = findViewById(R.id.bottomNav);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 2. Repository & Adapter Setup
        repository = new DataRepository(this);
        adapter = new SubjectAdapter(subjectList, this::onSubjectClicked);
        recyclerView.setAdapter(adapter);

        // 3. Navigation Logic
        setupBottomNavigation();

        // 4. Data Loading Flow
        loadOfflineData(); // Load instantly from Room DB
        syncWithLMS();     // Fetch fresh data from CU-LMS
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                recyclerView.smoothScrollToPosition(0);
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            } else if (id == R.id.nav_resources) {
                Toast.makeText(this, "Select a subject from the list", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    private void loadOfflineData() {
        new Thread(() -> {
            List<Subject> offlineSubjects = repository.getOfflineSubjects();
            if (offlineSubjects != null && !offlineSubjects.isEmpty()) {
                runOnUiThread(() -> updateUI(offlineSubjects));
            }
        }).start();
    }

    private void syncWithLMS() {
        progressBar.setVisibility(View.VISIBLE);
        
        LMSFetcher.fetchMyCourses(this, new LMSFetcher.LMSCallback() {
            @Override
            public void onSuccess(List<Subject> subjects) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    updateUI(subjects);
                    
                    // CRITICAL: Save to offline DB
                    repository.saveSubjects(subjects);

                    // START DEEP SYNC: Fetch attendance for each subject
                    fetchAllAttendance(subjects);

                    Toast.makeText(MainActivity.this, 
                        "MandalNet: Syncing Attendance...", 
                        Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    /**
     * Loops through all subjects to fetch their individual attendance percentages.
     */
    private void fetchAllAttendance(List<Subject> subjects) {
        for (Subject subject : subjects) {
            LMSFetcher.fetchAttendance(this, subject.getLmsLink(), new LMSFetcher.AttendanceCallback() {
                @Override
                public void onSuccess(String percentage) {
                    runOnUiThread(() -> {
                        // Update the specific subject object
                        subject.setAttendance(percentage);
                        // Update the UI card for this specific subject
                        adapter.notifyItemChanged(subjects.indexOf(subject));
                        // Update offline DB with the new attendance
                        repository.updateSubject(subject);
                    });
                }

                @Override
                public void onError(String error) {
                    // Fail silently for individual attendance items
                }
            });
        }
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

    @Override
    public void onBackPressed() {
        if (bottomNav.getSelectedItemId() != R.id.nav_home) {
            bottomNav.setSelectedItemId(R.id.nav_home);
        } else {
            super.onBackPressed();
        }
    }
}