package com.ittianyu.mobileguard.utils;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

/**
 * Created by yu.
 * Some windows method.
 */

public class WindowsUtils {
    /**
     * set the activity display in full screen
     * @param context the activity that need be show full screen.
     */
    public static void setFullScreen(Activity context) {
        context.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * hide action bar
     * @param context he activity that need hide action bar
     */
    public static void hideActionBar(AppCompatActivity context) {
        // Hide UI
        ActionBar actionBar = context.getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }
}
