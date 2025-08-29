package com.example.librewards.data.models;

import android.os.Parcel;
import android.os.Parcelable;

public class UserModel implements Parcelable {
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
    private int id;
    private String name;
    private int points;

    public UserModel(int id, String name, Integer points) {
        this.id = id;
        this.name = name;
        this.points = points;
    }

    public UserModel(Parcel in) {
        name = in.readString();
        points = in.readInt();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
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
}
