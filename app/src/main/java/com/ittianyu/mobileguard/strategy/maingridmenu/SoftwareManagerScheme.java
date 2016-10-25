package com.ittianyu.mobileguard.strategy.maingridmenu;

import android.content.Context;
import android.content.Intent;

import com.ittianyu.mobileguard.activity.SoftwareManagerActivity;
import com.ittianyu.mobileguard.strategy.OnClickListener;

/**
 * Created by yu on.
 * Software Manager scheme
 * just start SoftwareManagerActivity
 */
public class SoftwareManagerScheme implements OnClickListener {
    @Override
    public void onSelected(Context context) {
        context.startActivity(new Intent(context, SoftwareManagerActivity.class));
    }
}
