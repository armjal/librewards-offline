package com.example.librewards.data.db;

import static com.example.librewards.data.db.DatabaseConstants.DATABASE_NAME;
import static com.example.librewards.data.db.DatabaseConstants.REWARD_CODES_TABLE_NAME;
import static com.example.librewards.data.db.DatabaseConstants.START_CODES_TABLE_NAME;
import static com.example.librewards.data.db.DatabaseConstants.STOP_CODES_TABLE_NAME;
import static com.example.librewards.data.db.DatabaseConstants.USER_TABLE_NAME;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    public DatabaseHelper(Context context, String databaseName) {
        super(context, databaseName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_start_codes_query = "CREATE TABLE " + START_CODES_TABLE_NAME + " (id " +
                "INTEGER PRIMARY KEY AUTOINCREMENT,codes TEXT, used TEXT DEFAULT 'false') ";
        String create_stop_codes_query = "CREATE TABLE " + STOP_CODES_TABLE_NAME + " (id INTEGER " +
                "PRIMARY KEY AUTOINCREMENT,codes TEXT, used TEXT DEFAULT 'false') ";
        String create_reward_codes_query = "CREATE TABLE " + REWARD_CODES_TABLE_NAME + " (id " +
                "INTEGER PRIMARY KEY AUTOINCREMENT,codes TEXT, cost INTEGER) ";
        String create_user_query = "CREATE TABLE " + USER_TABLE_NAME + " (id INTEGER PRIMARY " +
                "KEY AUTOINCREMENT,name TEXT, points INTEGER DEFAULT 0)";

        db.execSQL(create_start_codes_query);
        db.execSQL(create_stop_codes_query);
        db.execSQL(create_reward_codes_query);
        db.execSQL(create_user_query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + START_CODES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + STOP_CODES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + REWARD_CODES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        onCreate(db);
    }

    public void processTransaction(Runnable actions) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            actions.run();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public Integer getInt(String tableName, String column, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer output = null;
        Cursor c = db.query(tableName, new String[]{column}, whereClause, whereArgs, null, null, null, "1");
        if (c.getCount() > 0 && c.moveToFirst()) {
            output = c.getInt(0);
        }
        c.close();
        return output;
    }

    public String getString(String tableName, String column, String whereClause,
                            String[] whereArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        String output = "";
        Cursor c = db.query(tableName, new String[]{column}, whereClause, whereArgs, null, null, null, "1");
        if (c.getCount() > 0 && c.moveToFirst()) {
            output = c.getString(0);
        }
        c.close();
        return output;
    }


    public List<String> getAllStrings(String tableName, String columnName, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        List<String> strings = new ArrayList<>();
        Cursor c = db.query(tableName, new String[]{columnName}, whereClause, whereArgs, null, null, null, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            strings.add(c.getString(0));
            c.moveToNext();
        }
        c.close();
        return strings;
    }
}
