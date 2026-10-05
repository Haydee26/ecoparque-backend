package prog2.ecoparque.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prog2.ecoparque.model.Cuidador;

public interface CuidadorRepository extends JpaRepository<Cuidador, Long> {
}
