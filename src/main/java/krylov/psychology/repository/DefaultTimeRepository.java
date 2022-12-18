package krylov.psychology.repository;

import krylov.psychology.model.DefaultTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DefaultTimeRepository extends JpaRepository<DefaultTime, Long> {
    Optional<DefaultTime> findByTime(LocalTime localTime);
    Optional<List<DefaultTime>> findAllByOrderByTime();
}
