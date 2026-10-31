package prog2.ecoparque.repository; 
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import prog2.ecoparque.model.Guia; 
public interface GuiaRepository extends JpaRepository<Guia, Long> {

    Optional<Guia> findFirstByNombreIgnoreCase(String nombre);
    
}
