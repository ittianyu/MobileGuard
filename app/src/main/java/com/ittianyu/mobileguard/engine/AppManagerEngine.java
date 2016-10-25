package com.ittianyu.mobileguard.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Environment;
import android.os.RemoteException;
import android.text.TextUtils;

import com.ittianyu.mobileguard.domain.AppInfoBean;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yu on.
 * provide app info
 */
public class AppManagerEngine {

    /**
     * get the sd card free space
     *
     * @return the byte of free space
     */
    public static long getSdCardFreeSpace() {
        File directory = Environment.getExternalStorageDirectory();
        return directory.getFreeSpace();
    }

    /**
     * get the rom free space
     *
     * @return the byte of free space
     */
    public static long getRomFreeSpace() {
        File directory = Environment.getDataDirectory();
        return directory.getFreeSpace();
    }

    /**
     * get all installed app info
     * Attention: the app size is asynchronous.
     * so you can set a app info listener.
     * you can also set null for listener If you don't use the size info or don't care it.
     * @param context
     * @param listener will be call when app info get completed if not null. The timeout is 3s. It will be call whether success or not.
     * @return a AppInfoBean list. It would never be null.
     */
    public static List<AppInfoBean> getInstalledAppInfo(Context context, final AppInfoListener listener) {
        // get manager
        PackageManager pm = context.getPackageManager();
        // get all installed app info
        final List<ApplicationInfo> infos = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        // use for counting the progress of getting size
        class CompletedCountBean {
            int completedCount;
        }
        final CompletedCountBean count = new CompletedCountBean();

        // Create bean list. Here input the certain init count for list
        final List<AppInfoBean> appInfos = new ArrayList<>(infos.size());

        // start a timer to manager the max wait time
        final Timer timer = new Timer(true);
        // if have listener, start it
        if (null != listener) {
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    synchronized (count) {
                        // change the cout first
                        // prevent the if(count.completedCount == infos.size())
                        count.completedCount = -1;
                        listener.onGetInfoCompleted(appInfos);
                    }
                }
            };
            timer.schedule(timerTask, 3000);
        }


        for (ApplicationInfo info : infos) {
            // new a AppInfoBean and add to list
            final AppInfoBean bean = new AppInfoBean();
            appInfos.add(bean);

            // set value to bean
            bean.setIcon(info.loadIcon(pm));
            bean.setName(info.loadLabel(pm).toString());
            bean.setPackageName(info.packageName);

            // according to flag to check system and sd card app
            if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                // if have system flag, it is a system app, the value should be true
                bean.setSystemApp(true);
            }// if not have, it should be false. Actually it is.

            if ((info.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                // if have EXTERNAL_STORAGE flag, it is a sd card app, the value should be true
                bean.setSystemApp(true);
            }// if not have, it should be false. Actually it is.

            // get apk path
            bean.setApkPath(info.sourceDir);
            // get app size
            getAppSize(context, info.packageName, new AppSizeInfoListener() {
                @Override
                public void onGetSizeInfoCompleted(AppSizeInfo sizeInfo) {
                    // set size
                    long totalSize = sizeInfo.cacheSize + sizeInfo.codeSize + sizeInfo.dataSize;
                    bean.setSize(totalSize);
                    bean.setCacheSize(sizeInfo.cacheSize);
                    // if listener is null, no need to call this
                    if(null == listener)
                        return;
                    synchronized (count) {
                        count.completedCount++;
                        // if the all app size are got, call the listener
                        if(count.completedCount == infos.size()) {
                            // stop the timer first
                            timer.cancel();
                            listener.onGetInfoCompleted(appInfos);
                        }
                    }
                }
            });
        }
        return appInfos;
    }

    /**
     * get app size by package name
     * @param context
     * @param packageName package name
     * @param listener it will be call when success to get size
     */
    public static void getAppSize(Context context, String packageName, final AppSizeInfoListener listener) {
        // check argument
        if(null == listener) {
            throw new NullPointerException("listener can't be null");
        }
        if(TextUtils.isEmpty(packageName)) {
            throw  new IllegalArgumentException("packageName can't be empty");
        }

        // get pm
        PackageManager pm = context.getPackageManager();
        Method getPackageSizeInfo = null;
        try {
            // get method getPackageSizeInfo
            getPackageSizeInfo = pm.getClass().getMethod(
                    "getPackageSizeInfo",
                    String.class, IPackageStatsObserver.class);
            // call method
            getPackageSizeInfo.invoke(pm, packageName,
                    new IPackageStatsObserver.Stub() {
                        @Override
                        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
                                throws RemoteException {
                            // call listener
                            listener.onGetSizeInfoCompleted(
                                    new AppSizeInfo(pStats.cacheSize, pStats.dataSize, pStats.codeSize));
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * clear all app cache
     * @param context
     * @param listener the completion listener, it can be null if you don't care the result.
     *                 onClearCompleted run on child thread.
     *                 onClearFailed run on UI thread.
     */
    public static void clearAllCache(Context context, final ClearCacheListener listener) {
        /*
        public void freeStorageAndNotify(long freeStorageSize, IPackageDataObserver observer) {
            freeStorageAndNotify(null, freeStorageSize, observer);
        }
         */
        PackageManager pm = context.getPackageManager();
        try {
            Method freeStorageAndNotify = pm.getClass().getDeclaredMethod("freeStorageAndNotify", long.class, IPackageDataObserver.class);
            freeStorageAndNotify.invoke(pm, Long.MAX_VALUE, new IPackageDataObserver.Stub(){
                @Override
                public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                    if(null != listener)
                        listener.onClearCompleted();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            if(null != listener)
                listener.onClearFailed();
        }
    }

    /**
     * will be call when clear completed
     */
    public static interface ClearCacheListener {
        /**
         * will be call when success
         */
        void onClearCompleted();

        /**
         * will be call when failed
         */
        void onClearFailed();
    }

    /**
     * will be call when app info get completed
     */
    public static interface AppInfoListener {
        void onGetInfoCompleted(List<AppInfoBean> apps);
    }

    /**
     * will be call when size info get completed
     */
    public static interface AppSizeInfoListener {
        void onGetSizeInfoCompleted(AppSizeInfo sizeInfo);
    }

    /**
     * app size info bean
     */
    public static class AppSizeInfo {
        private long cacheSize;
        private long dataSize;
        private long codeSize;

        public AppSizeInfo() {
        }

        public AppSizeInfo(long cacheSize, long dataSize, long codeSize) {
            this.cacheSize = cacheSize;
            this.dataSize = dataSize;
            this.codeSize = codeSize;
        }

        public long getCacheSize() {
            return cacheSize;
        }

        public void setCacheSize(long cacheSize) {
            this.cacheSize = cacheSize;
        }

        public long getDataSize() {
            return dataSize;
        }

        public void setDataSize(long dataSize) {
            this.dataSize = dataSize;
        }

        public long getCodeSize() {
            return codeSize;
        }

        public void setCodeSize(long codeSize) {
            this.codeSize = codeSize;
        }
    }

}
