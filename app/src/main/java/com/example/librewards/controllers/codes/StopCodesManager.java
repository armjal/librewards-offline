package com.example.librewards.controllers.codes;

import static com.example.librewards.DbConstants.STOP_CODES_FILE_NAME;
import static com.example.librewards.DbConstants.STOP_CODES_TABLE_NAME;

import com.example.librewards.DatabaseHelper;

public class StopCodesManager extends CodesManager {
    private final String codesTableName;
    private final String codesFileName;

    public StopCodesManager(DatabaseHelper myDb) {
        super(myDb);
        this.codesTableName = STOP_CODES_TABLE_NAME;
        this.codesFileName = STOP_CODES_FILE_NAME;
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
