package com.example.librewards.controllers.codes;

import com.example.librewards.DatabaseHelper;

import java.util.List;

public abstract class CodesManager {
    private final DatabaseHelper myDb;
    private List<String> codes;

    CodesManager(DatabaseHelper myDb){
        this.myDb = myDb;
    }

    public abstract String getCodesTableName();

    public abstract List<String> getOriginalCodes();

    public boolean notInCodesList(String inputtedCode) {
        return !codes.contains(inputtedCode);
    }

    public void refreshCodes() {
        codes = myDb.checkForTimerCodeUpdates(getOriginalCodes(), getCodesTableName());
    }

    public void removeUsedCode(String inputtedCode) {
        codes.remove(inputtedCode);
        myDb.deleteTimerCode(getCodesTableName(), inputtedCode);
    }
}
