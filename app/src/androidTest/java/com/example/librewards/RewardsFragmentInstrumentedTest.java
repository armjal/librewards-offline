package com.example.librewards;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.librewards.utils.FragmentTestUtils.launchFragmentInHiltContainer;

import android.os.Bundle;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.librewards.data.models.UserModel;
import com.example.librewards.repositories.RewardsRepositoryFake;
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
    @Inject
    RewardsRepositoryFake rewardsRepositoryFake;
    private UserModel user;
    private static final int POINTS_VALUE_ID = R.id.pointsRewards;
    private static final int NAME_VALUE_ID = R.id.nameRewards;
    private static final int POINTS_LABEL_ID = R.id.pointsLabelRewards;
    private static final int REWARDS_BUTTON_ID = R.id.rewardButton;
    private static final int REWARDS_TEXT_ID = R.id.rewardText;
    private static final int POPUP_TEXT = R.id.popupText;
    private static final int POPUP_CLOSE_BUTTON = R.id.closeBtn;

    @Before
    public void setUp() {
        hiltRule.inject();
        user = new UserModel(1, "test-name", 0);
        userRepositoryFake.setUser(user);
        rewardsRepositoryFake.populate();

        Bundle bundle = new Bundle();
        bundle.putParcelable("user", user);

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

    @Test
    public void test_rewardsFragment_givenCorrectCodeAndSufficientPoints_successfullyPurchasesItem() {
        userRepositoryFake.addPoints(user, 10);
        onView(withId(POINTS_VALUE_ID)).check(matches(isDisplayed())).check(matches(withText("10")));
        onView(withId(REWARDS_TEXT_ID)).perform(typeText("123456"));
        onView(withId(REWARDS_BUTTON_ID)).perform(click());
        onView(withId(POPUP_TEXT)).inRoot(isDialog()).check(matches(withText("Code accepted, keep it up! Your new " +
                "points balance is: 5")));
        onView(withId(POPUP_CLOSE_BUTTON)).inRoot(isDialog()).perform(click());
        onView(withId(POINTS_VALUE_ID)).check(matches(isDisplayed())).check(matches(withText("5")));
    }

    @Test
    public void test_rewardsFragment_givenCorrectCodeButInsufficientPoints_providesInsufficientPointsMessage() {
        onView(withId(POINTS_VALUE_ID)).check(matches(isDisplayed())).check(matches(withText("0")));
        onView(withId(REWARDS_TEXT_ID)).perform(typeText("123456"));
        onView(withId(REWARDS_BUTTON_ID)).perform(click());
        onView(withId(POPUP_TEXT)).inRoot(isDialog()).check(matches(withText("Woops! Unfortunately you don't have " +
                "sufficient points for this reward. Please choose another reward or carry on doing a great job at the " +
                "library to be able to get this")));
        onView(withId(POPUP_CLOSE_BUTTON)).inRoot(isDialog()).perform(click());
    }

    @Test
    public void test_rewardsFragment_givenIncorrectCode_doesNotPurchaseItem() {
        userRepositoryFake.addPoints(user, 10);
        onView(withId(POINTS_VALUE_ID)).check(matches(isDisplayed())).check(matches(withText("10")));
        onView(withId(REWARDS_TEXT_ID)).perform(typeText("incorrect-code"));
        onView(withId(REWARDS_BUTTON_ID)).perform(click());
        onView(withId(POINTS_VALUE_ID)).check(matches(isDisplayed())).check(matches(withText("10")));
    }
}
