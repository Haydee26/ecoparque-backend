package prog2.ecoparque.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prog2.ecoparque.exception.ReglaDeNegocioException;
import prog2.ecoparque.model.Parque;
import prog2.ecoparque.repository.ParqueRepository;
import prog2.ecoparque.repository.ZonaRepository;

/**
 * Reglas del area del parque: el area total es finita y las zonas
 * no pueden ocupar mas de lo que hay.
 */
@Service
public class ParqueService {

    /** Foto del estado del area, calculada al momento de pedirla. */
    public record EstadoParque(String nombre, double areaTotal, double areaUsada, double areaLibre) {
    }

    /** Margen para comparar decimales sin errores de redondeo. */
    static final double TOLERANCIA = 1e-9;

    private final ParqueRepository parqueRepository;
    private final ZonaRepository zonaRepository;

    public ParqueService(ParqueRepository parqueRepository, ZonaRepository zonaRepository) {
        this.parqueRepository = parqueRepository;
        this.zonaRepository = zonaRepository;
    }

    @Transactional(readOnly = true)
    public EstadoParque estado() {
        Parque parque = parqueRepository.findById(Parque.ID_UNICO).orElseThrow(this::sinConfigurar);
        return armarEstado(parque);
    }

    @Transactional
    public EstadoParque configurar(String nombre, Double areaTotal) {
        Validaciones.numeroPositivo(areaTotal, "El área total");
        Parque parque = parqueRepository.buscarBloqueando(Parque.ID_UNICO).orElseGet(Parque::new);
        double usada = areaUsada();
        if (areaTotal + TOLERANCIA < usada) {
            throw new ReglaDeNegocioException(String.format(
                    "No se puede fijar un área total de %.2f m²: las zonas ya ocupan %.2f m².",
                    areaTotal, usada));
        }
        parque.setNombre(nombre == null || nombre.isBlank() ? "EcoParque" : nombre.trim());
        parque.setAreaTotal(areaTotal);
        return armarEstado(parqueRepository.save(parque));
    }

    /** Parque bloqueado para modificarlo; lo usa ZonaService dentro de su transaccion. */
    Parque obtenerBloqueado() {
        return parqueRepository.buscarBloqueando(Parque.ID_UNICO).orElseThrow(this::sinConfigurar);
    }

    double areaUsada() {
        Double suma = zonaRepository.sumaExtensiones();
        return suma == null ? 0 : suma;
    }

    private EstadoParque armarEstado(Parque parque) {
        double usada = areaUsada();
        return new EstadoParque(parque.getNombre(), parque.getAreaTotal(), usada, parque.getAreaTotal() - usada);
    }

    private ReglaDeNegocioException sinConfigurar() {
        return new ReglaDeNegocioException(
                "Todavía no se configuró el área total del parque. Configúrela primero.");
    }
}
