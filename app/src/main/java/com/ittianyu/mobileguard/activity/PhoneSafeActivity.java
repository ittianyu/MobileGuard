package com.ittianyu.mobileguard.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.MenuItem;

import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.activity.base.BaseActivityUpEnableWithMenu;
import com.ittianyu.mobileguard.constant.Constant;
import com.ittianyu.mobileguard.fragment.PhoneSafeFragment;
import com.ittianyu.mobileguard.utils.ConfigUtils;

/**
 * PhoneSafe Activity
 * It will enter setting activity if haven't set safe phone. Otherwise show config activity
 */
public class PhoneSafeActivity extends BaseActivityUpEnableWithMenu {

    public PhoneSafeActivity() {
        super(R.string.phone_security, R.menu.menu_reset_password);
    }

    /**
     * change the initXXX order
     */
    @Override
    protected void init() {
        initData();
        initView();
        initEvent();
    }

    /**
     * 1
     */
    @Override
    protected void initData() {
        String safePhone = ConfigUtils.getString(this, Constant.KEY_SAFE_PHONE, "");
        if (TextUtils.isEmpty(safePhone)) {
            // has no safe phone, start Setting
            startActivity(new Intent(this, PhoneSafeSetting1Activity.class));
            finish();
            return;
        }
        // the phone safe activity, see initView
    }

    /**
     * 2
     */
    @Override
    protected void initView() {
        setContentView(R.layout.activity_phone_safe);
        // set fragment
        getFragmentManager().beginTransaction()
                .replace(R.id.activity_phone_safe, new PhoneSafeFragment())
                .commit();

    }

    /**
     * 3
     */
    @Override
    protected void initEvent() {

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
                startActivity(new Intent(this, PhoneSafeSettingPasswordActivity.class));
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
