package com.example.librewards;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.librewards.views.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

@HiltAndroidTest
@RunWith(AndroidJUnit4.class)
public class MainActivityInstrumentedTest {

    @Rule(order = 0)
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    private ActivityScenario<MainActivity> scenario;

    @Before
    public void setUp() {
        hiltRule.inject();
        scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.RESUMED);
    }

    String expectedHelpText = "Welcome to Lib Rewards\n\nHere, you will be awarded points for the duration of your " +
            "time spent " +
            "at the library. Simply enter the library's start and stop code for the day to be awarded the points. " +
            "Once you have enough points, you can then visit the library shop to spend them. A full list of goodies " +
            "and their cost will be on display in there. The more points you rack up, the more rewards you'll get. " +
            "Here's how many points you'll get for the time spent at the library: \n\n10 - 30 minutes = 10 " +
            "points\n30 - 45 minutes = 50 points\n45 - 60 minutes = 75 points\n1 - 2 hours = 125 points\n2 - 3 hours =" +
            " 225 points\n3 - 5 hours = 400 points\n5 or more hours = 700 points\n\nPlease note that after 24 " +
            "hours of not entering a stop code the stopwatch will reset.";

    @Test
    public void test_mainActivity_givenFirstStart_AsksForNameAndProvidesHelp() {
        onView(withId(R.id.welcomeText)).check(matches(isDisplayed())).check(matches(withText(
                "Welcome to Lib Rewards! Can we start by taking your name, please?")));
        onView(withId(R.id.enterName)).perform(typeText("random name"));
        onView(withId(R.id.nameButton)).perform(click());
        onView(withId(R.id.popupText)).inRoot(isDialog()).check(matches(withText(expectedHelpText)));
        onView(withId(R.id.closeBtn)).inRoot(isDialog()).perform(click());
        onView(withId(R.id.nameTimer)).check(matches(withText("random name")));
    }
}
