package com.mandalnet.culms.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface SemesterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<SemesterEntity> semesters);

    @Query("SELECT * FROM semesters")
    List<SemesterEntity> getAllSemesters();

    @Query("DELETE FROM semesters")
    void deleteAll();
}
