package com.mandalnet.culms.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface SubjectDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<SubjectEntity> subjects);

    @Query("SELECT * FROM subjects")
    List<SubjectEntity> getAllSubjects();

    @Query("DELETE FROM subjects")
    void deleteAll();
}