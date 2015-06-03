package com.example.sonymobile.smartextension.hellonotification;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by cdsteer on 30/05/15.
 */
public class Article {
    private String title;
    private String description;
    private String cpsID;
    private String imageUrl;
    private Bitmap image;
    private String url;
    private boolean interests;

    public Article(String title, String description, String cpsID, String imageURL, String url) {
        super();
        this.title = title;
        this.description = description;
        this.cpsID = cpsID;
        this.imageUrl = imageURL;
        this.url = url;
        loadImage(this.imageUrl);
        interests = false;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getcpsID() {
        return cpsID;
    }

    public String getImageURL() {
        return imageUrl;
    }

    public String getURL() {
        return url;
    }

    public void loadImage(String url) {
        try {
            image = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeInterest() {
        interests = !interests;
    }

    public boolean isInterested() {
        return interests;
    }

    public Bitmap getImage() {
        return image;
    }

}
