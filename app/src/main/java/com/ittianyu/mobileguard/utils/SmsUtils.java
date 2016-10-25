package com.ittianyu.mobileguard.utils;

import android.content.Intent;
import android.os.Build;
import android.telephony.SmsMessage;

/**
 * Created by yu.
 * contain some sms method. Such as getMessagesFromIntent.
 */
public class SmsUtils {
    /**
     * Read the PDUs out of an SMS_RECEIVED_ACTION or a
     * DATA_SMS_RECEIVED_ACTION intent.
     *
     * @param intent the intent to read from
     * @return an array of SmsMessages for the PDUs
     */
    public static SmsMessage[] getMessagesFromIntent(Intent intent) {
        Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
        String format = intent.getStringExtra("format");

        int pduCount = messages.length;
        SmsMessage[] msgs = new SmsMessage[pduCount];

        for (int i = 0; i < pduCount; i++) {
            byte[] pdu = (byte[]) messages[i];
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                msgs[i] = SmsMessage.createFromPdu(pdu, format);
            } else {
                msgs[i] = SmsMessage.createFromPdu(pdu);
            }
        }
        return msgs;
    }
}
