package krylov.psychology.repository;

import krylov.psychology.model.DayTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface DayTimeRepository extends JpaRepository<DayTime, Long> {
}
