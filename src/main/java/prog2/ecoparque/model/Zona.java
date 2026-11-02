package prog2.ecoparque.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Zona del parque. Ademas de su extension (m2) guarda donde esta en el mapa:
 * el mapa es una cuadricula y la zona ocupa un rectangulo de casilleros
 * (columna y fila de la esquina superior izquierda, mas ancho y alto en casilleros).
 * Los cuatro datos de ubicacion son opcionales: una zona sin ubicar no aparece dibujada.
 */
@Entity
@Table(name = "zona")

public class Zona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private Double extension;
    private Integer columna;
    private Integer fila;
    private Integer ancho;
    private Integer alto;

    public Zona() {
    }

    public Zona(String nombre, Double extension) {
        this.nombre = nombre;
        this.extension = extension;
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

    public Double getExtension() {
        return extension;
    }

    public void setExtension(Double extension) {
        this.extension = extension;
    }

    public Integer getColumna() {
        return columna;
    }

    public void setColumna(Integer columna) {
        this.columna = columna;
    }

    public Integer getFila() {
        return fila;
    }

    public void setFila(Integer fila) {
        this.fila = fila;
    }

    public Integer getAncho() {
        return ancho;
    }

    public void setAncho(Integer ancho) {
        this.ancho = ancho;
    }

    public Integer getAlto() {
        return alto;
    }

    public void setAlto(Integer alto) {
        this.alto = alto;
    }

    /** True si la zona ya tiene su lugar en el mapa. */
    public boolean ubicada() {
        return columna != null && fila != null && ancho != null && alto != null;
    }

    public void ubicar(Integer columna, Integer fila, Integer ancho, Integer alto) {
        this.columna = columna;
        this.fila = fila;
        this.ancho = ancho;
        this.alto = alto;
    }
}
