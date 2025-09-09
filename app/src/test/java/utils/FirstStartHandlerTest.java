package utils;


import static android.content.Context.MODE_PRIVATE;
import static com.example.librewards.utils.FirstStartHandler.handleFirstStart;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import android.content.SharedPreferences;
import android.os.Build;

import androidx.fragment.app.testing.EmptyFragmentActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import dagger.hilt.android.testing.HiltTestApplication;

@Config(application = HiltTestApplication.class, sdk = {Build.VERSION_CODES.P})
@RunWith(RobolectricTestRunner.class)
public class FirstStartHandlerTest {
    SharedPreferences sharedPreferences;
    EmptyFragmentActivity activity;
    private int intToChange = 0;

    @Before
    public void setUp() {
        try (ActivityController<EmptyFragmentActivity> controller =
                     Robolectric.buildActivity(EmptyFragmentActivity.class)) {
            activity = controller.create().start().resume().get();
        }
    }

    @Test
    public void test_handleFirstStart_givenFirstStart_runsActionsProvided() {
        handleFirstStart(activity, this::onFirstStartAddToInt);
        assertThat(intToChange, equalTo(5));
    }

    @Test
    public void test_handleFirstStart_givenNotFirstStart_doesNotRunActionsProvided() {
        sharedPreferences = activity.getSharedPreferences("librewards_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("firstStart", false).apply();

        handleFirstStart(activity, this::onFirstStartAddToInt);
        assertThat(intToChange, equalTo(0));
    }

    @Test
    public void test_handleFirstStart_givenTwoInvocations_onlyRunsActionsOnce() {
        handleFirstStart(activity, this::onFirstStartAddToInt);
        assertThat(intToChange, equalTo(5));
        handleFirstStart(activity, this::onFirstStartAddToInt);
        assertThat(intToChange, equalTo(5));

    }

    private void onFirstStartAddToInt() {
        intToChange += 5;
    }
}
