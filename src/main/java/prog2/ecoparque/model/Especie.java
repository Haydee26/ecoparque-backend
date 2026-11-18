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
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Formula;

/**
 * La especie es solo la descripcion: nombre cientifico, descripcion, zona y
 * habitats. Los animales concretos son los especimenes, y la salud es de cada
 * uno de ellos.
 */
@Entity
@Table(name = "especie")
public class Especie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombreCientifico;
    private String descripcion;

    /**
     * Cuantos especimenes tiene la especie. No es una columna: se calcula
     * contando en la tabla especimen cada vez que se lee la especie.
     */
    @Formula("(select count(*) from especimen e where e.especie_id = id)")
    private Integer cantidadEjemplares;

    @ManyToOne
    @JoinColumn(name = "zona_id")
    private Zona zona;
    @ManyToMany
    @JoinTable(name = "especie_habitat", joinColumns = @JoinColumn(name = "especie_id"), inverseJoinColumns = @JoinColumn(name = "habitat_id"))
    private Set<Habitat> habitats = new HashSet<>();

    public Especie() {
    }

    public Especie(String nombreCientifico, String descripcion, Zona zona) {
        this.nombreCientifico = nombreCientifico;
        this.descripcion = descripcion;
        this.zona = zona;
    }

    public Long getId() {
        return id;
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

    /**
     * Solo lectura: no hay setter porque la cantidad no se escribe, se cuenta!!
     */
    public Integer getCantidadEjemplares() {
        return cantidadEjemplares;
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
