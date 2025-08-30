package com.example.librewards.data.repositories;

import java.util.List;

public interface CodesRepositoryInterface{
    String getTableName();
    List<String> getOriginalCodes();
    String get(String value);
    void delete(String code);
    void populate();
    void checkForUpdates();
}
