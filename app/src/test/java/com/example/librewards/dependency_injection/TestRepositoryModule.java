package com.example.librewards.dependency_injection;

import com.example.librewards.data.repositories.RewardsRepository;
import com.example.librewards.data.repositories.StartCodesRepository;
import com.example.librewards.data.repositories.StopCodesRepository;
import com.example.librewards.data.repositories.UserRepository;
import com.example.librewards.dependency_injection.repositories.RewardsRepositoryFake;
import com.example.librewards.dependency_injection.repositories.StartCodesRepositoryFake;
import com.example.librewards.dependency_injection.repositories.StopCodesRepositoryFake;
import com.example.librewards.dependency_injection.repositories.UserRepositoryFake;

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
    public abstract UserRepository bindFakeUserRepository(UserRepositoryFake userRepositoryFake);

    @Binds
    @Singleton
    public abstract StartCodesRepository bindFakeStartCodesRepository(StartCodesRepositoryFake startCodesRepositoryFake);

    @Binds
    @Singleton
    public abstract StopCodesRepository bindFakeStopCodesRepository(StopCodesRepositoryFake stopCodesRepositoryFake);

    @Binds
    @Singleton
    public abstract RewardsRepository bindFakeRewardsRepository(RewardsRepositoryFake rewardsRepositoryFake);
}
