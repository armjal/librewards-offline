package com.example.librewards;

import static com.example.librewards.DbConstants.DATABASE_NAME;
import static com.example.librewards.DbConstants.NAME_TABLE_NAME;
import static com.example.librewards.DbConstants.POINTS_TABLE_NAME;
import static com.example.librewards.DbConstants.REWARD_CODES_FILE_NAME;
import static com.example.librewards.DbConstants.REWARD_CODES_TABLE_NAME;
import static com.example.librewards.DbConstants.START_CODES_FILE_NAME;
import static com.example.librewards.DbConstants.START_CODES_TABLE_NAME;
import static com.example.librewards.DbConstants.STOP_CODES_FILE_NAME;
import static com.example.librewards.DbConstants.STOP_CODES_TABLE_NAME;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private final ListFromFile listFromFile;

    public DatabaseHelper(Context context)  {
        super(context, DATABASE_NAME, null, 1);
        this.listFromFile = new ListFromFile(context);
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
        db.execSQL("DROP TABLE IF EXISTS "+ START_CODES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ STOP_CODES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ REWARD_CODES_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ POINTS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ NAME_TABLE_NAME);
        onCreate(db);
    }
    //Method that adds the name that the user gives to the database.
    public void addName(String yourName){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = ("INSERT INTO " + NAME_TABLE_NAME + '(' + "name" + ')' + "VALUES (?)");
        db.beginTransaction();
        SQLiteStatement stmt = db.compileStatement(sql);
        stmt.bindString(1,yourName);
        stmt.execute();
        stmt.clearBindings();
        db.setTransactionSuccessful();
        db.endTransaction();

    }
    //Cursor method that goes through the contents of a given column and table and returns values within them
    public Cursor getAllData(String col, String table){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT " +  col  + " FROM " +  table, null);
    }

    //Method that returns the name that a user gives using a cursor
    public String getName(){
        SQLiteDatabase db = this.getWritableDatabase();
        String output = "";
        Cursor c = db.rawQuery("SELECT " + "name" + " FROM " + NAME_TABLE_NAME, null);
        if(c.getCount() > 0 && c.moveToFirst()) {
                output = c.getString(c.getColumnIndex("name"));
        }
        c.close();
        return output;
    }

    //Method that returns points that a user has accumulated using a cursor
    public int getPoints(){
        SQLiteDatabase db = this.getWritableDatabase();
        int output = 0;
        Cursor c = db.rawQuery("SELECT " + "points" + " FROM " + POINTS_TABLE_NAME, null);
        if(c.getCount() > 0 && c.moveToFirst()){
                output = c.getInt(0);
            }


        c.close();
        return output;
    }

    //Method that returns the cost of a reward code that a user inputs
    public int getCost(String code) {
        SQLiteDatabase db = this.getWritableDatabase();
        int output = 0;
        Cursor c = db.rawQuery("SELECT " + "cost" + " FROM " + REWARD_CODES_TABLE_NAME + " WHERE codes = " + "'" + code + "'", null);
        if (c.getCount() > 0 && c.moveToFirst()) {
                output = c.getInt(c.getColumnIndex("cost"));

            }

        c.close();
        return output;
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
    //Method that updates the reward codes if the text file is different to the one stored in the database
    public List<String> refreshRewardCodes() {
        List<String> newCodesList = listFromFile.readRewardsLine(REWARD_CODES_FILE_NAME);
        int id = 1;
        //'j' is the integer that gets the cost of each code
        int j = 1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //'i' gets the code in the table. Each value increments by two as each two incremented values belong in the same column
        for (int i = 0; i < newCodesList.size()-1; i+=2) {
            contentValues.put("codes", newCodesList.get(i));
            contentValues.put("cost", newCodesList.get(j));
            //Uses the 'id' column to iterate through the list of codes and update each one
            db.update(REWARD_CODES_TABLE_NAME, contentValues, "id = ?", new String[]{String.valueOf(id)});
            //Iterates through codes by incrementing each id. Each id is assigned to a code and has always got a value
            id++;
            j+=2;
        }
        return newCodesList;
    }
    //Method that adds points to the current balance of points
    public void addPoints(int points){
        int id = 1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //Uses the current balance and updates the balance with the sum of he points being passed in
        contentValues.put("points", getPoints()+ points);
        db.update(POINTS_TABLE_NAME, contentValues, "id = ?", new String[]{String.valueOf(id)});
    }
    //Method that minuses points to the current balance of points
    public void minusPoints(int points){
        int id = 1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("points", getPoints() - points);
        db.update(POINTS_TABLE_NAME, contentValues, "id = ?", new String[]{String.valueOf(id)});
    }
    //Method that stores the reward codes and their cost
    public void storeRewards(List<String> rewardList){
        SQLiteDatabase db = this.getWritableDatabase();
        //Stores the contents in the two columns specified for the column.
        String sql = "INSERT INTO " + REWARD_CODES_TABLE_NAME + '('+ "codes,cost" + ')' +  "VALUES (?,?)";
        db.beginTransaction();
        //j is the integer that gets the cost of each code
        int j = 1;
        SQLiteStatement stmt = db.compileStatement(sql);
        //'i' gets the code in the table. Each value increments by two as each two incremented values belong in the same column
        for(int i=0 ; i< rewardList.size()-1; i+=2) {
            //Values are assigned to each row in the table
                stmt.bindString(1, rewardList.get(i));
                stmt.bindString(2, rewardList.get(j));
                stmt.execute();
                stmt.clearBindings();
                j+=2;
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }
    //Method that deletes a given start/stop code from a given table that has been used so the user cannot use again
    public void deleteCode(String table, String code){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + table + " WHERE "+ "codes" + "=\"" + code + "\";");
    }

    //Method that stores a list of start/stop codes in a given table
    public void storeCodes(List<String> codesList, String table){
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT INTO " + table + '('+ "codes" + ')' +  "VALUES (?)";
        db.beginTransaction();

        SQLiteStatement stmt = db.compileStatement(sql);
        for(int i=0 ; i< codesList.size(); i++){
            stmt.bindString(1,codesList.get(i));
            stmt.execute();
            stmt.clearBindings();
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public void addInitialCodes(){
        List<String> startList = listFromFile.readLine(START_CODES_FILE_NAME);
        List<String> stopList = listFromFile.readLine(STOP_CODES_FILE_NAME);
        List<String> rewardsList = listFromFile.readRewardsLine(REWARD_CODES_FILE_NAME);

        storeCodes(startList, START_CODES_TABLE_NAME);
        storeCodes(stopList, STOP_CODES_TABLE_NAME);
        storeRewards(rewardsList);
    }

    public List<String> checkForUpdates(List<String> currCodes, List<String> originalCodes, String table){
        List<String> tempCodes = new ArrayList<>();
        //Loop to check if the elements in the 'currCodes' list exactly matches those in the text file. The ones that
        //match get added into a temporary list
        for(int i = 0; i<currCodes.size(); i++){
            for (int j = 0; j<originalCodes.size(); j++){
                if(originalCodes.get(j).equals(currCodes.get(i))){
                    tempCodes.add(currCodes.get(i));
                }
            }
        }
        //Temporary list is compared with the current codes list. If they are not an
        //exact match, the codes update using the method in the DatabaseHelper class
        if(!(currCodes.equals(tempCodes))){
            currCodes = originalCodes;
            this.updateCodes(table,originalCodes);
        }
        return currCodes;
    }

    public List<String> getCurrentCodes(String table) {
        List<String> codes = new ArrayList<>();
        Cursor c = this.getAllData("codes", table);
        c.moveToFirst();
        while(!c.isAfterLast()) {
            codes.add(c.getString(c.getColumnIndex("codes")));
            c.moveToNext();
        }
        return codes;
    }
}
