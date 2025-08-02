package com.example.librewards.views;

import android.widget.Chronometer;

public interface TimerView {
    void changeTimerToDesiredState(String desiredState);
    void enforceTimerDayLimit();
    Chronometer getTimer();
}
