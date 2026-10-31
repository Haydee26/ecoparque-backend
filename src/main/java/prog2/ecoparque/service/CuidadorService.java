package prog2.ecoparque.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prog2.ecoparque.exception.RecursoNoEncontradoException;
import prog2.ecoparque.exception.ReglaDeNegocioException;
import prog2.ecoparque.model.Cuidador;
import prog2.ecoparque.repository.CuidadorRepository;

/**
 * Reglas de los datos personales de cuidador: nombre obligatorio y sin repetir,
 * teléfono con formato válido y fecha de ingreso obligatoria que no sea futura.
 */
@Service
public class CuidadorService {

    private final CuidadorRepository cuidadorRepository;

    public CuidadorService(CuidadorRepository cuidadorRepository) {
        this.cuidadorRepository = cuidadorRepository;
    }

    @Transactional(readOnly = true)
    public List<Cuidador> listar() {
        return cuidadorRepository.findAll(Sort.by("id"));
    }

    @Transactional
    public Cuidador crear(Cuidador datos) {
        validar(datos, null);
        // Se arma un registro nuevo para que nadie pueda pisar uno existente mandando un id.
        return cuidadorRepository.save(new Cuidador(datos.getNombre().trim(), datos.getDireccion(),
                datos.getTelefono(), datos.getFechaIngreso()));
    }

    @Transactional
    public Cuidador actualizar(Long id, Cuidador datos) {
        validar(datos, id);
        Cuidador cuidador = buscar(id);
        cuidador.setNombre(datos.getNombre().trim());
        cuidador.setDireccion(datos.getDireccion());
        cuidador.setTelefono(datos.getTelefono());
        cuidador.setFechaIngreso(datos.getFechaIngreso());
        return cuidadorRepository.save(cuidador);
    }

    @Transactional
    public void eliminar(Long id) {
        buscar(id);
        cuidadorRepository.deleteById(id);
        // flush: si tiene asignaciones, el error aparece acá y se informa con claridad.
        cuidadorRepository.flush();
    }

    private Cuidador buscar(Long id) {
        return cuidadorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el cuidador " + id + "."));
    }

    private void validar(Cuidador cuidador, Long idActual) {
        if (cuidador == null) {
            throw new ReglaDeNegocioException("Faltan los datos del cuidador.");
        }
        Validaciones.datosPersonales(cuidador.getNombre(), cuidador.getTelefono(), cuidador.getFechaIngreso());
        cuidadorRepository.findFirstByNombreIgnoreCase(cuidador.getNombre().trim())
                .filter(otro -> !otro.getId().equals(idActual))
                .ifPresent(otro -> {
                    throw new ReglaDeNegocioException("Ya hay un cuidador llamado " + otro.getNombre() + ".");
                });
    }
}
