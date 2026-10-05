package prog2.ecoparque.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prog2.ecoparque.model.Especie;

public interface EspecieRepository extends JpaRepository<Especie, Long> {
}
