package com.example.sonymobile.smartextension.hellonotification;

/**
 * Created by cdsteer on 30/05/15.
 */
public class Article {
    private String title;
    private String description;

    public Article(String title, String description) {
        super();
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
