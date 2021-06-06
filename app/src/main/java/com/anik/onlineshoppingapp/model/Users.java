package com.anik.onlineshoppingapp.model;

public class Users {

    public String number;
    public String password;
    public String name;
    public String orderNumber;
    public String address;
    public String image;

    public Users() {
    }

    public Users(String number, String password, String name, String orderNumber, String address, String image) {
        this.number = number;
        this.password = password;
        this.name = name;
        this.orderNumber = orderNumber;
        this.address = address;
        this.image = image;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
