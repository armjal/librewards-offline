package com.example.librewards.repositories;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE;
import static com.example.librewards.DbConstants.CODES_COLUMN_NAME;
import static com.example.librewards.DbConstants.COST_COLUMN_NAME;
import static com.example.librewards.DbConstants.ID_COLUMN_NAME;
import static com.example.librewards.DbConstants.REWARD_CODES_TABLE_NAME;
import static com.example.librewards.resources.RewardCodes.rewardCodesAndPoints;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.librewards.DatabaseHelper;

import java.util.Map;

public class RewardsRepository {
    private final SQLiteDatabase db;
    private final DatabaseHelper dbHelper;

    public RewardsRepository(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        db = dbHelper.getWritableDatabase();
    }

    public String getCode(String code) {
        return dbHelper.getString(REWARD_CODES_TABLE_NAME, CODES_COLUMN_NAME, CODES_COLUMN_NAME + " = ?",
                new String[]{code});
    }

    public int getCost(String code) {
        return dbHelper.getInt(REWARD_CODES_TABLE_NAME, COST_COLUMN_NAME, CODES_COLUMN_NAME + " = ?",
                new String[]{code});
    }

    public void populate() {
        int id = 1;
        for (Map.Entry<String, Integer> entry : rewardCodesAndPoints.entrySet()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ID_COLUMN_NAME, id);
            contentValues.put(CODES_COLUMN_NAME, entry.getKey());
            contentValues.put(COST_COLUMN_NAME, entry.getValue());
            db.insertWithOnConflict(REWARD_CODES_TABLE_NAME, null, contentValues, CONFLICT_REPLACE);
            id++;
        }
    }
}
