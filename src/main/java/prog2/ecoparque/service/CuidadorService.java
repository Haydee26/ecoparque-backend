package prog2.ecoparque.service;

import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prog2.ecoparque.model.Cuidador;
import prog2.ecoparque.repository.CuidadorRepository;

/**
 * Consulta de los empleados que tienen el rol de cuidador. El rol se da y se quita desde Empleados.
 */
@Service
public class CuidadorService {

    private final CuidadorRepository cuidadorRepository;

    public CuidadorService(CuidadorRepository cuidadorRepository) {
        this.cuidadorRepository = cuidadorRepository;
    }

    @Transactional(readOnly = true)
    public List<Cuidador> listar() {
        return cuidadorRepository.findAll().stream()
                .sorted(Comparator.comparing(Cuidador::getNombre, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }
}
