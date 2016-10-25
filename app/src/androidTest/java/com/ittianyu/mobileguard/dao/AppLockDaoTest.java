package com.ittianyu.mobileguard.dao;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Created by yu.
 */
@RunWith(AndroidJUnit4.class)
public class AppLockDaoTest {
    // Context of the app under test.
    private Context appContext = InstrumentationRegistry.getTargetContext();
    private AppLockDao dao = new AppLockDao(appContext);

    @Test
    public void insert() throws Exception {
        Assert.assertEquals(true, dao.insert("com.ittianyu.test1"));
        Assert.assertEquals(true, dao.insert("com.ittianyu.test2"));
    }

    @Test
    public void delete() throws Exception {
        Assert.assertEquals(true, dao.delete("com.ittianyu.test1"));
        Assert.assertEquals(true, dao.delete("com.ittianyu.test2"));
    }

    @Test
    public void selectAll() throws Exception {
        List<String> list = dao.selectAll();
        System.out.println(list);
    }

}