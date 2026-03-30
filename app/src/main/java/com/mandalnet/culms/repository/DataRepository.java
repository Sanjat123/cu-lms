package com.mandalnet.culms.repository;

import android.content.Context;
import com.mandalnet.culms.api.LMSFetcher;
import com.mandalnet.culms.database.AppDatabase;
import com.mandalnet.culms.database.SubjectEntity;
import com.mandalnet.culms.models.Subject;
import java.util.ArrayList;
import java.util.List;

/**
 * DataRepository: Handles data operations between the local database and the remote LMS.
 * Implements the Single Source of Truth pattern for MandalNet LMS.
 */
public class DataRepository {
    private Context context;
    private AppDatabase db;

    public DataRepository(Context context) {
        this.context = context;
        this.db = AppDatabase.getInstance(context);
    }

    /**
     * Retrieves cached subjects from the local Room database for offline viewing.
     */
    public List<Subject> getOfflineSubjects() {
        List<SubjectEntity> entities = db.subjectDao().getAllSubjects();
        List<Subject> subjects = new ArrayList<>();
        
        for (SubjectEntity e : entities) {
            // Mapping: Name, Code, Attendance, LMS Link
            subjects.add(new Subject(e.name, e.code, e.attendance, e.lmsLink));
        }
        return subjects;
    }

    /**
     * Method used by MainActivity to save the entire list at once.
     */
    public void saveSubjects(List<Subject> subjects) {
        new Thread(() -> {
            try {
                db.subjectDao().deleteAll();
                List<SubjectEntity> entities = new ArrayList<>();
                for (Subject s : subjects) {
                    entities.add(new SubjectEntity(
                        s.getName(), 
                        s.getCode(), 
                        s.getAttendance(), 
                        s.getLmsLink(), 
                        1 // Default status
                    ));
                }
                db.subjectDao().insertAll(entities);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Method used by MainActivity to update attendance live during deep sync.
     */
    public void updateSubject(Subject s) {
        new Thread(() -> {
            try {
                SubjectEntity entity = new SubjectEntity(
                    s.getName(), 
                    s.getCode(), 
                    s.getAttendance(), 
                    s.getLmsLink(), 
                    1
                );
                db.subjectDao().insertAll(java.util.Collections.singletonList(entity)); 
                // Note: insertAll with OnConflictStrategy.REPLACE will update the row
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Syncs local data with the remote CU-LMS website (Manual Refresh).
     */
    public void refreshSubjects(LMSFetcher.LMSCallback callback) {
        LMSFetcher.fetchMyCourses(context, new LMSFetcher.LMSCallback() {
            @Override
            public void onSuccess(List<Subject> subjects) {
                new Thread(() -> {
                    try {
                        saveSubjects(subjects);
                        callback.onSuccess(subjects);
                    } catch (Exception e) {
                        callback.onError("Database Error: " + e.getMessage());
                    }
                }).start();
            }

            @Override
            public void onError(String error) {
                callback.onError(error); 
            }
        });
    }
}