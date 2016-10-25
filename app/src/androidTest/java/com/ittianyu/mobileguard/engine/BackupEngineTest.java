package com.ittianyu.mobileguard.engine;

import android.content.Context;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.ittianyu.mobileguard.R;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

/**
 * Created by yu on 2016/10/7.
 */
@RunWith(AndroidJUnit4.class)
public class BackupEngineTest {
    // Context of the app under test.
    Context context = InstrumentationRegistry.getTargetContext();

    @Test
    public void backupContacts() throws Exception {
        // create directory to save backup files
        File appDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + context.getString(R.string.app_name));
        // if directory not exist, create it
        if (!appDir.isDirectory()) {
            appDir.mkdir();
        }
        boolean contacts = BackupRestoreEngine.backupContacts(context, appDir);
        Assert.assertEquals(true, contacts);

    }

    @Test
    public void restoreContacts() throws Exception {
        File contactsDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/" + context.getString(R.string.app_name) + "/contacts");
        String[] files = contactsDir.list();
        for (String file: files) {
            System.out.println(file);
            boolean b = BackupRestoreEngine.restoreContacts(context, new File(contactsDir, file));
            Assert.assertEquals(true, b);
        }

    }

}