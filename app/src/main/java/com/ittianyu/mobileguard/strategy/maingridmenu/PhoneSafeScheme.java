package com.ittianyu.mobileguard.strategy.maingridmenu;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.ittianyu.mobileguard.activity.PhoneSafeInputPasswordActivity;
import com.ittianyu.mobileguard.activity.PhoneSafeSettingPasswordActivity;
import com.ittianyu.mobileguard.constant.Constant;
import com.ittianyu.mobileguard.strategy.OnClickListener;
import com.ittianyu.mobileguard.utils.ConfigUtils;

/**
 * Created by yu.
 * Phone Safe scheme
 */
public class PhoneSafeScheme implements OnClickListener {

    @Override
    public void onSelected(final Context context) {
        String password = ConfigUtils.getString(context, Constant.KEY_PHONE_SAFE_PASSWORD, "");
        if(TextUtils.isEmpty(password)) {
            // have no password
            context.startActivity(new Intent(context, PhoneSafeSettingPasswordActivity.class));
        }else {
            // input password
            context.startActivity(new Intent(context, PhoneSafeInputPasswordActivity.class));
        }
    }

}
