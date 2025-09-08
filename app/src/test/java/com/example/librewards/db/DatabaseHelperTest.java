package com.example.librewards.db;

import static com.example.librewards.resources.TimerCodesTest.startCodesTest;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import com.example.librewards.data.db.DatabaseHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import dagger.hilt.android.testing.HiltTestApplication;

@Config(application = HiltTestApplication.class, sdk = {Build.VERSION_CODES.P})
@RunWith(RobolectricTestRunner.class)
public class DatabaseHelperTest {

    private DatabaseHelper databaseHelper;

    @Before
    public void setUp() {
        databaseHelper = new DatabaseHelper(ApplicationProvider.getApplicationContext(), "test_librewards.db");
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

    @Test
    public void test_databaseHelper_successfullyInteractsWithStartCodesTable() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("codes", "123456");

        db.insert("start_codes_table", null, contentValues);
        int id = databaseHelper.getInt("start_codes_table", "id", null, null);
        String code = databaseHelper.getString("start_codes_table", "codes", null, null);
        String used = databaseHelper.getString("start_codes_table", "used", null, null);

        assertThat(id, equalTo(1));
        assertThat(code, equalTo("123456"));
        assertThat(used, equalTo("false"));
    }

    @Test
    public void test_databaseHelper_successfullyInteractsWithStopCodesTable() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("codes", "123456");

        db.insert("stop_codes_table", null, contentValues);
        int id = databaseHelper.getInt("stop_codes_table", "id", null, null);
        String code = databaseHelper.getString("stop_codes_table", "codes", null, null);
        String used = databaseHelper.getString("stop_codes_table", "used", null, null);

        assertThat(id, equalTo(1));
        assertThat(code, equalTo("123456"));
        assertThat(used, equalTo("false"));
    }

    @Test
    public void test_databaseHelper_successfullyInteractsWithRewardCodesTable() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("codes", "123456");
        contentValues.put("cost", "5");

        db.insert("reward_codes_table", null, contentValues);
        int id = databaseHelper.getInt("reward_codes_table", "id", null, null);
        String code = databaseHelper.getString("reward_codes_table", "codes", null, null);
        int cost = databaseHelper.getInt("reward_codes_table", "cost", null, null);

        assertThat(id, equalTo(1));
        assertThat(code, equalTo("123456"));
        assertThat(cost, equalTo(5));
    }

    @Test
    public void test_databaseHelper_successfullyInteractsWithUserTable() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", "John");
        contentValues.put("points", "7");


        db.insert("user_table", null, contentValues);
        int id = databaseHelper.getInt("user_table", "id", null, null);
        String name = databaseHelper.getString("user_table", "name", null, null);
        int points = databaseHelper.getInt("user_table", "points", null, null);

        assertThat(id, equalTo(1));
        assertThat(name, equalTo("John"));
        assertThat(points, equalTo(7));
    }

    @Test
    public void test_databaseHelper_givenANeedToMaintainAtomicity_successfullyProcessesTransaction() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        try {
            databaseHelper.processTransaction(() -> {
                ContentValues userContentValues = new ContentValues();
                userContentValues.put("name", "John");
                userContentValues.put("points", "7");
                db.insert("user_table", null, userContentValues);

                ContentValues rewardsContentValues = new ContentValues();
                rewardsContentValues.put("codes", "123456");
                rewardsContentValues.put("cost", "5");
                db.insert("reward_codes_table", null, rewardsContentValues);
            });
        } catch (NullPointerException ignored) {
        }
        String name = databaseHelper.getString("user_table", "name", null, null);
        String code = databaseHelper.getString("reward_codes_table", "codes", null, null);

        assertThat(name, equalTo("John"));
        assertThat(code, equalTo("123456"));
    }

    @Test
    public void test_databaseHelper_givenAnExceptionRaisedDuringTransaction_doesNotWriteToDb() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        try {
            databaseHelper.processTransaction(() -> {
                ContentValues userContentValues = new ContentValues();
                userContentValues.put("name", "John");
                userContentValues.put("points", "7");
                db.insert("user_table", null, userContentValues);

                ContentValues rewardsContentValues = new ContentValues();
                rewardsContentValues.put("codes", "123456");
                rewardsContentValues.put("cost", "5");
                db.insert("reward_codes_table", null, rewardsContentValues);

                throw new NullPointerException();
            });
        } catch (NullPointerException ignored) {
        }
        String name = databaseHelper.getString("user_table", "name", null, null);
        String code = databaseHelper.getString("reward_codes_table", "codes", null, null);

        assertThat(name, equalTo(""));
        assertThat(code, equalTo(""));
    }

    @Test
    public void test_databaseHelper_getAllStrings_returnsListOfStringsForColumn() {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        for (String code : startCodesTest) {
            contentValues.put("codes", code);
            db.insert("start_codes_table", null, contentValues);
        }

        List<String> codesInDb = databaseHelper.getAllStrings("start_codes_table", "codes", null, null);
        assertThat(codesInDb, equalTo(startCodesTest));
    }
}
