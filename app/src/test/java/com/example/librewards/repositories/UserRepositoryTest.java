package com.example.librewards.repositories;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import android.os.Build;

import com.example.librewards.data.db.DatabaseHelper;
import com.example.librewards.data.models.UserModel;
import com.example.librewards.data.repositories.UserRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import javax.inject.Inject;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.HiltTestApplication;

@HiltAndroidTest
@Config(application = HiltTestApplication.class, sdk = {Build.VERSION_CODES.P})
@RunWith(RobolectricTestRunner.class)
public class UserRepositoryTest {
    @Rule
    public HiltAndroidRule hiltAndroidRule = new HiltAndroidRule(this);
    @Inject
    public DatabaseHelper databaseHelper;
    UserRepository userRepo;

    @Before
    public void setUp() {
        hiltAndroidRule.inject();
        userRepo = new UserRepository(databaseHelper);
        insertUserInDb();
    }

    @Test
    public void test_userRepo_getUser_successfullyReturnsAUserObjectWithValuesFromDb() {
        UserModel user = userRepo.getUser();

        assertThat(user, equalTo(new UserModel(1, "John", 100)));
    }

    public void insertUserInDb() {
        databaseHelper.getWritableDatabase().execSQL("INSERT INTO user_table (name, points) VALUES ('John', 100)");
    }
}

