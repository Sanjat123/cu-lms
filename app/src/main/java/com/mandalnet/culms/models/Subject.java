package com.mandalnet.culms.models;

/**
 * Subject Model: Represents a single course enrolled on CU-LMS.
 * Includes name, code, attendance percentage, and the direct LMS link.
 */
public class Subject {
    private String name;         
    private String code;      
    private String attendance;   
    private String lmsLink;      

    // Main Constructor
    public Subject(String name, String code, String attendance, String lmsLink) {
        this.name = name;
        this.code = code;
        this.attendance = attendance;
        this.lmsLink = lmsLink;
    }

    // --- Getters (Required for UI and Adapters) ---

    public String getName() { 
        return name; 
    }

    public String getCode() { 
        return code; 
    }

    public String getAttendance() { 
        return attendance; 
    }

    public String getLmsLink() { 
        return lmsLink; 
    }

    // --- Setters (Required for Background Updates like Attendance Scraper) ---

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    public void setLmsLink(String lmsLink) {
        this.lmsLink = lmsLink;
    }
}