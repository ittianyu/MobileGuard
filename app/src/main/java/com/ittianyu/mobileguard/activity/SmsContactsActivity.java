package com.ittianyu.mobileguard.activity;

import android.Manifest;
import android.text.TextUtils;

import com.ittianyu.mobileguard.R;
import com.ittianyu.mobileguard.activity.base.BaseContactsActivity;
import com.ittianyu.mobileguard.domain.ContactBean;
import com.ittianyu.mobileguard.engine.ContactsEngine;

import java.util.List;

/**
 * sms number list activity
 */
public class SmsContactsActivity extends BaseContactsActivity {

    @Override
    protected String[] getPermissions() {
        return new String[]{
                Manifest.permission.READ_SMS
        };
    }

    @Override
    protected List<ContactBean> getContactDatas() {
        // get list
        List<ContactBean> list = ContactsEngine.readSmsContacts(this);
        // set name unknown if name is empty
        for (ContactBean contact: list) {
            if (TextUtils.isEmpty(contact.getName()))
                contact.setName(getString(R.string.unknown));
        }
        return list;
    }
}
