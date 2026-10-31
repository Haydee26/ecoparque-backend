package prog2.ecoparque.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prog2.ecoparque.exception.RecursoNoEncontradoException;
import prog2.ecoparque.exception.ReglaDeNegocioException;
import prog2.ecoparque.model.AsignacionGuia;
import prog2.ecoparque.model.Guia;
import prog2.ecoparque.model.Itinerario;
import prog2.ecoparque.repository.AsignacionGuiaRepository;
import prog2.ecoparque.repository.GuiaRepository;
import prog2.ecoparque.repository.ItinerarioRepository;

/**
 * Reglas de las asignaciones de guia: la guia y el itinerario tienen que existir,
 * y el horario tiene hora desde y hora hasta, con la segunda posterior a la primera.
 */
@Service
public class AsignacionGuiaService {

    private final AsignacionGuiaRepository asignacionRepository;
    private final GuiaRepository guiaRepository;
    private final ItinerarioRepository itinerarioRepository;

    public AsignacionGuiaService(AsignacionGuiaRepository asignacionRepository,
            GuiaRepository guiaRepository, ItinerarioRepository itinerarioRepository) {
        this.asignacionRepository = asignacionRepository;
        this.guiaRepository = guiaRepository;
        this.itinerarioRepository = itinerarioRepository;
    }

    @Transactional(readOnly = true)
    public List<AsignacionGuia> listar() {
        return asignacionRepository.findAll(Sort.by("id"));
    }

    @Transactional
    public AsignacionGuia crear(AsignacionGuia datos) {
        validar(datos);
        AsignacionGuia nueva = new AsignacionGuia();
        copiar(datos, nueva);
        return asignacionRepository.save(nueva);
    }

    @Transactional
    public AsignacionGuia actualizar(Long id, AsignacionGuia datos) {
        validar(datos);
        AsignacionGuia asignacion = buscar(id);
        copiar(datos, asignacion);
        return asignacionRepository.save(asignacion);
    }

    @Transactional
    public void eliminar(Long id) {
        buscar(id);
        asignacionRepository.deleteById(id);
        asignacionRepository.flush();
    }

    private AsignacionGuia buscar(Long id) {
        return asignacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la asignación " + id + "."));
    }

    /** Pasa los datos validados, usando la guia y el itinerario reales de la base. */
    private void copiar(AsignacionGuia datos, AsignacionGuia destino) {
        Guia guia = guiaRepository.findById(datos.getGuia().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la guía " + datos.getGuia().getId() + "."));
        Itinerario itinerario = itinerarioRepository.findById(datos.getItinerario().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el itinerario " + datos.getItinerario().getId() + "."));
        destino.setGuia(guia);
        destino.setItinerario(itinerario);
        destino.setHora(datos.getHora());
        destino.setHoraHasta(datos.getHoraHasta());
    }

    private void validar(AsignacionGuia datos) {
        if (datos == null) {
            throw new ReglaDeNegocioException("Faltan los datos de la asignación.");
        }
        if (datos.getGuia() == null || datos.getGuia().getId() == null) {
            throw new ReglaDeNegocioException("Tenés que indicar la guía.");
        }
        if (datos.getItinerario() == null || datos.getItinerario().getId() == null) {
            throw new ReglaDeNegocioException("Tenés que indicar el itinerario.");
        }
        Validaciones.obligatorio(datos.getHora(), "La hora desde es obligatoria.");
        Validaciones.obligatorio(datos.getHoraHasta(), "La hora hasta es obligatoria.");
        if (!datos.getHoraHasta().isAfter(datos.getHora())) {
            throw new ReglaDeNegocioException("La hora hasta tiene que ser posterior a la hora desde.");
        }
    }
}
