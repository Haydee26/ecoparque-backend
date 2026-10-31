package prog2.ecoparque.dto;

import java.util.List;

/**
 * Lo unico que se muestra de una guia a quien no es administrador:
 * su nombre y de que es guia. Nunca incluye datos personales.
 */
public record GuiaPublica(Long id, String nombre, List<String> itinerarios, List<String> zonas) {
}
