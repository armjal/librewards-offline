package com.example.librewards.controllers.codes;

import com.example.librewards.DatabaseHelper;
import com.example.librewards.ListFromFile;

import java.util.List;

public abstract class CodesManager {
    private final DatabaseHelper myDb;
    private List<String> codes;

    CodesManager(DatabaseHelper myDb){
        this.myDb = myDb;
    }

    public abstract String getCodesTableName();

    public abstract String getCodesFileName();

    public boolean isInvalidCode(String inputtedCode) {
        return !codes.contains(inputtedCode);
    }

    private List<String> getCodesFromFile(ListFromFile listFromFile, String path){
        return listFromFile.readLine(path);
    }

    public void refreshCodes(ListFromFile listFromFile) {
        List<String> currentCodes = myDb.getCurrentCodes(getCodesTableName());
        List<String> originalCodes = getCodesFromFile(listFromFile, getCodesFileName());
        codes = myDb.checkForUpdates(currentCodes, originalCodes, getCodesTableName());
    }

    public void removeUsedCode(String inputtedCode) {
        codes.remove(inputtedCode);
        myDb.deleteCode(getCodesTableName(), inputtedCode);
    }
}
