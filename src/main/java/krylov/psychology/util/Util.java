package krylov.psychology.util;
import java.time.LocalTime;

public class Util {
    public static LocalTime convertIntToTime(int value) {
        final int MINUTES = 60;
        int hours = value / MINUTES;

        return LocalTime.of(hours, value%MINUTES);
    }
}
