package com.ittianyu.mobileguard.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.activity.base.BaseActivityUpEnable;
import com.ittianyu.mobileguard.constant.Constant;
import com.ittianyu.mobileguard.dao.AntivirusDao;
import com.ittianyu.mobileguard.domain.AppInfoBean;
import com.ittianyu.mobileguard.domain.VirusBean;
import com.ittianyu.mobileguard.engine.AppManagerEngine;
import com.ittianyu.mobileguard.utils.EncryptionUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Using the Kingsoft antivirus database
 * You need replace the antivirus.db if you want to use in commerce
 */
public class AntivirusActivity extends BaseActivityUpEnable {
    // view
    private ImageView ivScan;
    private TextView tvUseTime;
    private LinearLayout llLog;
    private ProgressBar pbProgress;

    // data
    private Thread initDataThread;
    private int useTime = 0;// use time in second
    private Timer timer;
    private int progress = 0;
    private boolean running;
    private ArrayList<VirusBean> viruses = new ArrayList<>();
    /**
     * construct method. set the action bar title
     */
    public AntivirusActivity() {
        super(R.string.anti_virus);
    }


    /**
     * 1
     */
    @Override
    protected void initView() {
        setContentView(R.layout.activity_antivirus);
        // bind view
        ivScan = (ImageView) findViewById(R.id.iv_scan);
        tvUseTime = (TextView) findViewById(R.id.tv_use_time);
        llLog = (LinearLayout) findViewById(R.id.ll_log);
        pbProgress = (ProgressBar) findViewById(R.id.pb_progress);


        // start a rotate animation
        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_forever);
        rotateAnimation.setDuration(2000);
        ivScan.startAnimation(rotateAnimation);
    }

    /**
     * 2
     */
    @Override
    protected void initData() {
        running = true;

        // start timer to count time
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        useTime++;
                        tvUseTime.setText(formatTime(useTime));
                    }
                });
            }
        };
        timer = new Timer(true);
        timer.schedule(timerTask, 1000, 1000);


        // start a thead to scan app
        initDataThread = new Thread() {
            @Override
            public void run() {
                List<AppInfoBean> apps = AppManagerEngine.getInstalledAppInfo(AntivirusActivity.this, null);
                // set progress max value
                pbProgress.setMax(apps.size());

                AntivirusDao dao = new AntivirusDao(AntivirusActivity.this);
                for (final AppInfoBean app: apps) {
                    if(!running) {// safe exit
                        return;
                    }
                    try {
                        String md5 = EncryptionUtils.md5File(new File(app.getApkPath()));
                        final VirusBean virusBean = dao.selectByMd5(md5);
                        // set progress
                        progress++;
                        pbProgress.setProgress(progress);

                        // add a TextView to llLog
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // create TextView and add to llLog
                                TextView textView = new TextView(AntivirusActivity.this);
                                textView.setText(app.getName());
                                llLog.addView(textView, 0);
                                // check whether is virus
                                if(null != virusBean) {
                                    // the app is virus
                                    // set the text color red
                                    textView.setTextColor(Color.RED);
                                }
                            }
                        });

                        // add virus to list
                        if(null != virusBean) {
                            virusBean.setPackageName(app.getPackageName());
                            viruses.add(virusBean);
                        }

                        // enhance the user feeling
                        SystemClock.sleep(new Random().nextInt(1024));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // cancel the timer when complete scan apps
                timer.cancel();

                // start result activity
                startResultActivity();

            }
        };
        initDataThread.start();

    }

    /**
     * start result activity
     */
    private void startResultActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(AntivirusActivity.this, AntivirusResultActivity.class);
                intent.putExtra(Constant.EXTRA_VIRUSES, viruses);
                startActivity(intent);
                finish();
            }
        });

    }

    /**
     * 3
     */
    @Override
    protected void initEvent() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // prevent memory leak
        // if the activity exit, the child thread should stop
        running = false;
        // cancel the timer
        timer.cancel();
    }

    /**
     * transfer the second to minute:second format.
     * @param second the time in second
     * @return a format String. Such as 05:30
     */
    private String formatTime(int second) {
        int minute = second / 60;
        second = second % 60;
        return String.format("%02d:%02d", minute, second);
    }
}
