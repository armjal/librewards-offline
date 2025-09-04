package com.example.librewards.repositories;

import static com.example.librewards.resources.TimerCodesTest.startCodesTest;


import android.os.Build;

import com.example.librewards.data.db.DatabaseHelper;
import com.example.librewards.data.repositories.StartCodesRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

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

    @Before
    public void setUp() {
        hiltAndroidRule.inject();

    }

    @Test
    public void test_startCodeRepo_getOriginalCodes_returnsOriginalCodes(){
        StartCodesRepository startCodesRepo = new StartCodesRepository(databaseHelper, startCodesTest);
        assert startCodesRepo.getOriginalCodes() == startCodesTest;
    }

    @Test
    public void test_startCodeRepo_get_returnsExistingCodeFromDb(){
        StartCodesRepository startCodesRepo = new StartCodesRepository(databaseHelper, startCodesTest);
        startCodesRepo.populate();
        assert startCodesRepo.get("123456").equals("123456");
    }
}
