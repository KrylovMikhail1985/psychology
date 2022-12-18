package krylov.psychology.repository;

import krylov.psychology.model.Day;
import krylov.psychology.model.QDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface DayRepository extends JpaRepository<Day, Long>,
        QuerydslPredicateExecutor<Day>,
        QuerydslBinderCustomizer<QDay> {
    @Override
    default void customize(QuerydslBindings bindings, QDay qDay) {}

    Optional<Day> findByDate(Date date);
}
