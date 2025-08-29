package com.example.librewards.repositories;

import static com.example.librewards.DbConstants.STOP_CODES_TABLE_NAME;
import static com.example.librewards.resources.TimerCodes.stopCodes;

import com.example.librewards.DatabaseHelper;

import java.util.List;

public class StopCodesRepository extends TimerRepository {
    public StopCodesRepository(DatabaseHelper dbHelper) {
        super(dbHelper);
    }

    @Override
    public String getTableName() {
        return STOP_CODES_TABLE_NAME;
    }

    @Override
    public List<String> getOriginalCodes() {
        return stopCodes;
    }
}
