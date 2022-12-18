package krylov.psychology.service;

import krylov.psychology.model.Day;

import java.util.Date;
import java.util.List;

public interface DayService {
    List<Day> findAllDayInPeriod(Date startDate, Date lastDate);
    List<Day> findAllDayFromDate(Date startDate);
    Day findDayByDate(Date date);
    Day findById(long id);
    Day create(Day day);
    Day update(long id, Day day);
    void delete(long id);

}
