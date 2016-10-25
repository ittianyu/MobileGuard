package com.ittianyu.mobileguard.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.text.TextUtils;

import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.constant.Constant;
import com.ittianyu.mobileguard.service.GpsTraceService;
import com.ittianyu.mobileguard.utils.SmsUtils;

import static com.ittianyu.mobileguard.constant.Constant.SMS_ALARM;
import static com.ittianyu.mobileguard.constant.Constant.SMS_GPS_TRACE;
import static com.ittianyu.mobileguard.constant.Constant.SMS_REMOTE_LOCK_SCREEN;
import static com.ittianyu.mobileguard.constant.Constant.SMS_REMOTE_WIPE_DATA;

/**
 * Sms receiver.
 * if receive #*alarm*#     #*wipe data*#   #*wipe data*#   #*lock screen*#     #*gps*#
 * it will do something
 */
public class SmsReceiver extends BroadcastReceiver {
    private MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
        // get messages
        SmsMessage[] messages;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        } else {
            messages = SmsUtils.getMessagesFromIntent(intent);
        }
        if(null == messages) {
            return;
        }

        // get pref
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        // check content
        for (SmsMessage message: messages) {
            String content = message.getMessageBody();
            System.out.println(message.getOriginatingAddress() + ":" + content);
            if(SMS_GPS_TRACE.equals(content)) {// gps trace
                // check config, if service is not on, skip this service
                if (!sp.getBoolean(Constant.KEY_CB_GPS_TRACE, false))
                    continue;
                // start service
                context.startService(new Intent(context, GpsTraceService.class));
                // abort
                abortBroadcast();
            } else if (content.contains(SMS_REMOTE_LOCK_SCREEN)) {// remote lock screen
                // check permission
                boolean deviceAdmin = sp.getBoolean(Constant.KEY_CB_DEVICE_ADMIN, false);
                boolean lockScreen = sp.getBoolean(Constant.KEY_CB_REMOTE_LOCK_SCREEN, false);
                // service is off
                if (!deviceAdmin || !lockScreen)
                    continue;
                // get device policy manager
                DevicePolicyManager mDPM =
                        (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                // get password
                int index = content.indexOf(SMS_REMOTE_LOCK_SCREEN);
                if(-1 != index) {// have password
                    String password = content.substring(index + + SMS_REMOTE_LOCK_SCREEN.length());
                    if(!TextUtils.isEmpty(password)) {
                        // set password
                        mDPM.resetPassword(password, 0);
                    }
                }
                // lock screen
                mDPM.lockNow();
                // abort
                abortBroadcast();
            } else if (SMS_REMOTE_WIPE_DATA.equals(content)) {// remote wipe data
                // check permission
                boolean deviceAdmin = sp.getBoolean(Constant.KEY_CB_DEVICE_ADMIN, false);
                boolean wipeData = sp.getBoolean(Constant.KEY_CB_REMOTE_WIPE_DATA, false);
                // service is off
                if (!deviceAdmin || !wipeData)
                    continue;
                // get device policy manager
                DevicePolicyManager mDPM =
                        (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                // wipe data
                mDPM.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                // abort
                abortBroadcast();
            } else if (SMS_ALARM.equals(content)) {// alarm
                // check permission
                if (!sp.getBoolean(Constant.KEY_CB_ALARM, false))
                    continue;
                // abort
                abortBroadcast();
                if(null != mediaPlayer)
                    return;
                // play alarm sound
                mediaPlayer = MediaPlayer.create(context, R.raw.alarm);
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }

        }

    }
}
