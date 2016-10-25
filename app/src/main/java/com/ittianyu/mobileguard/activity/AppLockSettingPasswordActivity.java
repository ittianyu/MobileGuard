package com.ittianyu.mobileguard.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.activity.base.SettingPasswordActivity;
import com.ittianyu.mobileguard.constant.Constant;
import com.ittianyu.mobileguard.service.AppLockService;
import com.ittianyu.mobileguard.utils.ActivityManagerUtils;
import com.ittianyu.mobileguard.utils.ConfigUtils;
import com.ittianyu.mobileguard.utils.EncryptionUtils;

/**
 * app lock setting password activity
 */
public class AppLockSettingPasswordActivity extends SettingPasswordActivity {
    private EditText etPassword;
    private EditText etRepassword;


    @Override
    protected void onOk(String password, String repassword) {
        // check empty
        if(TextUtils.isEmpty(password) || TextUtils.isEmpty(repassword)) {
            Toast.makeText(this, R.string.password_can_not_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        // check consistent
        if(!password.equals(repassword)) {
            Toast.makeText(this, R.string.two_password_is_not_consistent, Toast.LENGTH_SHORT).show();
            return;
        }
        // save password
        ConfigUtils.putString(this, Constant.KEY_APP_LOCK_PASSWORD, EncryptionUtils.md5N(password, Constant.ENCRYPTION_COUNT));
        // set the app lock on
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putBoolean(Constant.KEY_CB_APP_LOCK, true)
                .commit();
        // start the service
        ActivityManagerUtils.checkService(this, AppLockService.class);

        // enter activity
        startActivity(new Intent(this, AppLockActivity.class));
        finish();
    }
}
