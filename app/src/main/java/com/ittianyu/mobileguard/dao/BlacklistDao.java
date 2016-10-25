package com.ittianyu.mobileguard.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ittianyu.mobileguard.db.BlacklistDb;
import com.ittianyu.mobileguard.domain.BlacklistBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yu.
 * the blacklist database operator
 * Alarm: It should not be a static member variables. If so, it will cause a memory leak problem.
 */
public class BlacklistDao {
    private BlacklistDb dbHelper;

    public BlacklistDao(Context context) {
        this.dbHelper = new BlacklistDb(context);
    }

    /**
     * add a blacklist into database
     * @param blacklist the data need to added
     * @return true if success
     */
    public boolean add(BlacklistBean blacklist) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // key - value
        ContentValues values = new ContentValues();
        values.put(BlacklistDb.BLACKLIST_PHONE, blacklist.getPhone());
        values.put(BlacklistDb.BLACKLIST_MODE, blacklist.getMode());

        long rowId = db.insert(BlacklistDb.TB_BLACKLIST_NAME, null, values);
        db.close();
        
        return -1 != rowId;
    }

    /**
     * query all BlacklistBean
     * @return List<BlacklistBean>. It wouldn't be null.
     */
    public List<BlacklistBean> selectAll() {
        List<BlacklistBean> list = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(BlacklistDb.TB_BLACKLIST_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            // get value
            int id = cursor.getInt(cursor.getColumnIndex(BlacklistDb.BLACKLIST_ID));
            String phone = cursor.getString(cursor.getColumnIndex(BlacklistDb.BLACKLIST_PHONE));
            int mode = cursor.getInt(cursor.getColumnIndex(BlacklistDb.BLACKLIST_MODE));
            // add to list
            BlacklistBean bean = new BlacklistBean(id, phone, mode);
            list.add(bean);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * select by id
     * @param id
     * @return BlacklistBean if find, otherwise return null
     */
    public BlacklistBean selectById(int id) {
        BlacklistBean bean = null;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(BlacklistDb.TB_BLACKLIST_NAME, null,
                BlacklistDb.BLACKLIST_ID + "=?",
                new String[]{id + ""}, null, null, null);
        if(cursor.moveToNext()) {
            String phone = cursor.getString(cursor.getColumnIndex(BlacklistDb.BLACKLIST_PHONE));
            int mode = cursor.getInt(cursor.getColumnIndex(BlacklistDb.BLACKLIST_MODE));
            // create bean
            bean = new BlacklistBean(id, phone, mode);
        }
        cursor.close();
        db.close();

        return bean;
    }

    /**
     * select by phone
     * @param phone
     * @return BlacklistBean if find, otherwise return null
     */
    public BlacklistBean selectByPhone(String phone) {
        BlacklistBean bean = null;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(BlacklistDb.TB_BLACKLIST_NAME, null,
                BlacklistDb.BLACKLIST_PHONE + "=?",
                new String[]{phone + ""}, null, null, null);
        if(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(BlacklistDb.BLACKLIST_ID));
            int mode = cursor.getInt(cursor.getColumnIndex(BlacklistDb.BLACKLIST_MODE));
            // create bean
            bean = new BlacklistBean(id, phone, mode);
        }
        cursor.close();
        db.close();

        return bean;
    }

    /**
     * select data [startInedx, startInedx + count] order by _id desc
     * @param startIndex the index start from 0.
     * @param count the count
     * @return List<BlacklistBean>. It wouldn't be null.
     */
    public List<BlacklistBean> select(int startIndex, int count) {
        List<BlacklistBean> list = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(BlacklistDb.TB_BLACKLIST_NAME, null, null, null, null, null, BlacklistDb.BLACKLIST_ID + " desc", startIndex + "," + count);
        while (cursor.moveToNext()) {
            // get value
            int id = cursor.getInt(cursor.getColumnIndex(BlacklistDb.BLACKLIST_ID));
            String phone = cursor.getString(cursor.getColumnIndex(BlacklistDb.BLACKLIST_PHONE));
            int mode = cursor.getInt(cursor.getColumnIndex(BlacklistDb.BLACKLIST_MODE));
            // add to list
            BlacklistBean bean = new BlacklistBean(id, phone, mode);
            list.add(bean);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * update mode by id
     * @param blacklist
     * @return true if success
     */
    public boolean updateModeById(BlacklistBean blacklist) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BlacklistDb.BLACKLIST_MODE, blacklist.getMode());
        int count = db.update(BlacklistDb.TB_BLACKLIST_NAME, values,
                BlacklistDb.BLACKLIST_ID + "=?",
                new String[]{blacklist.getId() + ""});

        db.close();
        return 1 == count;
    }

    /**
     * delete blacklist by id
     * @param id
     * @return true if success
     */
    public boolean deleteById(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = db.delete(BlacklistDb.TB_BLACKLIST_NAME,
                BlacklistDb.BLACKLIST_ID + "=?",
                new String[]{id + ""});

        db.close();
        return 1 == count;
    }
    /**
     * delete blacklist by phone
     * @param phone
     * @return true if success
     */
    public boolean deleteByPhone(String phone) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = db.delete(BlacklistDb.TB_BLACKLIST_NAME,
                BlacklistDb.BLACKLIST_PHONE + "=?",
                new String[]{phone});

        db.close();
        return 1 == count;
    }

}
