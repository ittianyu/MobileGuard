package com.ittianyu.mobileguard.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.engine.PhoneLocationEngine;
import com.ittianyu.mobileguard.view.FloatToast;

/**
 * This service will show location when incoming call or dialing
 */
public class IncomingLocationService extends Service {
    private TelephonyManager manager;
    private PhoneStateListener listener;

    private FloatToast floatToast;// the toast
    private TextView tvLocation;
    private PhoneLocationEngine phoneLocationEngine = new PhoneLocationEngine();
    private OutgoingCallReceiver outgoingCallReceiver;
    private boolean outgoing;// whether outgoing call, use it when CALL_STATE_OFFHOOK

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // create float toast
        View locationView = View.inflate(this, R.layout.sys_toast, null);
        tvLocation = (TextView) locationView.findViewById(R.id.tv_float_toast_location);
        floatToast = FloatToast.makeView(IncomingLocationService.this, locationView);

        // register call state listener
        manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        // hide location
                        floatToast.close();
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK: {
                        if(outgoing) {// if the CALL_STATE_OFFHOOK is outgoing call, don't hide it until CALL_STATE_IDLE
                            outgoing = false;
                            break;
                        }
                        // hide location
                        floatToast.close();
                        break;
                    }
                    case TelephonyManager.CALL_STATE_RINGING: {
                        showLocation(incomingNumber);
                        break;
                    }
                }
            }
        };
        manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        // register new outgoing call receiver
        outgoingCallReceiver = new OutgoingCallReceiver();
        IntentFilter filter = new IntentFilter("android.intent.action.NEW_OUTGOING_CALL");
        registerReceiver(outgoingCallReceiver, filter);

    }

    /**
     * get location and show it
     * @param number
     */
    private void showLocation(String number) {
        // get location
        String location = phoneLocationEngine.getLocation(this, number);
        // if can't get the location, set unknown
        if(TextUtils.isEmpty(location)) {
            location = getString(R.string.unknown);
        }
        // set to view
        tvLocation.setText(location);
        // show float toast
        floatToast.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // unregister call listener
        manager.listen(listener, PhoneStateListener.LISTEN_NONE);
        // unregister outgoing call
        unregisterReceiver(outgoingCallReceiver);
    }

    /**
     * when outgoing call, record the state
     */
    class OutgoingCallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            outgoing = true;
            String outgoingNumber = getResultData();
            showLocation(outgoingNumber);
        }
    }
}
