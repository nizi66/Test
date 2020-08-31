package com.test.vo;

public class User {

    private String userName;

    private String email;

    private int i ;

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public String getEmail() {
        return email;
    }

    public User(){}

    public User(String userName, String email, String phone, int i) {
        this.userName = userName;
        this.email = email;
        this.phone = phone;
        this.i=i;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    private String phone;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
