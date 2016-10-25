package com.ittianyu.mobileguard.fragment;


import android.view.View;
import android.widget.ImageView;

import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.dao.AppLockDao;
import com.ittianyu.mobileguard.fragment.base.BaseAppLockFragment;

/**
 * a ListView to show unlocked apps
 */
public class LockedAppFragment extends BaseAppLockFragment {
    @Override
    protected boolean isNeededApp(AppLockDao dao, String packageName) {
        // if the db has the app, that means it was an locked app, we need.
        return dao.isExists(packageName);
    }

    @Override
    protected void setEventImageAndListener(ImageView ivEvent, String packageName, int position, View rootView) {
        defaultSetEventImageAndListener(ivEvent, packageName, position, R.drawable.ic_app_unlock, false, rootView);
    }
}
