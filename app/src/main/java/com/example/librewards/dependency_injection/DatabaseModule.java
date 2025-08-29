package com.example.librewards.dependency_injection;

import android.content.Context;

import com.example.librewards.data.db.DatabaseHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Provides
    @Singleton
    public static DatabaseHelper provideDbHelper(@ApplicationContext Context context) {
        return new DatabaseHelper(context);
    }
}
