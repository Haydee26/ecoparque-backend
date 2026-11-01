package prog2.ecoparque.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * Un empleado tal como se lista al administrador: sus datos, los anios que lleva en el parque
 * y los puestos que tiene (Administrador, Guia, Cuidador).
 */
public record EmpleadoDetalle(
        Long id,
        String nombre,
        String apellido,
        String dni,
        LocalDate fechaNacimiento,
        String direccion,
        String telefono,
        String correo,
        LocalDate fechaIngreso,
        Long anios,
        List<String> puestos) {
}
