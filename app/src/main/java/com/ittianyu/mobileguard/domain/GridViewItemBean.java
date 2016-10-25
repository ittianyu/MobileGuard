package com.ittianyu.mobileguard.domain;

import com.ittianyu.mobileguard.strategy.OnClickListener;

/**
 * Created by yu.
 * gv_menu item bean
 */

public class GridViewItemBean {
    private int iconId;
    private int nameId;
    private OnClickListener scheme;

    public OnClickListener getScheme() {
        return scheme;
    }

    public void setScheme(OnClickListener scheme) {
        this.scheme = scheme;
    }

    public GridViewItemBean(int iconId, int nameId, OnClickListener scheme) {
        this.iconId = iconId;
        this.nameId = nameId;
        this.scheme = scheme;
    }

    public GridViewItemBean() {
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public int getNameId() {
        return nameId;
    }

    public void setNameId(int nameId) {
        this.nameId = nameId;
    }
}
