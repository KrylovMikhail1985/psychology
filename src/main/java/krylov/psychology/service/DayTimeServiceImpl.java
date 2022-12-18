package krylov.psychology.service;

import krylov.psychology.model.Day;
import krylov.psychology.model.DayTime;
import krylov.psychology.model.DefaultTime;
import krylov.psychology.repository.DayTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DayTimeServiceImpl implements DayTimeService {
    @Autowired
    private DayTimeRepository dayTimeRepository;
    @Override
    public List<DayTime> createListOfDayTimesFromDefaultTime(Day day, List<DefaultTime> defaultTimeList) {
        List<DayTime> dayTimeList = new ArrayList<>();
        for (DefaultTime defaultTime: defaultTimeList) {
            DayTime dayTime = new DayTime();
            dayTime.setDay(day);
            dayTime.setLocalTime(defaultTime.getTime());
            dayTime.setTimeIsFree(true);
            dayTimeList.add(dayTime);
        }
        return dayTimeList;
    }
    @Override
    public DayTime enableDisable(long id) {
        DayTime dayTime = dayTimeRepository.findById(id).orElseThrow();
        if (dayTime.isTimeIsFree()) {
            dayTime.setTimeIsFree(false);
        } else {
            dayTime.setTimeIsFree(true);
        }
        return dayTimeRepository.save(dayTime);
    }
}
