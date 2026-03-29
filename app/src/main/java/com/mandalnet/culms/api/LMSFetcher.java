package com.mandalnet.culms.api;

import android.content.Context;
import com.mandalnet.culms.models.Subject;
import com.mandalnet.culms.utils.LMSPrefs;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
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
                // CU LMS Dashboard URL (Check if this is the correct one for your session)
                String url = "https://uims.cuchd.in/uims/StudentDashboard.aspx";

                Document doc = Jsoup.connect(url)
                        .header("Cookie", cookie)
                        .userAgent("Mozilla/5.0")
                        .get();

                List<Subject> subjectList = new ArrayList<>();

                // IMPORTANT: Yahan '.subject-card' ko replace karna hoga 
                // CU LMS ke actual HTML class se (Inspect Element karke dekhein)
                Elements cards = doc.select(".subject-card-class"); 

                for (Element card : cards) {
                    String name = card.select(".title-class").text();
                    String code = card.select(".code-class").text();
                    String attendance = card.select(".attendance-class").text();
                    String link = card.select("a").attr("href");

                    subjectList.add(new Subject(name, code, attendance, link));
                }

                callback.onSuccess(subjectList);

            } catch (IOException e) {
                callback.onError(e.getMessage());
            }
        }).start();
    }
}