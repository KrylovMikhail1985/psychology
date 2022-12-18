package krylov.psychology.repository;

import krylov.psychology.model.Therapy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TherapyRepository extends JpaRepository<Therapy, Long> {
    Optional<Therapy> findByEmail(String email);
}
