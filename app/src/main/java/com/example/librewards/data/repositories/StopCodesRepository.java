package com.example.librewards.data.repositories;

import static com.example.librewards.data.db.DatabaseConstants.STOP_CODES_TABLE_NAME;
import static com.example.librewards.resources.TimerCodes.stopCodes;

import com.example.librewards.data.db.DatabaseHelper;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class StopCodesRepository extends CodesRepository {

    @Inject
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
