package prog2.ecoparque.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prog2.ecoparque.exception.RecursoNoEncontradoException;
import prog2.ecoparque.exception.ReglaDeNegocioException;
import prog2.ecoparque.model.Parque;
import prog2.ecoparque.model.Zona;
import prog2.ecoparque.repository.ZonaRepository;

/**
 * Reglas de las zonas: nombre y extension validos, y que entre todas
 * no superen el area total del parque.
 */
@Service
public class ZonaService {

    private final ZonaRepository zonaRepository;
    private final ParqueService parqueService;

    public ZonaService(ZonaRepository zonaRepository, ParqueService parqueService) {
        this.zonaRepository = zonaRepository;
        this.parqueService = parqueService;
    }

    @Transactional(readOnly = true)
    public List<Zona> listar() {
        return zonaRepository.findAll(Sort.by("id"));
    }

    @Transactional
    public Zona crear(Zona datos) {
        validar(datos);
        Parque parque = parqueService.obtenerBloqueado();
        controlarArea(parque, parqueService.areaUsada() + datos.getExtension());
        // Se arma una zona nueva para que nadie pueda pisar una existente mandando un id.
        return zonaRepository.save(new Zona(datos.getNombre().trim(), datos.getExtension()));
    }

    @Transactional
    public Zona actualizar(Long id, Zona datos) {
        validar(datos);
        Parque parque = parqueService.obtenerBloqueado();
        Zona zona = buscar(id);
        double anterior = zona.getExtension() == null ? 0 : zona.getExtension();
        controlarArea(parque, parqueService.areaUsada() - anterior + datos.getExtension());
        zona.setNombre(datos.getNombre().trim());
        zona.setExtension(datos.getExtension());
        return zonaRepository.save(zona);
    }

    @Transactional
    public void eliminar(Long id) {
        buscar(id);
        zonaRepository.deleteById(id);
        // flush: si la zona esta en uso, el error aparece aca y se informa con claridad.
        zonaRepository.flush();
    }

    private Zona buscar(Long id) {
        return zonaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la zona " + id + "."));
    }

    private void validar(Zona zona) {
        if (zona == null) {
            throw new ReglaDeNegocioException("Faltan los datos de la zona.");
        }
        Validaciones.textoObligatorio(zona.getNombre(), "El nombre de la zona");
        Validaciones.numeroPositivo(zona.getExtension(), "La extensión de la zona");
    }

    private void controlarArea(Parque parque, double usadaResultante) {
        double total = parque.getAreaTotal();
        if (usadaResultante > total + ParqueService.TOLERANCIA) {
            throw new ReglaDeNegocioException(String.format(
                    "La zona supera el área disponible del parque: quedarían %.2f m² usados de %.2f m² totales.",
                    usadaResultante, total));
        }
    }
}
