package com.example.librewards.dependency_injection.repositories;

import com.example.librewards.data.db.DatabaseHelper;
import com.example.librewards.data.models.UserModel;
import com.example.librewards.data.notifiers.UserChangeNotifier;
import com.example.librewards.data.repositories.UserRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserRepositoryFake extends UserRepository {
    UserModel userModel = new UserModel(1, "test-name", 0);

    @Inject
    public UserRepositoryFake(DatabaseHelper dbHelper) {
        super(dbHelper);
    }

    @Override
    public void addName(String name) {
        userModel.setName(name);
        UserChangeNotifier.notifyNameChange(name);
    }

    @Override
    public UserModel getUser() {
        return userModel;
    }

    @Override
    public int getPoints() {
        return userModel.getPoints();
    }

    @Override
    public void addPoints(UserModel user, int pointsToUpdate) {
        int newPoints = user.getPoints() + pointsToUpdate;
        userModel.setPoints(newPoints);
        UserChangeNotifier.notifyPointsChanged(newPoints);
    }

    @Override
    public void minusPoints(UserModel user, int pointsToUpdate) {
        int newPoints = user.getPoints() - pointsToUpdate;
        userModel.setPoints(newPoints);
        UserChangeNotifier.notifyPointsChanged(newPoints);
    }

    public void setUser(UserModel user) {
        userModel = user;
    }
}
