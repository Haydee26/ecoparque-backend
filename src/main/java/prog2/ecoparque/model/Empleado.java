package prog2.ecoparque.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Una persona que trabaja en el parque. Guia y Cuidador son roles que se le dan a un empleado;
 * el administrador tambien es un empleado.
 */
@Entity
@Table(name = "empleado")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String apellido;
    @Column(unique = true)
    private String dni;
    private LocalDate fechaNacimiento;
    @Column(nullable = false)
    private String direccion;
    @Column(nullable = false)
    private String telefono;
    private String correo;
    private LocalDate fechaIngreso;

    /**
     * Solo para el alta y la modificacion: roles que tiene que tener el empleado ("GUIA", "CUIDADOR").
     * No se guarda aca; el servicio crea o quita las filas de guia y cuidador.
     * Si no viene (null), los roles no se tocan.
     */
    @Transient
    private List<String> roles;

    public Empleado() {
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles == null ? null : new ArrayList<>(roles);
    }

    /** "Nombre Apellido": asi se vincula con la persona del usuario y se muestra en pantalla. */
    public String nombreCompleto() {
        return (nombre == null ? "" : nombre.trim()) + " " + (apellido == null ? "" : apellido.trim());
    }
}
