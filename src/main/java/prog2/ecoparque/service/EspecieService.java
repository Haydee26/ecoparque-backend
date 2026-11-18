package prog2.ecoparque.service;

import java.util.HashSet;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prog2.ecoparque.exception.RecursoNoEncontradoException;
import prog2.ecoparque.exception.ReglaDeNegocioException;
import prog2.ecoparque.model.Especie;
import prog2.ecoparque.repository.EspecieRepository;
import prog2.ecoparque.repository.EspecimenRepository;

/**
 * Reglas de las especies: el nombre cientifico es obligatorio (identifica a la especie)
 * y no se puede borrar una especie que todavia tiene especimenes.
 */
@Service
public class EspecieService {
    
    private final EspecieRepository especieRepository;
    private final EspecimenRepository especimenRepository;

    public EspecieService(EspecieRepository especieRepository, EspecimenRepository especimenRepository) {
        this.especieRepository = especieRepository;
        this.especimenRepository = especimenRepository;
    }
    
    

    @Transactional(readOnly = true)
    public List<Especie> listar() {
        return especieRepository.findAll(Sort.by("id"));
    }

    @Transactional(readOnly = true)
    public long contar() {
        return especieRepository.count();
    }

     @Transactional
    public Especie crear(Especie datos) {
        validar(datos);
        // Se arma una especie nueva para que nadie pueda pisar una existente mandando un id.
        Especie nueva = new Especie();
        nueva.setNombreCientifico(datos.getNombreCientifico().trim());
        nueva.setDescripcion(datos.getDescripcion());
        nueva.setZona(datos.getZona());
        nueva.setHabitats(datos.getHabitats() == null ? new HashSet<>() : datos.getHabitats());
        return especieRepository.save(nueva);
    }

      @Transactional
    public Especie actualizar(Long id, Especie datos) {
        validar(datos);
        Especie especie = buscar(id);
        especie.setNombreCientifico(datos.getNombreCientifico().trim());
        especie.setDescripcion(datos.getDescripcion());
        especie.setZona(datos.getZona());
        especie.setHabitats(datos.getHabitats() == null ? new HashSet<>() : datos.getHabitats());
        return especieRepository.save(especie);
    }

   @Transactional
    public void eliminar(Long id) {
        buscar(id);
        // Una especie con animales no se borra: quedarian especimenes sin especie.
        long cantidad = especimenRepository.countByEspecieId(id);
        if (cantidad > 0) {
            throw new ReglaDeNegocioException("La especie tiene " + cantidad
                    + " especimenes: primero hay que darlos de baja o pasarlos a otra especie.");
        }
        especieRepository.deleteById(id);
        // flush: si la especie esta en uso (por ejemplo, en una responsabilidad), el error aparece aca.
        especieRepository.flush();
    }

    
     private Especie buscar(Long id) {
        return especieRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la especie " + id + "."));
    }

   private void validar(Especie especie) {
        if (especie == null) {
            throw new ReglaDeNegocioException("Faltan los datos de la especie.");
        }
        Validaciones.textoObligatorio(especie.getNombreCientifico(), "El nombre cientifico de la especie");
    }
}
