package prog2.ecoparque.dto;

/** Lo que llega al crear o modificar un usuario. El rol viaja por su nombre (por ejemplo "GUIA"). */
public record UsuarioDatos(String nombreUsuario, String clave, String rol, String nombrePersona) {
}
