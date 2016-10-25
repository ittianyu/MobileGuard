package com.ittianyu.mobileguard.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by yu.
 */
public class AppInfoBean {
    private Drawable icon;
    private String name;
    private String packageName;
    private long size;// cacheSize + codeSize + dataSize
    private boolean onSdCard;// whether installed on sd card
    private boolean systemApp;// whether system app
    private String apkPath;
    private long cacheSize;

    public AppInfoBean() {
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
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

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isOnSdCard() {
        return onSdCard;
    }

    public void setOnSdCard(boolean onSdCard) {
        this.onSdCard = onSdCard;
    }

    public boolean isSystemApp() {
        return systemApp;
    }

    public void setSystemApp(boolean systemApp) {
        this.systemApp = systemApp;
    }

    public String getApkPath() {
        return apkPath;
    }

    public void setApkPath(String apkPath) {
        this.apkPath = apkPath;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }

    @Override
    public String toString() {
        return "AppInfoBean{" +
                "icon=" + icon +
                ", name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                ", size=" + size +
                ", onSdCard=" + onSdCard +
                ", systemApp=" + systemApp +
                ", apkPath='" + apkPath + '\'' +
                ", cacheSize=" + cacheSize +
                '}';
    }
}
