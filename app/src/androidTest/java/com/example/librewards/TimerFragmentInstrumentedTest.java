package com.example.librewards;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.librewards.utils.FragmentTestUtils.launchFragmentInHiltContainer;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.os.Bundle;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.librewards.models.UserRepositoryFake;
import com.example.librewards.views.TimerFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

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
    private static final int POINTS_VALUE_ID = R.id.points;
    private static final int NAME_VALUE_ID = R.id.nameTimer;
    private static final int POINTS_LABEL_ID = R.id.textView2;
    private static final int START_BUTTON_ID = R.id.startButton;
    private static final int STOP_BUTTON_ID = R.id.stopButton;
    private static final int TIMER_ID = R.id.timer;

    @Before
    public void setUp() {
        hiltRule.inject();
        Bundle bundle = new Bundle();
        userRepositoryFake.setUser("test-name", 0);
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
        onView(withId(STOP_BUTTON_ID)).check(matches(not(isDisplayed()))).check(matches(withText("stop")));
        onView(withId(TIMER_ID)).check(matches(isDisplayed())).check(matches(withText("00:00")));
    }
}