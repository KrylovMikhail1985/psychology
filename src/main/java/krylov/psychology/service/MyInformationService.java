package krylov.psychology.service;

import krylov.psychology.model.MyInformation;

public interface MyInformationService {
    MyInformation find();
    MyInformation save(MyInformation myInformation);
}
