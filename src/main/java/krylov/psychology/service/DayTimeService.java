package krylov.psychology.service;

import krylov.psychology.model.Day;
import krylov.psychology.model.DayTime;
import krylov.psychology.model.DefaultTime;

import java.util.List;

public interface DayTimeService {
    List<DayTime> createListOfDayTimesFromDefaultTime(Day day, List<DefaultTime> defaultTimeList);
    DayTime enableDisable(long id);
}
