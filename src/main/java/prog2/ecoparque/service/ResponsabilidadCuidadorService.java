package prog2.ecoparque.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prog2.ecoparque.exception.RecursoNoEncontradoException;
import prog2.ecoparque.exception.ReglaDeNegocioException;
import prog2.ecoparque.model.Cuidador;
import prog2.ecoparque.model.Especie;
import prog2.ecoparque.model.ResponsabilidadCuidador;
import prog2.ecoparque.repository.CuidadorRepository;
import prog2.ecoparque.repository.EspecieRepository;
import prog2.ecoparque.repository.ResponsabilidadCuidadorRepository;

/**
 * Reglas de las responsabilidades: el cuidador y la especie tienen que existir,
 * la fecha "desde" es obligatoria y la fecha "hasta" (si se carga) no puede ser anterior.
 */
@Service
public class ResponsabilidadCuidadorService {

    private final ResponsabilidadCuidadorRepository responsabilidadRepository;
    private final CuidadorRepository cuidadorRepository;
    private final EspecieRepository especieRepository;

    public ResponsabilidadCuidadorService(ResponsabilidadCuidadorRepository responsabilidadRepository,
            CuidadorRepository cuidadorRepository, EspecieRepository especieRepository) {
        this.responsabilidadRepository = responsabilidadRepository;
        this.cuidadorRepository = cuidadorRepository;
        this.especieRepository = especieRepository;
    }

    @Transactional(readOnly = true)
    public List<ResponsabilidadCuidador> listar() {
        return responsabilidadRepository.findAll(Sort.by("id"));
    }

    @Transactional
    public ResponsabilidadCuidador crear(ResponsabilidadCuidador datos) {
        validar(datos);
        ResponsabilidadCuidador nueva = new ResponsabilidadCuidador();
        copiar(datos, nueva);
        return responsabilidadRepository.save(nueva);
    }

    @Transactional
    public ResponsabilidadCuidador actualizar(Long id, ResponsabilidadCuidador datos) {
        validar(datos);
        ResponsabilidadCuidador responsabilidad = buscar(id);
        copiar(datos, responsabilidad);
        return responsabilidadRepository.save(responsabilidad);
    }

    @Transactional
    public void eliminar(Long id) {
        buscar(id);
        responsabilidadRepository.deleteById(id);
        responsabilidadRepository.flush();
    }

    private ResponsabilidadCuidador buscar(Long id) {
        return responsabilidadRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la responsabilidad " + id + "."));
    }

    /** Pasa los datos validados, usando el cuidador y la especie reales de la base. */
    private void copiar(ResponsabilidadCuidador datos, ResponsabilidadCuidador destino) {
        Cuidador cuidador = cuidadorRepository.findById(datos.getCuidador().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe el cuidador " + datos.getCuidador().getId() + "."));
        Especie especie = especieRepository.findById(datos.getEspecie().getId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe la especie " + datos.getEspecie().getId() + "."));
        destino.setCuidador(cuidador);
        destino.setEspecie(especie);
        destino.setFechaDesde(datos.getFechaDesde());
        destino.setFechaHasta(datos.getFechaHasta());
    }

    private void validar(ResponsabilidadCuidador datos) {
        if (datos == null) {
            throw new ReglaDeNegocioException("Faltan los datos de la responsabilidad.");
        }
        if (datos.getCuidador() == null || datos.getCuidador().getId() == null) {
            throw new ReglaDeNegocioException("Tenés que indicar el cuidador.");
        }
        if (datos.getEspecie() == null || datos.getEspecie().getId() == null) {
            throw new ReglaDeNegocioException("Tenés que indicar la especie.");
        }
        Validaciones.obligatorio(datos.getFechaDesde(), "La fecha desde es obligatoria.");
        if (datos.getFechaHasta() != null && datos.getFechaHasta().isBefore(datos.getFechaDesde())) {
            throw new ReglaDeNegocioException("La fecha hasta no puede ser anterior a la fecha desde.");
        }
    }
}
