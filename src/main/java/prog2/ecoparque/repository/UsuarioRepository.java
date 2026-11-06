package prog2.ecoparque.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import prog2.ecoparque.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByNombreUsuarioAndClave(String nombreUsuario, String clave);

    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    /** Cuántos usuarios tienen un rol; sirve para no quedarse sin administradores ni borrar roles en uso. */
    long countByRolAsignado_Id(Long idRol);
}
