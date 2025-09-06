package com.example.librewards.repositories;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mockStatic;

import android.database.Cursor;
import android.os.Build;

import com.example.librewards.data.db.DatabaseHelper;
import com.example.librewards.data.repositories.RewardsRepository;
import com.example.librewards.resources.RewardCodes;
import com.example.librewards.resources.RewardCodesTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

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
    public void test_rewardsRepo_populate_populatesDbWithRewardCodes() {
        Map<String, Integer> rewardsBeforePopulation = getRewardsFromDb();

        rewardsRepo.populate();
        Map<String, Integer> rewardsAfterPopulation = getRewardsFromDb();

        assertThat(rewardsBeforePopulation, equalTo(Map.of()));
        assertThat(rewardsAfterPopulation, equalTo(RewardCodesTest.rewardCodesAndPoints));
    }

    @Test
    public void test_rewardsRepo_populate_givenNewRewardCodes_replacesExisting() {
        rewardsRepo.populate();
        Map<String, Integer> rewardsAfterPopulation = getRewardsFromDb();

        Map<String, Integer> newRewards = Map.of("876543", 20, "987643", 30);
        mockedRewardCodes.when(RewardCodes::getRewardCodes).thenReturn(newRewards);

        rewardsRepo.populate();
        Map<String, Integer> rewardsAfterSecondPopulation = getRewardsFromDb();

        assertThat(rewardsAfterPopulation, equalTo(RewardCodesTest.rewardCodesAndPoints));
        assertThat(rewardsAfterSecondPopulation, equalTo(newRewards));
    }

    @Test
    public void test_rewardsRepo_getCode_returnsCodeFromDb() {
        rewardsRepo.populate();

        String codeInDb = rewardsRepo.getCode("123456");

        assertThat(codeInDb, equalTo("123456"));
    }

    @Test
    public void test_rewardsRepo_getCode_givenIncorrectCode_returnsEmptyString() {
        rewardsRepo.populate();

        String codeInDb = rewardsRepo.getCode("incorrect code");

        assertThat(codeInDb, equalTo(""));
    }

    @Test
    public void test_rewardsRepo_getCost_returnsCostOfReward() {
        rewardsRepo.populate();

        Integer costInDb = rewardsRepo.getCost("123456");

        assertThat(costInDb, equalTo(5));
    }

    @Test
    public void test_rewardsRepo_getCost_givenIncorrectCode_returnsZero() {
        rewardsRepo.populate();

        Integer costInDb = rewardsRepo.getCost("incorrect code");

        assertThat(costInDb, equalTo(null));
    }

    private Map<String, Integer> getRewardsFromDb() {
        Cursor c = databaseHelper.getWritableDatabase().query("reward_codes_table", new String[]{"codes", "cost"},
                null, null,
                null, null, null);
        c.moveToFirst();
        Map<String, Integer> rewards = new HashMap<>();
        while (!c.isAfterLast()) {
            rewards.put(c.getString(0), c.getInt(1));
            c.moveToNext();
        }
        c.close();
        return rewards;
    }

    @After
    public void tearDown() {
        if (mockedRewardCodes != null) {
            mockedRewardCodes.close();
        }
    }
}
