package com.ittianyu.mobileguard.dao;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ittianyu.mobileguard.db.BlacklistDb;
import com.ittianyu.mobileguard.domain.BlacklistBean;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Created by yu.
 */
@RunWith(AndroidJUnit4.class)
public class BlacklistDaoTest {
    // Context of the app under test.
    private Context appContext = InstrumentationRegistry.getTargetContext();
    private BlacklistDao dao = new BlacklistDao(appContext);

    @Test
    public void selectAll() throws Exception {
        List<BlacklistBean> list = dao.selectAll();
        for (BlacklistBean bean : list) {
            System.out.println(bean);
        }
    }

    @Test
    public void selectById() throws Exception {
        BlacklistBean blacklistBean = dao.selectById(1);
        System.out.println(blacklistBean);
    }

    @Test
    public void updateModeById() throws Exception {
        BlacklistBean blacklistBean = new BlacklistBean(1, "110", 1);

        Assert.assertEquals(true, dao.updateModeById(blacklistBean));
        selectById();
    }

    @Test
    public void deleteById() throws Exception {
        Assert.assertEquals(true, dao.deleteById(1));
        Assert.assertEquals(true, dao.deleteById(2));
        selectAll();
    }

    @Test
    public void add() throws Exception {
        BlacklistBean bean = new BlacklistBean();

        for (int i = 0; i < 50; i++) {
            bean.setPhone("110000" + i);
            if(i % 3 == 0)
                bean.setMode(BlacklistDb.MODE_SMS);
            else if(i % 3 == 1)
                bean.setMode(BlacklistDb.MODE_CALL);
            else
                bean.setMode(BlacklistDb.MODE_ALL);
            Assert.assertEquals(true, dao.add(bean));
        }
    }

}