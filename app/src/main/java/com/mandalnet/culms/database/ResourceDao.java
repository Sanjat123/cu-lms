package com.mandalnet.culms.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ResourceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ResourceEntity> resources);

    @Query("SELECT * FROM resources WHERE subjectId = :subjectId")
    List<ResourceEntity> getResourcesForSubject(int subjectId);

    @Query("DELETE FROM resources")
    void deleteAll();
}
