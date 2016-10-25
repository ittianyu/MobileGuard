package com.ittianyu.mobileguard.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.ittianyu.mobileguard.BuildConfig;
import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.activity.base.BaseActivityFullScreen;
import com.ittianyu.mobileguard.constant.Constant;
import com.ittianyu.mobileguard.domain.VersionBean;
import com.ittianyu.mobileguard.utils.ProgressDownloadUtils;
import com.ittianyu.mobileguard.utils.ProgressResponseBody;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * start activity
 */
public class SplashActivity extends BaseActivityFullScreen {
    // constants
    private static final String VERSION_URL = "http://10.0.2.2/mobileguardversion.json";
    private static final String NEW_VERSION_APK = "MobileGuardNew.apk";
    // animation time
    private static final int ANIMATION_DURATION = 700;
    // show time
    private static final int LOGO_SHOW_TIME = 500;
    // result code
    private static final int REQUEST_CODE_INSTALL_NEW_VERSION = 1;
    private static final int TIMEOUT = 3;
    // data
    private OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .build();

    private boolean autoUpdate;

    /**
     * changet init order
     */
    @Override
    protected void init() {
        initData();
        initView();
        initEvent();
    }

    /**
     * 1
     */
    @Override
    protected void initData() {
        // get auto update cinfig
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        autoUpdate = sp.getBoolean(Constant.KEY_CB_AUTO_UPDATE, true);
//        System.out.println("autoUpdate:" + autoUpdate);
    }

    /**
     * 2
     */
    @Override
    protected void initView() {
        setContentView(R.layout.activity_splash);
        // set animation
        setAnimation();
        // get local version
//        getAppVersionInfo();// now replace by the word on image
        // auto update check
        if(autoUpdate) {
            // get latest version info
            getVersionInfo();
        }// if not update, it will start main activity on animation end
    }

    /**
     * 3
     */
    @Override
    protected void initEvent() {

    }

