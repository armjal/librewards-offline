package com.example.librewards.controllers.codes;

import android.content.Context;

import com.example.librewards.DatabaseHelper;
import com.example.librewards.R;

public class StartCodesManager extends CodesManager {
    private final String codesTableName;
    private final String codesFileName;

    public StartCodesManager(Context context, DatabaseHelper myDb) {
        super(context, myDb);
        this.codesTableName = context.getString(R.string.start_codes_table);
        this.codesFileName = context.getString(R.string.startcodes_file_name);
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
