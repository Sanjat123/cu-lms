package com.mandalnet.culms.repository;

import android.content.Context;
import com.mandalnet.culms.api.LMSFetcher;
import com.mandalnet.culms.database.AppDatabase;
import com.mandalnet.culms.database.SubjectEntity;
import com.mandalnet.culms.models.Subject;
import java.util.ArrayList;
import java.util.List;

public class DataRepository {
    private Context context;
    private AppDatabase db;

    public DataRepository(Context context) {
        this.context = context;
        this.db = AppDatabase.getInstance(context);
    }

    public List<Subject> getOfflineSubjects() {
        List<SubjectEntity> entities = db.subjectDao().getAllSubjects();
        List<Subject> subjects = new ArrayList<>();
        for (SubjectEntity e : entities) {
            subjects.add(new Subject(e.name, e.code, e.attendance, e.lmsLink));
        }
        return subjects;
    }

    public void refreshSubjects(LMSFetcher.LMSCallback callback) {
        LMSFetcher.getSubjects(context, new LMSFetcher.LMSCallback() {
            @Override
            public void onSuccess(List<Subject> subjects) {
                new Thread(() -> {
                    db.subjectDao().deleteAll();
                    List<SubjectEntity> entities = new ArrayList<>();
                    for (Subject s : subjects) {
                        entities.add(new SubjectEntity(s.getName(), s.getCode(), s.getAttendance(), s.getLmsLink(), 1));
                    }
                    db.subjectDao().insertAll(entities);
                    callback.onSuccess(subjects);
                }).start();
            }
            @Override
            public void onError(String error) { callback.onError(error); }
        });
    }
}