package prog2.ecoparque.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prog2.ecoparque.dto.UsuarioDatos;
import prog2.ecoparque.exception.RecursoNoEncontradoException;
import prog2.ecoparque.exception.ReglaDeNegocioException;
import prog2.ecoparque.model.Rol;
import prog2.ecoparque.model.Usuario;
import prog2.ecoparque.model.Empleado;
import prog2.ecoparque.repository.CuidadorRepository;
import prog2.ecoparque.repository.EmpleadoRepository;
import prog2.ecoparque.repository.GuiaRepository;
import prog2.ecoparque.repository.RolRepository;
import prog2.ecoparque.repository.UsuarioRepository;

/**
 * Reglas de los usuarios del sistema: nombre de usuario único, clave con un mínimo de largo,
 * rol obligatorio, claves guardadas encriptadas (BCrypt), guías y cuidadores asociados a una persona que existe, y el sistema
 * nunca se queda sin administradores ni deja que alguien se elimine a sí mismo.
 */
// Nombre de bean propio: en el paquete security ya existe otra clase UsuarioService (la del login).
@Service("gestionDeUsuarios")
public class UsuarioService {

    private static final int LARGO_MINIMO_CLAVE = 4;
    // BCrypt solo usa los primeros 72 bytes de la clave.
    private static final int LARGO_MAXIMO_CLAVE = 72;

    private final UsuarioRepository usuarioRepository;
    private final GuiaRepository guiaRepository;
    private final CuidadorRepository cuidadorRepository;
    private final EmpleadoRepository empleadoRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder codificador;

    public UsuarioService(UsuarioRepository usuarioRepository, GuiaRepository guiaRepository,
            CuidadorRepository cuidadorRepository, EmpleadoRepository empleadoRepository,
            RolRepository rolRepository, PasswordEncoder codificador) {
        this.usuarioRepository = usuarioRepository;
        this.guiaRepository = guiaRepository;
        this.cuidadorRepository = cuidadorRepository;
        this.empleadoRepository = empleadoRepository;
        this.rolRepository = rolRepository;
        this.codificador = codificador;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioRepository.findAll(Sort.by("id"));
    }

    /** Datos de quien inició sesión. */
    @Transactional(readOnly = true)
    public Usuario obtenerPorNombre(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario " + nombreUsuario + "."));
    }

    @Transactional
    public Usuario crear(UsuarioDatos datos) {
        validar(datos, null, true);
        // Se arma un usuario nuevo para que nadie pueda pisar uno existente mandando un id.
        return usuarioRepository.save(new Usuario(datos.nombreUsuario().trim(), codificador.encode(datos.clave()),
                buscarRol(datos), personaDe(datos)));
    }

    @Transactional
    public Usuario actualizar(Long id, UsuarioDatos datos) {
        Usuario usuario = buscar(id);
        validar(datos, id, false);
        if (esAdministrador(usuario.getRolAsignado()) && !esAdministrador(buscarRol(datos))
                && esElUnicoAdministrador()) {
            throw new ReglaDeNegocioException("No se puede quitar el rol al único administrador del sistema.");
        }
        usuario.setNombreUsuario(datos.nombreUsuario().trim());
        usuario.setRolAsignado(buscarRol(datos));
        usuario.setNombrePersona(personaDe(datos));
        // Una clave vacía significa "dejar la que ya tiene".
        if (datos.clave() != null && !datos.clave().isBlank()) {
            usuario.setClave(codificador.encode(datos.clave()));
        }
        return usuarioRepository.save(usuario);
    }

    /**
     * @param quienElimina nombre de usuario de quien hace el pedido
     */
    @Transactional
    public void eliminar(Long id, String quienElimina) {
        Usuario usuario = buscar(id);
        if (usuario.getNombreUsuario().equals(quienElimina)) {
            throw new ReglaDeNegocioException("No podés eliminar tu propio usuario.");
        }
        if (esAdministrador(usuario.getRolAsignado()) && esElUnicoAdministrador()) {
            throw new ReglaDeNegocioException("No se puede eliminar al único administrador del sistema.");
        }
        usuarioRepository.deleteById(id);
    }

