package prog2.ecoparque.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prog2.ecoparque.exception.RecursoNoEncontradoException;
import prog2.ecoparque.exception.ReglaDeNegocioException;
import prog2.ecoparque.model.Itinerario;
import prog2.ecoparque.model.Zona;
import prog2.ecoparque.repository.ItinerarioRepository;
import prog2.ecoparque.repository.ZonaRepository;

/**
 * Reglas de los itinerarios: código único, duración, longitud y máximo de visitantes
 * mayores que cero, y solo se pueden recorrer zonas que existen.
 */
@Service
public class ItinerarioService {

    private final ItinerarioRepository itinerarioRepository;
    private final ZonaRepository zonaRepository;

    public ItinerarioService(ItinerarioRepository itinerarioRepository, ZonaRepository zonaRepository) {
        this.itinerarioRepository = itinerarioRepository;
        this.zonaRepository = zonaRepository;
    }

    @Transactional(readOnly = true)
    public List<Itinerario> listar() {
        return itinerarioRepository.findAll(Sort.by("id"));
    }

    @Transactional
    public Itinerario crear(Itinerario datos) {
        validar(datos, null);
        Itinerario nuevo = new Itinerario();
        nuevo.setCodigo(datos.getCodigo().trim());
        nuevo.setDuracionMinutos(datos.getDuracionMinutos());
        nuevo.setLongitudMetros(datos.getLongitudMetros());
        nuevo.setMaximoVisitantes(datos.getMaximoVisitantes());
        nuevo.setCantidadEspecies(datos.getCantidadEspecies() == null ? 0 : datos.getCantidadEspecies());
        nuevo.setZonas(zonasExistentes(datos));
        return itinerarioRepository.save(nuevo);
    }

    @Transactional
    public Itinerario actualizar(Long id, Itinerario datos) {
        validar(datos, id);
        Itinerario itinerario = buscar(id);
        itinerario.setCodigo(datos.getCodigo().trim());
        itinerario.setDuracionMinutos(datos.getDuracionMinutos());
        itinerario.setLongitudMetros(datos.getLongitudMetros());
        itinerario.setMaximoVisitantes(datos.getMaximoVisitantes());
        // Si el pedido no trae la cantidad, se conserva la que ya tenía (no se borra).
        if (datos.getCantidadEspecies() != null) {
            itinerario.setCantidadEspecies(datos.getCantidadEspecies());
        }
        itinerario.setZonas(zonasExistentes(datos));
        return itinerarioRepository.save(itinerario);
    }

    @Transactional
    public void eliminar(Long id) {
        buscar(id);
        itinerarioRepository.deleteById(id);
        // flush: si el itinerario tiene guías asignadas, el error aparece acá y se informa con claridad.
        itinerarioRepository.flush();
    }

    private Itinerario buscar(Long id) {
        return itinerarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el itinerario " + id + "."));
    }

    /** Reemplaza las zonas recibidas por las que de verdad están en la base de datos. */
    private Set<Zona> zonasExistentes(Itinerario datos) {
        Set<Zona> resultado = new HashSet<>();
        if (datos.getZonas() == null) {
            return resultado;
        }
        for (Zona pedida : datos.getZonas()) {
            Validaciones.obligatorio(pedida == null ? null : pedida.getId(), "Hay una zona sin indicar.");
            resultado.add(zonaRepository.findById(pedida.getId()).orElseThrow(
                    () -> new ReglaDeNegocioException("La zona " + pedida.getId() + " no existe.")));
        }
        return resultado;
    }

    private void validar(Itinerario itinerario, Long idActual) {
        if (itinerario == null) {
            throw new ReglaDeNegocioException("Faltan los datos del itinerario.");
        }
        Validaciones.textoObligatorio(itinerario.getCodigo(), "El código del itinerario");
        Validaciones.enteroPositivo(itinerario.getDuracionMinutos(), "La duración");
        Validaciones.numeroPositivo(itinerario.getLongitudMetros(), "La longitud");
        Validaciones.enteroPositivo(itinerario.getMaximoVisitantes(), "El máximo de visitantes");
        Validaciones.enteroNoNegativo(itinerario.getCantidadEspecies(), "La cantidad de especies");
        itinerarioRepository.findFirstByCodigoIgnoreCase(itinerario.getCodigo().trim())
                .filter(otro -> !otro.getId().equals(idActual))
                .ifPresent(otro -> {
                    throw new ReglaDeNegocioException("Ya existe un itinerario con el código " + otro.getCodigo() + ".");
                });
    }
}
