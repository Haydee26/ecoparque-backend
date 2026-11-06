package prog2.ecoparque.security;

import java.util.EnumSet;
import java.util.Set;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import prog2.ecoparque.model.Permiso;
import prog2.ecoparque.model.Rol;
import prog2.ecoparque.repository.RolRepository;

/**
 * Al arrancar, se asegura de que existan los cuatro roles base. Si ya estan cargados
 * (por el script SQL) no toca nada, asi que se puede ejecutar todas las veces que haga falta.
 */
@Component
public class InicializadorDeRoles implements ApplicationRunner {

    private final RolRepository rolRepository;

    public InicializadorDeRoles(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        Set<Permiso> consulta = EnumSet.of(Permiso.ZONAS_VER, Permiso.HABITATS_VER, Permiso.ESPECIES_VER,
                Permiso.ITINERARIOS_VER, Permiso.ASIGNACIONES_VER, Permiso.RESPONSABILIDADES_VER);
        crearSiFalta(Rol.ADMINISTRADOR, "Gestiona todo el sistema", EnumSet.allOf(Permiso.class));
        crearSiFalta(Rol.GUIA, "Consulta el parque y sus propios itinerarios", EnumSet.copyOf(consulta));
        crearSiFalta(Rol.CUIDADOR, "Consulta el parque y las especies a su cargo", EnumSet.copyOf(consulta));
        crearSiFalta(Rol.INVITADO, "Visita: solo consulta el parque", EnumSet.copyOf(consulta));
    }

    private void crearSiFalta(String nombre, String descripcion, Set<Permiso> permisos) {
        if (rolRepository.findByNombreIgnoreCase(nombre).isEmpty()) {
            rolRepository.save(new Rol(nombre, descripcion, true, permisos));
        }
    }
}
