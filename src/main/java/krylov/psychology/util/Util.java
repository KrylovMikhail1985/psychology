package krylov.psychology.util;

import krylov.psychology.model.Day;
import krylov.psychology.model.DayTime;
import krylov.psychology.model.Therapy;

import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Util {
    private static final long DAY_INT = 86400000;
    public static   List<Day> createLocalDayList(List<Day> currentDayList, Date startDate) {
        List<Day> localDayList = new ArrayList<>();
        for (var i = 0; i < 5; i++) {
            Date newDate = new Date(startDate.getTime() + DAY_INT * i);
            Day newDay = new Day(newDate);
            for (Day day: currentDayList) {
                if (day.getDate().getTime() == newDate.getTime()) {
                    newDay = day;
                }
            }
            localDayList.add(newDay);
        }
        return localDayList;
    }
    public static int countOfDaysInMonth(Date date) {
        final int year1900 = 1900;
        final int one = 1;
        int year = date.getYear() + year1900;
        int month = date.getMonth() + one;
        YearMonth yearMonthObject = YearMonth.of(year, month);
        return yearMonthObject.lengthOfMonth();
    }
    public static List<Date> findAllDateFromDays(List<Day> dayList) {
        List<Date> dateList = new ArrayList<>();
        for (Day day: dayList) {
            dateList.add(day.getDate());
        }
        return dateList;
    }
    public static boolean thereIsNoTherapyInThisTime(DayTime dayTime) {
        LocalTime localTime = dayTime.getLocalTime();
        Day day = dayTime.getDay();
        // find all therapies in this day
        List<Therapy> therapyList = new ArrayList<>();
        for (DayTime time: day.getDayTimes()) {
            Therapy therapy = time.getTherapy();
            if (therapy != null) {
                therapyList.add(therapy);
            }
        }
        //check that
        for (Therapy therapy: therapyList) {
            LocalTime starTherapy = therapy.getDayTime().getLocalTime();
            LocalTime duration = therapy.getProduct().getDuration();
            LocalTime endTherapy = starTherapy.plusHours(duration.getHour()).plusMinutes(duration.getMinute());
            if ((localTime.isAfter(starTherapy) || localTime.equals(starTherapy))
                    && (localTime.isBefore(endTherapy) || localTime.equals(endTherapy))) {
                return false;
            }
        }
        return true;
    }
    public static List<Day> disableNotFitTime(List<Day> dayList, LocalTime duration) {
        for (Day day : dayList) {
            List<DayTime> dayTimeList = day.getDayTimes();
            for (DayTime dayTime : dayTimeList) {
                LocalTime startTherapy = dayTime.getLocalTime();
                LocalTime endTherapy = startTherapy.plusHours(duration.getHour()).plusMinutes(duration.getMinute());
                if (weHaveFalse(dayTimeList, startTherapy, endTherapy)) {
                    dayTime.setTimeIsFree(false);
                }
            }
        }
        return dayList;
    }
    private static boolean weHaveFalse(List<DayTime> dayTimeList, LocalTime startTime, LocalTime endTime) {
        for (DayTime dayTime : dayTimeList) {
            LocalTime time = dayTime.getLocalTime();
            if ((time.isAfter(startTime) || time.equals(startTime)) &&
                    (time.isBefore(endTime) || time.equals(endTime)) &&
                    !dayTime.isTimeIsFree()) {
                return true;
            }
        }
        return false;
    }
    public static Day thisDayWithActivatedDayTime(Day day, LocalTime startOfTherapy, LocalTime duration) {
        LocalTime endOfTherapy = startOfTherapy.plusHours(duration.getHour()).plusMinutes(duration.getMinute());
        for (DayTime dayTime: day.getDayTimes()) {
            LocalTime time = dayTime.getLocalTime();
            if ((time.isAfter(startOfTherapy) || time.equals(startOfTherapy)) &&
                    (time.isBefore(endOfTherapy) || time.equals(endOfTherapy))) {
                dayTime.setTimeIsFree(true);
            }
        }
        return day;
    }
    public static Day thisDayWithDeactivatedDayTimeIfNoTherapy(Day day, LocalTime startOfTherapy, LocalTime duration) {
        LocalTime endOfTherapy = startOfTherapy.plusHours(duration.getHour()).plusMinutes(duration.getMinute());
        for (DayTime dayTime: day.getDayTimes()) {
            LocalTime time = dayTime.getLocalTime();
            if ((time.isAfter(startOfTherapy) || time.equals(startOfTherapy))
                    && (time.isBefore(endOfTherapy) || time.equals(endOfTherapy))
                    && !dayTime.isTimeIsFree()) {
                System.out.println("Time: " + time + " is not active");
                throw new RuntimeException("Time: " + time + " is not active");
            }
        }

        for (DayTime dayTime: day.getDayTimes()) {
            LocalTime time = dayTime.getLocalTime();
            if ((time.isAfter(startOfTherapy) || time.equals(startOfTherapy)) &&
                    (time.isBefore(endOfTherapy) || time.equals(endOfTherapy))) {
                dayTime.setTimeIsFree(false);
            }
        }
        return day;
    }
}
