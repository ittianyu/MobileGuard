package com.ittianyu.mobileguard.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;

import com.ittianyu.mobileguard.activity.LockedActivity;
import com.ittianyu.mobileguard.constant.Constant;
import com.ittianyu.mobileguard.dao.AppLockDao;
import com.ittianyu.mobileguard.engine.ProcessManagerEngine;
import com.ittianyu.mobileguard.utils.ConfigUtils;

import java.util.List;

/**
 * always check the current task stack.
 * It will start a activity to request password If the start app package name in app_lock.db
 */
public class AppLockService extends Service {
    // constants
    private static final long SLEEP_TIME = 50;
    // data
    private boolean running;
    private String unlockedPackageName = "";
    private AppLockReceiver receiver = new AppLockReceiver();
    private List<String> lockedApps;
    private AppLockDataChangedObserver observer;
    private boolean exit;// used for exit safely when haven't lock password

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // check the password whether exist
        String password = ConfigUtils.getString(this, Constant.KEY_APP_LOCK_PASSWORD, "");
        // if the password is empty, stop service and exit
        if(TextUtils.isEmpty(password)) {
            exit = true;
            stopSelf();
            return;
        }

        // register broadcast receiver for custom unlock action
        IntentFilter filter = new IntentFilter(Constant.ACTION_UNLOCK_APP);// unlock app
        filter.addAction(Intent.ACTION_SCREEN_ON);// screen on for starting app lock
        filter.addAction(Intent.ACTION_SCREEN_OFF);// screen off for stopping app lock
        registerReceiver(receiver, filter);

        // register content observer
        observer = new AppLockDataChangedObserver(new Handler());
        getContentResolver().registerContentObserver(Constant.URI_APP_LOCK_DATA_CHANGED, true, observer);

        // request the permission to get top task package name
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                !ProcessManagerEngine.hasGetUsageStatsPermission(this)) {
            ProcessManagerEngine.requestUsageStatesPermission(this);
        }

        // start a thread to keep watch on task stack
        startAppLock();
    }

    /**
     * start a thread to keep watch on task stack
     */
    private void startAppLock() {
        running = true;
        final ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final AppLockDao dao = new AppLockDao(this);
        lockedApps = dao.selectAll();

        new Thread() {
            @Override
            public void run() {
                while (running) {
                    SystemClock.sleep(SLEEP_TIME);
//System.out.println("app lock watching");
                    // get package name
                    String packageName = ProcessManagerEngine.getTaskTopAppPackageName(AppLockService.this, am);
                    if(TextUtils.isEmpty(packageName))
                        continue;
                    // ignore the unlocked app
                    if(packageName.equals(unlockedPackageName))
                        continue;
//                    System.out.println(packageName);
                    // check whether in locked list
//                    if(!dao.isExists(packageName))
                    synchronized (AppLockService.this) {// must synchronized when ues and set
                        if (!lockedApps.contains(packageName))
                            continue;
                    }
                    // if in list
                    // start a activity to request password
                    Intent intent = new Intent(AppLockService.this, LockedActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constant.EXTRA_LOCKED_APP_PACKAGE_NAME, packageName);
                    startActivity(intent);
                }
            }
        }.start();
    }

    /**
     * unregister receiver and observer
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        // if exit, it means no lock password. So no need to unregister anything
        if(exit)
            return;
        // exit the thread
        running = false;
        // unregister receiver
        unregisterReceiver(receiver);
        // unregister observer
        getContentResolver().unregisterContentObserver(observer);
    }


    /**
     * Used for notify service to ignore the specific app
     * and start or stop app lock
     */
    private class AppLockReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constant.ACTION_UNLOCK_APP)) {
                unlockedPackageName = intent.getStringExtra(Constant.EXTRA_LOCKED_APP_PACKAGE_NAME);
            } else if(action.equals(Intent.ACTION_SCREEN_ON)) {
                // start app lock
                startAppLock();
            } else if(action.equals(Intent.ACTION_SCREEN_OFF)) {
                // stop app lock
                running = false;
                // clean the unlocked app
                unlockedPackageName = "";
            }
        }
    }

    private class AppLockDataChangedObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public AppLockDataChangedObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            final AppLockDao dao = new AppLockDao(AppLockService.this);
            synchronized (AppLockService.this){// must synchronized when ues and set
                lockedApps = dao.selectAll();
            }
        }
    }

}
