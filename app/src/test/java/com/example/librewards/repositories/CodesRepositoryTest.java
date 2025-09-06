package com.example.librewards.repositories;

import static com.example.librewards.resources.TimerCodesTest.startCodesTest;
import static com.example.librewards.resources.TimerCodesTest.stopCodesTest;
import static org.mockito.Mockito.mockStatic;

import android.os.Build;

import com.example.librewards.data.db.DatabaseHelper;
import com.example.librewards.data.repositories.CodesRepository;
import com.example.librewards.data.repositories.StartCodesRepository;
import com.example.librewards.data.repositories.StopCodesRepository;
import com.example.librewards.resources.TimerCodes;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import javax.inject.Inject;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;
import dagger.hilt.android.testing.HiltTestApplication;

@HiltAndroidTest
@Config(application = HiltTestApplication.class, sdk = {Build.VERSION_CODES.P})
@RunWith(ParameterizedRobolectricTestRunner.class)
public class CodesRepositoryTest {
    @Rule
    public HiltAndroidRule hiltAndroidRule = new HiltAndroidRule(this);
    @Inject
    public DatabaseHelper databaseHelper;
    MockedStatic<TimerCodes> mockedTimerCodes;
    Class<CodesRepository> codesRepoClass;
    CodesRepository codesRepo;
    String tableName;
    List<String> codes;
    public Supplier<List<String>> getCodesFunction;

    public CodesRepositoryTest(Class<CodesRepository> codesRepoClass, String tableName, List<String> codes,
                                    Supplier<List<String>> getCodesFunction) {
        this.codesRepoClass = codesRepoClass;
        this.tableName = tableName;
        this.codes = codes;
        this.getCodesFunction = getCodesFunction;
    }

    @ParameterizedRobolectricTestRunner.Parameters
    public static Collection<Object[]> codesRepoAndProperties() {
        return Arrays.asList(new Object[][]{
                {StartCodesRepository.class, "start_codes_table", startCodesTest,
                        (Supplier<List<String>>) TimerCodes::getStartCodes},
                {StopCodesRepository.class, "stop_codes_table", stopCodesTest,
                        (Supplier<List<String>>) TimerCodes::getStopCodes}
        });
    }


    @Before
    public void setUp() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException,
            InstantiationException {
        hiltAndroidRule.inject();
        codesRepo = codesRepoClass.getConstructor(DatabaseHelper.class).newInstance(databaseHelper);
        mockedTimerCodes = mockStatic(TimerCodes.class);
        mockedTimerCodes.when(() -> getCodesFunction.get()).thenReturn(codes);
    }

    @Test
    public void test_codeRepo_getOriginalCodes_returnsOriginalCodes() {
        assert codesRepo.getOriginalCodes() == codes;
    }

    @Test
    public void test_codeRepo_populate_successfullyPopulatesDbWithCodesAndUsedState() {
        List<String> codesColumnBeforePopulation = databaseHelper.getAllStrings(tableName, "codes", null,
                null);

        codesRepo.populate();
        List<String> codesColumnAfterPopulation = databaseHelper.getAllStrings(tableName, "codes", null,
                null);
        String isCodeUsed = databaseHelper.getString(tableName, "used", "codes = ?", new String[]{codes.get(0)});

        assert isCodeUsed.equals("false");
        assert codesColumnBeforePopulation.equals(new ArrayList<>());
        assert codesColumnAfterPopulation.equals(codes);
    }

    @Test
    public void test_codeRepo_get_returnsExistingCodeFromDb() {
        codesRepo.populate();

        assert codesRepo.get(codes.get(0)).equals(codes.get(0));
    }

    @Test
    public void test_codeRepo_get_givenIncorrectCode_returnsEmptyString() {
        codesRepo.populate();

        assert codesRepo.get("987623").isEmpty();
    }

    @Test
    public void test_codeRepo_delete_successfullySoftDeletesCodeAndSetsItToUsed() {
        codesRepo.populate();
        String codeBeforeDelete = codesRepo.get(codes.get(0));

        codesRepo.delete(codes.get(0));
        String codeAfterDelete = codesRepo.get(codes.get(0));
        String isCodeUsed = databaseHelper.getString(tableName, "used", "codes = ?", new String[]{codes.get(0)});

        assert codeBeforeDelete.equals(codes.get(0));
        assert codeAfterDelete.isEmpty();
        assert isCodeUsed.equals("true");
    }

    @Test
    public void test_codeRepo_delete_givenNonExistentCode_doesNotError() {
        codesRepo.populate();

        codesRepo.delete("hello");
    }

    @Test
    public void test_codeRepo_checkForUpdates_givenExistingCodes_doesNotReplace() {
        codesRepo.populate();

        codesRepo.checkForUpdates();

        assert codesRepo.get(codes.get(0)).equals(codes.get(0));
    }

    @Test
    public void test_codeRepo_checkForUpdates_givenNewCodes_updates() {
        codesRepo.populate();
        List<String> codesInDbBeforeUpdate = databaseHelper.getAllStrings(tableName, "codes", null, null);
        List<String> newCodes = List.of("random", "codes");
        mockedTimerCodes.when(() -> getCodesFunction.get()).thenReturn(List.of("random", "codes"));

        codesRepo.checkForUpdates();
        List<String> codesInDbAfterUpdate = databaseHelper.getAllStrings(tableName, "codes", null, null);
        String isRandomCodeUsed = databaseHelper.getString(tableName, "used", "codes = ?", new String[]{
                "random"});

        assert isRandomCodeUsed.equals("false");
        assert codesInDbBeforeUpdate.equals(codes);
        assert codesInDbAfterUpdate.equals(newCodes);
    }

    @After
    public void tearDown() {
        if (mockedTimerCodes != null) {
            mockedTimerCodes.close();
        }
    }

}
