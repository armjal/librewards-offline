package com.example.librewards;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;


class PointsCalculatorUnitTest {
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
    void calculateFromDuration_givenRangeOfTimes_returnsExpectedPointsEarned(long timeSpentInLibrary,
                                                                             int expectedPointsEarned) {
        int actualPointsEarned = PointsCalculator.calculatePointsFromDuration(timeSpentInLibrary);

        assertEquals(expectedPointsEarned, actualPointsEarned);

    }
}