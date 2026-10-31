package prog2.ecoparque.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prog2.ecoparque.exception.RecursoNoEncontradoException;
import prog2.ecoparque.exception.ReglaDeNegocioException;
import prog2.ecoparque.model.Guia;
import prog2.ecoparque.repository.GuiaRepository;

/**
 * Reglas de los datos personales de guía: nombre obligatorio y sin repetir,
 * teléfono con formato válido y fecha de ingreso obligatoria que no sea futura.
 */
@Service
public class GuiaService {

    private final GuiaRepository guiaRepository;

    public GuiaService(GuiaRepository guiaRepository) {
        this.guiaRepository = guiaRepository;
    }

    @Transactional(readOnly = true)
    public List<Guia> listar() {
        return guiaRepository.findAll(Sort.by("id"));
    }

    @Transactional
    public Guia crear(Guia datos) {
        validar(datos, null);
        // Se arma un registro nuevo para que nadie pueda pisar uno existente mandando un id.
        return guiaRepository.save(new Guia(datos.getNombre().trim(), datos.getDireccion(),
                datos.getTelefono(), datos.getFechaIngreso()));
    }

    @Transactional
    public Guia actualizar(Long id, Guia datos) {
        validar(datos, id);
        Guia guia = buscar(id);
        guia.setNombre(datos.getNombre().trim());
        guia.setDireccion(datos.getDireccion());
        guia.setTelefono(datos.getTelefono());
        guia.setFechaIngreso(datos.getFechaIngreso());
        return guiaRepository.save(guia);
    }

    @Transactional
    public void eliminar(Long id) {
        buscar(id);
        guiaRepository.deleteById(id);
        // flush: si tiene asignaciones, el error aparece acá y se informa con claridad.
        guiaRepository.flush();
    }

    private Guia buscar(Long id) {
        return guiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la guía " + id + "."));
    }

    private void validar(Guia guia, Long idActual) {
        if (guia == null) {
            throw new ReglaDeNegocioException("Faltan los datos de la guía.");
        }
        Validaciones.datosPersonales(guia.getNombre(), guia.getTelefono(), guia.getFechaIngreso());
        guiaRepository.findFirstByNombreIgnoreCase(guia.getNombre().trim())
                .filter(otro -> !otro.getId().equals(idActual))
                .ifPresent(otro -> {
                    throw new ReglaDeNegocioException("Ya hay una guía llamada " + otro.getNombre() + ".");
                });
    }
}
