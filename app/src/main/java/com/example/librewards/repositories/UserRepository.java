package com.example.librewards.repositories;

import static com.example.librewards.DbConstants.NAME_TABLE_NAME;
import static com.example.librewards.DbConstants.POINTS_TABLE_NAME;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.librewards.DatabaseHelper;
import com.example.librewards.models.UserModel;

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

    public UserModel getUser() {
        String name = dbHelper.getString(NAME_TABLE_NAME, "name", null, null);
        int points = dbHelper.getInt(POINTS_TABLE_NAME, "points", null, null);

        return new UserModel(name, points);
    }

    public int getPoints() {
        return dbHelper.getInt(POINTS_TABLE_NAME, "points", null, null);
    }

    public void addPoints(UserModel user, int pointsToUpdate) {
        int newPoints = user.getPoints() + pointsToUpdate;
        updatePoints(newPoints);
        user.setPoints(newPoints);
    }

    public void minusPoints(UserModel user, int pointsToUpdate) {
        int newPoints = user.getPoints() - pointsToUpdate;
        updatePoints(newPoints);
        user.setPoints(newPoints);
    }

    private void updatePoints(int updatedPoints){
        ContentValues contentValues = new ContentValues();
        contentValues.put("points", updatedPoints);
        db.update(POINTS_TABLE_NAME, contentValues, null, null);
    }
}
