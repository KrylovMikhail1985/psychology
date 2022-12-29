package krylov.psychology.service;

import krylov.psychology.model.Therapy;
import krylov.psychology.repository.TherapyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TherapyServiceImpl implements TherapyService {
    @Autowired
    private TherapyRepository therapyRepository;
    @Override
    public Therapy createTherapy(Therapy therapy) {
        return therapyRepository.save(therapy);
    }

    @Override
    public Therapy findById(long id) {
        return therapyRepository.findById(id).orElseThrow();
    }

    @Override
    public void deleteById(long id) {
        therapyRepository.deleteById(id);
    }
}
