package com.ittianyu.mobileguard.domain;

/**
 * Created by yu.
 * blacklist bean
 */
public class BlacklistBean {
    private int id;
    private String phone;
    private int mode;

    public BlacklistBean() {
    }

    public BlacklistBean(int id, String phone, int mode) {
        this.id = id;
        this.phone = phone;
        this.mode = mode;
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

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "BlacklistBean{" +
                "id=" + id +
                ", phone='" + phone + '\'' +
                ", mode=" + mode +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlacklistBean that = (BlacklistBean) o;

        return phone.equals(that.phone);

    }

    @Override
    public int hashCode() {
        return phone.hashCode();
    }
}
