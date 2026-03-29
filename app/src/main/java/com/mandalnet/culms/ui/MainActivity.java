package com.mandalnet.culms.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI Initialization
        recyclerView = findViewById(R.id.subjectRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Repository Initialization
        repository = new DataRepository(this);

        // Adapter setup with click listener
        adapter = new SubjectAdapter(subjectList);
        recyclerView.setAdapter(adapter);

        // Logic: 1. Pehle Offline data load karo (Fast)
        loadOfflineData();

        // Logic: 2. Phir Background mein Sync karo (Fresh Data)
        syncWithLMS();
    }

    private void loadOfflineData() {
        new Thread(() -> {
            List<Subject> offlineSubjects = repository.getOfflineSubjects();
            if (!offlineSubjects.isEmpty()) {
                runOnUiThread(() -> {
                    subjectList.clear();
                    subjectList.addAll(offlineSubjects);
                    adapter.notifyDataSetChanged();
                });
            }
        }).start();
    }

    private void syncWithLMS() {
        repository.refreshSubjects(new LMSFetcher.LMSCallback() {
            @Override
            public void onSuccess(List<Subject> subjects) {
                runOnUiThread(() -> {
                    subjectList.clear();
                    subjectList.addAll(subjects);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "Data Synced with LMS", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> 
                    Toast.makeText(MainActivity.this, "Sync Failed: Using Offline Data", Toast.LENGTH_LONG).show()
                );
            }
        });
    }

    // Is method ko SubjectAdapter mein set karein taaki click par PPTs khulein
    public void onSubjectClicked(Subject subject) {
        Intent intent = new Intent(this, ResourceActivity.class);
        intent.putExtra("SUBJECT_URL", subject.getLmsLink());
        intent.putExtra("SUBJECT_NAME", subject.getName());
        startActivity(intent);
    }
}