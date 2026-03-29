package com.mandalnet.culms.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "resources")
public class ResourceEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String title;
    public String downloadUrl;
    public String type;
    public String category;
    public int subjectId; // FK to subject

    public ResourceEntity(String title, String downloadUrl, String type, String category, int subjectId) {
        this.title = title;
        this.downloadUrl = downloadUrl;
        this.type = type;
        this.category = category;
        this.subjectId = subjectId;
    }
}
