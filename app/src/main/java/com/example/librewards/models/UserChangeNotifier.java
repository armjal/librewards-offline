package com.example.librewards.models;

import java.util.ArrayList;
import java.util.List;

public class UserChangeNotifier {
    private UserChangeNotifier(){
    }

    private static final List<UserChangeListener> listeners = new ArrayList<>();

    public static void addListener(UserChangeListener listener) {
        listeners.add(listener);
    }

    public static void notifyNameChange(String name) {
        for (UserChangeListener listener : listeners) {
            listener.onNameChanged(name);
        }
    }
}
