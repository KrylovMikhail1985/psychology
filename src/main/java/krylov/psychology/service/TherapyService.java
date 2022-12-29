package krylov.psychology.service;

import krylov.psychology.model.Therapy;

public interface TherapyService {
    Therapy createTherapy(Therapy therapy);
    Therapy findById(long id);
    void deleteById(long id);
}
