package prog2.ecoparque.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "especie")
public class Especie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombreEspanol;
    private String nombreCientifico;
    private String descripcion;
    @ManyToOne
    @JoinColumn(name = "zona_id")
    private Zona zona;
    @ManyToMany
    @JoinTable(name = "especie_habitat", joinColumns = @JoinColumn(name = "especie_id"), inverseJoinColumns = @JoinColumn(name = "habitat_id"))
    private Set<Habitat> habitats = new HashSet<>();

    public Especie() {
    }

    public Especie(String nombreEspanol, String nombreCientifico, String descripcion, Zona zona) {
        this.nombreEspanol = nombreEspanol;
        this.nombreCientifico = nombreCientifico;
        this.descripcion = descripcion;
        this.zona = zona;
    }

    public Long getId() {
        return id;
    }

    public String getNombreEspanol() {
        return nombreEspanol;
    }

    public void setNombreEspanol(String nombreEspanol) {
        this.nombreEspanol = nombreEspanol;
    }

    public String getNombreCientifico() {
        return nombreCientifico;
    }

    public void setNombreCientifico(String nombreCientifico) {
        this.nombreCientifico = nombreCientifico;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Zona getZona() {
        return zona;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
    }

    public Set<Habitat> getHabitats() {
        return habitats;
    }

    public void setHabitats(Set<Habitat> habitats) {
        this.habitats = habitats;
    }
}
