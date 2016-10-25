package com.ittianyu.mobileguard.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.activity.base.BaseActivityUpEnable;
import com.ittianyu.mobileguard.constant.Constant;
import com.ittianyu.mobileguard.utils.ConfigUtils;
import com.ittianyu.mobileguard.utils.EncryptionUtils;

/**
 * the activity which started when app was locked
 * you need input password to unlock
 */
public class LockedActivity extends BaseActivityUpEnable {
    // view
    private EditText etPassword;
    private TextView tvName;
    private ImageView ivIcon;


    //data
    private String packageName;
    private HomeKeyDownReceiver receiver;
    private String trueMd5Password;

    /**
     * construct method. set the action bar title
     */
    public LockedActivity() {
        super(R.string.app_lock);
    }

    /**
     * 1
     */
    @Override
    protected void initView() {
        setContentView(R.layout.activity_locked);
        // bind view
        etPassword = (EditText) findViewById(R.id.et_password);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivIcon = (ImageView) findViewById(R.id.iv_icon);

    }

    /**
     * 2
     */
    @Override
    protected void initData() {
        // get extra
        Bundle extras = getIntent().getExtras();
        if(null == extras) {
            return;
        }
        // get package name
        packageName = extras.getString(Constant.EXTRA_LOCKED_APP_PACKAGE_NAME);

        // get package info
        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo info = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            // get and set icon
            Drawable icon = info.loadIcon(pm);
            ivIcon.setImageDrawable(icon);
            // get app name
            CharSequence name = info.loadLabel(pm);
            tvName.setText(name);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        trueMd5Password = ConfigUtils.getString(this, Constant.KEY_APP_LOCK_PASSWORD, "");
    }
    
    /**
     * 3
     */
    @Override
    protected void initEvent() {
        // set on click listener
        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOk();
            }
        });

        // register broadcast to receiver the HOME key down
        IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        receiver = new HomeKeyDownReceiver();
        registerReceiver(receiver, filter);
    }

    /**
     * unregister receiver
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // unregister receiver
        unregisterReceiver(receiver);
    }

    /**
     * check password
     */
    private void onOk() {
        String password = etPassword.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.password_can_not_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        // check password
        if(!EncryptionUtils.md5N(password, Constant.ENCRYPTION_COUNT).equals(trueMd5Password)) {
            Toast.makeText(this, R.string.password_is_not_true, Toast.LENGTH_SHORT).show();
            return;
        }
        // password is true
        // send a broadcast to notify service don't interrupt the app
        Intent intent = new Intent(Constant.ACTION_UNLOCK_APP);
        intent.putExtra(Constant.EXTRA_LOCKED_APP_PACKAGE_NAME, packageName);
        sendBroadcast(intent);

        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            // if the key is back, goto the Launcher Activity
            case KeyEvent.KEYCODE_BACK: {
                gotoLauncher();
                break;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * start the launcher activity and finish self
     */
    private void gotoLauncher() {
        /*
<intent-filter>
    <action android:name="android.intent.action.MAIN" />
    <category android:name="android.intent.category.HOME" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.MONKEY"/>
</intent-filter>
         */
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
        finish();
    }

    /**
     * goto the  launcher activity if receiver the HOME key down
     */
    private class HomeKeyDownReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                gotoLauncher();
            }
        }
    }

}
