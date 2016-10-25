package com.ittianyu.mobileguard.activity;

import android.content.Intent;
import android.net.Uri;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.activity.base.BaseActivityUpEnable;
import com.ittianyu.mobileguard.domain.AppInfoBean;
import com.ittianyu.mobileguard.engine.AppManagerEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * clear cache activity
 */
public class CleanCacheActivity extends BaseActivityUpEnable {
    // view
    private TextView tvResult;
    private ProgressBar pbProgress;
    private ListView lvApp;
    private Button btnOk;

    // data
    private List<AppInfoBean> apps = new ArrayList<>();
    private AppAdapter adapter;

    /**
     * construct method. set the action bar title
     */
    public CleanCacheActivity() {
        super(R.string.clean_cache);
    }

    /**
     * 1
     */
    @Override
    protected void initView() {
        setContentView(R.layout.activity_clean_cache);
        // bind view
        tvResult = (TextView) findViewById(R.id.tv_result);
        pbProgress = (ProgressBar) findViewById(R.id.pb_progress);
        lvApp = (ListView) findViewById(R.id.lv_app);
        btnOk = (Button) findViewById(R.id.btn_ok);

        // hide view
        tvResult.setVisibility(View.GONE);
        btnOk.setVisibility(View.GONE);

    }

    /**
     * 2
     */
    @Override
    protected void initData() {
        // these operation are time consuming, need run on child thread
        new Thread() {
            @Override
            public void run() {
                // get all apps info
                AppManagerEngine.getInstalledAppInfo(CleanCacheActivity.this, new AppManagerEngine.AppInfoListener() {
                    @Override
                    public void onGetInfoCompleted(List<AppInfoBean> apps) {
                        onDataGetCompleted(apps);
                    }
                });
            }
        }.start();


    }

    /**
     * filter the no cache app and show list
     *
     * @param apps
     */
    private void onDataGetCompleted(final List<AppInfoBean> apps) {
        // change list data need run on the same handler with adapter notify
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // filter no cache app
                for (AppInfoBean app : apps) {
                    if (app.getCacheSize() > 0) {
                        CleanCacheActivity.this.apps.add(app);
                    }
                }
                // hide progress
                pbProgress.setVisibility(View.GONE);

                // check the count of cache app
                // if no app has cache
                if (0 == CleanCacheActivity.this.apps.size()) {
                    // show no cache data and set event
                    tvResult.setVisibility(View.VISIBLE);
                    lvApp.setVisibility(View.GONE);
                    btnOk.setVisibility(View.VISIBLE);
                    // just finish when click button
                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                    return;
                }

                // have apps

                // set adapter
                adapter = new AppAdapter();
                lvApp.setAdapter(adapter);

                // hide other view
                tvResult.setVisibility(View.GONE);
                lvApp.setVisibility(View.VISIBLE);
                btnOk.setVisibility(View.VISIBLE);
                // change button text
                btnOk.setText(R.string.clear_right_now);
                // clear cache when click button
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clearAllCache();
                    }
                });
            }
        });
    }

    /**
     * clear all cache in apps
     */
    private void clearAllCache() {
        AppManagerEngine.clearAllCache(this, new AppManagerEngine.ClearCacheListener() {
            @Override
            public void onClearCompleted() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CleanCacheActivity.this.onClearCompleted();
                    }
                });
            }

            @Override
            public void onClearFailed() {
                CleanCacheActivity.this.onClearFailed();
            }
        });
    }

    /**
     * 3
     */
    @Override
    protected void initEvent() {
        // set on item click listener for starting app info activity
        lvApp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppInfoBean bean = (AppInfoBean) lvApp.getItemAtPosition(position);
//                System.out.println(bean.getName());
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS",
                        Uri.parse("package:" + bean.getPackageName()));
                startActivity(intent);
            }
        });

    }

    /**
     * will be call when clear cache completed
     */
    private void onClearCompleted() {
        // change the button text and event
        btnOk.setText(R.string.ok);
        // finish when click button
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // show tips
        long totalSize = 0;
        for (AppInfoBean app : apps) {
            totalSize += app.getCacheSize();
        }
        Toast.makeText(this, getString(R.string.tips_clear_cache_completed,
                Formatter.formatFileSize(this, totalSize)),
                Toast.LENGTH_SHORT).show();

        // clear list
        apps.clear();
        adapter.notifyDataSetChanged();
    }

    /**
     * will be call when clear cache failed
     */
    private void onClearFailed() {
        Toast.makeText(this, R.string.tips_failed_to_clear, Toast.LENGTH_SHORT).show();
    }

    /**
     * app adapter
     */
    private class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return apps.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AppItem item = null;
            // if no cache, create a view
            if (null == convertView) {
                convertView = View.inflate(CleanCacheActivity.this, R.layout.item_clean_cache_lv, null);
                // bind view
                item = new AppItem();
                item.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
                item.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                item.tvSize = (TextView) convertView.findViewById(R.id.tv_size);
                // set item
                convertView.setTag(item);
            } else {
                item = (AppItem) convertView.getTag();
            }
            // get value
            AppInfoBean app = getItem(position);
            // set value
            item.ivIcon.setImageDrawable(app.getIcon());
            item.tvTitle.setText(app.getName());
            item.tvSize.setText(Formatter.formatFileSize(CleanCacheActivity.this, app.getCacheSize()));

            return convertView;
        }

        @Override
        public AppInfoBean getItem(int position) {
            return apps.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

    }

    /**
     * just use for AppAdapter
     */
    private static class AppItem {
        private ImageView ivIcon;
        private TextView tvTitle;
        private TextView tvSize;
    }

}
