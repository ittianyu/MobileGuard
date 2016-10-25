package com.ittianyu.mobileguard.constant;

import android.net.Uri;

/**
 * Created by yu.
 * This class define the constant which is needed more than once.
 */
public final class Constant {
    // action
    public static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    public static final String ACTION_UNLOCK_APP = "android.ittianyu.provider.UNLOCKED_APP";

    // Uri
    public static final Uri URI_APP_LOCK_DATA_CHANGED = Uri.parse("content://com.ittianyu.observer.APP_LOCK_DATA_CHANGED");

    // phone safe info
    public static final String KEY_SAFE_PHONE = "safe_phone";
    public static final String KEY_SIM_INFO = "sim_info";
    public static final String KEY_CONTACT_PHONE = "contact_phone";

    // phone safe setting
    public static final String KEY_CB_PHONE_SAFE = "cb_pref_phone_safe";
    public static final String KEY_CB_BIND_SIM = "cb_pref_bind_sim";
    public static final String KEY_CB_GPS_TRACE = "cb_pref_gps_trace";
    public static final String KEY_CB_DEVICE_ADMIN = "cb_pref_device_admin";
    public static final String KEY_CB_REMOTE_LOCK_SCREEN = "cb_pref_remote_lock_screen";
    public static final String KEY_CB_REMOTE_WIPE_DATA = "cb_pref_remote_wipe_data";
    public static final String KEY_CB_ALARM = "cb_pref_alarm";
    public static final String KEY_CB_APP_LOCK = "cb_pref_app_lock";

    // sms command
    public static final String SMS_GPS_TRACE = "#*gps*#";
    public static final String SMS_REMOTE_LOCK_SCREEN = "#*lock screen*#";
    public static final String SMS_REMOTE_WIPE_DATA = "#*wipe data*#";
    public static final String SMS_ALARM = "#*alarm*#";

    // setting center
    public static final String KEY_CB_AUTO_UPDATE = "cb_pref_auto_update";
    public static final String KEY_CB_BLACKLIST_INTERCEPT = "cb_pref_blacklist_intercept";
    public static final String KEY_CB_SHOW_INCOMING_LOCATION = "cb_pref_show_incoming_location";

    // FloatToast key
    public static final String KEY_FLOAT_TOAST_X = "float_toast_x";
    public static final String KEY_FLOAT_TOAST_Y = "float_toast_y";

    // Extra
    public static final String EXTRA_RESTORE_TYPE = "extra_restore_type";
    public static final String EXTRA_RESTORE_FILE = "extra_restore_file_name";
    public static final String EXTRA_LOCKED_APP_PACKAGE_NAME = "extra_locked_app_package_name";
    public static final String EXTRA_VIRUSES = "extra_viruses";

    // password key
    public static final int ENCRYPTION_COUNT = 3;
    public static final String KEY_APP_LOCK_PASSWORD = "app_lock_password";
    public static final String KEY_PHONE_SAFE_PASSWORD = "phone_safe_password";

}
