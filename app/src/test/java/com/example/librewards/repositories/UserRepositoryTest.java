package com.example.librewards.repositories;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import android.os.Build;

import com.example.librewards.data.db.DatabaseHelper;
import com.example.librewards.data.models.UserModel;
import com.example.librewards.data.notifiers.UserChangeListener;
import com.example.librewards.data.notifiers.UserChangeNotifier;
import com.example.librewards.data.repositories.UserRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
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
    private UserChangeListener mockUserChangeListener1;
    private UserChangeListener mockUserChangeListener2;

    @Before
    public void setUp() {
        hiltAndroidRule.inject();
        userRepo = new UserRepository(databaseHelper);
        mockUserChangeListener1 = Mockito.mock(UserChangeListener.class);
        mockUserChangeListener2 = Mockito.mock(UserChangeListener.class);

        UserChangeNotifier.addListener(mockUserChangeListener1);
        UserChangeNotifier.addListener(mockUserChangeListener2);

    }

    @Test
    public void test_userRepo_getUser_successfullyReturnsAUserObjectWithValuesFromDb() {
        insertUserInDb();

        UserModel user = userRepo.getUser();

        assertThat(user, equalTo(new UserModel(1, "John", 100)));
    }

    @Test
    public void test_userRepo_getUser_givenNoUsersInDb_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> userRepo.getUser());
    }

    @Test
    public void test_userRepo_addName_successfullyAddsUserNameToDb() {
        userRepo.addName("Test Name");

        userRepo.getUser();
        UserModel expectedUser = new UserModel(1, "Test Name", 0);

        assertThat(userRepo.getUser(), equalTo(expectedUser));
        verify(mockUserChangeListener1).onNameChanged(eq("Test Name"));
        verify(mockUserChangeListener2).onNameChanged(eq("Test Name"));
    }

    @Test
    public void test_userRepo_addPoints_successfullyAddsPointsToDb() {
        insertUserInDb();

        userRepo.addPoints(userRepo.getUser(), 10);

        assertThat(userRepo.getPoints(), equalTo(110));
        verify(mockUserChangeListener1).onPointsChanged(eq(110));
        verify(mockUserChangeListener2).onPointsChanged(eq(110));
    }

    @Test
    public void test_userRepo_minusPoints_successfullyMinusesPointsToDb() {
        insertUserInDb();

        userRepo.minusPoints(userRepo.getUser(), 10);

        assertThat(userRepo.getPoints(), equalTo(90));
        verify(mockUserChangeListener1).onPointsChanged(eq(90));
        verify(mockUserChangeListener2).onPointsChanged(eq(90));
    }

    private void insertUserInDb() {
        databaseHelper.getWritableDatabase().execSQL("INSERT INTO user_table (name, points) VALUES ('John', 100)");
    }
}

