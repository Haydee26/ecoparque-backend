package prog2.ecoparque.service;

import prog2.ecoparque.exception.ReglaDeNegocioException;

/**
 * Controles simples de numeros, reutilizables por los servicios.
 */
final class Validaciones {

    private Validaciones() {
    }

    /** El valor tiene que existir y ser mayor que cero. */
    static void numeroPositivo(Double valor, String campo) {
        if (valor == null) {
            throw new ReglaDeNegocioException(campo + " es obligatorio.");
        }
        if (valor.isNaN() || valor.isInfinite() || valor <= 0) {
            throw new ReglaDeNegocioException(campo + " debe ser mayor que 0.");
        }
    }

    /** Si viene un valor, no puede ser negativo (cero esta permitido). */
    static void enteroNoNegativo(Integer valor, String campo) {
        if (valor != null && valor < 0) {
            throw new ReglaDeNegocioException(campo + " no puede ser negativo.");
        }
    }

    /** El texto tiene que tener contenido. */
    static void textoObligatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new ReglaDeNegocioException(campo + " es obligatorio.");
        }
    }
}
