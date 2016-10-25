package com.ittianyu.mobileguard.domain;

import java.io.Serializable;

/**
 * Created by yu.
 */
public class VirusBean implements Serializable{
    private String md5;
    private String name;
    private String packageName;

    public VirusBean() {
    }

    public VirusBean(String md5, String name) {
        this.md5 = md5;
        this.name = name;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
