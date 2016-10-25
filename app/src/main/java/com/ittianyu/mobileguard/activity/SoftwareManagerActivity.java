package com.ittianyu.mobileguard.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.activity.base.BaseActivityUpEnableWithMenu;
import com.ittianyu.mobileguard.domain.AppInfoBean;
import com.ittianyu.mobileguard.engine.AppManagerEngine;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.RootToolsException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;


/**
 * Managing all installed app. Include system app and user app.
 */
public class SoftwareManagerActivity extends BaseActivityUpEnableWithMenu {
    // view
    private TextView tvRomFreeSpace;
    private TextView tvSdCardFreeSpace;
    private TextView tvTypeLabel;
    private ListView lvApp;
    private ProgressBar pbLoading;

    // data
    private List<AppInfoBean> systemApps = new ArrayList<>();
    private List<AppInfoBean> userApps = new ArrayList<>();
    private List<Boolean> checkeds = new ArrayList<>();// represent which item was checked
    private AppAdapter adapter = new AppAdapter();
    private AppRemovedReceiver uninstallReceiver = new AppRemovedReceiver();

    // logic
    private boolean root;

    // thread operation
    private Thread initDateThread;

    /**
     * construct method
     */
    public SoftwareManagerActivity() {
        super(R.string.software_manager, R.menu.menu_software_manager);
    }

    /**
     * 1
     */
    @Override
    protected void initView() {
        setContentView(R.layout.activity_software_manager);
        // bind view
        tvRomFreeSpace = (TextView) findViewById(R.id.tv_rom_free_sapce);
        tvSdCardFreeSpace = (TextView) findViewById(R.id.tv_sd_card_free_sapce);
        tvTypeLabel = (TextView) findViewById(R.id.tv_type_label);
        lvApp = (ListView) findViewById(R.id.lv_app);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);

