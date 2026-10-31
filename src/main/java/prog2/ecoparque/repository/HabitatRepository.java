package prog2.ecoparque.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import prog2.ecoparque.model.Habitat;

public interface HabitatRepository extends JpaRepository<Habitat, Long> {

    Optional<Habitat> findFirstByNombreIgnoreCase(String nombre);
}
