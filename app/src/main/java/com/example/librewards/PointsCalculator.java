package com.example.librewards;

public final class PointsCalculator {
    PointsCalculator() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static int calculateFromDuration(long totalDurationAtLibrary) {
        int pointsEarned = 0;
        int durationSeconds = (int) (totalDurationAtLibrary/1000);

        if(isInBetween(durationSeconds, 0, 120)){
            pointsEarned = 75;
        }
        else if(isInBetween(durationSeconds, 120, 180)){
            pointsEarned = 125;
        }
        else if(isInBetween(durationSeconds, 180, 260)){
            pointsEarned = 225;
        }
        else if(isInBetween(durationSeconds, 260, 400)){
            pointsEarned = 400;
        }
        else if(isInBetween(durationSeconds, 400, 600)){
            pointsEarned = 700;
        }
        else if(durationSeconds >= 600){
            pointsEarned = 800;
        }
        return pointsEarned;
    }

    private static boolean isInBetween(int duration, int min, int max) {
        return duration >= min && duration < max;
    }

}
