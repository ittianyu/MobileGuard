package com.ittianyu.mobileguard.activity.base;

import com.ittianyu.mobileguard.utils.WindowsUtils;
import com.jaeger.library.StatusBarUtil;

/**
 * Created by yu.
 * base template activity no action bar
 * if extend this activity, it will call initView initData initEvent in order when onCreate。
 * And the child activity no need to override onCreate. Just call setContentView at initView.
 */
public abstract class BaseActivityNoActionBar extends BaseActivity {

    /**
     * hide action bar
     * if you override this method, remember call super.onStart().
     */
    @Override
    protected void onStart() {
        super.onStart();
        WindowsUtils.hideActionBar(this);
        StatusBarUtil.setTransparent(this);
    }
}
