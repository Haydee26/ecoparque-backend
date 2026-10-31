package prog2.ecoparque.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prog2.ecoparque.exception.RecursoNoEncontradoException;
import prog2.ecoparque.exception.ReglaDeNegocioException;
import prog2.ecoparque.model.Habitat;
import prog2.ecoparque.repository.HabitatRepository;

/**
 * Reglas de los hábitats: nombre, clima y vegetación obligatorios, y no se repite el nombre.
 */
@Service
public class HabitatService {

    private final HabitatRepository habitatRepository;

    public HabitatService(HabitatRepository habitatRepository) {
        this.habitatRepository = habitatRepository;
    }

    @Transactional(readOnly = true)
    public List<Habitat> listar() {
        return habitatRepository.findAll(Sort.by("id"));
    }

    @Transactional
    public Habitat crear(Habitat datos) {
        validar(datos, null);
        // Se arma un hábitat nuevo para que nadie pueda pisar uno existente mandando un id.
        return habitatRepository.save(new Habitat(datos.getNombre().trim(), datos.getClima().trim(),
                datos.getVegetacionPredominante().trim()));
    }

    @Transactional
    public Habitat actualizar(Long id, Habitat datos) {
        validar(datos, id);
        Habitat habitat = buscar(id);
        habitat.setNombre(datos.getNombre().trim());
        habitat.setClima(datos.getClima().trim());
        habitat.setVegetacionPredominante(datos.getVegetacionPredominante().trim());
        return habitatRepository.save(habitat);
    }

    @Transactional
    public void eliminar(Long id) {
        buscar(id);
        habitatRepository.deleteById(id);
        // flush: si el hábitat está en uso, el error aparece acá y se informa con claridad.
        habitatRepository.flush();
    }

    private Habitat buscar(Long id) {
        return habitatRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el hábitat " + id + "."));
    }

    private void validar(Habitat habitat, Long idActual) {
        if (habitat == null) {
            throw new ReglaDeNegocioException("Faltan los datos del hábitat.");
        }
        Validaciones.textoObligatorio(habitat.getNombre(), "El nombre del hábitat");
        Validaciones.textoObligatorio(habitat.getClima(), "El clima");
        Validaciones.textoObligatorio(habitat.getVegetacionPredominante(), "La vegetación predominante");
        habitatRepository.findFirstByNombreIgnoreCase(habitat.getNombre().trim())
                .filter(otro -> !otro.getId().equals(idActual))
                .ifPresent(otro -> {
                    throw new ReglaDeNegocioException("Ya existe un hábitat llamado " + otro.getNombre() + ".");
                });
    }
}
