package com.example.librewards.dependency_injection;

import com.example.librewards.data.repositories.CodesRepositoryInterface;
import com.example.librewards.data.repositories.UserRepositoryInterface;
import com.example.librewards.repositories.StartCodesRepositoryFake;
import com.example.librewards.repositories.UserRepositoryFake;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class TestRepositoryModule {

    @Binds
    @Singleton
    public abstract UserRepositoryInterface bindFakeUserRepository(UserRepositoryFake userRepositoryFake);

    @Binds
    @Singleton
    public abstract CodesRepositoryInterface bindFakeStartCodesRepository(StartCodesRepositoryFake startCodesRepositoryFake);
}
