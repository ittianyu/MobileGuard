package com.ittianyu.mobileguard.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ittianyu.mobileguard.constant.Constant;
import com.ittianyu.mobileguard.db.AppLockDb;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yu.
 * Provider insert delete and select operation for app_lock.db
 */
public class AppLockDao {
    private AppLockDb dbHelper;
    private Context context;

    public AppLockDao(Context context) {
        dbHelper = new AppLockDb(context);
        this.context = context;
    }

    /**
     * notify the observer the app lock data changed
     */
    private void notifyDataChanged() {
        context.getContentResolver().notifyChange(Constant.URI_APP_LOCK_DATA_CHANGED, null);
    }

    /**
     * add a new record to db
     *
     * @param packageName the app package name
     * @return true if success, false otherwise
     */
    public boolean insert(String packageName) {
        // get db
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // column
        ContentValues values = new ContentValues();
        values.put(AppLockDb.LOCK_PACKAGE_NAME, packageName);
        // insert
        long rowId = db.insert(AppLockDb.TB_LOCK_NAME, null, values);

        // never forget closing
        db.close();
        // if success to change data, notify the observer
        if(-1 != rowId)
            notifyDataChanged();

        return -1 != rowId;
    }

    /**
     * delete a package from db
     *
     * @param packageName the app package name
     * @return true if success, false otherwise
     */
    public boolean delete(String packageName) {
        // get db
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // delete
        int count = db.delete(AppLockDb.TB_LOCK_NAME, AppLockDb.LOCK_PACKAGE_NAME + " = ?", new String[]{packageName});

        // never forget closing
        db.close();
        // if success to change data, notify the observer
        if(1 == count)
            notifyDataChanged();

        return 1 == count;
    }

    /**
     * query all packages name
     *
     * @return a List<String>. It wouldn't be null.
     */
    public List<String> selectAll() {
        // get db
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // select
        Cursor cursor = db.query(AppLockDb.TB_LOCK_NAME, new String[]{AppLockDb.LOCK_PACKAGE_NAME}, null, null, null, null, null);

        // create a list which init count is specific
        ArrayList<String> list = new ArrayList<>(cursor.getCount());
        // start get values
        while (cursor.moveToNext()) {
            // get value and add to list
            String string = cursor.getString(0);
            list.add(string);
        }

        // never forget closing
        cursor.close();
        db.close();

        return list;
    }

    /**
     * @param packageName the app package name
     * @return true if success, false otherwise
     */
    public boolean isExists(String packageName) {
        // get db
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query(AppLockDb.TB_LOCK_NAME, new String[]{"1"},
                AppLockDb.LOCK_PACKAGE_NAME + " = ?",
                new String[]{packageName}, null, null, null);

        boolean exists = cursor.moveToNext();

        // never forget closing
        cursor.close();
        db.close();

        return exists;
    }

}
