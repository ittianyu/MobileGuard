package com.ittianyu.mobileguard.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by yu.
 */
public class ProcessInfoBean {
    private Drawable icon;
    private String packageName;
    private String appName;
    private long memory;
    private boolean systemApp;

    public ProcessInfoBean() {
    }

    public ProcessInfoBean(Drawable icon, String packageName, String appName, long memory, boolean systemApp) {
        this.icon = icon;
        this.packageName = packageName;
        this.appName = appName;
        this.memory = memory;
        this.systemApp = systemApp;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getMemory() {
        return memory;
    }

    public void setMemory(long memory) {
        this.memory = memory;
    }

    public boolean isSystemApp() {
        return systemApp;
    }

    public void setSystemApp(boolean systemApp) {
        this.systemApp = systemApp;
    }
}
