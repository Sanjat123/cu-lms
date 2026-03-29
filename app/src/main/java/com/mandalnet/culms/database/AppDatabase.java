package com.mandalnet.culms.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {SubjectEntity.class, SemesterEntity.class, ResourceEntity.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

public abstract SubjectDao subjectDao();
    public abstract SemesterDao semesterDao();
    public abstract ResourceDao resourceDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "culms_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}