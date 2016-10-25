package com.ittianyu.mobileguard.activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.activity.base.BaseActivityUpEnableWithMenu;
import com.ittianyu.mobileguard.fragment.LockedAppFragment;
import com.ittianyu.mobileguard.fragment.UnlockedAppFragment;

/**
 * show unlocked list and locked list
 */
public class AppLockActivity extends BaseActivityUpEnableWithMenu implements View.OnClickListener {
    private LinearLayout llUnlocked;
    private LinearLayout llLocked;
    private View vUnlocked;
    private View vLocked;
    private UnlockedAppFragment unlockedFragment = new UnlockedAppFragment();
    private LockedAppFragment lockedFragment = new LockedAppFragment();
    /**
     * construct method. set the action bar title
     */
    public AppLockActivity() {
        super(R.string.app_lock, R.menu.menu_reset_password);
    }

    /**
     * 1
     */
    @Override
    protected void initView() {
        setContentView(R.layout.activity_app_lock);
        // bind view
        llLocked = (LinearLayout) findViewById(R.id.ll_locked);
        llUnlocked = (LinearLayout) findViewById(R.id.ll_unlocked);
        vLocked = findViewById(R.id.v_locked);
        vUnlocked = findViewById(R.id.v_unlocked);

        // replace fragment
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fl_app, unlockedFragment);
        transaction.commit();

    }

    /**
     * 2
     */
    @Override
    protected void initData() {

    }

    /**
     * 3
     */
    @Override
    protected void initEvent() {
        // set tab click listener
        llUnlocked.setOnClickListener(this);
        llLocked.setOnClickListener(this);

    }

    /**
     * on tab clicked. Change fragment and view
     * @param v
     */
    @Override
    public void onClick(View v) {
        // change fragment
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        switch (v.getId()) {
            case R.id.ll_unlocked:
                // replace fragment
                transaction.replace(R.id.fl_app, unlockedFragment);
                // show line view
                vUnlocked.setVisibility(View.VISIBLE);
                // hide another line view
                vLocked.setVisibility(View.INVISIBLE);
                break;
            case R.id.ll_locked:
                // replace fragment
                transaction.replace(R.id.fl_app, lockedFragment);
                // show line view
                vLocked.setVisibility(View.VISIBLE);
                // hide another line view
                vUnlocked.setVisibility(View.INVISIBLE);
                break;
        }
        transaction.commit();

    }

    /**
     * on menu click
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // reset password, start the setting password activity and finish
            case R.id.m_reset_password:
                startActivity(new Intent(this, AppLockSettingPasswordActivity.class));
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
