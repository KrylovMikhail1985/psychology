package krylov.psychology.repository;

import krylov.psychology.model.QTherapy;
import krylov.psychology.model.Therapy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TherapyRepository extends JpaRepository<Therapy, Long>,
        QuerydslPredicateExecutor<Therapy>,
        QuerydslBinderCustomizer<QTherapy> {
    @Override
    default void customize(QuerydslBindings bindings, QTherapy qTherapy) { }
    Optional<Therapy> findByEmail(String email);
}
