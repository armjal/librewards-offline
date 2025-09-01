package com.example.librewards;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.librewards.utils.FragmentTestUtils.launchFragmentInHiltContainer;

import android.os.Bundle;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.librewards.repositories.UserRepositoryFake;
import com.example.librewards.views.RewardsFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

@HiltAndroidTest
@RunWith(AndroidJUnit4.class)
public class RewardsFragmentInstrumentedTest {

    @Rule
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    @Inject
    UserRepositoryFake userRepositoryFake;
    private static final int POINTS_VALUE_ID = R.id.pointsRewards;
    private static final int NAME_VALUE_ID = R.id.nameRewards;
    private static final int POINTS_LABEL_ID = R.id.pointsLabelRewards;
    private static final int REWARDS_BUTTON_ID = R.id.rewardButton;
    private static final int REWARDS_TEXT_ID = R.id.rewardText;

    @Before
    public void setUp() {
        hiltRule.inject();
        userRepositoryFake.setUser("test-name", 0);

        Bundle bundle = new Bundle();
        bundle.putParcelable("user", userRepositoryFake.getUser());

        launchFragmentInHiltContainer(RewardsFragment.class, bundle, R.style.AppTheme, null);
    }

    @Test
    public void test_rewardsFragment_hasCoreUiElementsCorrectlySetup() {
        onView(withId(NAME_VALUE_ID)).check(matches(isDisplayed())).check(matches(withText("Hey, test-name")));
        onView(withId(POINTS_VALUE_ID)).check(matches(isDisplayed())).check(matches(withText("0")));
        onView(withId(POINTS_LABEL_ID)).check(matches(isDisplayed())).check(matches(withText("Points")));
        onView(withId(REWARDS_BUTTON_ID)).check(matches(isDisplayed())).check(matches(withText("GET REWARDED NOW!")));
        onView(withId(REWARDS_TEXT_ID)).check(matches(withHint("Please enter your chosen reward code")));
    }
}
