package prog2.ecoparque.exception;

/**
 * Se lanza cuando una operacion rompe una regla del ecoparque
 * (por ejemplo, superar el area total disponible).
 */
public class ReglaDeNegocioException extends RuntimeException {

    public ReglaDeNegocioException(String mensaje) {
        super(mensaje);
    }
}
