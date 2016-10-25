package com.ittianyu.mobileguard.activity.base;

import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.ittianyu.mobileguard.R;
import com.jaeger.library.StatusBarUtil;

/**
 * Created by yu.
 * base template activity with action title and display home as up enabled
 * if extend this activity, it will call initView initData initEvent in order when onCreate。
 * And the child activity no need to override onCreate. Just call setContentView at initView.
 */
public abstract class BaseActivityUpEnable extends BaseActivity {
    private final int actionBarTitleId;

    /**
     * construct method. set the action bar title
     * @param actionBarTitleId the resource id of title
     */
    public BaseActivityUpEnable(final int actionBarTitleId) {
        this.actionBarTitleId = actionBarTitleId;
    }

    /**
     * the method deal with the home selected event,
     * if you want to press back button auto back,
     * you need call super.onOptionsItemSelected() when override it
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * set title and setDisplayHomeAsUpEnabled
     * if you override this method, remember call super.onStart().
     */
    @Override
    protected void onStart() {
        super.onStart();
        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar) {
            actionBar.setTitle(actionBarTitleId);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.colorPrimary));
    }
}
