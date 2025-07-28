package com.example.librewards.models;

public class UserModel {
    private String name = "";
    private Integer points = 0;
    private UserChangeListener userChangeListener;

    public void setUserChangeListener(UserChangeListener userChangeListener){
        this.userChangeListener = userChangeListener;
    }
    public void setPoints(Integer points) {
        this.points = points;
        userChangeListener.onPointsChanged(points);
    }

    public void setName(String name) {
        this.name = name;
        UserChangeNotifier.notifyNameChange(name);
    }

    public String getName() {
        return name;
    }
}
