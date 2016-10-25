package com.ittianyu.mobileguard.activity.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


/**
 * Created by yu.
 * base template activity
 * if extend this activity, it will call initView initData initEvent in order when onCreate。
 * And the child activity no need to override onCreate. Just call setContentView at initView.
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

    }

    /**
     * init
     * will call initView initData  initEvent in order
     * If you want to change the order, you can override it in your class.
     */
    protected void init() {
        initView();
        initData();
        initEvent();
    }

    /**
     * init all view
     */
    protected abstract void initView();

    /**
     * init data
     */
    protected abstract void initData();

    /**
     * init event
     */
    protected abstract void initEvent();

}
