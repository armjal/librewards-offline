package com.example.librewards.repositories;

import com.example.librewards.data.db.DatabaseHelper;
import com.example.librewards.data.repositories.StartCodesRepository;
import com.example.librewards.resources.TimerCodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class StartCodesRepositoryFake extends StartCodesRepository {
    Map<String, String> codesMap = new HashMap<>();

    @Inject
    public StartCodesRepositoryFake(DatabaseHelper dbHelper) {
        super(dbHelper);
    }

    public String getTableName() {
        return "test_start_codes_table";
    }

    @Override
    public List<String> getOriginalCodes() {
        return Collections.emptyList();
    }

    @Override
    public String get(String value) {
        if (Objects.equals(codesMap.get(value), "false")) {
            return value;
        }
        return "";
    }

    @Override
    public void delete(String code) {
        codesMap.remove(code);
    }

    @Override
    public void populate() {
        for (String code : TimerCodes.startCodesTest) {
            codesMap.put(code, "false");
        }
    }

    @Override
    public void checkForUpdates() {
        if (!(new ArrayList<>(codesMap.keySet()).equals(TimerCodes.startCodesTest))) {
            codesMap = new HashMap<>();
            populate();
        }
    }
}
