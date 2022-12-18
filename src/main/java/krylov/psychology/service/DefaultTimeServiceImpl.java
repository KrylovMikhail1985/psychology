package krylov.psychology.service;

import krylov.psychology.model.DefaultTime;
import krylov.psychology.repository.DefaultTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
@Service
public class DefaultTimeServiceImpl implements DefaultTimeService {
    @Autowired
    private DefaultTimeRepository defaultTimeRepository;
    @Override
    public List<DefaultTime> findAllDefaultTimeSortedByTime() {
        return defaultTimeRepository.findAllByOrderByTime().orElseThrow();
    }

    @Override
    public DefaultTime findByTime(LocalTime time) {
        return defaultTimeRepository.findByTime(time).orElseThrow();
    }

    @Override
    public DefaultTime createDefaultTime(DefaultTime defaultTime) {
        return defaultTimeRepository.save(defaultTime);
    }

    @Override
    public DefaultTime updateDefaultTime(long id, LocalTime time) {
        DefaultTime defaultTime = defaultTimeRepository.findById(id).orElseThrow();
        defaultTime.setTime(time);
        return defaultTimeRepository.save(defaultTime);
    }

    @Override
    public void deleteDefaultTime(long id) {
        defaultTimeRepository.deleteById(id);

    }
}
