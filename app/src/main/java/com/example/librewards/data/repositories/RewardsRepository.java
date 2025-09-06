package com.example.librewards.data.repositories;

import static com.example.librewards.data.db.DatabaseConstants.CODES_COLUMN_NAME;
import static com.example.librewards.data.db.DatabaseConstants.COST_COLUMN_NAME;
import static com.example.librewards.data.db.DatabaseConstants.REWARD_CODES_TABLE_NAME;
import static com.example.librewards.resources.RewardCodes.getRewardCodes;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.librewards.data.db.DatabaseHelper;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RewardsRepository {
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

    public Integer getCost(String code) {
        return dbHelper.getInt(REWARD_CODES_TABLE_NAME, COST_COLUMN_NAME, CODES_COLUMN_NAME + " = ?",
                new String[]{code});
    }

    public void populate() {
        ContentValues contentValues = new ContentValues();
        db.delete(REWARD_CODES_TABLE_NAME, null, null);
        for (Map.Entry<String, Integer> entry : getRewardCodes().entrySet()) {
            contentValues.put(CODES_COLUMN_NAME, entry.getKey());
            contentValues.put(COST_COLUMN_NAME, entry.getValue());
            db.insert(REWARD_CODES_TABLE_NAME, null, contentValues);
        }
    }
}
