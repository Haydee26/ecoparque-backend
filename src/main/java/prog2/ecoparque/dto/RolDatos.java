package prog2.ecoparque.dto;

import java.util.List;

/** Lo que manda el administrador para crear o modificar un rol. */
public record RolDatos(String nombre, String descripcion, List<String> permisos) {
}
