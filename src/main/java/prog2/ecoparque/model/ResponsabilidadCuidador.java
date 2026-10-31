package prog2.ecoparque.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "responsabilidad_cuidador")
public class ResponsabilidadCuidador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "cuidador_id")
    private Cuidador cuidador;
    @ManyToOne
    @JoinColumn(name = "especie_id")
    private Especie especie;
    private LocalDate fechaDesde;
    /** Fecha en que termina la responsabilidad. Vacia significa que sigue vigente. */
    private LocalDate fechaHasta;

    public ResponsabilidadCuidador() {
    }

    public ResponsabilidadCuidador(Cuidador cuidador, Especie especie, LocalDate fechaDesde) {
        this.cuidador = cuidador;
        this.especie = especie;
        this.fechaDesde = fechaDesde;
    }

    public Long getId() {
        return id;
    }

    public Cuidador getCuidador() {
        return cuidador;
    }

    public void setCuidador(Cuidador cuidador) {
        this.cuidador = cuidador;
    }

    public Especie getEspecie() {
        return especie;
    }

    public void setEspecie(Especie especie) {
        this.especie = especie;
    }

    public LocalDate getFechaDesde() {
        return fechaDesde;
    }

    public void setFechaDesde(LocalDate fechaDesde) {
        this.fechaDesde = fechaDesde;
    }

    public LocalDate getFechaHasta() {
        return fechaHasta;
    }

    public void setFechaHasta(LocalDate fechaHasta) {
        this.fechaHasta = fechaHasta;
    }
}
