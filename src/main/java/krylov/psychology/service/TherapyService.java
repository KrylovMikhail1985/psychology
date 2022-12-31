package krylov.psychology.service;

import krylov.psychology.model.Therapy;

import java.util.List;

public interface TherapyService {
    Therapy createTherapy(Therapy therapy);
    Therapy findById(long id);
    void deleteById(long id);
    List<Therapy> findTherapyWithProductId(long id);
}
