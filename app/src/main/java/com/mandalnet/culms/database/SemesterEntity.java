package com.mandalnet.culms.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "semesters")
public class SemesterEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String name;
    public String year;

    public SemesterEntity(String name, String year) {
        this.name = name;
        this.year = year;
    }
}
