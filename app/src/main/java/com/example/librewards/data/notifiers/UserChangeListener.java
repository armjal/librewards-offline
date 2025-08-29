package com.example.librewards.data.notifiers;

public interface UserChangeListener {
    void onNameChanged(String newName);

    void onPointsChanged(int newPoints);
}
