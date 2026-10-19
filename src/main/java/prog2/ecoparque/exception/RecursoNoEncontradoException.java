package prog2.ecoparque.exception;

/**
 * Se lanza cuando se pide un registro que no existe (por ejemplo, un id inexistente).
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
