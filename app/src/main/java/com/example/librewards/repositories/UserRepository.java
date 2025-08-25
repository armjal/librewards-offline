package com.example.librewards.repositories;

import static com.example.librewards.DbConstants.NAME_TABLE_NAME;
import static com.example.librewards.DbConstants.POINTS_TABLE_NAME;

import android.content.ContentValues;
import android.database.Cursor;
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

    //Method that returns the name that a user gives using a cursor
    public String getName() {
        String output = "";
        Cursor c = dbHelper.select(NAME_TABLE_NAME, "name", "1");
        if (c.getCount() > 0 && c.moveToFirst()) {
            output = c.getString(0);
        }
        c.close();
        return output;
    }

    public int getPoints() {
        int output = 0;
        Cursor c = dbHelper.select(POINTS_TABLE_NAME, "points", "1");
        if (c.getCount() > 0 && c.moveToFirst()) {
            output = c.getInt(0);
        }
        c.close();
        return output;
    }

    public void addPoints(int points) {
        ContentValues contentValues = new ContentValues();
        //Uses the current balance and updates the balance with the sum of he points being passed in
        contentValues.put("points", getPoints() + points);
        db.update(POINTS_TABLE_NAME, contentValues, null, null);
    }

    public void minusPoints(int points) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("points", getPoints() - points);
        db.update(POINTS_TABLE_NAME, contentValues, null, null);
    }
}
