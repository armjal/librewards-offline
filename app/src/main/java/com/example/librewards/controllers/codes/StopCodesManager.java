package com.example.librewards.controllers.codes;

import static com.example.librewards.DbConstants.STOP_CODES_TABLE_NAME;
import static com.example.librewards.resources.TimerCodes.stopCodes;

import com.example.librewards.DatabaseHelper;

import java.util.List;

public class StopCodesManager extends CodesManager {
    private final String codesTableName;

    public StopCodesManager(DatabaseHelper myDb) {
        super(myDb);
        this.codesTableName = STOP_CODES_TABLE_NAME;
    }

    @Override
    public String getCodesTableName() {
        return codesTableName;
    }

    @Override
    public List<String> getOriginalCodes() {
        return stopCodes;
    }
}