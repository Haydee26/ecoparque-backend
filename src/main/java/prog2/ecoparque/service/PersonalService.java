package prog2.ecoparque.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prog2.ecoparque.dto.GuiaPublica;
import prog2.ecoparque.dto.MisDatos;
import prog2.ecoparque.exception.RecursoNoEncontradoException;
import prog2.ecoparque.model.AsignacionGuia;
import prog2.ecoparque.model.Cuidador;
import prog2.ecoparque.model.Empleado;
import prog2.ecoparque.model.Guia;
import prog2.ecoparque.model.ResponsabilidadCuidador;
import prog2.ecoparque.model.Rol;
import prog2.ecoparque.model.Usuario;
import prog2.ecoparque.model.Zona;
import prog2.ecoparque.repository.AsignacionGuiaRepository;
import prog2.ecoparque.repository.CuidadorRepository;
import prog2.ecoparque.repository.EmpleadoRepository;
import prog2.ecoparque.repository.GuiaRepository;
import prog2.ecoparque.repository.ResponsabilidadCuidadorRepository;
import prog2.ecoparque.repository.UsuarioRepository;

/**
 * Datos del personal pensados para mostrarse a cualquier usuario con sesion:
 * la lista de guias sin datos personales y los datos de uno mismo.
 */
@Service
public class PersonalService {

    private final GuiaRepository guiaRepository;
    private final CuidadorRepository cuidadorRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final AsignacionGuiaRepository asignacionRepository;
    private final ResponsabilidadCuidadorRepository responsabilidadRepository;

    public PersonalService(GuiaRepository guiaRepository, CuidadorRepository cuidadorRepository,
            UsuarioRepository usuarioRepository, EmpleadoRepository empleadoRepository, AsignacionGuiaRepository asignacionRepository,
            ResponsabilidadCuidadorRepository responsabilidadRepository) {
        this.guiaRepository = guiaRepository;
        this.cuidadorRepository = cuidadorRepository;
        this.usuarioRepository = usuarioRepository;
        this.empleadoRepository = empleadoRepository;
        this.asignacionRepository = asignacionRepository;
        this.responsabilidadRepository = responsabilidadRepository;
    }

    /** Nombre de cada guia y de que es guia (itinerarios y zonas). */
    @Transactional(readOnly = true)
    public List<GuiaPublica> guiasPublicas() {
        Map<Long, List<AsignacionGuia>> porGuia = new HashMap<>();
        for (AsignacionGuia asignacion : asignacionRepository.findAll()) {
            if (asignacion.getGuia() != null) {
                porGuia.computeIfAbsent(asignacion.getGuia().getId(), id -> new ArrayList<>()).add(asignacion);
            }
        }

        List<GuiaPublica> resultado = new ArrayList<>();
        List<Guia> guias = new ArrayList<>(guiaRepository.findAll());
        guias.sort(Comparator.comparing(Guia::getNombre, String.CASE_INSENSITIVE_ORDER));
        for (Guia guia : guias) {
            List<AsignacionGuia> propias = porGuia.getOrDefault(guia.getId(), List.of());
            resultado.add(new GuiaPublica(guia.getId(), guia.getNombre(), codigos(propias), zonas(propias)));
        }
        return resultado;
    }

    /** Datos de la persona que inicio sesion. */
    @Transactional(readOnly = true)
    public MisDatos misDatos(String nombreUsuario) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario " + nombreUsuario + "."));
        String rol = usuario.getRol() == null ? "" : usuario.getRol();
        String nombrePersona = usuario.getNombrePersona() == null ? "" : usuario.getNombrePersona().trim();

        Empleado empleado = nombrePersona.isEmpty() ? null
                : empleadoRepository.buscarPorNombreCompleto(nombrePersona).orElse(null);
        if (empleado != null) {
            List<AsignacionGuia> propias = new ArrayList<>();
            List<String> especies = List.of();
            if (Rol.GUIA.equals(rol)) {
                Guia guia = guiaRepository.findFirstByEmpleado_Id(empleado.getId()).orElse(null);
                if (guia != null) {
                    for (AsignacionGuia asignacion : asignacionRepository.findAll()) {
                        if (asignacion.getGuia() != null && Objects.equals(asignacion.getGuia().getId(), guia.getId())) {
                            propias.add(asignacion);
                        }
                    }
                }
            } else if (Rol.CUIDADOR.equals(rol)) {
                Cuidador cuidador = cuidadorRepository.findFirstByEmpleado_Id(empleado.getId()).orElse(null);
                if (cuidador != null) {
                    especies = especiesVigentes(cuidador);
                }
            }
            return new MisDatos(usuario.getNombreUsuario(), rol, empleado.nombreCompleto(),
                    empleado.getDireccion(), empleado.getTelefono(), empleado.getFechaIngreso(),
                    itinerariosConHorario(propias), especies);
        }

        String nombre = nombrePersona.isEmpty() ? usuario.getNombreUsuario() : nombrePersona;
        return new MisDatos(usuario.getNombreUsuario(), rol, nombre,
                null, null, null, List.of(), List.of());
    }

    private List<String> codigos(List<AsignacionGuia> asignaciones) {
        return asignaciones.stream()
                .map(AsignacionGuia::getItinerario)
                .filter(Objects::nonNull)
                .map(itinerario -> itinerario.getCodigo())
                .filter(Objects::nonNull)
                .distinct().sorted().toList();
    }

    private List<String> zonas(List<AsignacionGuia> asignaciones) {
        return asignaciones.stream()
                .map(AsignacionGuia::getItinerario)
                .filter(Objects::nonNull)
                .flatMap(itinerario -> itinerario.getZonas().stream())
                .map(Zona::getNombre)
                .filter(Objects::nonNull)
                .distinct().sorted().toList();
    }

    private List<String> itinerariosConHorario(List<AsignacionGuia> asignaciones) {
        List<String> resultado = new ArrayList<>();
        for (AsignacionGuia asignacion : asignaciones) {
            if (asignacion.getItinerario() == null) {
                continue;
            }
            String horario = asignacion.getHora() == null ? ""
                    : " (" + asignacion.getHora()
                    + (asignacion.getHoraHasta() == null ? "" : " a " + asignacion.getHoraHasta()) + ")";
            resultado.add(asignacion.getItinerario().getCodigo() + horario);
        }
        return resultado;
    }

    /** Especies a cargo hoy: las responsabilidades sin fecha hasta o cuya fecha hasta no paso. */
    private List<String> especiesVigentes(Cuidador cuidador) {
        LocalDate hoy = LocalDate.now();
        List<String> resultado = new ArrayList<>();
        for (ResponsabilidadCuidador r : responsabilidadRepository.findAll()) {
            boolean esDelCuidador = r.getCuidador() != null
                    && Objects.equals(r.getCuidador().getId(), cuidador.getId());
            boolean vigente = r.getFechaHasta() == null || !r.getFechaHasta().isBefore(hoy);
            if (esDelCuidador && vigente && r.getEspecie() != null) {
                resultado.add(r.getEspecie().getNombreEspanol());
            }
        }
        return resultado;
    }
}
