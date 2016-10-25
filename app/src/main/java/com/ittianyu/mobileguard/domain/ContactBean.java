package com.ittianyu.mobileguard.domain;

/**
 * Created by yu.
 * contact bean
 */

public class ContactBean {
    private int id;
    private String phone;
    private String email;
    private String name;

    public ContactBean() {
    }

    public ContactBean(int id, String phone, String email, String name) {
        this.id = id;
        this.phone = phone;
        this.email = email;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ContactBean{" +
                "id=" + id +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
