package prog2.ecoparque.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import prog2.ecoparque.model.Cuidador;

public interface CuidadorRepository extends JpaRepository<Cuidador, Long> {

    Optional<Cuidador> findFirstByEmpleado_Id(Long empleadoId);
}
