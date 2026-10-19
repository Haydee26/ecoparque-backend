package prog2.ecoparque.exception;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduce las excepciones del backend a respuestas claras para quien consulta la API.
 * Asi, cualquier cliente (la pagina, Postman) recibe siempre un estado HTTP correcto
 * y un mensaje entendible, en lugar de un error generico.
 */
@RestControllerAdvice
public class ManejadorDeErrores {

    /** Forma comun de todas las respuestas de error. */
    public record RespuestaError(int estado, String mensaje) {
    }

    @ExceptionHandler({RecursoNoEncontradoException.class, NoSuchElementException.class})
    public ResponseEntity<RespuestaError> noEncontrado(RuntimeException e) {
        String mensaje = e instanceof RecursoNoEncontradoException
                ? e.getMessage()
                : "No se encontró el registro pedido.";
        return responder(HttpStatus.NOT_FOUND, mensaje);
    }

    @ExceptionHandler(ReglaDeNegocioException.class)
    public ResponseEntity<RespuestaError> reglaDeNegocio(ReglaDeNegocioException e) {
        return responder(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<RespuestaError> integridad(DataIntegrityViolationException e) {
        return responder(HttpStatus.CONFLICT,
                "No se pudo completar la operación: el registro está en uso o repite uno existente.");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaError> datosInvalidos(MethodArgumentNotValidException e) {
        String mensaje = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.joining(" "));
        return responder(HttpStatus.BAD_REQUEST, mensaje);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RespuestaError> cuerpoIlegible(HttpMessageNotReadableException e) {
        return responder(HttpStatus.BAD_REQUEST, "El formato de los datos enviados no es válido.");
    }

    private ResponseEntity<RespuestaError> responder(HttpStatus estado, String mensaje) {
        return ResponseEntity.status(estado).body(new RespuestaError(estado.value(), mensaje));
    }
}
