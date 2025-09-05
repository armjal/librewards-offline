package com.example.librewards.repositories;

import static com.example.librewards.resources.TimerCodesTest.startCodesTest;
import static org.mockito.Mockito.mockStatic;

import android.os.Build;

import com.example.librewards.data.db.DatabaseHelper;
import com.example.librewards.data.repositories.StartCodesRepository;
import com.example.librewards.resources.TimerCodes;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.HiltTestApplication;

@HiltAndroidTest
@Config(application = HiltTestApplication.class, sdk = {Build.VERSION_CODES.P})
@RunWith(RobolectricTestRunner.class)
public class StartCodesRepositoryTest {
    @Rule
    public HiltAndroidRule hiltAndroidRule = new HiltAndroidRule(this);
    @Inject
    public DatabaseHelper databaseHelper;
    MockedStatic<TimerCodes> mockedTimerCodes;
    StartCodesRepository startCodesRepo;

    @Before
    public void setUp() {
        hiltAndroidRule.inject();
        startCodesRepo = new StartCodesRepository(databaseHelper);
        mockedTimerCodes = mockStatic(TimerCodes.class);
        mockedTimerCodes.when(TimerCodes::getStartCodes).thenReturn(startCodesTest);

    }

    @Test
    public void test_startCodeRepo_getOriginalCodes_returnsOriginalCodes() {
        startCodesRepo = new StartCodesRepository(databaseHelper);

        assert startCodesRepo.getOriginalCodes() == startCodesTest;
    }

    @Test
    public void test_startCodeRepo_populate_successfullyPopulatesDbWithCodesAndUsedState() {
        List<String> codesColumnBeforePopulation = databaseHelper.getAllStrings("start_codes_table", "codes", null,
                null);
        startCodesRepo.populate();
        List<String> codesColumnAfterPopulation = databaseHelper.getAllStrings("start_codes_table", "codes", null,
                null);
        String isCodeUsed = databaseHelper.getString("start_codes_table", "used", "codes = ?", new String[]{"123456"});

        assert isCodeUsed.equals("false");
        assert codesColumnBeforePopulation.equals(new ArrayList<>());
        assert codesColumnAfterPopulation.equals(startCodesTest);
    }

    @Test
    public void test_startCodeRepo_get_returnsExistingCodeFromDb() {
        startCodesRepo.populate();

        assert startCodesRepo.get("123456").equals("123456");
    }

    @Test
    public void test_startCodeRepo_get_givenIncorrectCode_returnsEmptyString() {
        startCodesRepo.populate();

        assert startCodesRepo.get("987623").isEmpty();
    }

    @Test
    public void test_startCodeRepo_delete_successfullySoftDeletesCodeAndSetsItToUsed() {
        startCodesRepo.populate();
        String codeBeforeDelete = startCodesRepo.get("123456");
        startCodesRepo.delete("123456");
        String codeAfterDelete = startCodesRepo.get("123456");
        String isCodeUsed = databaseHelper.getString("start_codes_table", "used", "codes = ?", new String[]{"123456"});

        assert codeBeforeDelete.equals("123456");
        assert codeAfterDelete.isEmpty();
        assert isCodeUsed.equals("true");
    }

    @Test
    public void test_startCodeRepo_delete_givenNonExistentCode_doesNotError() {
        startCodesRepo.populate();
        startCodesRepo.delete("hello");
    }

    @Test
    public void test_startCodeRepo_checkForUpdates_givenExistingCodes_doesNotUpdate() {
        startCodesRepo.populate();
        startCodesRepo.checkForUpdates();

        assert startCodesRepo.get("123456").equals("123456");
    }

    @Test
    public void test_startCodeRepo_checkForUpdates_givenNewCodes_updates() {
        startCodesRepo.populate();
        assert startCodesRepo.get("123456").equals("123456");
        mockedTimerCodes.when(TimerCodes::getStartCodes).thenReturn(List.of("random", "codes"));
        startCodesRepo.checkForUpdates();

        assert startCodesRepo.get("random").equals("random");
        assert startCodesRepo.get("codes").equals("codes");
        assert startCodesRepo.get("123456").isEmpty();
    }

    @Test
    public void test_startCodeRepo_get_givenUsedCode_returnsEmptyString() {
        startCodesRepo.populate();
        startCodesRepo.delete("123456");
    }

    @After
    public void tearDown() {
        if (mockedTimerCodes != null) {
            mockedTimerCodes.close();
        }
    }

}
