package com.example.librewards;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

class TimerFragmentUnitTest {
    static Stream<Arguments> provideInputsForPointsCalculatorTest() {
        return Stream.of(
                Arguments.of(10500, 0),
                Arguments.of(60000, 75),
                Arguments.of(120000, 125),
                Arguments.of(185000, 225),
                Arguments.of(507000, 700),
                Arguments.of(800000, 800)

        );
    }
    @ParameterizedTest
    @MethodSource("provideInputsForPointsCalculatorTest")
    void calculatePointsFromTime_givenRangeOfTimes_returnsExpectedPointsEarned(long timeSpentInLibrary, int expectedPointsEarned) {
        TimerFragment timerFragment = new TimerFragment();

        int actualPointsEarned = timerFragment.calculatePointsFromTimeSpent(timeSpentInLibrary);
        assertEquals(expectedPointsEarned, actualPointsEarned);

    }
}