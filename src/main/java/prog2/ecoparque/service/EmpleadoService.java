package prog2.ecoparque.service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prog2.ecoparque.dto.EmpleadoDetalle;
import prog2.ecoparque.exception.RecursoNoEncontradoException;
import prog2.ecoparque.exception.ReglaDeNegocioException;
import prog2.ecoparque.model.Empleado;
import prog2.ecoparque.model.Guia;
import prog2.ecoparque.model.Cuidador;
import prog2.ecoparque.model.RolUsuario;
import prog2.ecoparque.model.Usuario;
import prog2.ecoparque.repository.CuidadorRepository;
import prog2.ecoparque.repository.EmpleadoRepository;
import prog2.ecoparque.repository.GuiaRepository;
import prog2.ecoparque.repository.UsuarioRepository;

/**
 * Base de datos de empleados: datos personales, busqueda por nombre, apellido o DNI
 * y listado por antiguedad. Nombre y apellido juntos no se repiten (con ellos se vincula al usuario);
 * el DNI tampoco. Aca tambien se le da o se le quita a cada empleado el rol de guia y/o de cuidador;
 * un rol no se puede quitar si tiene asignaciones o responsabilidades, ni a quien tiene un usuario con ese rol.
 */
@Service
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final GuiaRepository guiaRepository;
    private final CuidadorRepository cuidadorRepository;
    private final UsuarioRepository usuarioRepository;

    public EmpleadoService(EmpleadoRepository empleadoRepository, GuiaRepository guiaRepository,
            CuidadorRepository cuidadorRepository, UsuarioRepository usuarioRepository) {
        this.empleadoRepository = empleadoRepository;
        this.guiaRepository = guiaRepository;
        this.cuidadorRepository = cuidadorRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /** Todos los empleados, del que hace mas tiempo que ingreso al mas nuevo; "texto" filtra por nombre, apellido o DNI. */
    @Transactional(readOnly = true)
    public List<EmpleadoDetalle> listar(String texto) {
        String buscado = normalizar(texto);
        Set<Long> guias = new HashSet<>();
        guiaRepository.findAll().forEach(g -> guias.add(g.getEmpleadoId()));
        Set<Long> cuidadores = new HashSet<>();
        cuidadorRepository.findAll().forEach(c -> cuidadores.add(c.getEmpleadoId()));
        Set<String> administradores = new HashSet<>();
        for (Usuario usuario : usuarioRepository.findAll()) {
            if (usuario.getRol() == RolUsuario.ADMINISTRADOR && usuario.getNombrePersona() != null) {
                administradores.add(normalizar(usuario.getNombrePersona()));
            }
        }

        LocalDate hoy = LocalDate.now();
        List<Empleado> empleados = new ArrayList<>(empleadoRepository.findAll());
        empleados.sort(Comparator
                .comparing(Empleado::getFechaIngreso, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(e -> normalizar(e.getApellido())));

        List<EmpleadoDetalle> resultado = new ArrayList<>();
        for (Empleado e : empleados) {
            String completo = normalizar(e.nombreCompleto());
            String inverso = normalizar(e.getApellido() + " " + e.getNombre());
            boolean coincide = buscado.isEmpty() || completo.contains(buscado) || inverso.contains(buscado)
                    || (e.getDni() != null && e.getDni().contains(buscado));
            if (!coincide) {
                continue;
            }
            List<String> puestos = new ArrayList<>();
            if (administradores.contains(completo)) {
                puestos.add("Administrador");
            }
            if (guias.contains(e.getId())) {
                puestos.add("Guía");
            }
            if (cuidadores.contains(e.getId())) {
                puestos.add("Cuidador");
            }
            Long anios = e.getFechaIngreso() == null || e.getFechaIngreso().isAfter(hoy)
                    ? null : ChronoUnit.YEARS.between(e.getFechaIngreso(), hoy);
            resultado.add(new EmpleadoDetalle(e.getId(), e.getNombre(), e.getApellido(), e.getDni(),
                    e.getFechaNacimiento(), e.getDireccion(), e.getTelefono(), e.getCorreo(),
                    e.getFechaIngreso(), anios, puestos));
        }
        return resultado;
    }

    @Transactional
    public Empleado crear(Empleado datos) {
        validar(datos, null);
        // Se arma un registro nuevo para que nadie pueda pisar uno existente mandando un id.
        Empleado nuevo = new Empleado();
        copiar(datos, nuevo);
        Empleado guardado = empleadoRepository.save(nuevo);
        aplicarRoles(guardado, datos.getRoles());
        return guardado;
    }

    @Transactional
    public Empleado actualizar(Long id, Empleado datos) {
        validar(datos, id);
        Empleado empleado = buscar(id);
        String nombreAnterior = empleado.nombreCompleto();
        copiar(datos, empleado);
        // Si cambio el nombre, los usuarios vinculados a esta persona siguen apuntando a ella.
        String nombreNuevo = empleado.nombreCompleto();
        if (!nombreAnterior.equalsIgnoreCase(nombreNuevo)) {
            for (Usuario usuario : usuarioRepository.findAll()) {
                if (usuario.getNombrePersona() != null
                        && usuario.getNombrePersona().trim().equalsIgnoreCase(nombreAnterior)) {
                    usuario.setNombrePersona(nombreNuevo);
                    usuarioRepository.save(usuario);
                }
            }
        }
        Empleado guardado = empleadoRepository.save(empleado);
        aplicarRoles(guardado, datos.getRoles());
        return guardado;
    }

    @Transactional
    public void eliminar(Long id) {
        Empleado empleado = buscar(id);
        for (Usuario usuario : usuarioRepository.findAll()) {
            if (usuario.getNombrePersona() != null
                    && usuario.getNombrePersona().trim().equalsIgnoreCase(empleado.nombreCompleto())) {
                throw new ReglaDeNegocioException("No se puede eliminar: " + empleado.nombreCompleto()
                        + " tiene el usuario " + usuario.getNombreUsuario() + ". Eliminá o cambiá ese usuario primero.");
            }
        }
        // Sus roles se quitan con el; si tiene asignaciones o responsabilidades, avisa y no borra nada.
        ajustarGuia(empleado, false);
        ajustarCuidador(empleado, false);
        empleadoRepository.deleteById(id);
    }

    /** Deja al empleado con exactamente los roles pedidos; si "roles" no viene, no cambia nada. */
    private void aplicarRoles(Empleado empleado, List<String> roles) {
        if (roles == null) {
            return;
        }
        Set<String> pedidos = new HashSet<>();
        for (String rol : roles) {
            pedidos.add(normalizar(rol));
        }
        ajustarGuia(empleado, pedidos.contains("guia"));
        ajustarCuidador(empleado, pedidos.contains("cuidador"));
    }

    private void ajustarGuia(Empleado empleado, boolean quiere) {
        Guia actual = guiaRepository.findFirstByEmpleado_Id(empleado.getId()).orElse(null);
        if (quiere && actual == null) {
            guiaRepository.save(new Guia(empleado));
        } else if (!quiere && actual != null) {
            rechazarSiTieneUsuario(empleado, RolUsuario.GUIA, "guía");
            try {
                guiaRepository.delete(actual);
                guiaRepository.flush();
            } catch (DataIntegrityViolationException e) {
                throw new ReglaDeNegocioException("No se puede quitar el rol de guía a " + empleado.nombreCompleto()
                        + ": tiene asignaciones a itinerarios. Eliminá primero esas asignaciones.");
            }
        }
    }

    private void ajustarCuidador(Empleado empleado, boolean quiere) {
        Cuidador actual = cuidadorRepository.findFirstByEmpleado_Id(empleado.getId()).orElse(null);
        if (quiere && actual == null) {
            cuidadorRepository.save(new Cuidador(empleado));
        } else if (!quiere && actual != null) {
            rechazarSiTieneUsuario(empleado, RolUsuario.CUIDADOR, "cuidador");
            try {
                cuidadorRepository.delete(actual);
                cuidadorRepository.flush();
            } catch (DataIntegrityViolationException e) {
                throw new ReglaDeNegocioException("No se puede quitar el rol de cuidador a " + empleado.nombreCompleto()
                        + ": tiene responsabilidades sobre especies. Eliminá primero esas responsabilidades.");
            }
        }
    }

    /** Un usuario con rol de guia o cuidador necesita que su persona siga teniendo ese rol. */
    private void rechazarSiTieneUsuario(Empleado empleado, RolUsuario rol, String nombreDelRol) {
        for (Usuario usuario : usuarioRepository.findAll()) {
            if (usuario.getRol() == rol && usuario.getNombrePersona() != null
                    && usuario.getNombrePersona().trim().equalsIgnoreCase(empleado.nombreCompleto())) {
                throw new ReglaDeNegocioException("No se puede quitar el rol de " + nombreDelRol + " a "
                        + empleado.nombreCompleto() + ": tiene el usuario " + usuario.getNombreUsuario()
                        + " con ese rol. Cambiá o eliminá ese usuario primero.");
            }
        }
    }

    private Empleado buscar(Long id) {
        return empleadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el empleado " + id + "."));
    }

    private void copiar(Empleado origen, Empleado destino) {
        destino.setNombre(origen.getNombre().trim());
        destino.setApellido(origen.getApellido().trim());
        destino.setDni(origen.getDni().trim());
        destino.setFechaNacimiento(origen.getFechaNacimiento());
        destino.setDireccion(origen.getDireccion().trim());
        destino.setTelefono(origen.getTelefono().trim());
        destino.setCorreo(vacioANulo(origen.getCorreo()));
        destino.setFechaIngreso(origen.getFechaIngreso());
    }

    private void validar(Empleado datos, Long idActual) {
        if (datos == null) {
            throw new ReglaDeNegocioException("Faltan los datos del empleado.");
        }
        Validaciones.textoObligatorio(datos.getNombre(), "El nombre");
        Validaciones.textoObligatorio(datos.getApellido(), "El apellido");
        Validaciones.dni(datos.getDni());
        Validaciones.fechaDeNacimiento(datos.getFechaNacimiento());
        Validaciones.textoObligatorio(datos.getDireccion(), "La dirección");
        Validaciones.textoObligatorio(datos.getTelefono(), "El teléfono");
        Validaciones.telefonoOpcional(datos.getTelefono());
        Validaciones.correoOpcional(datos.getCorreo());
        Validaciones.fechaDeIngreso(datos.getFechaIngreso());
        if (datos.getFechaIngreso().isBefore(datos.getFechaNacimiento().plusYears(18))) {
            throw new ReglaDeNegocioException("La fecha de ingreso no puede ser anterior a que cumpla 18 años.");
        }

        empleadoRepository.findFirstByDni(datos.getDni().trim())
                .filter(otro -> !otro.getId().equals(idActual))
                .ifPresent(otro -> {
                    throw new ReglaDeNegocioException("Ya hay un empleado con el DNI " + otro.getDni()
                            + " (" + otro.nombreCompleto() + ").");
                });
        String completo = datos.getNombre().trim() + " " + datos.getApellido().trim();
        empleadoRepository.buscarPorNombreCompleto(completo)
                .filter(otro -> !otro.getId().equals(idActual))
                .ifPresent(otro -> {
                    throw new ReglaDeNegocioException("Ya hay un empleado llamado " + otro.nombreCompleto() + ".");
                });
    }

    private String vacioANulo(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }

    /** Minusculas y sin tildes, para buscar sin que importen. */
    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        return Normalizer.normalize(texto.trim().toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }
}
