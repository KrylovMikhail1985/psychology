package krylov.psychology.service;

import krylov.psychology.model.MyInformation;
import krylov.psychology.repository.MyInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyInformationServiceImpl implements MyInformationService {
    @Autowired
    private MyInformationRepository myInformationRepository;
    @Override
    public MyInformation find() {
        return myInformationRepository.findById((long) 1).orElseThrow();
    }

    @Override
    public MyInformation save(MyInformation myInformation) {
        return myInformationRepository.save(myInformation);
    }
}
