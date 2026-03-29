package com.mandalnet.culms.ui;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mandalnet.culms.R;
import com.mandalnet.culms.api.LMSFetcher;
import com.mandalnet.culms.models.Subject;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SubjectAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.subjectRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Start Fetching Data
        loadLMSData();
    }

    private void loadLMSData() {
        LMSFetcher.getSubjects(this, new LMSFetcher.LMSCallback() {
            @Override
            public void onSuccess(List<Subject> subjects) {
                runOnUiThread(() -> {
                    adapter = new SubjectAdapter(subjects);
                    recyclerView.setAdapter(adapter);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> 
                    Toast.makeText(MainActivity.this, "Sync Failed: " + error, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}