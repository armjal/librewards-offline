package com.example.librewards.data.repositories;

import static com.example.librewards.data.db.DatabaseConstants.ID_COLUMN_NAME;
import static com.example.librewards.data.db.DatabaseConstants.NAME_COLUMN_NAME;
import static com.example.librewards.data.db.DatabaseConstants.POINTS_COLUMN_NAME;
import static com.example.librewards.data.db.DatabaseConstants.USER_TABLE_NAME;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.librewards.data.db.DatabaseHelper;
import com.example.librewards.data.notifiers.UserChangeNotifier;
import com.example.librewards.data.models.UserModel;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserRepository {
    private final SQLiteDatabase db;
    private final DatabaseHelper dbHelper;

    @Inject
    public UserRepository(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        db = dbHelper.getWritableDatabase();
    }

    public void addName(String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME_COLUMN_NAME, name);
        db.insert(USER_TABLE_NAME, null, contentValues);
        UserChangeNotifier.notifyNameChange(name);
    }

    public UserModel getUser() {
        int id = 1;
        String name = dbHelper.getString(USER_TABLE_NAME, NAME_COLUMN_NAME, null, null);
        int points = dbHelper.getInt(USER_TABLE_NAME, POINTS_COLUMN_NAME, null, null);

        return new UserModel(id, name, points);
    }

    public int getPoints() {
        return dbHelper.getInt(USER_TABLE_NAME, POINTS_COLUMN_NAME, null, null);
    }

    public void addPoints(UserModel user, int pointsToUpdate) {
        int newPoints = user.getPoints() + pointsToUpdate;
        updatePoints(String.valueOf(user.getId()), newPoints);
        user.setPoints(newPoints);
    }

    public void minusPoints(UserModel user, int pointsToUpdate) {
        int newPoints = user.getPoints() - pointsToUpdate;
        user.setPoints(newPoints);
        updatePoints(String.valueOf(user.getId()), newPoints);
    }

    private void updatePoints(String id, int updatedPoints) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(POINTS_COLUMN_NAME, updatedPoints);
        db.update(USER_TABLE_NAME, contentValues, ID_COLUMN_NAME + " = ?", new String[]{String.valueOf(id)});
        UserChangeNotifier.notifyPointsChanged(updatedPoints);
    }
}
