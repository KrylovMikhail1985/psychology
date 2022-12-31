package krylov.psychology.repository;

import krylov.psychology.model.MyInformation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyInformationRepository extends JpaRepository<MyInformation, Long> {
}
