package prog2.ecoparque.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prog2.ecoparque.model.Especie;
import java.util.List;

public interface EspecieRepository extends JpaRepository<Especie, Long> {
    
    List<Especie> findByEnfermaTrue();
}
