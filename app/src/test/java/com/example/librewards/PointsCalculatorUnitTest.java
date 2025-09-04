package com.example.librewards;

import static com.example.librewards.utils.PointsCalculator.calculatePointsFromDuration;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;


@RunWith(Parameterized.class)
public class PointsCalculatorUnitTest {
    private final int totalDuration;
    private final int expectedPoints;

    public PointsCalculatorUnitTest(int totalDuration, int expectedPoints) {
        this.totalDuration = totalDuration;
        this.expectedPoints = expectedPoints;
    }

    @Parameters(name = "Test #{index}: calculatesPointsFromDuration ({0}) returnsExpectedPoints {1}")
    public static Collection<Object[]> durationToPoints() {
        return Arrays.asList(new Object[][]{
                {10500, 0},
                {60000, 75},
                {120000, 125},
                {185000, 225},
                {507000, 700},
                {800000, 800}
        });
    }

    @Test
    public void calculatePointsFromDuration_givenRangeOfTimes_returnsExpectedPointsEarned() {
        int actualPointsEarned = calculatePointsFromDuration(totalDuration);
        assertEquals(expectedPoints, actualPointsEarned);

    }
}