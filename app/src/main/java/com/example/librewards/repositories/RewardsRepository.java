package com.example.librewards.repositories;

import static com.example.librewards.DbConstants.CODES_COLUMN_NAME;
import static com.example.librewards.DbConstants.COST_COLUMN_NAME;
import static com.example.librewards.DbConstants.ID_COLUMN_NAME;
import static com.example.librewards.DbConstants.REWARD_CODES_TABLE_NAME;
import static com.example.librewards.resources.RewardCodes.rewardCodesAndPoints;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.example.librewards.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RewardsRepository {
    private final SQLiteDatabase db;
    private final DatabaseHelper dbHelper;

    public RewardsRepository(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        db = dbHelper.getWritableDatabase();
    }


    public int getRewardCost(String code) {
        return dbHelper.getInt(REWARD_CODES_TABLE_NAME, COST_COLUMN_NAME, CODES_COLUMN_NAME + " = ?",
                new String[]{code});
    }

    public void storeRewards() {
        for (Map.Entry<String, Integer> entry : rewardCodesAndPoints.entrySet()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CODES_COLUMN_NAME, entry.getKey());
            contentValues.put(COST_COLUMN_NAME, entry.getValue());
            db.insert(REWARD_CODES_TABLE_NAME, null, contentValues);
        }
    }

    public List<String> refreshRewardCodes() {
        int id = 1;
        for (Map.Entry<String, Integer> entry : rewardCodesAndPoints.entrySet()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CODES_COLUMN_NAME, entry.getKey());
            contentValues.put(COST_COLUMN_NAME, entry.getValue());
            //Uses the 'id' column to iterate through the list of codes and update each one
            db.update(REWARD_CODES_TABLE_NAME, contentValues, ID_COLUMN_NAME + " = ?",
                    new String[]{String.valueOf(id)});
            id++;
        }
        return new ArrayList<>(rewardCodesAndPoints.keySet());
    }


}
