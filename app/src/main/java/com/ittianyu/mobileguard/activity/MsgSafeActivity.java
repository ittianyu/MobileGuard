package com.ittianyu.mobileguard.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.activity.base.BaseActivityUpEnableWithMenu;
import com.ittianyu.mobileguard.constant.Constant;
import com.ittianyu.mobileguard.dao.BlacklistDao;
import com.ittianyu.mobileguard.db.BlacklistDb;
import com.ittianyu.mobileguard.domain.BlacklistBean;

import java.util.List;
import java.util.Vector;

/**
 * Msg safe activity
 * show the blacklist
 */
public class MsgSafeActivity extends BaseActivityUpEnableWithMenu {
    // constants
    private static final int PER_COUNT = 20;

    // dialog view
    private EditText etPhoneNumber;
    private CheckBox cbCallIntercept;
    private CheckBox cbSmsIntercept;
    // frame view
    private ProgressBar pbLoading;
    private TextView tvNoData;
    private ListView lvBlacklist;

    //data
    private BlacklistDao dao;
    private Vector<BlacklistBean> blacklists = new Vector<>(0);
    private BlacklistAdapter adapter;

    // thread
    private Thread getMoreDataThread;

    public MsgSafeActivity() {
        super(R.string.msg_safe, R.menu.menu_msg_safe);
    }

    /**
     * 1
     */
    @Override
    protected void initView() {
        setContentView(R.layout.activity_msg_safe);
        // bind view
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        tvNoData = (TextView) findViewById(R.id.tv_no_data);
        lvBlacklist = (ListView) findViewById(R.id.lv_blacklist);

    }

    /**
     * 2
     */
    @Override
    protected void initData() {
        // create some object
        dao = new BlacklistDao(this);
        adapter = new BlacklistAdapter();
        lvBlacklist.setAdapter(adapter);

        // get data
        getMoreData();
    }

