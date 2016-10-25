package com.ittianyu.mobileguard.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okio.BufferedSource;
import okio.Okio;

/**
 * Created by yu.
 * Encryption utils, such as md5
 */
public class EncryptionUtils {

    /**
     * call n md5
     * For example,
     *      if count equal 2, it will call twice md5,  md5(md5(string))
     * @param count
     * @return
     */
    public static String md5N(final String string, final int count) {
        if(count <= 0)
            throw new IllegalArgumentException("count can't < 0");
        String result = null;
        for (int i = 0; i < count; i++) {
            result = md5(string);
        }
        return result;
    }

    /**
     * use md5 algorithm
     * @param string the String need to encrypt
     * @return the encrypted result
     */
    public static String md5(final String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    /**
     * use md5 algorithm
     * @param file the file need calculate md5, don't support directory
     * @return the file md5 code all in lower case
     */
    public static String md5File(final File file) throws IOException {
        // check argument
        if(!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath() + " can't be found");
        }
        if(file.isDirectory()) {
            throw new IllegalArgumentException(file.getAbsolutePath() + " is directory");
        }

        byte[] hash;
        try {
            // get md5 algorithm
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            // read file
            BufferedSource source = Okio.buffer(Okio.source(file));
            byte[] buf = new byte[1024];
            int count = -1;
            while ( (count = source.read(buf)) != -1) {
                md5.update(buf, 0, count);// update md5
            }
            source.close();
            hash = md5.digest();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
}
