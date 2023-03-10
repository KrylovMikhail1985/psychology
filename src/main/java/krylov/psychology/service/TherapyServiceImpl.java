package krylov.psychology.service;

import krylov.psychology.model.QTherapy;
import krylov.psychology.model.Therapy;
import krylov.psychology.repository.TherapyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Override
    public List<Therapy> findTherapyWithProductId(long id) {
        return (List<Therapy>) therapyRepository.findAll(QTherapy.therapy.product.id.eq(id));
    }
}
