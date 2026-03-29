package com.mandalnet.culms.ui;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mandalnet.culms.R;
import com.mandalnet.culms.models.Resource;
import com.mandalnet.culms.utils.LMSPrefs;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.List;

public class ResourceActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ResourceAdapter adapter;
    private List<Resource> resourceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource);

        String subjectUrl = getIntent().getStringExtra("SUBJECT_URL");
        recyclerView = findViewById(R.id.resourceRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ResourceAdapter(resourceList);
        recyclerView.setAdapter(adapter);

        new Thread(() -> {
            try {
                String cookie = LMSPrefs.getCookie(this);
                Document doc = Jsoup.connect(subjectUrl).header("Cookie", cookie).get();
                
                // Moodle specific resource links
                Elements links = doc.select("a[href*='resource/view.php'], a[href*='mod/resource']");

                for (Element link : links) {
                    String title = link.select(".instancename").text();
                    if (title.isEmpty()) title = link.text();
                    
                    String downloadUrl = link.attr("abs:href");
                    String type = title.toLowerCase().contains("pdf") ? "PDF" : "PPT";
                    
                    resourceList.add(new Resource(title, downloadUrl, type, "Study Material"));
                }
                runOnUiThread(() -> adapter.notifyDataSetChanged());
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}