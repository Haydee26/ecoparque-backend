package prog2.ecoparque.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Datos generales del ecoparque. Hay un unico registro (id 1) que guarda
 * el area total disponible, en metros cuadrados.
 */
@Entity
@Table(name = "parque")
public class Parque {

    public static final Long ID_UNICO = 1L;

    @Id
    private Long id = ID_UNICO;
    private String nombre;
    private Double areaTotal;

    public Parque() {
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

    public Double getAreaTotal() {
        return areaTotal;
    }

    public void setAreaTotal(Double areaTotal) {
        this.areaTotal = areaTotal;
    }
}
