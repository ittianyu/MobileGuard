package com.ittianyu.mobileguard.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.activity.base.BaseActivityNoActionBar;
import com.ittianyu.mobileguard.domain.GridViewItemBean;
import com.ittianyu.mobileguard.engine.ProcessManagerEngine;
import com.ittianyu.mobileguard.engine.ServiceManagerEngine;
import com.ittianyu.mobileguard.strategy.maingridmenu.AdvancedToolsScheme;
import com.ittianyu.mobileguard.strategy.maingridmenu.AntivirusScheme;
import com.ittianyu.mobileguard.strategy.maingridmenu.CleanCacheScheme;
import com.ittianyu.mobileguard.strategy.maingridmenu.MsgSafeScheme;
import com.ittianyu.mobileguard.strategy.maingridmenu.PhoneSafeScheme;
import com.ittianyu.mobileguard.strategy.maingridmenu.ProgressManagerScheme;
import com.ittianyu.mobileguard.strategy.maingridmenu.SettingScheme;
import com.ittianyu.mobileguard.strategy.maingridmenu.SoftwareManagerScheme;
import com.ittianyu.mobileguard.strategy.maingridmenu.TrafficStatsCountScheme;

import java.util.Timer;
import java.util.TimerTask;

/**
 * main activity
 * extend BaseActivity
 */
public class MainActivity extends BaseActivityNoActionBar {
    // constant
    private static final long PERIOD = 3000;
//    private static final int DURATION_ROTATE = 3000;

    // view
    private GridView gvMenu;
//    private ImageView ivLightEffect;
    private View rlCircle;
    private TextView tvRamUseRate;

    // data
    private GridViewItemBean[] items;
    private GridViewAdapter adapter = new GridViewAdapter();
    private Typeface typeface;
    private Timer timer = new Timer(true);// used for refresh RAM use rate

    /**
     * 1
     * init view
     */
    protected void initView() {
        setContentView(R.layout.activity_main);
        // bind
        gvMenu = (GridView) findViewById(R.id.gv_menu);
//        ivLightEffect = (ImageView) findViewById(R.id.iv_light_effect);
        rlCircle = findViewById(R.id.rl_circle);
        tvRamUseRate = (TextView) findViewById(R.id.tv_ram_use_rate);

        // start animation
        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_forever);
        rlCircle.startAnimation(rotateAnimation);

//        Animation blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink_forever);
//        ivLightEffect.startAnimation(blinkAnimation);
    }

    /**
     * 2
     * init date
     */
    @Override
    protected void initData() {
        // load font
        typeface = Typeface.createFromAsset(getAssets(), "fonts/ping_fang_light.ttf");
        tvRamUseRate.setTypeface(typeface);

        // create items
        items = new GridViewItemBean[]{
                new GridViewItemBean(R.drawable.ic_phone_safe, R.string.phone_security, new PhoneSafeScheme()),
                new GridViewItemBean(R.drawable.ic_msg_safe, R.string.msg_safe, new MsgSafeScheme()),
                new GridViewItemBean(R.drawable.ic_software_manager, R.string.software_manager, new SoftwareManagerScheme()),
                new GridViewItemBean(R.drawable.ic_advanced_tools, R.string.advanced_tools, new AdvancedToolsScheme()),
                new GridViewItemBean(R.drawable.ic_traffic_stats, R.string.traffic_stats, new TrafficStatsCountScheme()),
                new GridViewItemBean(R.drawable.ic_progress_manager, R.string.process_manager, new ProgressManagerScheme()),
                new GridViewItemBean(R.drawable.ic_antivirus, R.string.anti_virus, new AntivirusScheme()),
                new GridViewItemBean(R.drawable.ic_setting, R.string.setting, new SettingScheme()),
                new GridViewItemBean(R.drawable.ic_clean_cache, R.string.clean_cache, new CleanCacheScheme())
        };
        // set adapter
        gvMenu.setAdapter(adapter);

        // refresh RAM use rate
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                long freeMemory = ProcessManagerEngine.getFreeMemory(MainActivity.this);
                long totalMemory = ProcessManagerEngine.getTotalMemory();
                float rate = 1 - (float)((double)freeMemory / totalMemory);
                final String useRate = String.format("%1$.0f", rate * 100);
//                System.out.println(useRate);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvRamUseRate.setText(useRate);
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, PERIOD);

        // request permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            startActivity(new Intent(this, PermissionActivity.class));
            return;
        }

        // check all services when start the app
        ServiceManagerEngine.checkAndAutoStart(this);
    }

    /**
     * 3
     * init event
     */
    protected void initEvent() {
        gvMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                items[position].getScheme().onSelected(MainActivity.this);
            }
        });
    }

    /**
     * stop timer
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }

    /**
     * gv_menu adapter
     */
    private class GridViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GridViewItemBean item = items[position];
            View view = View.inflate(MainActivity.this, R.layout.item_main_gv_menu, null);
            ImageView ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
            TextView tvName = (TextView) view.findViewById(R.id.tv_name);
            ivIcon.setImageResource(item.getIconId());
            tvName.setText(item.getNameId());
            tvName.setTypeface(typeface);
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
