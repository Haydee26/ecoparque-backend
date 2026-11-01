package prog2.ecoparque.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

/**
 * Rol de guia de un empleado. Los datos personales viven en Empleado;
 * desde aca solo se leen (nombre, direccion y telefono).
 */
@Entity
@Table(name = "guia")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Guia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "empleado_id", nullable = false, unique = true)
    @JsonIgnore
    private Empleado empleado;

    /** Solo para recibir el pedido de alta: id del empleado al que se le da el rol. */
    @Transient
    private Long empleadoIdPedido;

    public Guia() {
    }

    public Guia(Empleado empleado) {
        this.empleado = empleado;
    }

    public Long getId() {
        return id;
    }

    @JsonIgnore
    public Empleado getEmpleado() {
        return empleado;
    }

    @JsonProperty("empleadoId")
    public Long getEmpleadoId() {
        return empleado != null ? empleado.getId() : empleadoIdPedido;
    }

    @JsonProperty("empleadoId")
    public void setEmpleadoId(Long empleadoId) {
        this.empleadoIdPedido = empleadoId;
    }

    @JsonProperty(value = "nombre", access = JsonProperty.Access.READ_ONLY)
    public String getNombre() {
        return empleado == null ? null : empleado.nombreCompleto();
    }

    @JsonProperty(value = "direccion", access = JsonProperty.Access.READ_ONLY)
    public String getDireccion() {
        return empleado == null ? null : empleado.getDireccion();
    }

    @JsonProperty(value = "telefono", access = JsonProperty.Access.READ_ONLY)
    public String getTelefono() {
        return empleado == null ? null : empleado.getTelefono();
    }
}
