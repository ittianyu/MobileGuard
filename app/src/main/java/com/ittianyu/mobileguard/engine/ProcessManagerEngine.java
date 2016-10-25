package com.ittianyu.mobileguard.engine;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Debug;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.ittianyu.mobileguard.domain.ProcessInfoBean;
import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import okio.BufferedSource;
import okio.Okio;

/**
 * Created by yu.
 * manage alive app
 */
public class ProcessManagerEngine {

    /**
     * @return the device total memory in byte
     */
    public static long getTotalMemory() {
        File file = new File("/proc/meminfo");
        BufferedSource source = null;
        try {
            // read meminfo file
            source = Okio.buffer(Okio.source(file));
            String totalMemoryStr = source.readUtf8Line();
            // get total memory
            String[] split = totalMemoryStr.split("\\s+");
            return Integer.valueOf(split[1]).intValue() * 1024;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (source != null) {
                    source.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    /**
     * @param context
     * @return the system free memory in byte
     */
    public static long getFreeMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }

    /**
     * get running processes info
     * @param context
     * @return
     */
    public static List<ProcessInfoBean> getRunningProcessesInfo(Context context) {
        List<ProcessInfoBean> list = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            list = getRunningProcessesInfoByProc(context);
        } else {
            list = getRunningProcessesInfoCompat(context);
        }
        return list;
    }

    /**
     * get running processes info
     * you just can use it before Android 5.0
     * @param context
     * @return
     */
    public static List<ProcessInfoBean> getRunningProcessesInfoCompat(Context context) {
        // get manager
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        // get processes info
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
        // create list. Specific it init size
        List<ProcessInfoBean> infos = new ArrayList<>(processes.size());
        // get info
        for (ActivityManager.RunningAppProcessInfo processInfo : processes) {
            // create bean
            ProcessInfoBean bean = new ProcessInfoBean();
            // set value
            bean.setPackageName(processInfo.processName);
            // get package info
            ApplicationInfo applicationInfo = null;
            try {
                applicationInfo = pm.getApplicationInfo(bean.getPackageName(), PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                // if package is empty, continue
                continue;
            }
            // set icon
            bean.setIcon(applicationInfo.loadIcon(pm));
            // app name
            bean.setAppName(applicationInfo.loadLabel(pm).toString());
            // system app
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                bean.setSystemApp(true);
            }// if not, need set false. Actually it was.
            // memory
            Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{processInfo.pid});
            if (processMemoryInfo.length >= 1) {
                bean.setMemory(processMemoryInfo[0].getTotalPss() * 1024);
            }
            // add to list
            infos.add(bean);
        }

        return infos;
    }

    /**
     * get running processes info by proc
     * @param context
     * @return
     */
    public static List<ProcessInfoBean> getRunningProcessesInfoByProc(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        // get running app processes info
        List<AndroidAppProcess> processes = AndroidProcesses.getRunningAppProcesses();
        // create list. Specific it init size
        List<ProcessInfoBean> infos = new ArrayList<>(processes.size());
        for (AndroidAppProcess process: processes) {
            // create bean
            ProcessInfoBean bean = new ProcessInfoBean();
            // get package name
            bean.setPackageName(process.getPackageName());
            // check empty
            if(TextUtils.isEmpty(bean.getPackageName())) {
                continue;
            }
            // get package info
            ApplicationInfo applicationInfo = null;
            try {
                applicationInfo = pm.getApplicationInfo(bean.getPackageName(), PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                // if package is empty, continue
                continue;
            }
            // set icon
            bean.setIcon(applicationInfo.loadIcon(pm));
            // app name
            bean.setAppName(applicationInfo.loadLabel(pm).toString());
            // system app
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                bean.setSystemApp(true);
            }// if not, need set false. Actually it was.
            // memory
            Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{process.pid});
            if (processMemoryInfo.length >= 1) {
                bean.setMemory(processMemoryInfo[0].getTotalPss() * 1024);
            }
            // add to list
            infos.add(bean);
        }
        return infos;
    }

    /**
     * get current task top app package name
     * @param context
     * @param am
     * @return the top app package name
     */
    public static String getTaskTopAppPackageName(Context context, ActivityManager am) {
        String packageName = "";
        // if the sdk >= 21. It can only use getRunningAppProcesses to get task top package name
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usage = (UsageStatsManager)context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> stats = usage.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);
            if (stats != null) {
                SortedMap<Long, UsageStats> runningTask = new TreeMap<Long,UsageStats>();
                for (UsageStats usageStats : stats) {
                    runningTask.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (runningTask.isEmpty()) {
                    return null;
                }
                packageName =  runningTask.get(runningTask.lastKey()).getPackageName();
            }
        } else {// if sdk <= 20, can use getRunningTasks
            List<ActivityManager.RunningTaskInfo> infos = am.getRunningTasks(1);
            packageName = infos.get(0).topActivity.getPackageName();
        }
        return packageName;
    }

    /**
     * check whether has permission to get usage stats
     * @param context
     * @return true if have, false otherwise
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean hasGetUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager)context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), context.getPackageName());
        }
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    /**
     * request the get usage states permission.
     * @param context
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void requestUsageStatesPermission(Context context) {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
