package com.example.librewards.repositories;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mockStatic;

import android.os.Build;

import com.example.librewards.data.db.DatabaseHelper;
import com.example.librewards.data.repositories.RewardsRepository;
import com.example.librewards.resources.RewardCodes;
import com.example.librewards.resources.RewardCodesTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.HiltTestApplication;

@HiltAndroidTest
@Config(application = HiltTestApplication.class, sdk = {Build.VERSION_CODES.P})
@RunWith(RobolectricTestRunner.class)
public class RewardsRepositoryTest {
    @Rule
    public HiltAndroidRule hiltAndroidRule = new HiltAndroidRule(this);
    @Inject
    public DatabaseHelper databaseHelper;
    RewardsRepository rewardsRepo;
    MockedStatic<RewardCodes> mockedRewardCodes;

    @Before
    public void setUp() {
        hiltAndroidRule.inject();
        rewardsRepo = new RewardsRepository(databaseHelper);
        mockedRewardCodes = mockStatic(RewardCodes.class);
        mockedRewardCodes.when(RewardCodes::getRewardCodes).thenReturn(RewardCodesTest.rewardCodesAndPoints);
    }

    @Test
    public void test_rewardsRepo_getCode_returnsCodeFromDb() {
        rewardsRepo.populate();

        String codeInDb = rewardsRepo.getCode("123456");

        assertThat(codeInDb, equalTo("123456"));
    }
}