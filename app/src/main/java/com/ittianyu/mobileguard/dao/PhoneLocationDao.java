package com.ittianyu.mobileguard.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ittianyu.mobileguard.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yu.
 * Provide service to locate telephone cellphone number.
 * Need database assets/phone_location.db
 * Alarm: It should not be a static member variables. If so, it will cause a memory leak problem.
 */
public class PhoneLocationDao {
    private static final String DB_NAME = "phone_location.db";
    private static final String TB_NUMBER = "number";
    private static final String TB_LOCATION = "location";

    private Context context;

    public PhoneLocationDao(Context context) {
        this.context = context;
    }

    /**
     * get a SQLiteDatabase object from assets/phone_location.db
     * the db will be copied to files dir at first run.
     * @return return SQLiteDatabase object if success, null otherwise
     */
    private SQLiteDatabase getWritableDatabase() {
        try {
            // check db whether exist
            File file = new File(context.getFilesDir(), DB_NAME);
            if(!file.exists()) {
                // file not exists, need copy
                InputStream in = context.getAssets().open(DB_NAME);
                FileUtils.saveFileWithStream(file, in);
            }
            // open database
            return SQLiteDatabase.openDatabase(file.getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * query cellphone number location by areaNumber
     * @param areaNumber the top 7 of normal cellphone number. Such as 1300000
     * @return location if success, empty String otherwise. It wouldn't be null.
     */
    public String queryCellphoneLocation(String areaNumber) {
        // check argument
        if(areaNumber.length() != 7)
            throw new IllegalArgumentException("the length of areaNumber is wrong. Excepted length is 7, the actual length is " + areaNumber.length());

        String location = "";
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select location from location where id = (select outkey from number where id = ?)",
                new String[]{areaNumber});
        if(cursor.moveToNext()) {
            location = cursor.getString(cursor.getColumnIndex("location"));
        }
        cursor.close();
        db.close();
        return location;
    }

    /**
     * query telephone number location by areaNumber
     * @param areaNumber the top 3 or 4 of normal cellphone number. Such as 010, 0888
     * @return location if success, empty String otherwise. It wouldn't be null.
     */
    public String queryTelephoneLocation(String areaNumber) {
        // check argument
        if(areaNumber.length() > 4 || areaNumber.length() < 3)
            throw new IllegalArgumentException("the length of areaNumber is not in range. Excepted length is 3 or 4, the actual length is " + areaNumber.length());

        String location = "";
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("select substr(location, 1, length(location) - 2) as loc from location where area = ? limit 1",
                new String[]{areaNumber.substring(1)});
        if(cursor.moveToNext()) {
            location = cursor.getString(cursor.getColumnIndex("loc"));
        }
        cursor.close();
        db.close();
        return location;
    }

}