    /**
     * get more data and add to list
     */
    private void getMoreData() {
        // show progress bar before read data
        pbLoading.setVisibility(View.VISIBLE);

        // if the previous thread is running, no need run a new thread
        if (null != getMoreDataThread && getMoreDataThread.isAlive()) {
            return;
        }

        // read data
        getMoreDataThread = new Thread() {
            @Override
            public void run() {
                // select data
                final List<BlacklistBean> tempList = dao.select(blacklists.size(), PER_COUNT);
                if (0 == tempList.size()) {
                    // have no more data
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // hide progress bar
                            pbLoading.setVisibility(View.GONE);
                            // the first get data, and database is empty
                            if (0 == blacklists.size()) {
                                // refresh ui
                                onDataChanged();
                                return;
                            }
                            // tips
                            Toast.makeText(MsgSafeActivity.this, R.string.no_more_data, Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }

                // read data finish, need to hide progress bar and show other view
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // add to list
                        blacklists.addAll(tempList);
                        // hide progress bar
                        pbLoading.setVisibility(View.GONE);
                        // change ui
                        onDataChanged();
                    }
                });
            }
        };
        // start thead
        getMoreDataThread.start();
    }

    /**
     * on get data finish
     * when add or delete data, need call this method to update UI
     * show list view if data exist, otherwise show TextView that content is "no data"
     */
    private void onDataChanged() {
        // check data
        if (0 == blacklists.size()) {
            // no data, show TextView
            tvNoData.setVisibility(View.VISIBLE);
            // hide list view
            lvBlacklist.setVisibility(View.GONE);
        } else {
            // have data, show ListView
            lvBlacklist.setVisibility(View.VISIBLE);
            // hide text view
            tvNoData.setVisibility(View.GONE);
            // notify data changed
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 3
     */
    @Override
    protected void initEvent() {
        // set scroll listener for get more datas when see the last view
        lvBlacklist.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // the scroll state is idle and last view is showed
                if (SCROLL_STATE_IDLE == scrollState && view.getLastVisiblePosition() == blacklists.size() - 1) {
                    // get more data
                    getMoreData();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    /**
     * on menu selected
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.m_manually_add:
                manuallyAdd();
                break;
            case R.id.m_add_from_contacts:
                addFromContacts();
                break;
            case R.id.m_add_from_phone_history:
                addFromPhoneHistory();
                break;
            case R.id.m_add_from_sms_history:
                addFromSmsHistory();
                break;
        }

        return super.onOptionsItemSelected(item);// can't be replace by true, the super class have deal BACK menu
    }

    /**
     * start SmsContactsActivity for result
     */
    private void addFromSmsHistory() {
        startActivityForResult(new Intent(this, SmsContactsActivity.class), 0);// no need to identify request
    }

    /**
     * start the CallContactsActivity for result
     */
    private void addFromPhoneHistory() {
        startActivityForResult(new Intent(this, CallContactsActivity.class), 0);// no need to identify request
    }

    /**
     * start ContactsActivity for result
     */
    private void addFromContacts() {
        startActivityForResult(new Intent(this, ContactsActivity.class), 0);// no need to identify request
    }

    /**
     * When the Contacts Activity finish, it will be call.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            String phone = data.getStringExtra(Constant.KEY_CONTACT_PHONE);
            showAddDialog(phone);
        }
    }

    /**
     * manually add blacklist
     * show a custom
     */
    private void manuallyAdd() {
        showAddDialog(null);
    }

    /**
     * manually add blacklist
     * show a custom
     */
    private void showAddDialog(String phone) {
        // create custom view
        View view = View.inflate(this, R.layout.dialog_manually_add_blacklist, null);
        etPhoneNumber = (EditText) view.findViewById(R.id.et_phone_number);
        cbCallIntercept = (CheckBox) view.findViewById(R.id.cb_call_intercept);
        cbSmsIntercept = (CheckBox) view.findViewById(R.id.cb_sms_intercept);
        // set phone on edit text
        if (!TextUtils.isEmpty(phone))
            etPhoneNumber.setText(phone);

        // builder dialog and show
        new AlertDialog.Builder(this)
                .setTitle(R.string.manually_add)
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onDataAdd();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }


    /**
     * will be call when select ok in add dialog
     * add number to
     */
    private void onDataAdd() {
        // check empty
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, R.string.phone_number_can_not_be_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        // check intercept
        if (!cbCallIntercept.isChecked() && !cbSmsIntercept.isChecked()) {
            Toast.makeText(this, R.string.tips_intercept_is_not_selected, Toast.LENGTH_SHORT).show();
            return;
        }

        // get mode
        int mode = 0;
        if (cbCallIntercept.isChecked())
            mode |= BlacklistDb.MODE_CALL;
        if (cbSmsIntercept.isChecked())
            mode |= BlacklistDb.MODE_SMS;

        // prevent repeat data
        BlacklistBean bean = dao.selectByPhone(phoneNumber);
        if (bean != null) {
            // the phone already exist, need update
            bean.setMode(mode);
            if (!dao.updateModeById(bean)) {
                // update failed
                Toast.makeText(this, R.string.failed_to_add, Toast.LENGTH_SHORT).show();
                return;
            }
            // update success, need update in list
            blacklists.remove(bean);// BlacklistBean override equal and hashcode method: according to phone
            // add to list first position
            blacklists.add(0, bean);
            // add success
            Toast.makeText(this, R.string.success_to_add, Toast.LENGTH_SHORT).show();
            // refresh list view
            onDataChanged();
            return;
        }
        // if not exist, create a new bean and add it
        bean = new BlacklistBean(0, phoneNumber, mode);
        // add to database
        if (!dao.add(bean)) {
            // add failed
            Toast.makeText(this, R.string.failed_to_add, Toast.LENGTH_SHORT).show();
            return;
        }
        // add success
        Toast.makeText(this, R.string.success_to_add, Toast.LENGTH_SHORT).show();
        // add to list first position
        blacklists.add(0, bean);
        // refresh list view
        onDataChanged();
    }

    /**
     * blacklist adapter
     */
    private class BlacklistAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return blacklists.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewItemBean item = null;
            if (null == convertView) {// if not have create view
                // new item
                item = new ViewItemBean();
                // create view
                convertView = View.inflate(MsgSafeActivity.this, R.layout.item_msg_safe_blacklist_lv, null);
                // bind child view to item
                item.tvPhoneNumber = (TextView) convertView.findViewById(R.id.tv_phone_number);
                item.tvMode = (TextView) convertView.findViewById(R.id.tv_mode);
                item.ivDelete = (ImageView) convertView.findViewById(R.id.iv_delete);
                // set item to tag
                convertView.setTag(item);
            } else {
                // get item
                item = (ViewItemBean) convertView.getTag();
            }
            // get data
            final BlacklistBean blacklist = blacklists.get(position);

            // set data
            item.tvPhoneNumber.setText(blacklist.getPhone());
            switch (blacklist.getMode()) {
                case BlacklistDb.MODE_CALL:
                    item.tvMode.setText(R.string.call_intercept);
                    break;
                case BlacklistDb.MODE_SMS:
                    item.tvMode.setText(R.string.sms_intercept);
                    break;
                case BlacklistDb.MODE_ALL:
                    item.tvMode.setText(getString(R.string.call_intercept) + " " + getString(R.string.sms_intercept));
                    break;
            }
            // init event
            // delete event
            item.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // show a dialog to query user whether delete
                    new AlertDialog.Builder(MsgSafeActivity.this)
                            .setTitle(R.string.tips)
                            .setMessage(getString(R.string.whether_delete_phone_number) + blacklist.getPhone())
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // delete
                                    if (dao.deleteByPhone(blacklist.getPhone())) {
                                        // success
                                        Toast.makeText(MsgSafeActivity.this, R.string.success_to_delete, Toast.LENGTH_SHORT).show();
                                        // remove it from list(remember to do this operator)
                                        blacklists.remove(position);
                                        // refresh list view
                                        onDataChanged();
                                        // check the count, get more data if current data less than 10
                                        if (blacklists.size() == 10) {
                                            getMoreData();
                                        }
                                    } else {
                                        // failed
//                                        System.out.println("delete failed. id:" + blacklist.getId() + " phone:" + blacklist.getPhone());
                                        Toast.makeText(MsgSafeActivity.this, R.string.failed_to_delete, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton(R.string.cancel, null)
                            .show();
                }
            });

            return convertView;
        }


    }

    /**
     * package ListView item child view
     */
    private static class ViewItemBean {
        private TextView tvPhoneNumber;
        private TextView tvMode;
        private ImageView ivDelete;

    }
}
