package com.example.grad_app.helpers;


public class User {
    //kayıt olurken fieldlar

    private String name;
    private String surname;
    private String email;
    private String enrollment_year;
    private String grad_year;

    //profil oluştururken fieldlar
    private String degree;
    private String job_country;
    private String job_city;
    private String job_company;
    private String phone_no;

    private String photo;



    private String user_ID;
    //todo sosyal medya


    public User() {

    }


    public User(String name, String surname, String email, String degree, String job_country,
                String job_city, String job_company, String phone_no, String enrollment_year,
                String grad_year, String photo, String user_ID) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.degree = degree;
        this.job_country = job_country;
        this.job_city = job_city;
        this.job_company = job_company;
        this.phone_no = phone_no;
        this.enrollment_year = enrollment_year;
        this.grad_year = grad_year;
        this.photo = photo;
        this.user_ID = user_ID;
    }

    public String getName() {
        return name;
    }
    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }
    public String getEnrollment_year() {
        return enrollment_year;
    }

    public String getGrad_year() {
        return grad_year;
    }

    public String getDegree() {
        return degree;
    }

    public String getJob_country() {
        return job_country;
    }

    public String getJob_city() {
        return job_city;
    }

    public String getJob_company() {
        return job_company;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public String getPhoto() {
        return photo;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUser_ID() {
        return user_ID;
    }
}
