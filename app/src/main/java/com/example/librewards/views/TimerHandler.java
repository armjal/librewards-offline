package com.example.librewards.views;

import android.os.SystemClock;

public class TimerHandler {
    private final TimerView timerView;

    public TimerHandler(TimerView timerView) {
        this.timerView = timerView;
    }

    public void start() {
        timerView.changeTimerToDesiredState("start");
        timerView.getTimer().setOnChronometerTickListener(chronometer -> {
            if ((SystemClock.elapsedRealtime() - timerView.getTimer().getBase()) >= 500000) {
                timerView.enforceTimerDayLimit();
            }
        });
    }

    public long stop() {
        timerView.changeTimerToDesiredState("stop");

        return SystemClock.elapsedRealtime() - timerView.getTimer().getBase();
    }
}
