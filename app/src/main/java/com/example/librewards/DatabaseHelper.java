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
import android.database.sqlite.SQLiteStatement;

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

    public void addName(String yourName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", yourName);
        db.insert(NAME_TABLE_NAME, null, contentValues);
    }

    //Method that returns the name that a user gives using a cursor
    public String getName() {
        String output = "";
        Cursor c = select(NAME_TABLE_NAME, "name", "1");
        if (c.getCount() > 0 && c.moveToFirst()) {
            output = c.getString(0);
        }
        c.close();
        return output;
    }

    //Method that returns points that a user has accumulated using a cursor
    public int getPoints() {
        int output = 0;
        Cursor c = select(POINTS_TABLE_NAME, "points", "1");
        if (c.getCount() > 0 && c.moveToFirst()) {
            output = c.getInt(0);
        }
        c.close();
        return output;
    }

    public int getCost(String code) {
        int output = 0;
        Cursor c = select(REWARD_CODES_TABLE_NAME, "cost", "codes = ?", new String[]{code});
        if (c.getCount() > 0 && c.moveToFirst()) {
            output = c.getInt(0);

        }
        c.close();
        return output;
    }

    private Cursor select(String tableName, String column, String limit) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.query(tableName, new String[]{column}, null, null, null, null, null, limit);
    }

    private Cursor select(String tableName, String column, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.query(tableName, new String[]{column}, whereClause, whereArgs, null, null, null, "1");
    }


    //Method that updates the codes in the database by taking in a table name and a list of codes that has been read from a file
    public void updateCodes(String table, List<String> newCodesList) {
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

    //Method that adds points to the current balance of points
    public void addPoints(int points) {
        int id = 1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //Uses the current balance and updates the balance with the sum of he points being passed in
        contentValues.put("points", getPoints() + points);
        db.update(POINTS_TABLE_NAME, contentValues, "id = ?", new String[]{String.valueOf(id)});
    }

    //Method that minuses points to the current balance of points
    public void minusPoints(int points) {
        int id = 1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("points", getPoints() - points);
        db.update(POINTS_TABLE_NAME, contentValues, "id = ?", new String[]{String.valueOf(id)});
    }

    //Method that stores the reward codes and their cost
    public void storeRewards() {
        SQLiteDatabase db = this.getWritableDatabase();
        for (Map.Entry<String, Integer> entry : rewardCodesAndPoints.entrySet()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("codes", entry.getKey());
            contentValues.put("cost", entry.getValue());
            db.insert(REWARD_CODES_TABLE_NAME, null, contentValues);
        }
    }

    //Method that deletes a given start/stop code from a given table that has been used so the user cannot use again
    public void deleteCode(String table, String code) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + table + " WHERE " + "codes" + "=\"" + code + "\";");
    }

    //Method that stores a list of start/stop codes in a given table
    public void storeCodes(List<String> codesList, String table) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT INTO " + table + '(' + "codes" + ')' + "VALUES (?)";
        db.beginTransaction();

        SQLiteStatement stmt = db.compileStatement(sql);
        for (int i = 0; i < codesList.size(); i++) {
            stmt.bindString(1, codesList.get(i));
            stmt.execute();
            stmt.clearBindings();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void addInitialCodes() {
        storeCodes(startCodes, START_CODES_TABLE_NAME);
        storeCodes(stopCodes, STOP_CODES_TABLE_NAME);
        storeRewards();
    }

    public List<String> checkForUpdates(List<String> currCodes, List<String> originalCodes, String table) {
        List<String> tempCodes = new ArrayList<>();
        //Loop to check if the elements in the 'currCodes' list exactly matches those in the text file. The ones that
        //match get added into a temporary list
        for (int i = 0; i < currCodes.size(); i++) {
            for (int j = 0; j < originalCodes.size(); j++) {
                if (originalCodes.get(j).equals(currCodes.get(i))) {
                    tempCodes.add(currCodes.get(i));
                }
            }
        }
        //Temporary list is compared with the current codes list. If they are not an
        //exact match, the codes update using the method in the DatabaseHelper class
        if (!(currCodes.equals(tempCodes))) {
            currCodes = originalCodes;
            this.updateCodes(table, originalCodes);
        }
        return currCodes;
    }

    public List<String> getCurrentCodes(String table) {
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
}
