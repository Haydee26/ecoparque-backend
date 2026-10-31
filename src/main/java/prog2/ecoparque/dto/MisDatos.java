package prog2.ecoparque.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * Datos de la persona que inicio sesion. Solo se entregan a ella misma.
 * Si la persona no es guia ni cuidador (por ejemplo, el administrador),
 * los datos personales quedan vacios.
 */
public record MisDatos(
        String nombreUsuario,
        String rol,
        String nombre,
        String direccion,
        String telefono,
        LocalDate fechaIngreso,
        List<String> itinerarios,
        List<String> especies) {
}