        // set adapter. This method must run before initData
        lvApp.setAdapter(adapter);
    }

    /**
     * 2
     */
    @Override
    protected void initData() {
        // check whether root
        root = RootTools.isRootAvailable();

        // get free space
        long romFreeSpace = AppManagerEngine.getRomFreeSpace();
        long sdCardFreeSpace = AppManagerEngine.getSdCardFreeSpace();
        // set space info to TextView
        tvRomFreeSpace.setText(getString(R.string.rom_free_space) + Formatter.formatFileSize(this, romFreeSpace));
        tvSdCardFreeSpace.setText(getString(R.string.sd_card_free_space) + Formatter.formatFileSize(this, sdCardFreeSpace));

        // if the thread is running, no need to start a new thread
        if (null != initDateThread && initDateThread.isAlive()) {
            return;
        }
        // start a new thread
        // the following operator are time consuming, need run on child thread
        initDateThread = new Thread() {
            @Override
            public void run() {
                // get apps info
                AppManagerEngine.getInstalledAppInfo(
                        SoftwareManagerActivity.this,
                        new AppManagerEngine.AppInfoListener() {
                            @Override
                            public void onGetInfoCompleted(List<AppInfoBean> apps) {
                                initAppsInfo(apps);
                            }
                        });
            }
        };

        initDateThread.start();
    }

    /**
     * init apps info
     *
     * @param apps
     */
    private void initAppsInfo(final List<AppInfoBean> apps) {
        // change data and notify need run on the same handler
        // prevent Exception "The content of the adapter has changed but ListView did not receive a notification."
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // clear list before add
                systemApps.clear();
                userApps.clear();
                checkeds.clear();
                // add to list
                for (AppInfoBean app : apps) {
                    if (app.isSystemApp()) {
                        systemApps.add(app);
                    } else {
                        userApps.add(app);
                    }
                    checkeds.add(false);
                }
                // because of two label, so need more
                checkeds.add(false);
                checkeds.add(false);
                // refresh ui
                onDataChanged();
            }
        });
    }

    /**
     * It will notify ListView and progress bar refresh
     */
    private void onDataChanged() {
        // refresh ListView
        adapter.notifyDataSetChanged();
        // hide progress bar
        pbLoading.setVisibility(View.GONE);
        // show type label
        tvTypeLabel.setVisibility(View.VISIBLE);
    }

    /**
     * 3
     */
    @Override
    protected void initEvent() {
        // set scroll listener to implement type label change text
        lvApp.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // prevent the data haven't loaded
                if (0 == userApps.size() || 0 == systemApps.size())
                    return;
                // the system app label is at first or above the screen
                if (firstVisibleItem >= userApps.size() + 1) {
                    // change the type label to system app
                    tvTypeLabel.setText(getString(R.string.system_app) + "(" + systemApps.size() + ")");
                } else {
                    // change the type label to user app
                    tvTypeLabel.setText(getString(R.string.user_app) + "(" + userApps.size() + ")");
                }
            }
        });
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

        // register app removed receiver
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(uninstallReceiver, filter);
    }

    /**
     * unregister app removed receiver
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(uninstallReceiver);
    }

    /**
     * when receiver the app uninstalled, notify refresh ListView
     */
    private class AppRemovedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            initData();
            System.out.println("AppRemovedReceiver");
        }
    }

    /**
     * app adapter
     */
    private class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // +2: because of the two tips label
            return systemApps.size() + userApps.size() + 2;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Context context = SoftwareManagerActivity.this;
            // the first position is user label
            if (0 == position) {
                TextView tv = (TextView) View.inflate(context, R.layout.item_software_manager_type_label_lv, null);
                tv.setText(getString(R.string.user_app) + "(" + userApps.size() + ")");
                return tv;
            }
            // the userApps.size() + 1 position is system label
            if (userApps.size() + 1 == position) {
                TextView tv = (TextView) View.inflate(context, R.layout.item_software_manager_type_label_lv, null);
                tv.setText(getString(R.string.system_app) + "(" + systemApps.size() + ")");
                return tv;
            }
            // other position is app view
            AppItem item = null;
            View view = null;
            // check cache
            if (null == convertView || convertView instanceof TextView) {
                // no cache, create a new view
                view = View.inflate(context, R.layout.item_software_manager_app_lv, null);
                // create item and cache it
                item = new AppItem();
                view.setTag(item);
                // bind child view
                item.tvTitle = (TextView) view.findViewById(R.id.tv_title);
                item.tvSize = (TextView) view.findViewById(R.id.tv_size);
                item.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
                item.cb = (CheckBox) view.findViewById(R.id.cb);

            } else {
                // have cache and the view is not TextView
                view = convertView;
                item = (AppItem) view.getTag();
            }
            // get AppBean
            AppInfoBean bean = this.getItem(position);

            // set value
            item.ivIcon.setImageDrawable(bean.getIcon());
            item.tvTitle.setText(bean.getName());
            item.tvSize.setText(Formatter.formatFileSize(context, bean.getSize()));

            // when app is user app or system has root, need set cb enable and set checked and listener
            if (!bean.isSystemApp() || root) {
                item.cb.setEnabled(true);
                item.cb.setChecked(checkeds.get(position));// update checked
                // set checkbox listener, can't use checked change event, it would effect setChecked
                item.cb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        checkeds.set(position, cb.isChecked());
                        System.out.println("position:" + position + " isChecked:" + cb.isChecked());
                    }
                });
            } else {
                item.cb.setEnabled(false);
                item.cb.setChecked(false);// system app can't uninstall
            }

            return view;
        }

        /**
         * the the item bean
         *
         * @param position
         * @return return item bean if success, null otherwise
         */
        @Override
        public AppInfoBean getItem(int position) {
            AppInfoBean bean = null;
            // the type label, return null
            if (0 == position || userApps.size() + 1 == position)
                return bean;

            if (position < userApps.size() + 1) {
                // user apps
                bean = userApps.get(position - 1);// for example, position is 4, then the bean position should be 3
            } else {
                // system apps
                /*
                0 user app
                1 up1
                2 up2
                3 system app
                4 sp1
                5 sp2
                 */
                bean = systemApps.get(position - userApps.size() - 2);// for example, user apps count is 2, position is 5, then bean position is 1
            }
            return bean;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

    /**
     * menu click event
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.m_select_all:
                selectAllItem();
                break;
            case R.id.m_cancel_all:
                cancelAllItem();
                break;
            case R.id.m_uninstall:
                onUninstallSelectedApp();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * query user whether uninstall selected app
     */
    private void onUninstallSelectedApp() {
        // create dialog
        new AlertDialog.Builder(this)
                .setTitle(R.string.tips)
                .setMessage(R.string.message_uninstall_app)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        uninstallSelectedApp();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    /**
     * uninstall selected apps
     */
    private void uninstallSelectedApp() {
        for (int i = 1; i < checkeds.size(); i++) {
            // if not checked, run next
            if (!checkeds.get(i)) {
                continue;
            }
            // get app info
            AppInfoBean app = (AppInfoBean) lvApp.getItemAtPosition(i);
            if (null == app)
                continue;
            System.out.println(app.getName());
            System.out.println(app.getApkPath());
            // if system app and system have been root
            if (app.isSystemApp() && root) {
                // need to apply root access
                try {
                    if (!RootTools.isAccessGiven())
                        continue;
                    // change system dir access
                    RootTools.sendShell("mount -o remount rw /system", 10000);
                    // delete apk file
                    RootTools.sendShell("rm -r " + app.getApkPath(), 10000);
                    // restore system dir access
                    RootTools.sendShell("mount -o remount r /system", 10000);
                } catch (TimeoutException e) {
                    e.printStackTrace();
                } catch (RootToolsException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                continue;
            }
            // uninstall user app
            Intent intent = new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + app.getPackageName()));
            startActivity(intent);
        }

    }

    /**
     * cancel all ListView item
     */
    private void cancelAllItem() {
        for (int i = 1; i < checkeds.size(); i++) {
            checkeds.set(i, false);
        }
        // refresh ui
        adapter.notifyDataSetChanged();
    }

    /**
     * select all ListView item
     */
    private void selectAllItem() {
        // set checked
        if (root) {
            // it can select all when root
            for (int i = 1; i < checkeds.size(); i++) {
                checkeds.set(i, true);
            }
        } else {
            // can only select user app
            for (int i = 1; i <= userApps.size(); i++) {
                checkeds.set(i, true);
            }
        }
        // refresh ui
        adapter.notifyDataSetChanged();
    }

    /**
     * @return the count of checked item
     */
    private int getCheckedItemCount() {
        int count = 0;
        for (int i = 1; i < checkeds.size(); i++) {
            if (i == userApps.size() + 1)
                continue;
            if (checkeds.get(i))
                count++;
        }
        return count;
    }

    /**
     * Will be call before shown
     * Use it to enable or disable item
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // find items
        MenuItem uninstallItem = menu.findItem(R.id.m_uninstall);
        MenuItem selectAllItem = menu.findItem(R.id.m_select_all);
        MenuItem cancelAllItem = menu.findItem(R.id.m_cancel_all);
        // get checked item count
        int checkedCount = getCheckedItemCount();
        if (0 == checkedCount) {
            // no checked item, disable it
            uninstallItem.setEnabled(false);
            cancelAllItem.setEnabled(false);
        } else {
            // enable it
            uninstallItem.setEnabled(true);
            cancelAllItem.setEnabled(true);
        }
        // if all item are checked, no need enable select all
        if (checkedCount == checkeds.size()) {
            selectAllItem.setEnabled(false);
        } else {
            selectAllItem.setEnabled(true);
        }

        return true;
    }
}

/**
 * just used for cache child view of AppListView Item
 */
class AppItem {
    ImageView ivIcon;
    TextView tvTitle;
    TextView tvSize;
    CheckBox cb;
}