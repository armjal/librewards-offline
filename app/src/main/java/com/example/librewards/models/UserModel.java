package com.example.librewards.models;

import android.os.Parcel;
import android.os.Parcelable;

public class UserModel implements Parcelable {
    private String name;
    private int points;

    public UserModel(String name, Integer points) {
        this.name = name;
        this.points = points;
    }

    public void setPoints(Integer points) {
        this.points = points;
        UserChangeNotifier.notifyPointsChanged(points);
    }

    public void setName(String name) {
        this.name = name;
        UserChangeNotifier.notifyNameChange(name);
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public UserModel(Parcel in) {
        name = in.readString();
        points = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(points);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserModel> CREATOR = new Creator<>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };
}
