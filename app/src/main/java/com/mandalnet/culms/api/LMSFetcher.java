package com.mandalnet.culms.api;

import android.content.Context;
import com.mandalnet.culms.models.Subject;
import com.mandalnet.culms.utils.LMSPrefs;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.List;

public class LMSFetcher {
    public interface LMSCallback {
        void onSuccess(List<Subject> subjects);
        void onError(String error);
    }

    public static void getSubjects(Context context, LMSCallback callback) {
        new Thread(() -> {
            try {
                String cookie = LMSPrefs.getCookie(context);
                String url = "https://lms.cuchd.in/my/"; 

                Document doc = Jsoup.connect(url)
                        .header("Cookie", cookie)
                        .userAgent("Mozilla/5.0")
                        .get();

                List<Subject> subjectList = new ArrayList<>();
                // Moodle Dashboard Course Selectors
                Elements courses = doc.select(".coursename, .course-listitem"); 

                for (Element course : courses) {
                    String name = course.text();
                    String link = course.select("a").attr("abs:href");
                    
                    if (!name.isEmpty() && !link.isEmpty()) {
                        // Moodle mein attendance dashboard par nahi hoti, default placeholder set kiya hai
                        subjectList.add(new Subject(name, "LMS-COURSE", "Click to View", link));
                    }
                }
                callback.onSuccess(subjectList);
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        }).start();
    }
}