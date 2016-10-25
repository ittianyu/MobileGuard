package com.ittianyu.mobileguard.activity.base;

import android.view.Menu;

/**
 * Created by yu.
 * base template activity with action title and display home as up enabled
 * if extend this activity, it will call initView initData initEvent in order when onCreate。
 * And the child activity no need to override onCreate. Just call setContentView at initView.
 */
public abstract class BaseActivityUpEnableWithMenu extends BaseActivityUpEnable {
    private int menuId;

    /**
     * construct method
     * @param actionBarTitleId the resource id of title
     * @param menuId the resource id of menu
     */
    public BaseActivityUpEnableWithMenu(int actionBarTitleId, int menuId) {
        super(actionBarTitleId);
        this.menuId = menuId;
    }

    /**
     * create menu use menuId which input in construct
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(menuId, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
