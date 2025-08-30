/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.librewards.utils;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.core.util.Preconditions;
import androidx.fragment.app.Fragment;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import com.example.librewards.debug.HiltTestActivity;

public class FragmentTestUtils {
    private static final String THEME_EXTRAS_BUNDLE_KEY = "androidx.fragment.app.testing.FragmentScenario" +
            ".EmptyFragmentActivity.THEME_EXTRAS_BUNDLE_KEY";

    /**
     * launchFragmentInContainer from the androidx.fragment:fragment-testing library
     * is NOT possible to use right now as it uses a hardcoded Activity under the hood
     * (i.e. [EmptyFragmentActivity]) which is not annotated with @AndroidEntryPoint.
     * As a workaround, use this function that is equivalent. It requires you to add
     * [HiltTestActivity] in the debug folder and include it in the debug AndroidManifest.xml file
     * as can be found in this project.
     * NOTE: THIS WAS TRANSFORMED INTO A JAVA FILE FROM THE SUPPLIED KOTLIN FILE
     */
    public static <T extends Fragment> ActivityScenario<HiltTestActivity> launchFragmentInHiltContainer(
            @NonNull Class<T> fragmentClass,
            @NonNull Bundle fragmentArgs,
            @StyleRes int themeResId,
            FragmentAction<T> action
    ) {
        Intent startActivityIntent = Intent.makeMainActivity(
                new ComponentName(
                        ApplicationProvider.getApplicationContext(),
                        HiltTestActivity.class
                )
        );

        if (themeResId != 0) {
            Bundle themeBundle = new Bundle();
            themeBundle.putInt("android.support.v4.app.FragmentActivity.THEME_EXTRA", themeResId); // Old key
            startActivityIntent.putExtra(THEME_EXTRAS_BUNDLE_KEY, themeResId); // androidx key
        }

        ActivityScenario<HiltTestActivity> scenario = ActivityScenario.launch(startActivityIntent);

        scenario.onActivity(activity -> {

            Fragment fragment = activity.getSupportFragmentManager().getFragmentFactory().instantiate(
                    Preconditions.checkNotNull(fragmentClass.getClassLoader()),
                    fragmentClass.getName()
            );
            fragment.setArguments(fragmentArgs);

            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, fragment, null)
                    .commitNowAllowingStateLoss();

            if (action != null) {
                //noinspection unchecked
                action.perform((T) fragment);
            }
        });
        return scenario;
    }

    public interface FragmentAction<T extends Fragment> {
        void perform(T fragment);
    }
}