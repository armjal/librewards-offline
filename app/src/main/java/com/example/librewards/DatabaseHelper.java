package com.example.librewards;

import static com.example.librewards.DbConstants.DATABASE_NAME;
import static com.example.librewards.DbConstants.NAME_TABLE_NAME;
import static com.example.librewards.DbConstants.POINTS_TABLE_NAME;
import static com.example.librewards.DbConstants.REWARD_CODES_TABLE_NAME;
import static com.example.librewards.DbConstants.START_CODES_TABLE_NAME;
import static com.example.librewards.DbConstants.STOP_CODES_TABLE_NAME;

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
        String create_start_codes_query = "CREATE TABLE " + START_CODES_TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT,codes TEXT) ";
        String create_stop_codes_query = "CREATE TABLE " + STOP_CODES_TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT,codes TEXT) ";
        String create_reward_codes_query = "CREATE TABLE " + REWARD_CODES_TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT,codes TEXT, cost INTEGER) ";
        String create_points_query = "CREATE TABLE " + POINTS_TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT,points INTEGER)";
        String create_name_query = "CREATE TABLE " + NAME_TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT)";
        db.execSQL(create_start_codes_query);
        db.execSQL(create_stop_codes_query);
        db.execSQL(create_reward_codes_query);
        db.execSQL(create_points_query);
        db.execSQL(create_name_query);
        db.execSQL("INSERT INTO " + POINTS_TABLE_NAME + '(' + "points" + ')' + "VALUES (?)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + START_CODES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + STOP_CODES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + REWARD_CODES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + POINTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NAME_TABLE_NAME);
        onCreate(db);
    }

    public void processTransaction(Runnable actions){
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        actions.run();
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public Cursor select(String tableName, String column, String limit) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.query(tableName, new String[]{column}, null, null, null, null, null, limit);
    }

    public Cursor select(String tableName, String column, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.query(tableName, new String[]{column}, whereClause, whereArgs, null, null, null, "1");
    }
}
