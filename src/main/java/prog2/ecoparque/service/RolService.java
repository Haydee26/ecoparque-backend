package prog2.ecoparque.service;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prog2.ecoparque.dto.PermisoInfo;
import prog2.ecoparque.dto.RolDatos;
import prog2.ecoparque.dto.RolDetalle;
import prog2.ecoparque.exception.RecursoNoEncontradoException;
import prog2.ecoparque.exception.ReglaDeNegocioException;
import prog2.ecoparque.model.Permiso;
import prog2.ecoparque.model.Rol;
import prog2.ecoparque.model.Usuario;
import prog2.ecoparque.repository.RolRepository;
import prog2.ecoparque.repository.UsuarioRepository;

/**
 * Reglas de los roles: nombre unico, al menos un permiso, "editar" incluye "ver",
 * los roles base no se borran ni se renombran, el administrador siempre tiene todos
 * los permisos y un rol que tiene usuarios no se puede eliminar.
 */
@Service
public class RolService {

    private static final int LARGO_MAXIMO_NOMBRE = 30;

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;

    public RolService(RolRepository rolRepository, UsuarioRepository usuarioRepository) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<RolDetalle> listar() {
        return rolRepository.findAll(Sort.by("id")).stream().map(this::detalle).toList();
    }

    /** Catalogo completo de permisos, para mostrar los tildes. */
    public List<PermisoInfo> catalogo() {
        return Arrays.stream(Permiso.values())
                .map(p -> new PermisoInfo(p.name(), p.getSeccion(), p.getAccion())).toList();
    }

    /** Permisos de quien inició sesión (las pantallas los usan para armar menús y botones). */
    @Transactional(readOnly = true)
    public List<String> permisosDe(String nombreUsuario) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario " + nombreUsuario + "."));
        if (usuario.getRolAsignado() == null) {
            return List.of();
        }
        return usuario.getRolAsignado().getPermisos().stream().map(Permiso::name).sorted().toList();
    }

    @Transactional
    public RolDetalle crear(RolDatos datos) {
        String nombre = normalizarNombre(datos);
        if (rolRepository.findByNombreIgnoreCase(nombre).isPresent()) {
            throw new ReglaDeNegocioException("Ya existe un rol llamado " + nombre + ".");
        }
        Rol rol = new Rol(nombre, descripcion(datos), false, permisosDe(datos));
        return detalle(rolRepository.save(rol));
    }

    @Transactional
    public RolDetalle actualizar(Long id, RolDatos datos) {
        Rol rol = buscar(id);
        if (Rol.ADMINISTRADOR.equals(rol.getNombre())) {
            throw new ReglaDeNegocioException("El rol ADMINISTRADOR siempre tiene todos los permisos: no se modifica.");
        }
        String nombre = normalizarNombre(datos);
        if (rol.isBase() && !rol.getNombre().equalsIgnoreCase(nombre)) {
            throw new ReglaDeNegocioException("Los roles base no se pueden renombrar.");
        }
        rolRepository.findByNombreIgnoreCase(nombre)
                .filter(otro -> !otro.getId().equals(id))
                .ifPresent(otro -> {
                    throw new ReglaDeNegocioException("Ya existe un rol llamado " + nombre + ".");
                });
        rol.setNombre(nombre);
        rol.setDescripcion(descripcion(datos));
        rol.setPermisos(permisosDe(datos));
        return detalle(rolRepository.save(rol));
    }

    @Transactional
    public void eliminar(Long id) {
        Rol rol = buscar(id);
        if (rol.isBase()) {
            throw new ReglaDeNegocioException("Los roles base (administrador, guía, cuidador e invitado) no se pueden eliminar.");
        }
        long enUso = usuarioRepository.countByRolAsignado_Id(id);
        if (enUso > 0) {
            throw new ReglaDeNegocioException("No se puede eliminar el rol " + rol.getNombre() + ": lo tienen "
                    + enUso + " usuario(s). Cambiales el rol primero.");
        }
        rolRepository.delete(rol);
    }

    private Rol buscar(Long id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el rol " + id + "."));
    }

    private RolDetalle detalle(Rol rol) {
        List<String> permisos = rol.getPermisos().stream().map(Permiso::name).sorted().toList();
        return new RolDetalle(rol.getId(), rol.getNombre(), rol.getDescripcion(), rol.isBase(), permisos,
                usuarioRepository.countByRolAsignado_Id(rol.getId()));
    }

    private String normalizarNombre(RolDatos datos) {
        if (datos == null) {
            throw new ReglaDeNegocioException("Faltan los datos del rol.");
        }
        Validaciones.textoObligatorio(datos.nombre(), "El nombre del rol");
        String nombre = datos.nombre().trim().toUpperCase().replaceAll("\\s+", "_");
        if (nombre.length() > LARGO_MAXIMO_NOMBRE) {
            throw new ReglaDeNegocioException("El nombre del rol no puede superar los " + LARGO_MAXIMO_NOMBRE + " caracteres.");
        }
        return nombre;
    }

    private String descripcion(RolDatos datos) {
        return datos.descripcion() == null ? "" : datos.descripcion().trim();
    }

    /** Convierte los codigos recibidos en permisos; "editar" agrega "ver" de su seccion. */
    private Set<Permiso> permisosDe(RolDatos datos) {
        Set<Permiso> permisos = EnumSet.noneOf(Permiso.class);
        if (datos.permisos() != null) {
            for (String codigo : datos.permisos()) {
                try {
                    Permiso permiso = Permiso.valueOf(codigo.trim());
                    permisos.add(permiso);
                    if (permiso.esEditar()) {
                        permisos.add(permiso.verDeLaSeccion());
                    }
                } catch (IllegalArgumentException | NullPointerException e) {
                    throw new ReglaDeNegocioException("El permiso " + codigo + " no existe.");
                }
            }
        }
        if (permisos.isEmpty()) {
            throw new ReglaDeNegocioException("Elegí al menos un permiso para el rol.");
        }
        return permisos;
    }
}
