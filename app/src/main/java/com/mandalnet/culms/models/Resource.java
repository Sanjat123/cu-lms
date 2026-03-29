package com.mandalnet.culms.models;

public class Resource extends LMSResource {
    private String category; // "Syllabus", "PPT", "Lecture Notes", "Study Material"

    public Resource(String title, String downloadUrl, String type, String category) {
        super(title, downloadUrl, type);
        this.category = category;
    }

    public String getCategory() { return category; }
}
