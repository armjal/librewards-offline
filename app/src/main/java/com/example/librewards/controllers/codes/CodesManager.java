package com.example.librewards.controllers.codes;

import static com.example.librewards.views.ViewUtils.toastMessage;

import android.content.Context;

import com.example.librewards.DatabaseHelper;
import com.example.librewards.ListFromFile;
import com.example.librewards.R;

import java.util.List;

public abstract class CodesManager {
    private final Context context;
    private final DatabaseHelper myDb;
    private List<String> codes;

    CodesManager(Context context, DatabaseHelper myDb){
        this.context = context;
        this.myDb = myDb;
    }

    public abstract String getCodesTableName();

    public abstract String getCodesFileName();

    public boolean validateCode(String inputtedCode) {
        if (inputtedCode.isEmpty()) {
            toastMessage(context.getString(R.string.emptyCode), context);
            return false;
        } else if (!codes.contains(inputtedCode)) {
            toastMessage(context.getString(R.string.invalidCode), context);
            return false;
        }
        return true;
    }

    private List<String> getCodesFromFile(String path){
        ListFromFile listFromFile = new ListFromFile(context);
        return listFromFile.readLine(path);
    }

    public void refreshCodes() {
        List<String> currStartCodes = myDb.getCurrentCodes(getCodesTableName());
        List<String> originalStartCodes = getCodesFromFile(getCodesFileName());
        codes = myDb.checkForUpdates(currStartCodes, originalStartCodes, getCodesTableName());
    }

    public void removeUsedCode(String inputtedCode) {
        codes.remove(inputtedCode);
        myDb.deleteCode(getCodesTableName(), inputtedCode);
    }
}
