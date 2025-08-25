package com.example.librewards.repositories;

import static com.example.librewards.DbConstants.REWARD_CODES_TABLE_NAME;
import static com.example.librewards.resources.RewardCodes.rewardCodesAndPoints;

import android.content.ContentValues;
import android.database.Cursor;
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
        int output = 0;
        Cursor c = dbHelper.select(REWARD_CODES_TABLE_NAME, "cost", "codes = ?", new String[]{code});
        if (c.getCount() > 0 && c.moveToFirst()) {
            output = c.getInt(0);

        }
        c.close();
        return output;
    }

    public void storeRewards() {
        for (Map.Entry<String, Integer> entry : rewardCodesAndPoints.entrySet()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("codes", entry.getKey());
            contentValues.put("cost", entry.getValue());
            db.insert(REWARD_CODES_TABLE_NAME, null, contentValues);
        }
    }

    public List<String> refreshRewardCodes() {
        int id = 1;
        for (Map.Entry<String, Integer> entry : rewardCodesAndPoints.entrySet()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("codes", entry.getKey());
            contentValues.put("cost", entry.getValue());
            //Uses the 'id' column to iterate through the list of codes and update each one
            db.update(REWARD_CODES_TABLE_NAME, contentValues, "id = ?", new String[]{String.valueOf(id)});
            id++;
        }
        return new ArrayList<>(rewardCodesAndPoints.keySet());
    }


}
