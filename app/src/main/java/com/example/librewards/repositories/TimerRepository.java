package com.example.librewards.repositories;

import static com.example.librewards.DbConstants.CODES_COLUMN_NAME;
import static com.example.librewards.DbConstants.ID_COLUMN_NAME;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.librewards.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class TimerRepository {
    private final SQLiteDatabase db;
    private final DatabaseHelper dbHelper;

    public TimerRepository(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
        db = dbHelper.getWritableDatabase();
    }

    public void deleteTimerCode(String table, String code) {
        db.delete(table, CODES_COLUMN_NAME + " = ?", new String[]{code});
    }

    public void storeTimerCodes(List<String> codesList, String table) {
        for (String code : codesList) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(CODES_COLUMN_NAME, code);
            db.insert(table, null, contentValues);
        }
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

    public void updateTimerCodes(String table, List<String> newCodesList) {
        int id = 1;
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < newCodesList.size(); i++) {
            contentValues.put(CODES_COLUMN_NAME, newCodesList.get(i));
            //Uses the 'id' column to iterate through the list of codes and update each one
            db.update(table, contentValues, ID_COLUMN_NAME + " = ?", new String[]{String.valueOf(id)});
            //Iterates through codes by incrementing each id. Each id is assigned to a code and has always got a value
            id++;
        }
    }

    public List<String> getCurrentTimerCodes(String table) {
        List<String> codes = new ArrayList<>();
        Cursor c = dbHelper.select(table, CODES_COLUMN_NAME, null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            codes.add(c.getString(0));
            c.moveToNext();
        }
        c.close();
        return codes;
    }


}
