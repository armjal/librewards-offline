package com.example.librewards.views;
import android.os.SystemClock;

import com.example.librewards.DatabaseHelper;
import com.example.librewards.PointsCalculator;
import com.example.librewards.controllers.codes.StartCodesManager;
import com.example.librewards.controllers.codes.StopCodesManager;

public class TimerHandler {
    StartCodesManager startCodesManager;
    StopCodesManager stopCodesManager;
    private final TimerView timerView;
    private long totalDuration = 0;

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
        totalDuration = SystemClock.elapsedRealtime() - timerView.getTimer().getBase();

        return totalDuration;
    }

    public int saveTotalPointsFromDuration(DatabaseHelper myDb) {
        int pointsEarned = PointsCalculator.calculateFromDuration(totalDuration);
        myDb.addPoints(pointsEarned);
        return pointsEarned;
    }
}
