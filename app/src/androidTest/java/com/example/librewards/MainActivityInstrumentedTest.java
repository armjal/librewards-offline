package com.example.librewards;

import static android.content.Context.MODE_PRIVATE;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.core.IsNot.not;

import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.librewards.data.db.DatabaseHelper;
import com.example.librewards.data.models.UserModel;
import com.example.librewards.data.repositories.RewardsRepository;
import com.example.librewards.data.repositories.StartCodesRepository;
import com.example.librewards.data.repositories.StopCodesRepository;
import com.example.librewards.data.repositories.UserRepository;
import com.example.librewards.views.MainActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

@HiltAndroidTest
@RunWith(AndroidJUnit4.class)
public class MainActivityInstrumentedTest {

    @Rule
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);
    @Rule
    public TestName testName = new TestName();
    private ActivityScenario<MainActivity> scenario;
    public UserModel user;
    @Inject
    public DatabaseHelper dbHelper;
    @Inject
    public UserRepository userRepository;
    @Inject
    public StartCodesRepository startCodesRepository;
    @Inject
    public StopCodesRepository stopCodesRepository;
    @Inject
    public RewardsRepository rewardsRepository;
    private SQLiteDatabase db;

    @Before
    public void setUp() {
        hiltRule.inject();
        setFirstStartGlobally(true);
        db = dbHelper.getWritableDatabase();
        if (!testName.getMethodName().equals("test_mainActivity_givenFirstStart_asksForNameAndProvidesHelp")) {
            user = new UserModel(1, "test-name", 0);
            userRepository.addName("test-name");
            setFirstStartGlobally(false);
        }
        scenario = ActivityScenario.launch(MainActivity.class);
        scenario.moveToState(Lifecycle.State.RESUMED);
    }

    private void setFirstStartGlobally(boolean isFirstStart) {
        SharedPreferences sharedPreferences = ApplicationProvider.getApplicationContext().getSharedPreferences(
                "librewards_prefs",
                MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("firstStart", isFirstStart);
        editor.commit();
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
    public void test_mainActivity_givenFirstStart_asksForNameAndProvidesHelp() {
        onView(withId(R.id.welcomeText)).check(matches(isDisplayed())).check(matches(withText(
                "Welcome to Lib Rewards! Can we start by taking your name, please?")));
        onView(withId(R.id.enterName)).perform(typeText("random name"), closeSoftKeyboard());
        onView(withId(R.id.nameButton)).perform(click());
        onView(withId(R.id.popupText)).inRoot(isDialog()).check(matches(withText(expectedHelpText)));
        onView(withId(R.id.closeBtn)).inRoot(isDialog()).perform(click());
        onView(withId(R.id.nameTimer)).check(matches(withText("Hey, random name")));
    }

    @Test
    public void test_mainActivity_hasCoreUiElementsCorrectlySetup() {
        onView(withId(R.id.logo)).check(matches(isDisplayed()));
        onView(withId(R.id.tabLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.viewPager)).check(matches(isDisplayed()));
        onView(withId(R.id.helpButton)).check(matches(isDisplayed()));
        onView(withId(R.id.nameTimer)).check(matches(withText("Hey, test-name")));
        onView(withId(R.id.popupNameContainer)).check(matches(not(isDisplayed())));
        onView(withId(R.id.timer)).check(matches(isDisplayed()));
        onView(withId(R.id.pointsTimer)).check(matches(isDisplayed()));
        onView(withId(R.id.pointsLabelTimer)).check(matches(isDisplayed()));
        onView(withId(R.id.timerCodeText)).check(matches(isDisplayed()));
        onView(withId(R.id.startButton)).check(matches(isDisplayed()));
    }

    @Test
    public void test_mainActivity_givenUserRequiresHelp_clicksHelpButtonForGuidance() {
        onView(withId(R.id.helpButton)).check(matches(isDisplayed())).perform(click());
        onView(withId(R.id.popupText)).inRoot(isDialog()).check(matches(withText(expectedHelpText)));
        onView(withId(R.id.closeBtn)).inRoot(isDialog()).perform(click());
    }

    @Test
    public void test_mainActivity_givenUserClicksOnTabs_shouldNavigateToEitherFragment() {
        onView(withId(R.id.timer)).check(matches(isDisplayed()));
        onView(withId(R.id.rewardButton)).check(doesNotExist());
        onView(withId(R.id.nameTimer)).check(matches(withText("Hey, test-name")));
        onView(allOf(withText("Rewards"), isDescendantOfA(ViewMatchers.withId(R.id.tabLayout)))).perform(click());
        onView(withId(R.id.nameRewards)).check(matches(withText("Hey, test-name")));
        onView(withId(R.id.rewardButton)).check(matches(isDisplayed()));
        onView(withId(R.id.timer)).check(doesNotExist());
        onView(allOf(withText("Timer"), isDescendantOfA(ViewMatchers.withId(R.id.tabLayout)))).perform(click());
        onView(withId(R.id.rewardButton)).check(doesNotExist());
    }

    @Test
    public void test_mainActivity_givenPointsUpdate_amendsBothFragments() {
        onView(withId(R.id.pointsTimer)).check(matches(withText("0")));
        scenario.onActivity(activity -> userRepository.addPoints(user, 10));
        onView(withId(R.id.pointsTimer)).check(matches(withText("10")));
        onView(allOf(withText("Rewards"), isDescendantOfA(ViewMatchers.withId(R.id.tabLayout)))).perform(click());
        onView(withId(R.id.pointsRewards)).check(matches(withText("10")));
    }

    @After
    public void tearDown(){
        db.delete("user_table", null, null);
        db.delete("start_codes_table", null, null);
        db.delete("stop_codes_table", null, null);
        db.delete("reward_codes_table", null, null);
    }
}
