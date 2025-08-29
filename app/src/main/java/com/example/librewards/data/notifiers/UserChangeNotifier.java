package com.example.librewards.data.notifiers;

import java.util.ArrayList;
import java.util.List;

public class UserChangeNotifier {
    private static final List<UserChangeListener> listeners = new ArrayList<>();

    private UserChangeNotifier() {
    }

    public static void addListener(UserChangeListener listener) {
        listeners.add(listener);
    }

    public static void notifyNameChange(String name) {
        for (UserChangeListener listener : listeners) {
            listener.onNameChanged(name);
        }
    }

    public static void notifyPointsChanged(int points) {
        for (UserChangeListener listener : listeners) {
            listener.onPointsChanged(points);
        }
    }
}
