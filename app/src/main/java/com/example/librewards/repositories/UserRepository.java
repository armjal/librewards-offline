package com.example.librewards.repositories;

import static com.example.librewards.DbConstants.NAME_TABLE_NAME;
import static com.example.librewards.DbConstants.POINTS_TABLE_NAME;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.librewards.DatabaseHelper;

public class UserRepository {
    private final SQLiteDatabase db;
    private final DatabaseHelper dbHelper;

    public UserRepository(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        db = dbHelper.getWritableDatabase();
    }

    public void addName(String yourName) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", yourName);
        db.insert(NAME_TABLE_NAME, null, contentValues);
    }

    public String getName() {
        return dbHelper.getString(NAME_TABLE_NAME, "name", null, null);
    }

    public int getPoints() {
        return dbHelper.getInt(POINTS_TABLE_NAME, "points", null, null);
    }

    public void addPoints(int points) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("points", getPoints() + points);
        db.update(POINTS_TABLE_NAME, contentValues, null, null);
    }

    public void minusPoints(int points) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("points", getPoints() - points);
        db.update(POINTS_TABLE_NAME, contentValues, null, null);
    }
}
