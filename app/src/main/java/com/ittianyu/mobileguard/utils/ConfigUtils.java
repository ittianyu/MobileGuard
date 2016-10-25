package com.ittianyu.mobileguard.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by yu.
 * read and write config utils
 */

public class ConfigUtils {
    private static final String NAME = "config";

    /**
     * Put a key-value into config
     * @param context context
     * @param key the key
     * @param value the value
     */
    public static void putString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key, value)
                .commit();
    }

    /**
     * Get value by key in config
     * @param context context
     * @param key the key
     * @param defaultValue if can't find the value of key, will return default value
     * @return value
     */
    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }

    /**
     * Put a key-value into config
     * @param context context
     * @param key the key
     * @param value the value
     */
    public static void putInt(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt(key, value)
                .commit();
    }

    /**
     * Get value by key in config
     * @param context context
     * @param key the key
     * @param defaultValue if can't find the value of key, will return default value
     * @return value
     */
    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, defaultValue);
    }



}
