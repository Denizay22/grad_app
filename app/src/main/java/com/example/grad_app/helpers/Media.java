package com.example.grad_app.helpers;

public class Media {
    private String user_ID;
    private String name_surname;
    private String date_year;
    private String date_month;
    private String date_day;
    private String text;
    private String media;
    private String media_ID;
    private String media_file_extension;
    private String profile_photo;


    public Media(){

    }

    public Media(String user_ID, String name_surname, String date_month, String date_day,String date_year,  String text, String media, String media_ID, String media_file_extension, String profile_photo) {
        this.user_ID = user_ID;
        this.name_surname = name_surname;
        this.date_year = date_year;
        this.date_month = date_month;
        this.date_day = date_day;
        this.text = text;
        this.media = media;
        this.media_ID = media_ID;
        this.media_file_extension = media_file_extension;
        this.profile_photo = profile_photo;
    }

    public String getUser_ID() {
        return user_ID;
    }
    public String getName_surname() {
        return name_surname;
    }
    public void setName_surname(String name_surname) {
        this.name_surname = name_surname;
    }
    public String getDate_year() {
        return date_year;
    }
    public String getDate_month() {
        return date_month;
    }
    public String getDate_day() {
        return date_day;
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getMedia() {
        return media;
    }
    public String getProfile_photo() {
        return profile_photo;
    }
    public String getMedia_ID() {
        return media_ID;
    }
    public String getMedia_file_extension() {
        return media_file_extension;
    }
}
