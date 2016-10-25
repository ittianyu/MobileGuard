package com.ittianyu.mobileguard.engine;

import android.content.Context;

import com.ittianyu.mobileguard.dao.PhoneLocationDao;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yu.
 * Provide service to locate telephone cellphone number.
 */
public class PhoneLocationEngine {
    private static final String REX_CELLPHONE = "^1[34578]\\d{9}$";
    private static final String REX_TELEPHONE = "^(0\\d{2,3}-?\\s?)\\d{7,8}(\\d{1,4})?";

    private Pattern cellphonePattern = Pattern.compile(REX_CELLPHONE);
    private Pattern telephonePattern = Pattern.compile(REX_TELEPHONE);

    public enum PhoneType {
        CELL, TELE, UNKNOWN
    }

    /**
     * locate telephone and cellphone number.
     * @param context
     * @param number can be telephone or cellphone number
     * @return location if success, empty String otherwise. It wouldn't be null.
     */
    public String getLocation(Context context, String number) {
        PhoneLocationDao dao = new PhoneLocationDao(context);
        String location = "";
        // check the number is telephone or cellphone
        PhoneType phoneType = matchPhone(number);
        switch (phoneType) {
            case CELL:
                // cellphone
                location = dao.queryCellphoneLocation(number.substring(0, 7));
                break;
            case TELE:
                // telephone
                location = dao.queryTelephoneLocation(getTelephoneAreaNumber(number));
                break;
        }
        return location;
    }

    /**
     * match phone number.
     * It will tell you the number is cellphone or telephone number.
     * @param number
     * @return PhoneType. It may be CELL, TELE, UNKNOWN
     */
    public PhoneType matchPhone(String number) {
        // check the number is telephone or cellphone
        Matcher cellphoneMatcher = cellphonePattern.matcher(number);
        if(cellphoneMatcher.matches()) {
            return PhoneType.CELL;
        }
        Matcher telephoneMatcher = telephonePattern.matcher(number);
        if(telephoneMatcher.matches()) {
            return PhoneType.TELE;
        }
        return PhoneType.UNKNOWN;
    }

    /**
     * get telephone area number
     * @param number telephone number. Such as 01012345678 , 010-12345678 or 010 12345678
     * @return area number if success, empty string otherwise.
     */
    public static String getTelephoneAreaNumber(String number){
        // error number
        if(number.length() < 10)
            return "";
        if(number.charAt(1) == '1' || number.charAt(1) == '2') {
            // area number length is 3
            return number.substring(0, 3);
        }
        // area number length is 4
        return number.substring(0, 4);
    }

}
