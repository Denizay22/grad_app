package com.example.grad_app.helpers;

public class Notice {

    private String headline;
    private String today_year;
    private String today_month;
    private String today_day;
    private String expiration_year;
    private String expiration_month;
    private String expiration_Day;
    private String name_surname;
    private String photo;
    private String notice;

    public Notice(){

    }

    public Notice(String headline, String today_month, String today_day, String today_year, String expiration_month, String expiration_Day, String expiration_year, String name_surname, String photo, String notice) {
        this.headline = headline;
        this.today_month = today_month;
        this.today_day = today_day;
        this.today_year = today_year;
        this.expiration_month = expiration_month;
        this.expiration_Day = expiration_Day;
        this.expiration_year = expiration_year;
        this.name_surname = name_surname;
        this.photo = photo;
        this.notice = notice;
    }

    public String getHeadline() {
        return headline;
    }
    public String getToday_year() {
        return today_year;
    }
    public String getToday_month() {
        return today_month;
    }
    public String getToday_day() {
        return today_day;
    }
    public String getExpiration_year() {
        return expiration_year;
    }
    public String getExpiration_month() {
        return expiration_month;
    }
    public String getExpiration_Day() {
        return expiration_Day;
    }
    public String getName_surname() {
        return name_surname;
    }
    public void setName_surname(String name_surname) {
        this.name_surname = name_surname;
    }
    public String getPhoto() {
        return photo;
    }
    public String getNotice() {
        return notice;
    }
}
