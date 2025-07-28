package com.example.librewards.models;

public interface UserChangeListener {
    public void onNameChanged(String newName);
    public void onPointsChanged(int newPoints);
}
