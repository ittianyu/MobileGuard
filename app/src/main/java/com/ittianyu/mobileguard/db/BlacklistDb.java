package com.ittianyu.mobileguard.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yu.
 * BlacklistDb
 * create database and update it in this class
 */
public class BlacklistDb extends SQLiteOpenHelper {
    // database file name
    public static final String DB_NAME = "blacklist.db";
    // table name
    public static final String TB_BLACKLIST_NAME = "blacklist";
    // column
    public static final String BLACKLIST_ID = "_id";
    public static final String BLACKLIST_PHONE = "phone";
    public static final String BLACKLIST_MODE = "mode";
    // constant
    public static final int MODE_CALL = 1 << 0;
    public static final int MODE_SMS = 1 << 1;
    public static final int MODE_ALL = MODE_CALL | MODE_SMS;

    public BlacklistDb(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + TB_BLACKLIST_NAME + " ("
                + BLACKLIST_ID + " integer primary key autoincrement, "
                + BLACKLIST_PHONE + " char(11) not null UNIQUE, "
                + BLACKLIST_MODE + " integer not null);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
