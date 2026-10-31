package prog2.ecoparque.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prog2.ecoparque.repository.ResumenRepository;
import prog2.ecoparque.service.ParqueService.EstadoParque;

/**
 * Arma el resumen del parque. Todos los numeros se calculan en el momento
 * a partir de los datos reales; no se guardan en ningun lado, asi que
 * nunca quedan desactualizados.
 */
@Service
public class ResumenService {

    /** Ocupacion del area a partir de la cual se avisa. */
    private static final double UMBRAL_OCUPACION = 90.0;

    /** Resumen completo. Si falta configurar el area, esos datos van en null. */
    public record Resumen(
            long animalesTotal,
            long especiesTotal,
            long personalTotal,
            long guias,
            long cuidadores,
            Double areaTotal,
            double areaUsada,
            Double areaLibre,
            Double ocupacionPorcentaje,
            List<String> alertas) {
    }

    private final ResumenRepository resumenRepository;
    private final ParqueService parqueService;

    public ResumenService(ResumenRepository resumenRepository, ParqueService parqueService) {
        this.resumenRepository = resumenRepository;
        this.parqueService = parqueService;
    }

    @Transactional(readOnly = true)
    public Resumen resumen() {
        long guias = valor(resumenRepository.totalGuias());
        long cuidadores = valor(resumenRepository.totalCuidadores());
        List<String> alertas = new ArrayList<>();

        Double areaTotal = null;
        Double areaLibre = null;
        Double ocupacion = null;
        double areaUsada = parqueService.areaUsada();

        EstadoParque parque = parqueService.estadoSiExiste().orElse(null);
        if (parque == null) {
            alertas.add("Falta configurar el área total del parque.");
        } else {
            areaTotal = parque.areaTotal();
            areaUsada = parque.areaUsada();
            areaLibre = parque.areaLibre();
            ocupacion = Math.round(areaUsada / areaTotal * 1000.0) / 10.0;
            if (ocupacion >= UMBRAL_OCUPACION) {
                alertas.add("El parque usa el " + ocupacion + "% de su área total.");
            }
        }

        for (String especie : resumenRepository.especiesSinCuidador()) {
            alertas.add("La especie " + especie + " no tiene cuidador asignado.");
        }
        for (String codigo : resumenRepository.itinerariosSinGuia()) {
            alertas.add("El itinerario " + codigo + " no tiene guía asignado.");
        }

        return new Resumen(
                valor(resumenRepository.totalEjemplares()),
                valor(resumenRepository.totalEspecies()),
                guias + cuidadores,
                guias,
                cuidadores,
                areaTotal,
                areaUsada,
                areaLibre,
                ocupacion,
                alertas);
    }

    private long valor(Long numero) {
        return numero == null ? 0 : numero;
    }
}
