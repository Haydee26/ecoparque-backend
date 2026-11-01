package prog2.ecoparque.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "habitat")
public class Habitat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String clima;
    private String vegetacionPredominante;

    /** Continente o continentes donde se encuentra el hábitat (tabla habitat_continente). */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "habitat_continente", joinColumns = @JoinColumn(name = "habitat_id"))
    @Column(name = "continente", length = 50, nullable = false)
    private Set<String> continentes = new LinkedHashSet<>();

    public Habitat() {
    }

    public Habitat(String nombre, String clima, String vegetacionPredominante) {
        this.nombre = nombre;
        this.clima = clima;
        this.vegetacionPredominante = vegetacionPredominante;
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

    public String getClima() {
        return clima;
    }

    public void setClima(String clima) {
        this.clima = clima;
    }

    public String getVegetacionPredominante() {
        return vegetacionPredominante;
    }

    public void setVegetacionPredominante(String vegetacionPredominante) {
        this.vegetacionPredominante = vegetacionPredominante;
    }

    public Set<String> getContinentes() {
        return continentes;
    }

    public void setContinentes(Set<String> nuevos) {
        // Se copia primero: si llega la misma colección, vaciarla borraría los datos.
        Set<String> copia = nuevos == null ? new LinkedHashSet<>() : new LinkedHashSet<>(nuevos);
        this.continentes.clear();
        this.continentes.addAll(copia);
    }
}
