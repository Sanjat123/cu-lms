package com.mandalnet.culms.models;

public class Unit {
    private String title;
    private String lmsLink;

    public Unit(String title, String lmsLink) {
        this.title = title;
        this.lmsLink = lmsLink;
    }

    public String getTitle() { return title; }
    public String getLmsLink() { return lmsLink; }
}