package com.ittianyu.mobileguard.strategy.maingridmenu;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;

import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.activity.CleanCacheActivity;
import com.ittianyu.mobileguard.strategy.OnClickListener;

/**
 * Clean Cache scheme
 * Just start CleanCacheActivity
 */
public class CleanCacheScheme implements OnClickListener {
    @Override
    public void onSelected(Context context) {
        // check the version of system
        // if the version >= 213(Android 6.0), this service can't be used
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.tips)
                    .setMessage(context.getString(R.string.tips_system_version_check)
                            + Build.VERSION.RELEASE
                            + context.getString(R.string.tips_clear_cache_recommend_use_system_service))
                    .setPositiveButton(R.string.ok, null)
                    .show();
            return;
        }
        context.startActivity(new Intent(context, CleanCacheActivity.class));
    }
}
