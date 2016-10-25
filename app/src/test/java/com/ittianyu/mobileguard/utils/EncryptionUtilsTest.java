package com.ittianyu.mobileguard.utils;

import junit.framework.Assert;

import org.junit.Test;

import java.io.File;

/**
 * Created by yu.
 */
public class EncryptionUtilsTest {
    @Test
    public void md5() throws Exception {
        String md5 = EncryptionUtils.md5File(new File("G:\\实用软件\\Hash_1.0.4.exe"));
        System.out.println(md5);
        Assert.assertEquals("3776F61A3376CCB340312256BEBCF1DC", md5.toUpperCase());
    }

}