package com.ittianyu.mobileguard.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.activity.base.BaseActivityUpEnable;
import com.ittianyu.mobileguard.constant.Constant;
import com.ittianyu.mobileguard.domain.VirusBean;

import java.util.ArrayList;

/**
 * show the scan result
 */
public class AntivirusResultActivity extends BaseActivityUpEnable {
    // view
    private TextView tvResult;
    private Button btnResult;
    // data
    private ArrayList<VirusBean> viruses;

    /**
     * construct method. set the action bar title
     */
    public AntivirusResultActivity() {
        super(R.string.anti_virus);
    }


    /**
     * 1
     */
    @Override
    protected void initView() {
        setContentView(R.layout.activity_antivirus_result);
        // bind view
        tvResult = (TextView) findViewById(R.id.tv_result);
        btnResult = (Button) findViewById(R.id.btn_result);

    }

    /**
     * 2
     */
    @Override
    protected void initData() {
        // get list

        viruses = (ArrayList<VirusBean>) getIntent().getSerializableExtra(Constant.EXTRA_VIRUSES);
        // if no viruses, show no virus and set exit event
        if (null == viruses || 0 == viruses.size()) {
            btnResult.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            return;
        }

        // if have viruses, show result and set uninstall event
        tvResult.setText(getString(R.string.found_virus, viruses.size()));
        tvResult.setTextColor(Color.RED);

        btnResult.setText(R.string.clear_right_now);

        // set uninstall listener
        btnResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // clear viruses
                clearViruses();
                // reset btn text and event
                clearFinish();
            }
        });

    }

    /**
     * set the btnResult text to ok and set listener to finish
     */
    private void clearFinish() {
        btnResult.setText(R.string.ok);
        btnResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * uninstall all apps in viruses
     */
    private void clearViruses() {
        // uninstall user app
        for (VirusBean virus : viruses) {
            Intent intent = new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + virus.getPackageName()));
            startActivity(intent);
        }
    }

    /**
     * 3
     */
    @Override
    protected void initEvent() {

    }
}
