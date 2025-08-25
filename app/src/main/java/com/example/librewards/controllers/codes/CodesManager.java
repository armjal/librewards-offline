package com.example.librewards.controllers.codes;

import com.example.librewards.repositories.TimerRepository;

import java.util.List;

public abstract class CodesManager {
    private final TimerRepository timerRepo;
    private List<String> codes;

    CodesManager(TimerRepository timerRepo){
        this.timerRepo = timerRepo;
    }

    public abstract String getCodesTableName();

    public abstract List<String> getOriginalCodes();

    public boolean notInCodesList(String inputtedCode) {
        return !codes.contains(inputtedCode);
    }

    public void refreshCodes() {
        codes = timerRepo.checkForTimerCodeUpdates(getOriginalCodes(), getCodesTableName());
    }

    public void removeUsedCode(String inputtedCode) {
        codes.remove(inputtedCode);
        timerRepo.deleteTimerCode(getCodesTableName(), inputtedCode);
    }
}
