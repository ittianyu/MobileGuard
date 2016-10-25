package com.ittianyu.mobileguard.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yu.
 * used for App Lock
 */
public class AppLockDb extends SQLiteOpenHelper {
    // database file name
    public static final String DB_NAME = "app_lock.db";
    // table name
    public static final String TB_LOCK_NAME = "lock";
    // column
    public static final String LOCK_ID = "_id";
    public static final String LOCK_PACKAGE_NAME = "package_name";

    public AppLockDb(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TB_LOCK_NAME + " ( "
                + LOCK_ID + " integer primary key autoincrement, "
                + LOCK_PACKAGE_NAME + " text not null UNIQUE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
