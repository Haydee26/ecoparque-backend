package prog2.ecoparque.repository; 
import org.springframework.data.jpa.repository.JpaRepository;

import prog2.ecoparque.model.Guia; 
public interface GuiaRepository extends JpaRepository<Guia, Long> {
    
}
