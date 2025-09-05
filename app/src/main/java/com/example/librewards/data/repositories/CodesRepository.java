package com.example.librewards.data.repositories;

import static com.example.librewards.data.db.DatabaseConstants.CODES_COLUMN_NAME;
import static com.example.librewards.data.db.DatabaseConstants.ID_COLUMN_NAME;
import static com.example.librewards.data.db.DatabaseConstants.USED_CODE_COLUMN_NAME;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.librewards.data.db.DatabaseHelper;

import java.util.List;

public abstract class CodesRepository implements CodesRepositoryInterface {
    public static final String USED_CODE_VAL = "true";
    public static final String UNUSED_CODE_VAL = "false";

    private final SQLiteDatabase db;
    private final DatabaseHelper dbHelper;

    public CodesRepository(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        db = dbHelper.getWritableDatabase();
    }

    public abstract String getTableName();

    public abstract List<String> getOriginalCodes();

    public String get(String value) {
        return dbHelper.getString(getTableName(), CODES_COLUMN_NAME,
                USED_CODE_COLUMN_NAME + " = ? AND " + CODES_COLUMN_NAME + " = ?",
                new String[]{UNUSED_CODE_VAL, value});
    }

    public void delete(String code) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(USED_CODE_COLUMN_NAME, USED_CODE_VAL);
        db.update(getTableName(), contentValues, CODES_COLUMN_NAME + " = ?", new String[]{code});
    }

    public void populate() {
        for (String code : getOriginalCodes()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CODES_COLUMN_NAME, code);
            db.insert(getTableName(), null, contentValues);
        }
    }

    public void checkForUpdates() {
        List<String> originalCodes = getOriginalCodes();
        List<String> allCodes = getAll();
        if (!(allCodes.equals(originalCodes))) {
            this.update(getTableName(), originalCodes);
        }
    }

    private List<String> getAll() {
        return dbHelper.getAllStrings(getTableName(), CODES_COLUMN_NAME, null, null);
    }

    private void update(String table, List<String> newCodesList) {
        ContentValues contentValues = new ContentValues();
        for (int i = 1, k = 0; k < newCodesList.size(); i++, k++) {
            contentValues.put(CODES_COLUMN_NAME, newCodesList.get(k));
            db.update(table, contentValues, ID_COLUMN_NAME + " = ?", new String[]{String.valueOf(i)});
        }
    }
}
