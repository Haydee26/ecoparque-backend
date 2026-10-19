package prog2.ecoparque.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import prog2.ecoparque.model.Parque;

public interface ParqueRepository extends JpaRepository<Parque, Long> {

    /**
     * Busca el parque bloqueando su fila hasta que termine la operacion.
     * Asi, dos pedidos simultaneos no pueden pasarse del area total entre los dos.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Parque p where p.id = :id")
    Optional<Parque> buscarBloqueando(@Param("id") Long id);
}
