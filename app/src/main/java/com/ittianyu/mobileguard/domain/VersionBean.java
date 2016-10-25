package com.ittianyu.mobileguard.domain;

/**
 * Created by yu.
 * version bean
 */

public class VersionBean {
    private int version;
    private String url;
    private String description;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "VersionBean{" +
                "version=" + version +
                ", url='" + url + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
