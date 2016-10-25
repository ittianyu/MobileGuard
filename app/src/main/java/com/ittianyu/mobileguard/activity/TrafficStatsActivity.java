package com.ittianyu.mobileguard.activity;

import android.net.TrafficStats;
import android.text.format.Formatter;
import android.widget.TextView;

import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.activity.base.BaseActivityUpEnable;

/**
 * show the user traffic stats
 */
public class TrafficStatsActivity extends BaseActivityUpEnable {
    // view
    private TextView tvTotalTrafficStats;
    private TextView tvTotalTrafficStatsSum;
    private TextView tvMobileTrafficStats;
    private TextView tvMobileTrafficStatsSum;

    /**
     * construct method. set the action bar title
     */
    public TrafficStatsActivity() {
        super(R.string.traffic_stats);
    }

    /**
     * change the default call
     */
    @Override
    protected void init() {
        initView();
    }

    /**
     * 1
     */
    @Override
    protected void initView() {
        setContentView(R.layout.activity_traffic_stats);
        // bind view
        tvTotalTrafficStats = (TextView) findViewById(R.id.tv_total_traffic_stats);
        tvTotalTrafficStatsSum = (TextView) findViewById(R.id.tv_total_traffic_stats_sum);
        tvMobileTrafficStats = (TextView) findViewById(R.id.tv_mobile_traffic_stats);
        tvMobileTrafficStatsSum = (TextView) findViewById(R.id.tv_mobile_traffic_stats_sum);

    }

    /**
     * init data
     */
    @Override
    protected void initData() {
        long totalRxBytes = TrafficStats.getTotalRxBytes();
        long totalTxBytes = TrafficStats.getTotalTxBytes();
        long mobileRxBytes = TrafficStats.getMobileRxBytes();
        long mobileTxBytes = TrafficStats.getMobileTxBytes();

        long totalBytes = totalRxBytes + totalTxBytes;
        long mobileBytes = mobileRxBytes + mobileTxBytes;

        tvTotalTrafficStatsSum.setText(getString(R.string.total_traffic_stats_sum, Formatter.formatFileSize(this, totalBytes)));
        tvMobileTrafficStatsSum.setText(getString(R.string.mobile_traffic_stats_sum, Formatter.formatFileSize(this, mobileBytes)));
        tvTotalTrafficStats.setText(getString(R.string.traffic_stats_upload_download, Formatter.formatFileSize(this, totalTxBytes), Formatter.formatFileSize(this, totalRxBytes)));
        tvMobileTrafficStats.setText(getString(R.string.traffic_stats_upload_download, Formatter.formatFileSize(this, mobileTxBytes), Formatter.formatFileSize(this, mobileRxBytes)));

    }

    /**
     *
     */
    @Override
    protected void initEvent() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }
}
