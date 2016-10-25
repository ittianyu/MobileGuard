package com.ittianyu.mobileguard.fragment.base;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.dao.AppLockDao;
import com.ittianyu.mobileguard.domain.AppInfoBean;
import com.ittianyu.mobileguard.engine.AppManagerEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * a ListView to show apps
 */
public abstract class BaseAppLockFragment extends Fragment {
    // constants
    private static final long DURATION_REMOVE = 200;

    // view
    private ListView lvApp;
    private ProgressBar pvLoading;

    // data
    private List<AppInfoBean> apps = new ArrayList<>();
    private AppAdapter adapter = new AppAdapter();

    // thread operation
    private Thread initDataThread;

    /**
     * inflate view and bind view
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_app_lock, container, false);
        // bind view
        lvApp = (ListView) view.findViewById(R.id.lv_app);
        pvLoading = (ProgressBar) view.findViewById(R.id.pb_loading);

        initEvent();

        return view;
    }

    /**
     * init event
     */
    private void initEvent() {
        lvApp.setAdapter(adapter);
    }

    /**
     * init data
     */
    @Override
    public void onResume() {
        super.onResume();

        initData();
    }

    /**
     * init data
     */
    private void initData() {
        // if the thread is running, no need to start a new thread
        if (null != initDataThread && initDataThread.isAlive()) {
            return;
        }
        // prevent violence click
        final FragmentActivity context = getActivity();
        if (null == context)
            return;
        // start a new thread to init data
        initDataThread = new Thread() {
            @Override
            public void run() {
                // get all apps
                List<AppInfoBean> apps = AppManagerEngine.getInstalledAppInfo(context, null);
                initAppsAndRefreshUi(context, apps);
            }
        };
        initDataThread.start();
    }

    /**
     * distribute the apps and refresh ListView
     *
     * @param context
     * @param appsInfo
     */
    private void initAppsAndRefreshUi(final FragmentActivity context, final List<AppInfoBean> appsInfo) {
        // refresh ui need run on ui thread
        // and remember the change data should'n in background thread
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // clear data
                apps.clear();
                // distribute apps
                AppLockDao dao = new AppLockDao(context);
                for (AppInfoBean app : appsInfo) {
                    // if in locked list
                    if (isNeededApp(dao, app.getPackageName())) {
                        // add to locked list
                        apps.add(app);
                    }
                }
                // notify update ListView
                adapter.notifyDataSetChanged();
                // hide loading bar
                pvLoading.setVisibility(View.GONE);
            }
        });
    }


    /**
     * if the app is need app, it will add to list and show
     *
     * @param dao         AppLockDao
     * @param packageName the package name
     * @return The app will be add to list and show if true
     */
    protected abstract boolean isNeededApp(AppLockDao dao, String packageName);

    /**
     * app adapter
     */
    private class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return apps.size();
        }

        @Override
        public AppInfoBean getItem(int position) {
            return apps.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Context context = getActivity();
            View view = null;
            ViewItem item = null;
            // if no cache
            if (null == convertView) {
                // create view and ViewItem
                view = View.inflate(context, R.layout.item_app_lock_lv, null);
                item = new ViewItem();
                // set view item
                view.setTag(item);
                // bind child view
                item.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
                item.ivEvent = (ImageView) view.findViewById(R.id.iv_unlock);
                item.tvTitle = (TextView) view.findViewById(R.id.tv_title);

            } else {
                view = convertView;
                item = (ViewItem) view.getTag();
            }

            // get item
            final AppInfoBean bean = getItem(position);
            // set  value
            item.tvTitle.setText(bean.getName());
            item.ivIcon.setImageDrawable(bean.getIcon());

            // set image and listener
            setEventImageAndListener(item.ivEvent, bean.getPackageName(), position, view);

            return view;
        }
    }

    /**
     * It will be call when ListView call getView()
     * You can use defaultSetEventImageAndListener() to implement the base function
     *
     * @param ivEvent     the ImageView which in right of item
     * @param packageName the current app package name
     * @param position    the current app position
     * @param rootView    the item root view
     */
    protected abstract void setEventImageAndListener(ImageView ivEvent, final String packageName, final int position, View rootView);

    /**
     * the default scheme for setEventImageAndListener
     *
     * @param ivEvent     the ImageView which in right of item
     * @param packageName the current app package name
     * @param position    the current app position
     * @param imageId     the ivEvent drawable id
     * @param isAdd       It will add to db if true, otherwise remove from db
     * @param rootView    the item root view
     */
    protected void defaultSetEventImageAndListener(final ImageView ivEvent, final String packageName, final int position, int imageId, final boolean isAdd, final View rootView) {
        ivEvent.setImageResource(imageId);

        // set on item click listener for unlocking app
        ivEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int fromX, toX;
                if (isAdd) {
                    // translate to right
                    fromX = 0;
                    toX = 1;
                } else {
                    // translate to left
                    fromX = 0;
                    toX = -1;
                }
                // remove animation
                TranslateAnimation animation = new TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, fromX,// from x
                        Animation.RELATIVE_TO_SELF, toX,// to x
                        Animation.RELATIVE_TO_SELF, 0,// from y
                        Animation.RELATIVE_TO_SELF, 0);// to y
                animation.setDuration(DURATION_REMOVE);
                // register listener, call remove method when animation end
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // if success to lock
                        apps.remove(position);
                        // refresh ui
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                AppLockDao dao = new AppLockDao(getActivity());
                boolean isSuccess = false;
                if (isAdd) {
                    isSuccess = dao.insert(packageName);
                } else {
                    isSuccess = dao.delete(packageName);
                }
                // if success, start animation
                if(isSuccess) {
                    // start animation and it will remove the root view when animation end
                    rootView.startAnimation(animation);
                }
            }
        });
    }

    /**
     * used for cache child view
     */
    private static class ViewItem {
        private ImageView ivIcon;
        private TextView tvTitle;
        private ImageView ivEvent;
    }
}
