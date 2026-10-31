package prog2.ecoparque.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import prog2.ecoparque.model.Itinerario;

public interface ItinerarioRepository extends JpaRepository<Itinerario, Long> {

    Optional<Itinerario> findFirstByCodigoIgnoreCase(String codigo);
}
