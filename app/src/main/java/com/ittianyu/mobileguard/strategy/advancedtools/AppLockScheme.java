package com.ittianyu.mobileguard.strategy.advancedtools;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.ittianyu.mobileguard.activity.AppLockInputPasswordActivity;
import com.ittianyu.mobileguard.activity.AppLockSettingPasswordActivity;
import com.ittianyu.mobileguard.constant.Constant;
import com.ittianyu.mobileguard.strategy.OnClickListener;
import com.ittianyu.mobileguard.utils.ConfigUtils;

/**
 * Created by yu on.
 * start AppLockSettingPasswordActivity if no password, otherwise start AppLockInputPasswordActivity
 */
public class AppLockScheme implements OnClickListener {

    @Override
    public void onSelected(final Context context) {
        String password = ConfigUtils.getString(context, Constant.KEY_APP_LOCK_PASSWORD, "");
        if(TextUtils.isEmpty(password)) {
            // have no password
            context.startActivity(new Intent(context, AppLockSettingPasswordActivity.class));
        }else {
            // input password
            context.startActivity(new Intent(context, AppLockInputPasswordActivity.class));
        }
    }
}
