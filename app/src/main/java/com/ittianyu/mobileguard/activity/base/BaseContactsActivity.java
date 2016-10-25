package com.ittianyu.mobileguard.activity.base;

import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fastaccess.permission.base.PermissionHelper;
import com.fastaccess.permission.base.callback.OnPermissionCallback;
import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.constant.Constant;
import com.ittianyu.mobileguard.domain.ContactBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * BaseContactsActivity
 * show a ListView which content is Name and phone
 */
public abstract class BaseContactsActivity extends BaseActivityUpEnable implements OnPermissionCallback {
    // view
    private ListView lvContacts;

    // data
    private List<ContactBean> contacts = new ArrayList<ContactBean>();
    private AlertDialog dialog;
    private ContactAdapter adapter = new ContactAdapter();
    private PermissionHelper permissionHelper;
    protected String[] permissions;

    public BaseContactsActivity() {
        super(R.string.contacts_activity_title);
    }

    /**
     * 1
     */
    @Override
    protected void initView() {
        setContentView(R.layout.activity_contacts);
        // bind ListView
        lvContacts = (ListView) findViewById(R.id.lv_contacts);
        // set adapter
        lvContacts.setAdapter(adapter);
    }

    /**
     * 2
     */
    @Override
    protected void initData() {
        permissions = getPermissions();
        if(null == permissions || permissions.length == 0) {
            return;
        }
        // I found PermissionHelper.getInstance() just create a new object.
        // so it was not a single class, you have no need to worry about memory leak.
        permissionHelper = PermissionHelper.getInstance(this);
        // request permission
        permissionHelper.request(permissions);

    }

    /**
     * You need override this method to return permissions
     * @return permissions that need grant
     */
    protected abstract String[] getPermissions();

    /**
     * loading data in child thread
     */
    private void loadingData() {
        // loding data in child thread
        new Thread() {
            @Override
            public void run() {
                // show a loading dialog
                showLoadingDialog();
                // enhance the user feeling
                SystemClock.sleep(200);

                // get contacts
                List<ContactBean> tempList = getContactDatas();

                // get success, change data
                changeDataAndRefreshUi(tempList);
            }
        }.start();
    }

    /**
     * add the data to list and notify adapter
     * @param tempList
     */
    private void changeDataAndRefreshUi(final List<ContactBean> tempList) {
        // the ui operation need run on ui thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                contacts.addAll(tempList);
                // it might be null if user touch Back Button
                if (null != dialog) {
                    dialog.cancel();
                    dialog = null;
                }
                // notify update list view
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * show loading dialog
     */
    private void showLoadingDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog = new AlertDialog.Builder(BaseContactsActivity.this)
                        .setTitle(R.string.tips)
                        .setMessage(R.string.loding_tips)
                        .create();
                dialog.show();
            }
        });
    }

    /**
     * get contact, subclass need to override to implement get data from different way
     * @return
     */
    protected abstract List<ContactBean> getContactDatas();

    /**
     * 3
     */
    @Override
    protected void initEvent() {
        // set on touch item listener
        lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // set selected phone as result
                ContactBean contact = contacts.get(position);
                Intent intent = new Intent();
                intent.putExtra(Constant.KEY_CONTACT_PHONE, contact.getPhone());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // permission callback start
    @Override
    public void onPermissionGranted(@NonNull String[] permissionName) {
        System.out.println("onPermissionGranted:" + Arrays.toString(permissionName));
        loadingData();
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

    private class ContactAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return contacts.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // get view
            View view;
            if (null == convertView) {
                view = View.inflate(BaseContactsActivity.this, R.layout.item_contacts_lv, null);
            } else {
                view = convertView;
            }
            // set value to TextView
            TextView tvName = (TextView) view.findViewById(R.id.tv_name);
            TextView tvPhone = (TextView) view.findViewById(R.id.tv_phone);
            ContactBean contact = contacts.get(position);
            tvName.setText(contact.getName());
            tvPhone.setText(contact.getPhone());
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
