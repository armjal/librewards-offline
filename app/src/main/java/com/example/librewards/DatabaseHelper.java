package com.example.librewards;

import static com.example.librewards.DbConstants.DATABASE_NAME;
import static com.example.librewards.DbConstants.NAME_TABLE_NAME;
import static com.example.librewards.DbConstants.POINTS_TABLE_NAME;
import static com.example.librewards.DbConstants.REWARD_CODES_TABLE_NAME;
import static com.example.librewards.DbConstants.START_CODES_TABLE_NAME;
import static com.example.librewards.DbConstants.STOP_CODES_TABLE_NAME;
import static com.example.librewards.resources.RewardCodes.rewardCodesAndPoints;
import static com.example.librewards.resources.TimerCodes.startCodes;
import static com.example.librewards.resources.TimerCodes.stopCodes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_start_codes_query = "CREATE TABLE " + START_CODES_TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT,codes TEXT) ";
        String create_stop_codes_query = "CREATE TABLE " + STOP_CODES_TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT,codes TEXT) ";
        String create_reward_codes_query = "CREATE TABLE " + REWARD_CODES_TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT,codes TEXT, cost INTEGER) ";
        String create_points_query = "CREATE TABLE " + POINTS_TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT,points INTEGER)";
        String create_name_query = "CREATE TABLE " + NAME_TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT)";
        db.execSQL(create_start_codes_query);
        db.execSQL(create_stop_codes_query);
        db.execSQL(create_reward_codes_query);
        db.execSQL(create_points_query);
        db.execSQL(create_name_query);
        db.execSQL("INSERT INTO " + POINTS_TABLE_NAME + '(' + "points" + ')' + "VALUES (?)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + START_CODES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + STOP_CODES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + REWARD_CODES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + POINTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NAME_TABLE_NAME);
        onCreate(db);
    }

    public int getRewardCost(String code) {
        int output = 0;
        Cursor c = select(REWARD_CODES_TABLE_NAME, "cost", "codes = ?", new String[]{code});
        if (c.getCount() > 0 && c.moveToFirst()) {
            output = c.getInt(0);

        }
        c.close();
        return output;
    }

    //Method that updates the codes in the database by taking in a table name and a list of codes that has been read from a file
    public void updateTimerCodes(String table, List<String> newCodesList) {
        int id = 1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < newCodesList.size(); i++) {
            contentValues.put("codes", newCodesList.get(i));
            //Uses the 'id' column to iterate through the list of codes and update each one
            db.update(table, contentValues, "id = ?", new String[]{String.valueOf(id)});
            //Iterates through codes by incrementing each id. Each id is assigned to a code and has always got a value
            id++;
        }
    }

    public List<String> refreshRewardCodes() {
        int id = 1;
        SQLiteDatabase db = this.getWritableDatabase();
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

    private void storeRewards(SQLiteDatabase db) {
        for (Map.Entry<String, Integer> entry : rewardCodesAndPoints.entrySet()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("codes", entry.getKey());
            contentValues.put("cost", entry.getValue());
            db.insert(REWARD_CODES_TABLE_NAME, null, contentValues);
        }
    }

    public void deleteTimerCode(String table, String code) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(table, "codes = ?", new String[]{code});
    }

    private void storeTimerCodes(SQLiteDatabase db, List<String> codesList, String table) {
        for (String code : codesList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("codes", code);
            db.insert(table, null, contentValues);
        }

    }

    public void addInitialCodes() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        storeTimerCodes(db, startCodes, START_CODES_TABLE_NAME);
        storeTimerCodes(db, stopCodes, STOP_CODES_TABLE_NAME);
        storeRewards(db);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public List<String> checkForTimerCodeUpdates(List<String> originalCodes, String table) {
        List<String> currentCodes = getCurrentTimerCodes(table);
        List<String> tempCodes = new ArrayList<>();
        //Loop to check if the elements in the 'currCodes' list exactly matches those in the text file. The ones that
        //match get added into a temporary list
        for (int i = 0; i < currentCodes.size(); i++) {
            for (int j = 0; j < originalCodes.size(); j++) {
                if (originalCodes.get(j).equals(currentCodes.get(i))) {
                    tempCodes.add(currentCodes.get(i));
                }
            }
        }
        //Temporary list is compared with the current codes list. If they are not an
        //exact match, the codes update using the method in the DatabaseHelper class
        if (!(currentCodes.equals(tempCodes))) {
            currentCodes = originalCodes;
            this.updateTimerCodes(table, originalCodes);
        }
        return currentCodes;
    }

    public List<String> getCurrentTimerCodes(String table) {
        List<String> codes = new ArrayList<>();
        Cursor c = select(table, "codes", null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            codes.add(c.getString(0));
            c.moveToNext();
        }
        c.close();
        return codes;
    }

    public Cursor select(String tableName, String column, String limit) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.query(tableName, new String[]{column}, null, null, null, null, null, limit);
    }

    public Cursor select(String tableName, String column, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.query(tableName, new String[]{column}, whereClause, whereArgs, null, null, null, "1");
    }
}
