package com.ittianyu.mobileguard.engine;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ittianyu.mobileguard.domain.ContactBean;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Created by yu on 2016/10/1.
 */
@RunWith(AndroidJUnit4.class)
public class ReadContactsEngineTest {
    @Test
    public void readContacts() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        List<ContactBean> contacts = ContactsEngine.readContacts(appContext);
        System.out.println(contacts);
    }

}