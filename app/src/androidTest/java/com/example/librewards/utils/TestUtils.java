package com.example.librewards.utils;

import android.database.sqlite.SQLiteDatabase;

public class TestUtils {
    public static void clearTables(SQLiteDatabase db){
        db.delete("user_table", null, null);
        db.delete("start_codes_table", null, null);
        db.delete("stop_codes_table", null, null);
        db.delete("reward_codes_table", null, null);
    }
}
