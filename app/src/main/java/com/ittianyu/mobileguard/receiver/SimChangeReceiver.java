package com.ittianyu.mobileguard.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.constant.Constant;
import com.ittianyu.mobileguard.engine.ServiceManagerEngine;
import com.ittianyu.mobileguard.utils.ConfigUtils;

/**
 * SIM card changed receiver
 * It will send a message to safe phone number if sim card changed.
 */
public class SimChangeReceiver extends BroadcastReceiver {
    private static final String TAG = "SimChangeReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // check all services when system startup
        ServiceManagerEngine.checkAndAutoStart(context);

        // check the service is on
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        boolean phoneSafe = sp.getBoolean(Constant.KEY_CB_PHONE_SAFE, false);
        boolean bindSim = sp.getBoolean(Constant.KEY_CB_BIND_SIM, false);
        // haven't start bind sim or phone safe service
        if(!bindSim || !phoneSafe) {
            return;
        }
        // get old sim info
        String oldSimInfo = ConfigUtils.getString(context, Constant.KEY_SIM_INFO, "");
        // get current sim info
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String currentSimInfo = manager.getSimSerialNumber();
        // the two sim info equal
        if(currentSimInfo.equals(oldSimInfo)) {
            return;
        }
        // send alarm info to safe phone number
        String safePhone = ConfigUtils.getString(context, Constant.KEY_SAFE_PHONE, "");
        if(TextUtils.isEmpty(safePhone)) {
            Log.e(TAG, "safe phone is empty");
            return;
        }
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(safePhone, null, context.getString(R.string.tips_sim_changed), null, null);
        System.out.println("success send a sms to " + safePhone + ":\n" + context.getString(R.string.tips_sim_changed));
    }
}
