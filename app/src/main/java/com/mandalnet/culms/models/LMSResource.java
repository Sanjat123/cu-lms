package com.mandalnet.culms.models;

public class LMSResource {
    private String title;    // e.g., "Unit 1: Introduction to Java"
    private String downloadUrl; // PDF/PPT ka actual link
    private String type;     // PPT, PDF, or Video

    public LMSResource(String title, String downloadUrl, String type) {
        this.title = title;
        this.downloadUrl = downloadUrl;
        this.type = type;
    }

    public String getTitle() { return title; }
    public String getDownloadUrl() { return downloadUrl; }
    public String getType() { return type; }
}