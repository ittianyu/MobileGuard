package com.ittianyu.mobileguard.activity;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.Toast;

import com.fastaccess.permission.base.activity.BasePermissionActivity;
import com.fastaccess.permission.base.model.PermissionModel;
import com.fastaccess.permission.base.model.PermissionModelBuilder;
import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.engine.ServiceManagerEngine;
import com.ittianyu.mobileguard.utils.WindowsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * request permission if the system version >= 6.0
 */
public class PermissionActivity extends BasePermissionActivity {
    private List<PermissionModel> permissions = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // add all needed permission. need run before super.onCreate
        addPermissions();

        super.onCreate(savedInstanceState);

        // check whether granted
        isPermissionsGranted();

        // set no action bar
        WindowsUtils.hideActionBar(this);
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        System.out.println(permissionHelper);
//
//    }

    /**
     * add all needed permission
     */
    private void addPermissions(){
        /*
group:android.permission-group.LOCATION
    permission:android.permission.ACCESS_FINE_LOCATION
    permission:android.permission.ACCESS_COARSE_LOCATION
 */
        permissions.add(PermissionModelBuilder.withContext(this)
                .withCanSkip(false)
                .withPermissionName(Manifest.permission.ACCESS_FINE_LOCATION)
                .withTitle(R.string.title_location)
                .withMessage(R.string.message_location)
                .withExplanationMessage(R.string.explanation_message)
                .withLayoutColorRes(R.color.colorPrimary)
                .withImageResourceId(R.drawable.permission_three)
                .build());

/*
group:android.permission-group.STORAGE
    permission:android.permission.READ_EXTERNAL_STORAGE
    permission:android.permission.WRITE_EXTERNAL_STORAGE
 */
        permissions.add(PermissionModelBuilder.withContext(this)
                .withTitle(R.string.title_storage)
                .withCanSkip(false)
                .withPermissionName(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withMessage(R.string.message_storage)
                .withExplanationMessage(R.string.explanation_message)
                .withLayoutColorRes(R.color.colorAccent)
                .withImageResourceId(R.drawable.permission_two)
                .build());

/*
group:android.permission-group.CONTACTS
    permission:android.permission.WRITE_CONTACTS
    permission:android.permission.READ_CONTACTS
*/
        permissions.add(PermissionModelBuilder.withContext(this)
                .withCanSkip(false)
                .withTitle(R.string.title_contacts)
                .withPermissionName(Manifest.permission.WRITE_CONTACTS)
                .withMessage(R.string.message_contacts)
                .withExplanationMessage(R.string.explanation_message)
                .withLayoutColorRes(R.color.blue)
                .withImageResourceId(R.drawable.permission_one)
                .build());

        // Manifest.permission.SYSTEM_ALERT_WINDOW
        permissions.add(PermissionModelBuilder.withContext(this)
                .withCanSkip(false)
                .withTitle(R.string.title_system_alert_window)
                .withPermissionName(Manifest.permission.SYSTEM_ALERT_WINDOW)
                .withMessage(R.string.message_system_alert_window)
                .withExplanationMessage(R.string.explanation_message)
                .withLayoutColorRes(R.color.colorPrimaryDark)
                .withImageResourceId(R.drawable.permission_two)
                .build());


/*
group:android.permission-group.SMS
    permission:android.permission.READ_SMS
    permission:android.permission.RECEIVE_SMS
    permission:android.permission.SEND_SMS
 */
        permissions.add(PermissionModelBuilder.withContext(this)
                .withCanSkip(false)
                .withTitle(R.string.title_sms)
                .withPermissionName(Manifest.permission.SEND_SMS)
                .withMessage(R.string.message_sms)
                .withExplanationMessage(R.string.explanation_message)
                .withLayoutColorRes(R.color.colorPrimary)
                .withImageResourceId(R.drawable.permission_three)
                .build());


/*
group:android.permission-group.PHONE
    permission:android.permission.READ_CALL_LOG
    permission:android.permission.READ_PHONE_STATE
    permission:android.permission.CALL_PHONE
    permission:android.permission.WRITE_CALL_LOG
    permission:android.permission.PROCESS_OUTGOING_CALLS
    permission:com.android.voicemail.permission.ADD_VOICEMAIL
 */
        permissions.add(PermissionModelBuilder.withContext(this)
                .withCanSkip(false)
                .withTitle(R.string.title_phone)
                .withPermissionName(Manifest.permission.READ_CALL_LOG)
                .withMessage(R.string.message_phone)
                .withExplanationMessage(R.string.explanation_message)
                .withLayoutColorRes(R.color.colorPrimaryDark)
                .withImageResourceId(R.drawable.permission_two).build());

    }

    /**
     * check all permissions is granted
     */
    private void isPermissionsGranted() {
        int count = 0;
        for (PermissionModel permission: permissions) {
            if(permission.getPermissionName().equals(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
                if (permissionHelper.isSystemAlertGranted()) {
                    count++;
                }
            } else if (permissionHelper.isPermissionGranted(permission.getPermissionName())) {
                count++;
            }
        }
        if(count == permissions.size()) {
            // all permissions have granted
            finish();
        }
    }

    @NonNull
    @Override
    protected List<PermissionModel> permissions() {
        return permissions;
    }

    @Override
    protected int theme() {
        return R.style.AppTheme;
    }

    @Override
    protected void onIntroFinished() {
        Toast.makeText(this, R.string.tips_thank_you_for_your_use, Toast.LENGTH_SHORT).show();
        Log.i("onIntroFinished", "Intro has finished");
        // do whatever you like!
        finish();
    }

    @Nullable
    @Override
    protected ViewPager.PageTransformer pagerTransformer() {
        return null;//use default
    }

    @Override
    protected boolean backPressIsEnabled() {
        return false;
    }

    @Override
    protected void permissionIsPermanentlyDenied(@NonNull String permissionName) {
        Log.e("DANGER", "Permission ( " + permissionName + " ) is permanentlyDenied and can only be granted via settings screen");
        /** {@link com.fastaccess.permission.base.PermissionHelper#openSettingsScreen(Context)} can help you to open it if you like */
    }

    @Override
    protected void onUserDeclinePermission(@NonNull String permissionName) {
        Log.w("Warning", "Permission ( " + permissionName + " ) is skipped you can request it again by calling doing such\n " +
                "if (permissionHelper.isExplanationNeeded(permissionName)) {\n" +
                "        permissionHelper.requestAfterExplanation(permissionName);\n" +
                "    }\n" +
                "    if (permissionHelper.isPermissionPermanentlyDenied(permissionName)) {\n" +
                "        /** read {@link #permissionIsPermanentlyDenied(String)} **/\n" +
                "    }");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // check all services when start the app
        ServiceManagerEngine.checkAndAutoStart(this);
    }
}
