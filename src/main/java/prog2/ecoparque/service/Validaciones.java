package prog2.ecoparque.service;

import java.time.LocalDate;
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
            throw new ReglaDeNegocioException(esObligatorio(campo));
        }
        if (valor.isNaN() || valor.isInfinite() || valor <= 0) {
            throw new ReglaDeNegocioException(campo + " debe ser mayor que 0.");
        }
    }

    /** Si viene un valor, tiene que ser 0 o mayor (cero esta permitido). */
    static void enteroNoNegativo(Integer valor, String campo) {
        if (valor != null && valor < 0) {
            throw new ReglaDeNegocioException(campo + " debe ser 0 o mayor.");
        }
    }

    /** El dato tiene que estar presente. */
    static void obligatorio(Object valor, String mensaje) {
        if (valor == null) {
            throw new ReglaDeNegocioException(mensaje);
        }
    }

    /** El texto tiene que tener contenido. */
    static void textoObligatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new ReglaDeNegocioException(esObligatorio(campo));
        }
    }

    /** El valor tiene que existir y ser un entero mayor que cero. */
    static void enteroPositivo(Integer valor, String campo) {
        if (valor == null) {
            throw new ReglaDeNegocioException(esObligatorio(campo));
        }
        if (valor <= 0) {
            throw new ReglaDeNegocioException(campo + " debe ser mayor que 0.");
        }
    }

    /** La fecha de ingreso es obligatoria y no puede ser futura. */
    static void fechaDeIngreso(LocalDate fecha) {
        obligatorio(fecha, "La fecha de ingreso es obligatoria.");
        if (fecha.isAfter(LocalDate.now())) {
            throw new ReglaDeNegocioException("La fecha de ingreso no puede ser futura.");
        }
    }

    /** El teléfono es opcional; si viene, solo admite números, espacios, +, - y paréntesis. */
    static void telefonoOpcional(String telefono) {
        if (telefono != null && !telefono.isBlank() && !telefono.matches("[0-9+()\\- ]{6,20}")) {
            throw new ReglaDeNegocioException(
                    "El teléfono solo puede tener números, espacios, +, - y paréntesis (entre 6 y 20 caracteres).");
        }
    }

    /** Los datos que comparten guías y cuidadores. */
    static void datosPersonales(String nombre, String telefono, LocalDate fechaIngreso) {
        textoObligatorio(nombre, "El nombre");
        telefonoOpcional(telefono);
        fechaDeIngreso(fechaIngreso);
    }

    /** "La clave es obligatoria." / "El nombre es obligatorio." según el género del campo. */
    private static String esObligatorio(String campo) {
        return campo + (campo.startsWith("La ") ? " es obligatoria." : " es obligatorio.");
    }
}
