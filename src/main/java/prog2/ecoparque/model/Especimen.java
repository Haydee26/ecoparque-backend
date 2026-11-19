package prog2.ecoparque.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;


/*CADA ANIMAL PERTENECE A UNA ESPECIE 1-n Y TIENE SU PROPIO ESTADO DE SALUD*/
@Entity //Le dice a JPA que esta clase se guarda en la base.
@Table(name = "especimen") //El nombre de la tabla en MySQL.
public class Especimen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //El id es la clave primaria y lo numera MySQL solo (AUTO_INCREMENT).
    private Long id;

    private String nombre;

    @ManyToOne //Muchos especímenes pertenecen a una especie: es la relación 1 a N vista desde el lado de “muchos”.
    @JoinColumn(name = "especie_id") //Crea la columna especie_id, que es la clave foránea hacia especie.
    private Especie especie; // "apunta" a la especie, no la copia

    private LocalDate fechaIngreso;

    @Enumerated(EnumType.STRING)
    /*Guarda el estado como texto (“ENFERMO”) y no como número (1). Así, si mañana 
    agregás un estado en el medio de la lista, los datos viejos no se rompen.*/
    private EstadoSalud estadoSalud = EstadoSalud.SANO; // Si no te dicen el estado, el animal entra sano.
    private String observacionSalud;
    private String avisadoPor;
    private LocalDate fechaAvisoSalud;

    public Especimen() {
    }

    public Especimen(String nombre, Especie especie, LocalDate fechaIngreso,
            EstadoSalud estadoSalud, String observacionSalud) {
        this.nombre = nombre;
        this.especie = especie;
        this.fechaIngreso = fechaIngreso;
        this.estadoSalud = estadoSalud;
        this.observacionSalud = observacionSalud;

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

    public Especie getEspecie() {
        return especie;
    }

    public void setEspecie(Especie especie) {
        this.especie = especie;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public EstadoSalud getEstadoSalud() {
        return estadoSalud;
    }

    public void setEstadoSalud(EstadoSalud estadoSalud) {
        this.estadoSalud = estadoSalud;
    }

    public String getObservacionSalud() {
        return observacionSalud;
    }

    public void setObservacionSalud(String observacionSalud) {
        this.observacionSalud = observacionSalud;
    }

    public String getAvisadoPor() {
        return avisadoPor;
    }

    public void setAvisadoPor(String avisadoPor) {
        this.avisadoPor = avisadoPor;
    }

    public LocalDate getFechaAvisoSalud() {
        return fechaAvisoSalud;
    }

    public void setFechaAvisoSalud(LocalDate fechaAvisoSalud) {
        this.fechaAvisoSalud = fechaAvisoSalud;
    }

}
