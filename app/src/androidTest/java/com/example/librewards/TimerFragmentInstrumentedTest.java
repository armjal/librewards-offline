package com.example.librewards;

import static com.example.librewards.utils.FragmentTestUtils.launchFragmentInHiltContainer;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.os.Bundle;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.librewards.data.models.UserModel;
import com.example.librewards.views.TimerFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

@HiltAndroidTest
@RunWith(AndroidJUnit4.class)
public class TimerFragmentInstrumentedTest {

    @Rule
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Before
    public void setUp() {
        hiltRule.inject();

        Bundle bundle = new Bundle();
        bundle.putParcelable("user", new UserModel(1, "hi", 2));
        launchFragmentInHiltContainer(TimerFragment.class, bundle, R.style.AppTheme, null);
    }

    @Test
    public void useAppContext() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.librewards", appContext.getPackageName());
    }
}
