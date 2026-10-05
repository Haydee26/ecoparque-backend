package prog2.ecoparque.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prog2.ecoparque.model.Habitat;

public interface HabitatRepository extends JpaRepository<Habitat, Long> {
}
