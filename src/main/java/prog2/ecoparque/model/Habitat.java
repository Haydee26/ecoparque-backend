package prog2.ecoparque.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "habitat")
public class Habitat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String clima;
    private String vegetacionPredominante;

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
}
