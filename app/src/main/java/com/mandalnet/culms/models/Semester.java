package com.mandalnet.culms.models;

public class Semester {
    private int id;
    private String name; // e.g. "Semester 3"
    private String year; // "2024"

    public Semester(int id, String name, String year) {
        this.id = id;
        this.name = name;
        this.year = year;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getYear() { return year; }
}
