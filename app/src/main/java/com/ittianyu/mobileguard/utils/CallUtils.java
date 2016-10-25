package com.ittianyu.mobileguard.utils;

import android.content.Context;
import android.net.Uri;
import android.os.IBinder;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

/**
 * Created by yu.
 * some call operator
 */
public class CallUtils {
    private static final String URI_CALL = "content://call_log/calls";

    /**
     * End call or go to the Home screen
     * use reflex to call endCall in ITelephony
     * @return whether it hung up
     */
    public static boolean endCall() {
        // the src code is like this
        /*
        private ITelephony getITelephony() {
            return ITelephony.Stub.asInterface(ServiceManager.getService(Context.TELEPHONY_SERVICE));
        }
        public boolean endCall() {
            try {
                ITelephony telephony = getITelephony();
                if (telephony != null)
                    return telephony.endCall();
            } catch (RemoteException e) {
                Log.e(TAG, "Error calling ITelephony#endCall", e);
            }
            return false;
        }
        */

        try {
            Class serviceManager = Class.forName("android.os.ServiceManager");
            Method getService = serviceManager.getDeclaredMethod("getService", String.class);
            ITelephony telephony = ITelephony.Stub.asInterface((IBinder) getService.invoke(null, Context.TELEPHONY_SERVICE));
            return telephony.endCall();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * delete call by number, and this method will just delete the latest record(the max date record)
     * @param context
     * @param number the number needed delete
     * @return The number of rows deleted.
     */
    public static int deleteLatestCall(Context context, String number) {
        return context.getContentResolver().delete(Uri.parse(URI_CALL),
                "date = (select max(date) from calls where number=?)", new String[]{number});
    }

}
