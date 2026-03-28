package com.mandalnet.culms.models;

public class Subject {
    private String name;         
    private String code;      
    private String attendance;   
    private String lmsLink;      

    public Subject(String name, String code, String attendance, String lmsLink) {
        this.name = name;
        this.code = code;
        this.attendance = attendance;
        this.lmsLink = lmsLink;
    }

    // Getters (UI mein dikhane ke liye)
    public String getName() { return name; }
    public String getCode() { return code; }
    public String getAttendance() { return attendance; }
    public String getLmsLink() { return lmsLink; }
}