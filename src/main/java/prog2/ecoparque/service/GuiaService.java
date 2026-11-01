package prog2.ecoparque.service;

import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prog2.ecoparque.model.Guia;
import prog2.ecoparque.repository.GuiaRepository;

/**
 * Consulta de los empleados que tienen el rol de guia. El rol se da y se quita desde Empleados.
 */
@Service
public class GuiaService {

    private final GuiaRepository guiaRepository;

    public GuiaService(GuiaRepository guiaRepository) {
        this.guiaRepository = guiaRepository;
    }

    @Transactional(readOnly = true)
    public List<Guia> listar() {
        return guiaRepository.findAll().stream()
                .sorted(Comparator.comparing(Guia::getNombre, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }
}
