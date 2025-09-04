package com.example.librewards.data.repositories;

import static com.example.librewards.data.db.DatabaseConstants.START_CODES_TABLE_NAME;

import com.example.librewards.data.db.DatabaseHelper;
import com.example.librewards.resources.TimerCodes;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class StartCodesRepository extends CodesRepository {
    private List<String> startCodes = TimerCodes.startCodes;
    @Inject
    public StartCodesRepository(DatabaseHelper dbHelper) {
        super(dbHelper);
    }

    public StartCodesRepository(DatabaseHelper dbHelper, List<String> startCodes) {
        super(dbHelper);
        this.startCodes = startCodes;
    }


    @Override
    public String getTableName() {
        return START_CODES_TABLE_NAME;
    }

    @Override
    public List<String> getOriginalCodes() {
        return this.startCodes;
    }
}
