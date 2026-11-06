package prog2.ecoparque.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Usuario del sistema. En la base apunta a su rol (tabla rol); hacia afuera (JSON) el rol
 * sigue siendo simplemente su nombre, por ejemplo "GUIA", asi las pantallas no cambian.
 */
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombreUsuario;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String clave;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id")
    @JsonIgnore
    private Rol rolAsignado;
    private String nombrePersona;

    public Usuario() {
    }

    public Usuario(String nombreUsuario, String clave, Rol rol, String nombrePersona) {
        this.nombreUsuario = nombreUsuario;
        this.clave = clave;
        this.rolAsignado = rol;
        this.nombrePersona = nombrePersona;
    }

    public Long getId() {
        return id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    @JsonIgnore
    public Rol getRolAsignado() {
        return rolAsignado;
    }

    public void setRolAsignado(Rol rolAsignado) {
        this.rolAsignado = rolAsignado;
    }

    /** Nombre del rol: asi lo ven las pantallas (el JSON de salida lo muestra como "rol"). */
    @JsonProperty("rol")
    public String getRol() {
        return rolAsignado != null ? rolAsignado.getNombre() : null;
    }

    public String getNombrePersona() {
        return nombrePersona;
    }

    public void setNombrePersona(String nombrePersona) {
        this.nombrePersona = nombrePersona;
    }
}
