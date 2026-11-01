package prog2.ecoparque.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import prog2.ecoparque.model.Empleado;

public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    Optional<Empleado> findFirstByDni(String dni);

    /** Busca por "Nombre Apellido", sin importar mayusculas. */
    @Query("select e from Empleado e where lower(concat(trim(e.nombre), ' ', trim(e.apellido))) = lower(:nombre)")
    Optional<Empleado> buscarPorNombreCompleto(@Param("nombre") String nombre);
}
