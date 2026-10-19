package prog2.ecoparque.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import prog2.ecoparque.model.Zona;

public interface ZonaRepository extends JpaRepository<Zona, Long> {

    Optional<Zona> findByNombre(String nombre);

    /** Suma de la extension de todas las zonas (0 si no hay ninguna). */
    @Query("select coalesce(sum(z.extension), 0.0) from Zona z")
    Double sumaExtensiones();
}
