package com.example.librewards.models;

public interface UserChangeListener {
    void onNameChanged(String newName);
    void onPointsChanged(int newPoints);
}
