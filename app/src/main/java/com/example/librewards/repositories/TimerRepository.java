package com.example.librewards.repositories;

import static com.example.librewards.DbConstants.CODES_COLUMN_NAME;
import static com.example.librewards.DbConstants.ID_COLUMN_NAME;
import static com.example.librewards.DbConstants.USED_CODE_COLUMN_NAME;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.librewards.DatabaseHelper;

import java.util.List;

public class TimerRepository {
    private final SQLiteDatabase db;
    private final DatabaseHelper dbHelper;

    public TimerRepository(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        db = dbHelper.getWritableDatabase();
    }

    public void deleteTimerCode(String table, String code) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(USED_CODE_COLUMN_NAME, true);
        db.update(table, contentValues, CODES_COLUMN_NAME + " = ?", new String[]{code});
    }

    public void storeTimerCodes(List<String> codesList, String table) {
        for (String code : codesList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CODES_COLUMN_NAME, code);
            db.insert(table, null, contentValues);
        }
    }

    public List<String> checkForTimerCodeUpdates(List<String> originalCodes, String table) {
        List<String> allCodes = getAllCodes(table);
        if (!(allCodes.equals(originalCodes))) {
            allCodes = originalCodes;
            this.updateTimerCodes(table, originalCodes);
        }
        return allCodes;
    }

    public void updateTimerCodes(String table, List<String> newCodesList) {
        ContentValues contentValues = new ContentValues();
        for (int i = 1, k = 0; i < newCodesList.size() - 1; i++, k++) {
            contentValues.put(CODES_COLUMN_NAME, newCodesList.get(k));
            db.update(table, contentValues, ID_COLUMN_NAME + " = ?", new String[]{String.valueOf(i)});
        }
    }

    public List<String> getAllCodes(String table) {
        return dbHelper.getAllStrings(table, CODES_COLUMN_NAME, null, null);
    }
}
