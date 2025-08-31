package com.example.librewards.dependency_injection;

import android.content.Context;

import com.example.librewards.data.db.DatabaseHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.testing.TestInstallIn;
@Module
@TestInstallIn(
        components = SingletonComponent.class,
        replaces = com.example.librewards.dependency_injection.DatabaseModule.class
)
public class TestDatabaseModule {
    public static final String TEST_DATABASE_NAME = "test_librewards.db";
    @Provides
    @Singleton
    public static DatabaseHelper provideDbHelper(@ApplicationContext Context context) {
        return new DatabaseHelper(context, TEST_DATABASE_NAME);
    }
}
