package com.example.librewards.models;

public class UserModel {
    private String name = "";
    private Integer points = 0;

    public void setPoints(Integer points) {
        this.points = points;
    }

    public void setName(String name) {
        this.name = name;
        UserChangeNotifier.notifyNameChange(name);
    }

    public String getName() {
        return name;
    }
}
