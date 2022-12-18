package krylov.psychology.service;

import krylov.psychology.model.Day;
import krylov.psychology.model.QDay;
import krylov.psychology.repository.DayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DayServiceImpl implements DayService {
    @Autowired
    private DayRepository dayRepository;
    private final long day = 86400000;
    @Override
    public List<Day> findAllDayInPeriod(Date startDate, Date lastDate) {
        startDate = new Date(startDate.getTime() - day);
        lastDate = new Date(lastDate.getTime() + day);
        return (List<Day>) dayRepository.findAll(QDay.day.date.after(startDate).and(QDay.day.date.before(lastDate)));
    }

    @Override
    public List<Day> findAllDayFromDate(Date startDate) {
        startDate = new Date(startDate.getTime() - day);
        return (List<Day>) dayRepository.findAll(QDay.day.date.after(startDate));
    }

    @Override
    public Day findDayByDate(Date date) {
        return dayRepository.findByDate(date).orElseThrow();
    }

    @Override
    public Day findById(long id) {
        return dayRepository.findById(id).orElseThrow();
    }

    @Override
    public Day create(Day day) {
        return dayRepository.save(day);
    }

    @Override
    public Day update(long id, Day day) {
        day.setId(id);
        return dayRepository.save(day);
    }

    @Override
    public void delete(long id) {
        dayRepository.deleteById(id);
    }
}
