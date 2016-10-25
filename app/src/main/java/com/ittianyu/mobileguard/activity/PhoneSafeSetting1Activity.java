package com.ittianyu.mobileguard.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.fastaccess.permission.base.PermissionHelper;
import com.fastaccess.permission.base.callback.OnPermissionCallback;
import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.activity.base.BaseActivityUpEnable;
import com.ittianyu.mobileguard.constant.Constant;
import com.ittianyu.mobileguard.service.PhoneSafeService;
import com.ittianyu.mobileguard.utils.ActivityManagerUtils;
import com.ittianyu.mobileguard.utils.ConfigUtils;

import java.util.Arrays;

/**
 * Phone safe setting activity
 * set the safe phone number
 * save the sim info
 * set phone safe service on
 */
public class PhoneSafeSetting1Activity extends BaseActivityUpEnable implements View.OnClickListener, OnPermissionCallback {
    // constant
    private static final int REQUEST_CONTACTS = 1;
    // view
    private EditText etSafePhone;
    // data
    private PermissionHelper permissionHelper;

    /**
     * set title
     */
    public PhoneSafeSetting1Activity() {
        super(R.string.title_phone_safe_setting1);
    }

    /**
     * 1
     */
    @Override
    protected void initView() {
        setContentView(R.layout.activity_phone_safe_setting1);
        // bind view
        etSafePhone = (EditText) findViewById(R.id.et_safe_phone_number);
    }

    /**
     * 2
     */
    @Override
    protected void initData() {
        etSafePhone.setText(ConfigUtils.getString(this, Constant.KEY_SAFE_PHONE, ""));

        permissionHelper = PermissionHelper.getInstance(this);
    }

    /**
     * 3
     */
    @Override
    protected void initEvent() {
        findViewById(R.id.btn_next).setOnClickListener(this);
        findViewById(R.id.btn_select_from_contact).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                onFinish();
                break;
            case R.id.btn_select_from_contact:
                onSelectFromContact();
                break;

        }
    }
    /**
     * start a new activity and finish self
     * @param activity
     */
    protected void startActivity(Class<? extends Activity> activity) {
        startActivity(new Intent(this, activity));
        finish();
    }

    /**
     * on select from contact.
     * It will start a list activity, and wait for result.
     */
    private void onSelectFromContact() {
        startActivityForResult(new Intent(this, ContactsActivity.class), REQUEST_CONTACTS);
    }

    /**
     * When the ContactsActivity finish, it will be call.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CONTACTS == requestCode) {
            if (data != null) {
                String safePhone = data.getStringExtra(Constant.KEY_CONTACT_PHONE);
                etSafePhone.setText(safePhone);
            }
        }
    }

    /**
     * On setting finish
     */
    private void onFinish() {
        System.out.println("onFinish");
        // request permission
        permissionHelper.request(Manifest.permission.READ_PHONE_STATE);
    }

    /**
     * save the safe phone and sim info
     */
    private void saveConfig() {
        // get safe phone
        String safePhone = etSafePhone.getText().toString().trim();
        if(TextUtils.isEmpty(safePhone)) {
            Toast.makeText(this, R.string.safe_phone_can_not_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        // save safe phone to file
        ConfigUtils.putString(this, Constant.KEY_SAFE_PHONE, safePhone);

        // get sim info
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String simSerialNumber = manager.getSimSerialNumber();
        // save sim info
        ConfigUtils.putString(this, Constant.KEY_SIM_INFO, simSerialNumber);

        // set service on in config
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean(Constant.KEY_CB_PHONE_SAFE, true)
                .putBoolean(Constant.KEY_CB_BIND_SIM, true)
                .commit();
        // start the service
        ActivityManagerUtils.checkService(this, PhoneSafeService.class);

        // goto PhoneSafeActivity
        startActivity(PhoneSafeActivity.class);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // permission callback start
    @Override
    public void onPermissionGranted(@NonNull String[] permissionName) {
        System.out.println("onPermissionGranted:" + Arrays.toString(permissionName));
        saveConfig();
    }

    @Override
    public void onPermissionDeclined(@NonNull String[] permissionName) {
        Toast.makeText(this, R.string.no_permission, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionPreGranted(@NonNull String permissionsName) {
        System.out.println("onPermissionPreGranted:" + permissionsName);
        onPermissionGranted(new String[]{permissionsName});
    }

    @Override
    public void onPermissionNeedExplanation(@NonNull String permissionName) {
        System.out.println("onPermissionNeedExplanation:" + permissionName);
    }

    @Override
    public void onPermissionReallyDeclined(@NonNull String permissionName) {
        System.out.println("onPermissionReallyDeclined:" + permissionName);
    }

    @Override
    public void onNoPermissionNeeded() {
        onPermissionGranted(null);
    }
    // permission callback end

}