    /**
     * check version.
     * Need run on UI thread
     * Showing a dialog to query user whether to update app, if local version code < latest version code
     *
     * @param latestVersionInfo the latest version bean
     */
    private void checkVersion(final VersionBean latestVersionInfo) {
        if (null == latestVersionInfo || BuildConfig.VERSION_CODE >= latestVersionInfo.getVersion()) {
            startMainActivity();
            return;
        }
        // show dialog to query whether to update
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.update_tips)
                .setMessage(getString(R.string.update_description) + latestVersionInfo.getDescription())
//                .setCancelable(false)// can't be canceled by esc or touch other position
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        startMainActivity();
                    }
                })
                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateApp(latestVersionInfo.getUrl());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startMainActivity();
                    }
                })
                .create();
        dialog.show();
    }

    /**
     * download the new apk and install
     *
     * @param url
     */
    private void updateApp(final String url) {
        // download apk, the install will be started after download success
        downloadNewVersion(url);
    }

    /**
     * 安装新版本
     *
     * @param file the apk
     */
    private void installNewVersion(final File file) throws FileNotFoundException {
/*
<activity android:name=".PackageInstallerActivity"
        android:configChanges="orientation|keyboardHidden"
        android:theme="@style/TallTitleBarTheme">
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <data android:scheme="content" />
        <data android:scheme="file" />
        <data android:mimeType="application/vnd.android.package-archive" />
    </intent-filter>
</activity>
 */
        if (null == file)
            throw new NullPointerException("file can't be null");
        if (!file.exists())
            throw new FileNotFoundException("file " + file.getAbsolutePath() + " can't be found");

        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivityForResult(intent, REQUEST_CODE_INSTALL_NEW_VERSION);
    }

    /**
     * on the started activity return result
     * will start the main activity
     *
     * @param requestCode request code
     * @param resultCode  result code
     * @param data        data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_INSTALL_NEW_VERSION:
                startMainActivity();
                break;
        }
    }

    /**
     * download the new apk
     *
     * @param url the url of new version
     */
    private void downloadNewVersion(final String url) {
/*        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SplashActivity.this, R.string.failed_to_download, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream in = response.body().byteStream();
                FileUtils.saveFileWithStream(new File(Environment.getExternalStorageDirectory(), NEW_VERSION_APK), in);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SplashActivity.this, R.string.success_to_download, Toast.LENGTH_SHORT).show();
                        // install apk
                        try {
                            installNewVersion(new File(Environment.getExternalStorageDirectory(), NEW_VERSION_APK));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            Toast.makeText(SplashActivity.this, R.string.failed_to_install_new_version, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });*/
        // show progress dialog
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.download);
        progressDialog.setMessage(getString(R.string.download_tips));
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                startMainActivity();
            }
        });
        progressDialog.setIndeterminate(false);// show progress bar
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.show();

        // download
        ProgressDownloadUtils downloadUtils = new ProgressDownloadUtils(url, new File(Environment.getExternalStorageDirectory(), NEW_VERSION_APK), new ProgressResponseBody.ProgressListener() {
            @Override
            public void onPreExecute(long contentLength) {
                // set the max, change contentLength from b to kb
                progressDialog.setMax((int) (contentLength / 1024));
            }

            @Override
            public void update(long totalBytes, boolean done) {
                // set the progress, change contentLength from b to kb
                progressDialog.setProgress((int) (totalBytes / 1024));
                // download success
                if (done) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.cancel();// if not call, will cause "Activity has leaked window"
                            Toast.makeText(SplashActivity.this, R.string.success_to_download, Toast.LENGTH_SHORT).show();
                            // install apk
                            try {
                                installNewVersion(new File(Environment.getExternalStorageDirectory(), NEW_VERSION_APK));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                                Toast.makeText(SplashActivity.this, R.string.failed_to_install_new_version, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SplashActivity.this, R.string.failed_to_download, Toast.LENGTH_SHORT).show();
                        startMainActivity();
                    }
                });
            }
        });
        downloadUtils.download(0L);

    }

    /**
     * start the main activity
     */
    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    /**
     * get the version info in gradle and set to TextView
     */
    private void getAppVersionInfo() {
//        TextView tvVersionName = (TextView) findViewById(R.id.tv_splash_version_name);
//        tvVersionName.setText(BuildConfig.VERSION_NAME);
    }

    /**
     * get latest version info
     * the result will show at least ANIMATION_DURATION millisecond.
     * It means that the result will be show until animation finish
     */
    private void getVersionInfo() {
        // get the start time
        final long startTime = System.currentTimeMillis();

        // send request
        Request request = new Request.Builder().get()
                .url(VERSION_URL)
                .build();
        client.newCall(request).enqueue(new Callback() {
            // failed
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SplashActivity.this, R.string.failed_to_connect_internet, Toast.LENGTH_SHORT).show();
                    }
                });
                // wait for animation and check version
                waitForAnimationFinishAndCheckVersion(startTime, null);
            }

            // parse json
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonString = response.body().string();
                System.out.println(jsonString);
                Gson gson = new Gson();
                VersionBean latestVersionInfo = null;
                try {
                    latestVersionInfo = gson.fromJson(jsonString, VersionBean.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SplashActivity.this, R.string.json_parse_error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                System.out.println(latestVersionInfo);
                // wait for animation and check version
                waitForAnimationFinishAndCheckVersion(startTime, latestVersionInfo);
            }
        });
    }

    /**
     * wait for animation finish.
     * Need run on child thread
     * and check version will run on ui thread
     *
     * @param startTime         the time at the begin of getVersionInfo
     * @param latestVersionInfo the latest version bean
     */
    private void waitForAnimationFinishAndCheckVersion(final long startTime, final VersionBean latestVersionInfo) {
        // wait for animation finish
        long endTime = System.currentTimeMillis();
        long interval = ANIMATION_DURATION + LOGO_SHOW_TIME - (endTime - startTime);
        if (interval > 0) {
            SystemClock.sleep(interval);
        }

        // check version
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                checkVersion(latestVersionInfo);
            }
        });

    }

    /**
     * set animation
     * alpha freom 0 to 1 in 3s
     */
    private void setAnimation() {
        // alpha
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(ANIMATION_DURATION);
//        alphaAnimation.setFillAfter(true);
        // rotate
        RotateAnimation rotateAnimation = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(ANIMATION_DURATION);
//        rotateAnimation.setFillAfter(true);
        // scale
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(ANIMATION_DURATION);
//        scaleAnimation.setFillAfter(true);

        // animation set
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(alphaAnimation);
        animationSet.addAnimation(rotateAnimation);
        animationSet.addAnimation(scaleAnimation);

        // set animation listener
        // when animation finish, start main activity if autoUpdate equal false
        if(!autoUpdate) {
            animationSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    System.out.println("onAnimationEnd:startMainActivity");
                    // wait for LOGO_SHOW_TIME then start main activity
                    new Thread(){
                        @Override
                        public void run() {
                            SystemClock.sleep(LOGO_SHOW_TIME);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    startMainActivity();
                                }
                            });
                        }
                    }.start();
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }

        // get root view of activity
        View vRoot = findViewById(R.id.activity_splash);
        // start animation
        vRoot.startAnimation(animationSet);
    }


}
