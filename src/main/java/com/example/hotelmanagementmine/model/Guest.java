package com.example.hotelmanagementmine.model;

public class Guest {
    private int id;
    private String name;
    private String phone;
    private String email;
    private String idNumber;

    public Guest() {}

    public Guest(int id, String name, String phone, String email, String idNumber) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.idNumber = idNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    @Override
    public String toString() {
        return "Guest{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", idNumber='" + idNumber + '\'' +
                '}';
    }
}
