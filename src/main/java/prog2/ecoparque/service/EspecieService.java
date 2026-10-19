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

/**
 * Reglas de las especies: nombre obligatorio y cantidad de ejemplares
 * que nunca puede ser negativa.
 */
@Service
public class EspecieService {

    private final EspecieRepository especieRepository;

    public EspecieService(EspecieRepository especieRepository) {
        this.especieRepository = especieRepository;
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
        Especie nueva = new Especie(datos.getNombreEspanol().trim(), datos.getNombreCientifico(),
                datos.getDescripcion(), datos.getZona());
        nueva.setCantidadEjemplares(datos.getCantidadEjemplares() == null ? 0 : datos.getCantidadEjemplares());
        nueva.setHabitats(datos.getHabitats() == null ? new HashSet<>() : datos.getHabitats());
        return especieRepository.save(nueva);
    }

    @Transactional
    public Especie actualizar(Long id, Especie datos) {
        validar(datos);
        Especie especie = buscar(id);
        especie.setNombreEspanol(datos.getNombreEspanol().trim());
        especie.setNombreCientifico(datos.getNombreCientifico());
        especie.setDescripcion(datos.getDescripcion());
        especie.setZona(datos.getZona());
        especie.setHabitats(datos.getHabitats() == null ? new HashSet<>() : datos.getHabitats());
        // Si el pedido no trae la cantidad, se conserva la que ya tenia (no se borra).
        if (datos.getCantidadEjemplares() != null) {
            especie.setCantidadEjemplares(datos.getCantidadEjemplares());
        }
        return especieRepository.save(especie);
    }

    @Transactional
    public void eliminar(Long id) {
        buscar(id);
        especieRepository.deleteById(id);
        // flush: si la especie esta en uso, el error aparece aca y se informa con claridad.
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
        Validaciones.textoObligatorio(especie.getNombreEspanol(), "El nombre de la especie");
        Validaciones.enteroNoNegativo(especie.getCantidadEjemplares(), "La cantidad de ejemplares");
    }
}
