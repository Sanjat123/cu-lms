package com.mandalnet.culms.api;

import android.content.Context;
import com.mandalnet.culms.models.Subject;
import com.mandalnet.culms.models.Unit;
import com.mandalnet.culms.utils.LMSPrefs;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.List;

public class LMSFetcher {

    // Callback for Subject/Course list
    public interface LMSCallback {
        void onSuccess(List<Subject> subjects);
        void onError(String error);
    }

    // Callback for Units/Resources list
    public interface UnitCallback {
        void onSuccess(List<Unit> units);
        void onError(String error);
    }

    // Callback for User Profile Data
    public interface ProfileCallback {
        void onSuccess(String name, String imageUrl, String email);
        void onError(String error);
    }

    // Callback for Attendance Data
    public interface AttendanceCallback {
        void onSuccess(String percentage);
        void onError(String error);
    }

    /**
     * Scrapes enrolled subjects directly from the My Courses endpoint.
     * URL: https://lms.cuchd.in/my/courses.php
     */
    public static void fetchMyCourses(Context context, LMSCallback callback) {
        new Thread(() -> {
            try {
                String cookie = LMSPrefs.getCookie(context);
                Document doc = Jsoup.connect("https://lms.cuchd.in/my/courses.php")
                        .header("Cookie", cookie)
                        .userAgent("Mozilla/5.0")
                        .timeout(15000)
                        .get();

                List<Subject> subjectList = new ArrayList<>();
                Elements courses = doc.select(".course-listitem, .coursebox, .card.dashboard-card, .coursename");

                for (Element course : courses) {
                    Element linkTag = course.select("a[href*='course/view.php']").first();
                    
                    if (linkTag != null) {
                        String name = linkTag.text().trim();
                        String link = linkTag.attr("abs:href");

                        if (!name.isEmpty()) {
                            String code = "CU-" + (Math.abs(name.hashCode() % 10000));
                            // Constructor order: Name, Code, Attendance, Link
                            subjectList.add(new Subject(name, code, "N/A", link));
                        }
                    }
                }

                if (!subjectList.isEmpty()) {
                    callback.onSuccess(subjectList);
                } else {
                    callback.onError("No courses found. Check if your enrollment is active.");
                }
            } catch (Exception e) {
                callback.onError("Fetch Error: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Scrapes Attendance percentage for a specific subject by visiting its attendance module.
     */
    public static void fetchAttendance(Context context, String courseUrl, AttendanceCallback callback) {
        new Thread(() -> {
            try {
                String cookie = LMSPrefs.getCookie(context);
                // 1. Visit the course main page
                Document courseDoc = Jsoup.connect(courseUrl)
                        .header("Cookie", cookie)
                        .get();

                // 2. Find the link to the Attendance activity
                Element attendanceLink = courseDoc.select("a[href*='attendance']").first();
                
                if (attendanceLink != null) {
                    String attUrl = attendanceLink.attr("abs:href");
                    
                    // 3. Visit the attendance report page
                    Document attDoc = Jsoup.connect(attUrl)
                            .header("Cookie", cookie)
                            .get();

                    // 4. Extract percentage from the table
                    String percentage = attDoc.select(".cell.c2, .percentage, td:contains(%)").last() != null ? 
                                     attDoc.select(".cell.c2, .percentage, td:contains(%)").last().text().trim() : "0%";
                    
                    callback.onSuccess(percentage);
                } else {
                    callback.onSuccess("N/A"); 
                }
            } catch (Exception e) {
                callback.onError(e.getMessage());
            }
        }).start();
    }

    /**
     * Scrapes User Profile details (Name, Image, Email).
     */
    public static void fetchUserProfile(Context context, ProfileCallback callback) {
        new Thread(() -> {
            try {
                String cookie = LMSPrefs.getCookie(context);
                Document doc = Jsoup.connect("https://lms.cuchd.in/user/profile.php")
                        .header("Cookie", cookie)
                        .userAgent("Mozilla/5.0")
                        .timeout(10000)
                        .get();

                String name = doc.select(".page-header-headings h2, h1").text().trim();
                String imageUrl = doc.select(".userpicture").attr("abs:src");
                String email = doc.select(".contentnode dd").first() != null ? 
                               doc.select(".contentnode dd").first().text() : "CU Student";

                if (!name.isEmpty()) {
                    callback.onSuccess(name, imageUrl, email);
                } else {
                    callback.onError("Could not parse profile data.");
                }
            } catch (Exception e) {
                callback.onError("Profile Sync Failed: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Scrapes PDF/PPT resources (Units) from a specific course page.
     */
    public static void fetchUnits(Context context, String courseUrl, UnitCallback callback) {
        new Thread(() -> {
            try {
                String cookie = LMSPrefs.getCookie(context);
                Document doc = Jsoup.connect(courseUrl)
                        .header("Cookie", cookie)
                        .userAgent("Mozilla/5.0")
                        .timeout(10000)
                        .get();

                List<Unit> unitList = new ArrayList<>();
                Elements resources = doc.select(".activityinstance");

                for (Element res : resources) {
                    String title = res.select(".instancename").text().replace(" File", "").trim();
                    String downloadLink = res.select("a").attr("abs:href");

                    if (!title.isEmpty() && !downloadLink.isEmpty() && downloadLink.contains("resource/view.php")) {
                        unitList.add(new Unit(title, downloadLink));
                    }
                }

                if (!unitList.isEmpty()) {
                    callback.onSuccess(unitList);
                } else {
                    callback.onError("No PDF or PPT files found.");
                }
            } catch (Exception e) {
                callback.onError("Resource Error: " + e.getMessage());
            }
        }).start();
    }
}