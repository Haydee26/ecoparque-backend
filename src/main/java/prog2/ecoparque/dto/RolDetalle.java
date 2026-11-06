package prog2.ecoparque.dto;

import java.util.List;

/** Un rol con sus permisos y cuantos usuarios lo tienen. */
public record RolDetalle(Long id, String nombre, String descripcion, boolean base, List<String> permisos,
        long usuarios) {
}
