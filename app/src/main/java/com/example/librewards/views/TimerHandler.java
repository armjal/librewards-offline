package com.example.librewards.views;
import android.os.SystemClock;

import com.example.librewards.controllers.codes.StartCodesManager;
import com.example.librewards.controllers.codes.StopCodesManager;

public class TimerHandler {
    private final StartCodesManager startCodesManager;
    private final StopCodesManager stopCodesManager;
    private final TimerView timerView;

    public TimerHandler(TimerView timerView, StartCodesManager startCodesManager, StopCodesManager stopCodesManager) {
        this.startCodesManager = startCodesManager;
        this.stopCodesManager = stopCodesManager;
        this.timerView = timerView;
    }

    public void start(String inputtedCode) {
        startCodesManager.removeUsedCode(inputtedCode);
        timerView.changeTimerToDesiredState("start");
        timerView.getTimer().setOnChronometerTickListener(chronometer -> {
            if ((SystemClock.elapsedRealtime() - timerView.getTimer().getBase()) >= 500000) {
                timerView.enforceTimerDayLimit();
            }
        });
    }

    public long stop(String inputtedCode) {
        stopCodesManager.removeUsedCode(inputtedCode);
        timerView.changeTimerToDesiredState("stop");

        return SystemClock.elapsedRealtime() - timerView.getTimer().getBase();
    }
}
