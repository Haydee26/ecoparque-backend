package prog2.ecoparque.dto;

import java.time.LocalDate;

/**
 * Una persona del personal (guia o cuidador) con su fecha de ingreso
 * y los anios completos que lleva en el parque.
 */
public record EmpleadoAntiguedad(Long id, String nombre, String tipo, LocalDate fechaIngreso, Long anios) {
}
