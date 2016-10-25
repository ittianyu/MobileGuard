package com.ittianyu.mobileguard.strategy.advancedtools;

import android.content.Context;
import android.content.Intent;

import com.ittianyu.mobileguard.activity.PhoneLocationActivity;
import com.ittianyu.mobileguard.strategy.OnClickListener;

/**
 * Created by yu.
 * just start PhoneLocationActivity
 */
public class PhoneLocationScheme implements OnClickListener {
    @Override
    public void onSelected(Context context) {
        context.startActivity(new Intent(context, PhoneLocationActivity.class));
    }
}