    private Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario " + id + "."));
    }

    private boolean esElUnicoAdministrador() {
        return rolRepository.findByNombreIgnoreCase(Rol.ADMINISTRADOR)
                .map(rol -> usuarioRepository.countByRolAsignado_Id(rol.getId()) <= 1)
                .orElse(true);
    }

    private boolean esAdministrador(Rol rol) {
        return rol != null && Rol.ADMINISTRADOR.equals(rol.getNombre());
    }

    /** Busca en la tabla rol el rol que llegó por nombre. */
    private Rol buscarRol(UsuarioDatos datos) {
        return rolRepository.findByNombreIgnoreCase(datos.rol().trim())
                .orElseThrow(() -> new ReglaDeNegocioException("El rol " + datos.rol() + " no existe."));
    }

    private String personaDe(UsuarioDatos datos) {
        return datos.nombrePersona() == null ? "" : datos.nombrePersona().trim();
    }

    private void validar(UsuarioDatos usuario, Long idActual, boolean esNuevo) {
        if (usuario == null) {
            throw new ReglaDeNegocioException("Faltan los datos del usuario.");
        }
        Validaciones.textoObligatorio(usuario.nombreUsuario(), "El nombre de usuario");
        Validaciones.textoObligatorio(usuario.rol(), "El rol");
        buscarRol(usuario);

        usuarioRepository.findByNombreUsuario(usuario.nombreUsuario().trim())
                .filter(otro -> !otro.getId().equals(idActual))
                .ifPresent(otro -> {
                    throw new ReglaDeNegocioException("Ya existe un usuario llamado " + otro.getNombreUsuario() + ".");
                });

        // Al crear la clave es obligatoria; al modificar, si viene vacía se conserva la anterior.
        boolean traeClave = usuario.clave() != null && !usuario.clave().isBlank();
        if (esNuevo) {
            Validaciones.textoObligatorio(usuario.clave(), "La clave");
        }
        if (traeClave && usuario.clave().length() < LARGO_MINIMO_CLAVE) {
            throw new ReglaDeNegocioException("La clave debe tener al menos " + LARGO_MINIMO_CLAVE + " caracteres.");
        }
        if (traeClave && usuario.clave().getBytes(java.nio.charset.StandardCharsets.UTF_8).length > LARGO_MAXIMO_CLAVE) {
            throw new ReglaDeNegocioException("La clave no puede superar los " + LARGO_MAXIMO_CLAVE + " caracteres.");
        }

        validarPersona(usuario);
    }

    /**
     * La persona del usuario tiene que ser un empleado ya cargado (por nombre y apellido).
     * Un guía o cuidador la necesita siempre, y con el rol que dice; el administrador, si la completa;
     * el invitado no la necesita.
     */
    private void validarPersona(UsuarioDatos usuario) {
        // El invitado es una persona de afuera: no tiene que ser un empleado.
        if (Rol.INVITADO.equals(usuario.rol().trim().toUpperCase())) {
            return;
        }
        String persona = personaDe(usuario);
        String rol = usuario.rol().trim().toUpperCase();
        boolean necesitaPersona = Rol.GUIA.equals(rol) || Rol.CUIDADOR.equals(rol);
        if (necesitaPersona) {
            Validaciones.textoObligatorio(persona, "El nombre de la persona");
        }
        if (persona.isEmpty()) {
            return;
        }
        Empleado empleado = empleadoRepository.buscarPorNombreCompleto(persona)
                .orElseThrow(() -> new ReglaDeNegocioException("No hay un empleado llamado " + persona
                        + ". Cargalo primero en Empleados (nombre y apellido)."));
        if (Rol.GUIA.equals(rol) && guiaRepository.findFirstByEmpleado_Id(empleado.getId()).isEmpty()) {
            throw new ReglaDeNegocioException(empleado.nombreCompleto() + " todavía no es guía. Dale el rol en Guías.");
        }
        if (Rol.CUIDADOR.equals(rol)
                && cuidadorRepository.findFirstByEmpleado_Id(empleado.getId()).isEmpty()) {
            throw new ReglaDeNegocioException(empleado.nombreCompleto() + " todavía no es cuidador. Dale el rol en Cuidadores.");
        }
    }
}
