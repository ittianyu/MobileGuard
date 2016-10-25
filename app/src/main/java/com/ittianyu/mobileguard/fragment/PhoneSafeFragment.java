package com.ittianyu.mobileguard.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.fastaccess.permission.base.PermissionHelper;
import com.fastaccess.permission.base.callback.OnPermissionCallback;
import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.constant.Constant;
import com.ittianyu.mobileguard.receiver.DeviceAdminSampleReceiver;
import com.ittianyu.mobileguard.service.PhoneSafeService;
import com.ittianyu.mobileguard.utils.ConfigUtils;

import java.util.Arrays;

/**
 * Created by yu on.
 * Phone safe fragment.
 * Show the setting list.
 */
public class PhoneSafeFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, OnPermissionCallback {
    // constant
    private static final String KEY_CHANGE_SAFE_PHONE = "pref_change_safe_phone";
    private static final int REQUEST_CODE_DEVICE_ADMIN = 1;
    // data
    private boolean waitForResult = false;
    private PermissionHelper permissionHelper;
    private boolean readPhoneState;

    /**
     * add resource
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_phone_safe);

        permissionHelper = PermissionHelper.getInstance(getActivity(), this);
    }

    /**
     * update KEY_CHANGE_SAFE_PHONE summary
     * register ChangeListener
     */
    @Override
    public void onResume() {
        super.onResume();
        // update summary
        Preference preference = findPreference(KEY_CHANGE_SAFE_PHONE);
        preference.setSummary(getString(R.string.current_safe_phone_number) + ConfigUtils.getString(getActivity(), Constant.KEY_SAFE_PHONE, ""));

        // register listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * unregister ChangeListener
     */
    @Override
    public void onPause() {
        super.onPause();
        // unregister listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * will be call when sp changed
     * @param sharedPreferences
     * @param key
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        boolean value = sharedPreferences.getBoolean(key, false);
        System.out.println("key:" + key + ", value:" + value);
        Context context = getActivity();
        if (key.equals(Constant.KEY_CB_PHONE_SAFE)) {
            Intent intent = new Intent(context, PhoneSafeService.class);
            if (value) {
                // start phone safe service
                context.startService(intent);
            } else {
                // shutdown phone safe service
                context.stopService(intent);
            }
        } else if (key.equals(Constant.KEY_CB_BIND_SIM)) {
            if(waitForResult)
                return;
            if (value) {
                // disable until we're really active
                waitForResult = true;
                CheckBoxPreference cbp = (CheckBoxPreference) findPreference(Constant.KEY_CB_DEVICE_ADMIN);
                cbp.setChecked(false);
                // request permission
                readPhoneState = true;
                permissionHelper.request(Manifest.permission.READ_PHONE_STATE);
            }
        } else if (key.equals(Constant.KEY_CB_DEVICE_ADMIN)) {
            if(waitForResult)
                return;
            ComponentName deviceAdminSample = new ComponentName(context, DeviceAdminSampleReceiver.class);
            if (value) {
                System.out.println("KEY_CB_DEVICE_ADMIN enable");
                // Launch the activity to have the user enable our admin.
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, deviceAdminSample);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                        context.getString(R.string.add_admin_extra_app_text));
                startActivityForResult(intent, REQUEST_CODE_DEVICE_ADMIN);
                // disable until we're really active
                waitForResult = true;
                CheckBoxPreference cbp = (CheckBoxPreference) findPreference(Constant.KEY_CB_DEVICE_ADMIN);
                cbp.setChecked(false);
            } else {
                System.out.println("KEY_CB_DEVICE_ADMIN disable");
                DevicePolicyManager mDPM =
                        (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                mDPM.removeActiveAdmin(deviceAdminSample);
            }
        }

    }

    private void getSimInfoAndSave(Context context) {
        // get sim info
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simSerialNumber = manager.getSimSerialNumber();
        // save sim info
        ConfigUtils.putString(context, Constant.KEY_SIM_INFO, simSerialNumber);
    }

    /**
     * wait for activate device admin
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DEVICE_ADMIN) {
            // if not activate the permission, set the check false
            if (Activity.RESULT_OK == resultCode) {
                CheckBoxPreference cbp = (CheckBoxPreference) findPreference(Constant.KEY_CB_DEVICE_ADMIN);
                cbp.setChecked(true);
            }
            waitForResult = false;
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // permission callback start
    @Override
    public void onPermissionGranted(@NonNull String[] permissionName) {
        System.out.println("onPermissionGranted:" + Arrays.toString(permissionName));
        if(null == permissionName || Arrays.asList(permissionName).contains(Manifest.permission.READ_PHONE_STATE)) {
            if(readPhoneState) {
                readPhoneState = false;
                getSimInfoAndSave(getActivity());

                CheckBoxPreference cbp = (CheckBoxPreference) findPreference(Constant.KEY_CB_BIND_SIM);
                cbp.setChecked(true);

            }
        }
        waitForResult = false;
    }

    @Override
    public void onPermissionDeclined(@NonNull String[] permissionName) {
        Toast.makeText(getActivity(), R.string.no_permission, Toast.LENGTH_SHORT).show();
        waitForResult = false;
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
        waitForResult = false;
    }

    @Override
    public void onNoPermissionNeeded() {
        onPermissionGranted(null);
    }
    // permission callback end

}
