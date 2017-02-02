package com.danifernandez.tmdbapp.utils;

import android.graphics.Bitmap;

public class Movie {

    private int id;
    private String title;
    private String year;
    private String overview;
    private String picture;
    private Bitmap bitmap;

    public Movie(int id, String title, String year, String overview, String picture){
        this.id = id;
        this.title = title;
        this.year = year;
        this.overview = overview;
        this.picture = picture;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String toString(){
        return "Movie: (" + id + "," + title + "," + year + "," + picture + "," + overview + ")";
    }
}
