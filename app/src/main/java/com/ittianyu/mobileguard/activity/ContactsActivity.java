package com.ittianyu.mobileguard.activity;

import android.Manifest;

import com.ittianyu.mobileguard.activity.base.BaseContactsActivity;
import com.ittianyu.mobileguard.domain.ContactBean;
import com.ittianyu.mobileguard.engine.ContactsEngine;

import java.util.List;

/**
 * show contacts list
 */
public class ContactsActivity extends BaseContactsActivity {

    @Override
    protected String[] getPermissions() {
        return new String[]{
                Manifest.permission.READ_CONTACTS
        };
    }

    @Override
    protected List<ContactBean> getContactDatas() {
        return ContactsEngine.readContacts(ContactsActivity.this);
    }
}
