package krylov.psychology.service;

import krylov.psychology.model.DefaultTime;

import java.time.LocalTime;
import java.util.List;

public interface DefaultTimeService {
    List<DefaultTime> findAllDefaultTimeSortedByTime();

    DefaultTime findByTime(LocalTime time);
    DefaultTime createDefaultTime(DefaultTime time);
    DefaultTime updateDefaultTime(long id, LocalTime time);
    void deleteDefaultTime(long id);
}
