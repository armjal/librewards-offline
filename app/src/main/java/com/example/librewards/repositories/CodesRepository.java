package com.example.librewards.repositories;

import static com.example.librewards.DbConstants.CODES_COLUMN_NAME;
import static com.example.librewards.DbConstants.ID_COLUMN_NAME;
import static com.example.librewards.DbConstants.USED_CODE_COLUMN_NAME;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.librewards.DatabaseHelper;

import java.util.List;

public abstract class CodesRepository {
    private final SQLiteDatabase db;
    private final DatabaseHelper dbHelper;

    public CodesRepository(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        db = dbHelper.getWritableDatabase();
    }

    public abstract String getTableName();

    public abstract List<String> getOriginalCodes();

    public String getCode(String value) {
        return dbHelper.getString(getTableName(), CODES_COLUMN_NAME,
                USED_CODE_COLUMN_NAME + " = ? AND " + CODES_COLUMN_NAME + " = ?",
                new String[]{"false", value});
    }

    public void deleteCode(String code) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(USED_CODE_COLUMN_NAME, "true");
        db.update(getTableName(), contentValues, CODES_COLUMN_NAME + " = ?", new String[]{code});
    }

    public void storeCodes() {
        for (String code : getOriginalCodes()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CODES_COLUMN_NAME, code);
            db.insert(getTableName(), null, contentValues);
        }
    }

    public void checkForCodeUpdates() {
        List<String> originalCodes = getOriginalCodes();
        List<String> allCodes = getAllCodes();
        if (!(allCodes.equals(originalCodes))) {
            this.updateCodes(getTableName(), originalCodes);
        }
    }

    private void updateCodes(String table, List<String> newCodesList) {
        ContentValues contentValues = new ContentValues();
        for (int i = 1, k = 0; i < newCodesList.size() - 1; i++, k++) {
            contentValues.put(CODES_COLUMN_NAME, newCodesList.get(k));
            db.update(table, contentValues, ID_COLUMN_NAME + " = ?", new String[]{String.valueOf(i)});
        }
    }

    private List<String> getAllCodes() {
        return dbHelper.getAllStrings(getTableName(), CODES_COLUMN_NAME, null, null);
    }
}
