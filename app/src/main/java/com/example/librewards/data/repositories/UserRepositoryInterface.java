package com.example.librewards.data.repositories;

import com.example.librewards.data.models.UserModel;

public interface UserRepositoryInterface{
    void addName(String name);
    UserModel getUser();
    int getPoints();
    void addPoints(UserModel user, int pointsToUpdate);
    void minusPoints(UserModel user, int pointsToUpdate);
}
