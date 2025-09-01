package com.example.librewards.data.repositories;

import static android.database.sqlite.SQLiteDatabase.CONFLICT_REPLACE;
import static com.example.librewards.data.db.DatabaseConstants.CODES_COLUMN_NAME;
import static com.example.librewards.data.db.DatabaseConstants.COST_COLUMN_NAME;
import static com.example.librewards.data.db.DatabaseConstants.ID_COLUMN_NAME;
import static com.example.librewards.data.db.DatabaseConstants.REWARD_CODES_TABLE_NAME;
import static com.example.librewards.resources.RewardCodes.rewardCodesAndPoints;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.librewards.data.db.DatabaseHelper;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RewardsRepository{
    private final SQLiteDatabase db;
    private final DatabaseHelper dbHelper;

    @Inject
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
