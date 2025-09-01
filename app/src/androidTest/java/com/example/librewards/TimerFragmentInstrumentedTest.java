package com.example.librewards;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.librewards.utils.FragmentTestUtils.launchFragmentInHiltContainer;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.os.Bundle;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.librewards.repositories.StartCodesRepositoryFake;
import com.example.librewards.repositories.StopCodesRepositoryFake;
import com.example.librewards.repositories.UserRepositoryFake;
import com.example.librewards.views.TimerFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

@HiltAndroidTest
@RunWith(AndroidJUnit4.class)
public class TimerFragmentInstrumentedTest {

    @Rule
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    @Inject
    UserRepositoryFake userRepositoryFake;
    @Inject
    StartCodesRepositoryFake startCodesRepositoryFake;
    @Inject
    StopCodesRepositoryFake stopCodesRepositoryFake;
    private static final int POINTS_VALUE_ID = R.id.points;
    private static final int NAME_VALUE_ID = R.id.nameTimer;
    private static final int POINTS_LABEL_ID = R.id.textView2;
    private static final int START_BUTTON_ID = R.id.startButton;
    private static final int TIMER_CODE_TEXT = R.id.timerCodeText;
    private static final int STOP_BUTTON_ID = R.id.stopButton;
    private static final int TIMER_ID = R.id.timer;
    private static final int POPUP_TEXT = R.id.popupText;

    @Before
    public void setUp() {
        hiltRule.inject();
        startCodesRepositoryFake.populate();
        stopCodesRepositoryFake.populate();
        userRepositoryFake.setUser("test-name", 0);

        Bundle bundle = new Bundle();
        bundle.putParcelable("user", userRepositoryFake.getUser());

        launchFragmentInHiltContainer(TimerFragment.class, bundle, R.style.AppTheme, null);
    }

    @Test
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.librewards", appContext.getPackageName());
    }

    @Test
    public void test_timerFragment_hasCoreUiElementsCorrectlySetup() {
        onView(withId(NAME_VALUE_ID)).check(matches(isDisplayed())).check(matches(withText("Hey, test-name")));
        onView(withId(POINTS_VALUE_ID)).check(matches(isDisplayed())).check(matches(withText("0")));
        onView(withId(POINTS_LABEL_ID)).check(matches(isDisplayed())).check(matches(withText("Points")));
        onView(withId(START_BUTTON_ID)).check(matches(isDisplayed())).check(matches(withText("start")));
        onView(withId(TIMER_CODE_TEXT)).check(matches(withHint("Please enter the start code")));
        onView(withId(STOP_BUTTON_ID)).check(matches(not(isDisplayed()))).check(matches(withText("stop")));
        onView(withId(TIMER_ID)).check(matches(isDisplayed())).check(matches(withText("00:00")));
    }

    @Test
    public void test_timerFragment_startsTimerWithCorrectCode() throws InterruptedException {
        onView(withId(TIMER_CODE_TEXT)).perform(typeText("123456"));
        onView(withId(START_BUTTON_ID)).perform(click());
        onView(withId(TIMER_CODE_TEXT)).check(matches(withHint("Please enter the stop code")));
        onView(withId(STOP_BUTTON_ID)).check(matches(isDisplayed()));
        Thread.sleep(1000);
        onView(withId(TIMER_ID)).check(matches(not(withText("00:00"))));
        onView(withId(START_BUTTON_ID)).check(matches(not(isDisplayed())));
    }


    @Test
    public void test_timerFragment_startsTimerWithCorrectCodeAndStopsTooEarly() {
        onView(withId(TIMER_CODE_TEXT)).perform(typeText("123456"));
        onView(withId(START_BUTTON_ID)).perform(click());
        onView(withId(TIMER_CODE_TEXT)).perform(typeText("111111"));
        onView(withId(STOP_BUTTON_ID)).check(matches(withText("stop"))).perform(click());
        onView(withId(POPUP_TEXT)).check(matches(withText("Unfortunately you have not spent the minimum required time " +
                "at the library to receive points!")));

    }

}