package com.example.librewards.repositories;

import com.example.librewards.data.db.DatabaseHelper;
import com.example.librewards.data.models.UserModel;
import com.example.librewards.data.repositories.UserRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserRepositoryFake extends UserRepository {
    String name = "";
    int points = 0;

    @Inject
    public UserRepositoryFake(DatabaseHelper dbHelper) {
        super(dbHelper);
    }

    @Override
    public void addName(String name) {

    }

    @Override
    public UserModel getUser() {
        return new UserModel(1, name, points);
    }

    @Override
    public int getPoints() {
        return points;
    }

    @Override
    public void addPoints(UserModel user, int pointsToUpdate) {
        int newPoints = user.getPoints() + pointsToUpdate;
        user.setPoints(newPoints);
        points = newPoints;
    }

    @Override
    public void minusPoints(UserModel user, int pointsToUpdate) {
        int newPoints = user.getPoints() - pointsToUpdate;
        user.setPoints(newPoints);
        points = newPoints;
    }

    public void setUser(String name, int points) {
        this.name = name;
        this.points = points;
    }
}
