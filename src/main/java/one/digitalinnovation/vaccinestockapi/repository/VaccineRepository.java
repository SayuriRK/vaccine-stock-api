package one.digitalinnovation.vaccinestockapi.repository;

import one.digitalinnovation.vaccinestockapi.entity.Vaccine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VaccineRepository extends JpaRepository<Vaccine, Long> {
    Optional<Vaccine> findByName(String name);
}
