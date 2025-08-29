package com.example.librewards.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class FirstStartHandler {
    private static final String FIRST_START_PREFS_BOOL = "firstStart";
    private static final String LIBREWARDS_PREFS = "librewards_prefs";

    private static SharedPreferences sharedPreferences;

    private FirstStartHandler() {
    }

    public static void handleFirstStart(Activity activity, Runnable actions) {
        sharedPreferences = activity.getSharedPreferences(LIBREWARDS_PREFS, Context.MODE_PRIVATE);
        boolean isFirstStart = sharedPreferences.getBoolean(FIRST_START_PREFS_BOOL, true);

        if (isFirstStart) {
            actions.run();
        }
        disableFirstStart();
    }

    private static void disableFirstStart() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FIRST_START_PREFS_BOOL, false);
        editor.apply();
    }
}
