package com.ittianyu.mobileguard.strategy.maingridmenu;

import android.content.Context;
import android.content.Intent;

import com.ittianyu.mobileguard.activity.AdvancedToolsActivity;
import com.ittianyu.mobileguard.strategy.OnClickListener;

/**
 * Created by yu.
 * Advanced Tools scheme
 */
public class AdvancedToolsScheme implements OnClickListener {
    @Override
    public void onSelected(Context context) {
        context.startActivity(new Intent(context, AdvancedToolsActivity.class));
    }
}
