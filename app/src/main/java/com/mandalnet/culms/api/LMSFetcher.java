package com.mandalnet.culms.api;

import android.content.Context;
import com.mandalnet.culms.utils.LMSPrefs;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;

public class LMSFetcher {

    public static void fetchSubjects(Context context) {
        new Thread(() -> {
            try {
                String savedCookie = LMSPrefs.getCookie(context);
                
                // LMS ke us page ka URL jahan subjects dikhte hain
                Document doc = Jsoup.connect("https://uims.cuchd.in/uims/StudentDashboard.aspx")
                        .header("Cookie", savedCookie)
                        .get();

                // Yahan humein HTML tags ke hisaab se data nikalna hoga
                // Example: agar subject name <h5 class="title"> mein hai
                Elements subjects = doc.select("h5.title");
                
                subjects.forEach(element -> {
                    System.out.println("Subject Found: " + element.text());
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}