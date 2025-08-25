package com.example.librewards.controllers.codes;

import static com.example.librewards.DbConstants.START_CODES_TABLE_NAME;
import static com.example.librewards.resources.TimerCodes.startCodes;

import com.example.librewards.repositories.TimerRepository;

import java.util.List;

public class StartCodesManager extends CodesManager {
    private final String codesTableName;

    public StartCodesManager(TimerRepository timerRepo) {
        super(timerRepo);
        this.codesTableName = START_CODES_TABLE_NAME;
    }

    @Override
    public String getCodesTableName() {
        return codesTableName;
    }

    @Override
    public List<String> getOriginalCodes() {
        return startCodes;
    }
}
