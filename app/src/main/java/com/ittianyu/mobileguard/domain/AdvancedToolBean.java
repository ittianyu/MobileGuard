package com.ittianyu.mobileguard.domain;

import com.ittianyu.mobileguard.strategy.OnClickListener;

/**
 * Created by yu.
 * AdvancedToolBean
 */
public class AdvancedToolBean {
    private int imageId;
    private int titleId;
    private int summaryId;
    private OnClickListener clickListener;

    public AdvancedToolBean() {
    }

    public AdvancedToolBean(int imageId, int titleId, int summaryId, OnClickListener clickListener) {
        this.imageId = imageId;
        this.titleId = titleId;
        this.summaryId = summaryId;
        this.clickListener = clickListener;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getTitleId() {
        return titleId;
    }

    public void setTitleId(int titleId) {
        this.titleId = titleId;
    }

    public int getSummaryId() {
        return summaryId;
    }

    public void setSummaryId(int summaryId) {
        this.summaryId = summaryId;
    }

    public OnClickListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(OnClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
