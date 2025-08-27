package com.example.librewards;

import static com.example.librewards.DbConstants.DATABASE_NAME;
import static com.example.librewards.DbConstants.REWARD_CODES_TABLE_NAME;
import static com.example.librewards.DbConstants.START_CODES_TABLE_NAME;
import static com.example.librewards.DbConstants.STOP_CODES_TABLE_NAME;
import static com.example.librewards.DbConstants.USER_TABLE_NAME;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
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
        actions.run();
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public int getInt(String tableName, String column, String whereClause, String[] whereArgs) {
        int output = 0;
        Cursor c = selectOne(tableName, column, whereClause, whereArgs);
        if (c.getCount() > 0 && c.moveToFirst()) {
            output = c.getInt(0);
        }
        c.close();
        return output;
    }

    public String getString(String tableName, String column, String whereClause,
                            String[] whereArgs) {
        String output = "";
        Cursor c = selectOne(tableName, column, whereClause, whereArgs);
        if (c.getCount() > 0 && c.moveToFirst()) {
            output = c.getString(0);
        }
        c.close();
        return output;
    }

    public Cursor select(String tableName, String column, String limit) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.query(tableName, new String[]{column}, null, null, null, null, null, limit);
    }

    public Cursor selectOne(String tableName, String column, String whereClause,
                            String[] whereArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.query(tableName, new String[]{column}, whereClause, whereArgs, null, null, null
                , "1");
    }
}
