package com.mandalnet.culms.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "subjects")
public class SubjectEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String name;
    public String code;
    public String attendance;
    public String lmsLink;

    public SubjectEntity(String name, String code, String attendance, String lmsLink) {
        this.name = name;
        this.code = code;
        this.attendance = attendance;
        this.lmsLink = lmsLink;
    }
}