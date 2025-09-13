package com.example.librewards.dependency_injection.repositories;

import com.example.librewards.data.db.DatabaseHelper;
import com.example.librewards.data.repositories.StopCodesRepository;
import com.example.librewards.resources.TimerCodesTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class StopCodesRepositoryFake extends StopCodesRepository {
    Map<String, String> codesMap = new HashMap<>();

    @Inject
    public StopCodesRepositoryFake(DatabaseHelper dbHelper) {
        super(dbHelper);
    }

    public String getTableName() {
        return "test_stop_codes_table";
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
        for (String code : TimerCodesTest.stopCodesTest) {
            codesMap.put(code, "false");
        }
    }

    @Override
    public void checkForUpdates() {
        if (!(new ArrayList<>(codesMap.keySet()).equals(TimerCodesTest.stopCodesTest))) {
            codesMap = new HashMap<>();
            populate();
        }
    }
}
