package com.ittianyu.mobileguard.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.ittianyu.mobileguard.constant.Constant;
import com.ittianyu.mobileguard.service.AppLockService;
import com.ittianyu.mobileguard.service.BlacklistInterceptService;
import com.ittianyu.mobileguard.service.IncomingLocationService;
import com.ittianyu.mobileguard.service.PhoneSafeService;
import com.ittianyu.mobileguard.utils.ActivityManagerUtils;

/**
 * Created by yu.
 * manage all of services in the app
 */
public class ServiceManagerEngine {

    /**
     * check services are on, if on but the service is not running, starting it
     * @param context
     */
    public static void checkAndAutoStart(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        // phone safe service
        if(sp.getBoolean(Constant.KEY_CB_PHONE_SAFE, false))
            ActivityManagerUtils.checkService(context, PhoneSafeService.class);
        // blacklist intercept service
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            if(sp.getBoolean(Constant.KEY_CB_BLACKLIST_INTERCEPT, true))
                ActivityManagerUtils.checkService(context, BlacklistInterceptService.class);
        }
        // incoming call location
        if(sp.getBoolean(Constant.KEY_CB_SHOW_INCOMING_LOCATION, true))
            ActivityManagerUtils.checkService(context, IncomingLocationService.class);
        // incoming call location
        if(sp.getBoolean(Constant.KEY_CB_APP_LOCK, false))
            ActivityManagerUtils.checkService(context, AppLockService.class);

    }

}
