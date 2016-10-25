package com.ittianyu.mobileguard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.activity.base.BaseActivityUpEnable;
import com.ittianyu.mobileguard.constant.Constant;
import com.ittianyu.mobileguard.domain.AdvancedToolBean;
import com.ittianyu.mobileguard.strategy.advancedtools.AppLockScheme;
import com.ittianyu.mobileguard.strategy.advancedtools.BackupScheme;
import com.ittianyu.mobileguard.strategy.advancedtools.PhoneLocationScheme;
import com.ittianyu.mobileguard.strategy.advancedtools.RestoreScheme;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * advanced tools activity
 * show a function list
 */
public class AdvancedToolsActivity extends BaseActivityUpEnable {
    private static final int REQUEST_CODE_RESTORE = 1;
    private ListView lvTools;
    private List<AdvancedToolBean> tools = new ArrayList<>();
    private ToolsAdapter adapter = new ToolsAdapter();
    private RestoreScheme restoreScheme;

    /**
     * construct method
     */
    public AdvancedToolsActivity() {
        super(R.string.advanced_tools);
    }

    /**
     * 1
     */
    @Override
    protected void initView() {
        setContentView(R.layout.activity_advanced_tools);
        // bind view
        lvTools = (ListView) findViewById(R.id.lv_tools);


    }

    /**
     * 2
     */
    @Override
    protected void initData() {
        // add phone location query
        tools.add(new AdvancedToolBean(R.drawable.ic_phone_location,
                R.string.phone_location_query,
                R.string.summary_phone_location_query,
                new PhoneLocationScheme()));
        // add backup
        tools.add(new AdvancedToolBean(R.drawable.ic_backup,
                R.string.contacts_sms_backup,
                R.string.summary_contacts_sms_backup,
                new BackupScheme()));

        restoreScheme = new RestoreScheme();
        // add restore
        tools.add(new AdvancedToolBean(R.drawable.ic_restore,
                R.string.contacts_sms_restore,
                R.string.summary_contacts_sms_restore,
                restoreScheme));
        // add app lock
        tools.add(new AdvancedToolBean(R.drawable.ic_app_unlock,
                R.string.app_lock,
                R.string.summary_app_lock,
                new AppLockScheme()));

        // set adapter
        lvTools.setAdapter(adapter);
    }


    /**
     * 3
     */
    @Override
    protected void initEvent() {
        // click listener
        lvTools.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tools.get(position).getClickListener().onSelected(AdvancedToolsActivity.this);
            }
        });
    }

    /**
     * it will be call when select restore
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_RESTORE: {
                if(null == data)
                    return;
                Bundle extras = data.getExtras();
                File file = (File) extras.get(Constant.EXTRA_RESTORE_FILE);
                int which = extras.getInt(Constant.EXTRA_RESTORE_TYPE);
                restoreScheme.startRestore(file, which);

                break;
            }
        }
    }

    /**
     * tools adapter
     */
    private class ToolsAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return tools.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // the list count are few, no need to cache
            // create view
            View view = View.inflate(AdvancedToolsActivity.this, R.layout.item_advanced_tools_lv, null);
            TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
            TextView tvSummary = (TextView) view.findViewById(R.id.tv_summary);
            ImageView ivImage = (ImageView) view.findViewById(R.id.iv_image);
            // get data
            AdvancedToolBean tool = tools.get(position);
            // set value
            tvTitle.setText(tool.getTitleId());
            tvSummary.setText(tool.getSummaryId());
            ivImage.setImageResource(tool.getImageId());

            return view;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }
        @Override
        public long getItemId(int position) {
            return 0;
        }

    }
}
