package com.ittianyu.mobileguard.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.constant.Constant;
import com.ittianyu.mobileguard.service.AppLockService;
import com.ittianyu.mobileguard.service.BlacklistInterceptService;
import com.ittianyu.mobileguard.service.IncomingLocationService;

/**
 * the fragment to show setting and deal with select evnet
 */
public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * add resource
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_setting);
        disablePreferences();
    }

    /**
     * disable some preferences.
     * for example, blacklist can't be used when the system version >= 4.4
     */
    private void disablePreferences() {
        // if the version >= 19(Android 4.4), blacklist can't be used
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Preference preference = findPreference(Constant.KEY_CB_BLACKLIST_INTERCEPT);
            preference.setEnabled(false);
        }

    }

    /**
     * will be call when sp changed
     * @param sharedPreferences
     * @param key
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        boolean value = sharedPreferences.getBoolean(key, false);
        Context context = getActivity();

        if(key.equals(Constant.KEY_CB_BLACKLIST_INTERCEPT)) {
            // blacklist intercept
            startOrStopService(context, BlacklistInterceptService.class, value);
        } else if(key.equals(Constant.KEY_CB_SHOW_INCOMING_LOCATION)) {
            // incoming call location
            startOrStopService(context, IncomingLocationService.class, value);
        }
         else if(key.equals(Constant.KEY_CB_APP_LOCK)) {
            // incoming call location
            startOrStopService(context, AppLockService.class, value);
        }

    }

    /**
     * start service if start is true, stop it otherwise.
     * @param context context
     * @param cls service class
     * @param start start service if start is true, stop it otherwise.
     */
    private void startOrStopService(Context context,  Class<?> cls, boolean start) {
        Intent intent = new Intent(context, cls);
        if(start) {
            // start this service
            context.startService(intent);
        } else {
            // stop this service
            context.stopService(intent);
        }
    }

    /**
     * unregister sp changed listener
     */
    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * register sp changed listener
     */
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }
}
