package com.example.librewards.data.repositories;

import static com.example.librewards.data.db.DatabaseConstants.START_CODES_TABLE_NAME;
import static com.example.librewards.resources.TimerCodes.startCodes;

import com.example.librewards.data.db.DatabaseHelper;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class StartCodesRepository extends CodesRepository {

    @Inject
    public StartCodesRepository(DatabaseHelper dbHelper) {
        super(dbHelper);
    }

    @Override
    public String getTableName() {
        return START_CODES_TABLE_NAME;
    }

    @Override
    public List<String> getOriginalCodes() {
        return startCodes;
    }
}
