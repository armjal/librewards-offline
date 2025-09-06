package com.example.librewards.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.core.app.ApplicationProvider;

import com.example.librewards.data.db.DatabaseHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class DatabaseHelperTest {

    private DatabaseHelper databaseHelper;

    @Before
    public void setUp() {
        databaseHelper = new DatabaseHelper(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void test_databaseHelper_successfullyCreateAllTablesAndColumns() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor c1 = db.query("start_codes_table", new String[]{"id", "codes", "used"}, null, null, null, null, null);
        Cursor c2 = db.query("stop_codes_table", new String[]{"id", "codes", "used"}, null, null, null, null, null);
        Cursor c3 = db.query("reward_codes_table", new String[]{"id", "codes", "cost"}, null, null, null, null, null,
                null);
        Cursor c4 = db.query("user_table", new String[]{"id", "name", "points"}, null, null, null, null, null);
        c1.close();
        c2.close();
        c3.close();
        c4.close();
    }
}
