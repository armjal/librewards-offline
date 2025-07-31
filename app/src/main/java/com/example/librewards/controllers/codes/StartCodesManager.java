package com.example.librewards.controllers.codes;

import static com.example.librewards.DbConstants.START_CODES_FILE_NAME;
import static com.example.librewards.DbConstants.START_CODES_TABLE_NAME;

import com.example.librewards.DatabaseHelper;

public class StartCodesManager extends CodesManager {
    private final String codesTableName;
    private final String codesFileName;

    public StartCodesManager(DatabaseHelper myDb) {
        super(myDb);
        this.codesTableName = START_CODES_TABLE_NAME;
        this.codesFileName = START_CODES_FILE_NAME;
    }

    @Override
    public String getCodesTableName() {
        return codesTableName;
    }

    @Override
    public String getCodesFileName() {
        return codesFileName;
    }
}
