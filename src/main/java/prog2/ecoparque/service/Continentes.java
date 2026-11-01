package prog2.ecoparque.service;

import java.text.Normalizer;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import prog2.ecoparque.exception.ReglaDeNegocioException;

/**
 * Lista de continentes válidos para los hábitats. Acepta el nombre sin tildes o en minúsculas
 * ("africa", "AMERICA DEL SUR") y lo guarda siempre con su escritura oficial.
 */
final class Continentes {

    static final List<String> VALIDOS = List.of("África", "América del Norte", "América Central",
            "América del Sur", "Antártida", "Asia", "Europa", "Oceanía");

    private Continentes() {
    }

    /** Devuelve los continentes con su escritura oficial, sin repetidos; exige al menos uno. */
    static Set<String> normalizar(Collection<String> recibidos) {
        if (recibidos == null || recibidos.isEmpty()) {
            throw new ReglaDeNegocioException("Indicá al menos un continente.");
        }
        Set<String> resultado = new LinkedHashSet<>();
        for (String recibido : recibidos) {
            String buscado = sinTildes(recibido);
            String oficial = VALIDOS.stream()
                    .filter(valido -> sinTildes(valido).equals(buscado))
                    .findFirst()
                    .orElseThrow(() -> new ReglaDeNegocioException("\"" + (recibido == null ? "" : recibido.trim())
                            + "\" no es un continente válido. Los válidos son: " + String.join(", ", VALIDOS) + "."));
            resultado.add(oficial);
        }
        return resultado;
    }

    private static String sinTildes(String texto) {
        if (texto == null) {
            return "";
        }
        return Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }
}
