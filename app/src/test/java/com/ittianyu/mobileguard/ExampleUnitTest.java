package com.ittianyu.mobileguard;

import org.junit.Test;

import static com.ittianyu.mobileguard.constant.Constant.SMS_REMOTE_LOCK_SCREEN;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testGetLockScreenPassword(){
        String content = SMS_REMOTE_LOCK_SCREEN + "123456";
        int index = content.indexOf(SMS_REMOTE_LOCK_SCREEN);
        if(-1 != index) {// have password
            String password = content.substring(index + SMS_REMOTE_LOCK_SCREEN.length());
            System.out.println(password);
        }

    }
}