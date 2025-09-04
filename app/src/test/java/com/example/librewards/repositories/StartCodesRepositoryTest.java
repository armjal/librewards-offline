package com.example.librewards.repositories;

import static com.example.librewards.resources.TimerCodesTest.startCodesTest;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import android.os.Build;

import com.example.librewards.data.db.DatabaseHelper;
import com.example.librewards.data.repositories.StartCodesRepository;
import com.example.librewards.resources.TimerCodes;
import com.example.librewards.resources.TimerCodesTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

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
    @Before
    public void setUp() {
        hiltAndroidRule.inject();
        mockedTimerCodes = mockStatic(TimerCodes.class);
        mockedTimerCodes.when(TimerCodes::getStartCodes).thenReturn(startCodesTest);

    }

    @Test
    public void test_startCodeRepo_getOriginalCodes_returnsOriginalCodes(){
        StartCodesRepository startCodesRepo = new StartCodesRepository(databaseHelper);

        assert startCodesRepo.getOriginalCodes() == startCodesTest;
    }

    @Test
    public void test_startCodeRepo_get_returnsExistingCodeFromDb(){
        StartCodesRepository startCodesRepo = new StartCodesRepository(databaseHelper);
        startCodesRepo.populate();

        assert startCodesRepo.get("123456").equals("123456");
    }

    @Test
    public void test_startCodeRepo_get_givenIncorrectCode_returnsEmptyString(){
        StartCodesRepository startCodesRepo = new StartCodesRepository(databaseHelper);
        startCodesRepo.populate();

        assert startCodesRepo.get("987623").isEmpty();
    }

    @Test
    public void test_startCodeRepo_delete_successfullyDeletesCodeFromDb(){
        StartCodesRepository startCodesRepo = new StartCodesRepository(databaseHelper);
        startCodesRepo.populate();
        String codeBeforeDelete = startCodesRepo.get("123456");
        startCodesRepo.delete("123456");
        String codeAfterDelete = startCodesRepo.get("123456");

        assert codeBeforeDelete.equals("123456");
        assert codeAfterDelete.isEmpty();
    }

    @Test
    public void test_startCodeRepo_delete_givenNonExistentCode_doesNotError(){
        StartCodesRepository startCodesRepo = new StartCodesRepository(databaseHelper);
        startCodesRepo.populate();
        startCodesRepo.delete("hello");
    }

    @Test
    public void test_startCodeRepo_checkForUpdates_givenExistingCodes_doesNotUpdate(){
        StartCodesRepository startCodesRepo = new StartCodesRepository(databaseHelper);
        startCodesRepo.populate();
        startCodesRepo.checkForUpdates();

        assert startCodesRepo.get("123456").equals("123456");
    }

    @After
    public void tearDown() {
        if (mockedTimerCodes != null) {
            mockedTimerCodes.close();
        }
    }

}
