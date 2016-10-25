package com.ittianyu.mobileguard.utils;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import java.util.List;

/**
 * Created by yu.
 * provider some ActivityManager operator
 */

public class ActivityManagerUtils {
    /**
     * check whether specific service is running.
     * @param context
     * @param serviceClassFullName the full class name of service
     * @return true if service is running
     */
    public static boolean isServiceRunning(Context context, String serviceClassFullName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(1024);
        for (ActivityManager.RunningServiceInfo info: runningServices) {
            if(info.getClass().getName().equals(serviceClassFullName))
                return true;
        }
        return false;
    }

    /**
     * check the specific service is running, if not, start it.
     * @param context
     * @param serviceClass the service class
     */
    public static void checkService(Context context, Class<? extends Service> serviceClass) {
        if (!ActivityManagerUtils.isServiceRunning(context, serviceClass.getName())) {
            context.startService(new Intent(context, serviceClass));
        }
    }

}
