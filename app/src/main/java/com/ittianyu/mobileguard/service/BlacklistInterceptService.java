package com.ittianyu.mobileguard.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Telephony;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.ittianyu.mobileguard.constant.Constant;
import com.ittianyu.mobileguard.dao.BlacklistDao;
import com.ittianyu.mobileguard.db.BlacklistDb;
import com.ittianyu.mobileguard.domain.BlacklistBean;
import com.ittianyu.mobileguard.utils.CallUtils;
import com.ittianyu.mobileguard.utils.SmsUtils;

/**
 * blacklist intercept service
 * will intercept call or sms if the phone number in blacklist
 */
public class BlacklistInterceptService extends Service {
    private static final String URI_CALL = "content://call_log/calls";

    private SmsReceiver smsReceiver;
    private TelephonyManager telephonyManager;
    private PhoneStateListener phoneStateListener = new PhoneStateListener(){
        @Override
        public void onCallStateChanged(int state, final String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            // state is not CALL_STATE_RINGING
            if(TelephonyManager.CALL_STATE_RINGING != state) {
                return;
            }
            // check whether in blacklist
            BlacklistDao dao = new BlacklistDao(BlacklistInterceptService.this);
            BlacklistBean bean = dao.selectByPhone(incomingNumber);
            if(null == bean) {// not in blacklist
                return;
            }
            // check mode whether has call
            int mode = bean.getMode();
            if((BlacklistDb.MODE_CALL & mode) != 0) {// has call mode, need intercept
                // register call database data change listener
                getContentResolver().registerContentObserver(Uri.parse(URI_CALL),
                        true, new ContentObserver(new Handler()) {
                            @Override
                            public void onChange(boolean selfChange) {
                                // unregister listener, just delete once
                                getContentResolver().unregisterContentObserver(this);
                                // delete the current call record
                                int count = CallUtils.deleteLatestCall(BlacklistInterceptService.this, incomingNumber);
                                System.out.println("delete count = " + count);
                            }
                        });

                if (CallUtils.endCall()) {
                    System.out.println("call from " + incomingNumber + " was intercepted");
                } else {
                    System.out.println("call from " + incomingNumber + " was failed to intercepted");
                }
            }
        }
    };



    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        System.out.println("BlacklistInterceptService onCreate");
        // register call listener
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        // register sms receiver
        smsReceiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ACTION_SMS_RECEIVED);
        // set max priority
        filter.setPriority(Integer.MAX_VALUE);
        // register
        registerReceiver(smsReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("BlacklistInterceptService onDestroy");
        // unregister call listener
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        // unregister sms receiver
        unregisterReceiver(smsReceiver);
    }
}




class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // get sms messages
        SmsMessage[] messages = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        } else {
            messages = SmsUtils.getMessagesFromIntent(intent);
        }
        // check whether sender in blacklist
        BlacklistDao dao = new BlacklistDao(context);
        for (SmsMessage message: messages) {
            // get sender
            String sender = message.getOriginatingAddress();
            // select by phone
            BlacklistBean bean = dao.selectByPhone(sender);
            if(null == bean) {// not in blacklist
                continue;
            }
            // check intercept mode
            if((BlacklistDb.MODE_SMS & bean.getMode()) != 0) {// has sms mode, need intercept
                // abort sms
                abortBroadcast();
            }
        }

    }
}
