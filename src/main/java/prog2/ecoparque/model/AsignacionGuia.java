package prog2.ecoparque.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalTime;

@Entity
@Table(name = "asignacion_guia")
public class AsignacionGuia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /** Al listar asignaciones solo viajan el id y el nombre de la guia; sus datos personales no. */
    @JsonIgnoreProperties({"direccion", "telefono", "fechaIngreso"})
    @ManyToOne
    @JoinColumn(name = "guia_id")
    private Guia guia;
    @ManyToOne
    @JoinColumn(name = "itinerario_id")
    private Itinerario itinerario;
    /** Hora de inicio ("desde"). Se llama "hora" para no cambiar la columna ni lo que ya envian las pantallas. */
    private LocalTime hora;
    /** Hora de fin ("hasta"). */
    private LocalTime horaHasta;

    public AsignacionGuia() {
    }

    public AsignacionGuia(Guia guia, Itinerario itinerario, LocalTime hora) {
        this.guia = guia;
        this.itinerario = itinerario;
        this.hora = hora;
    }

    public Long getId() {
        return id;
    }

    public Guia getGuia() {
        return guia;
    }

    public void setGuia(Guia guia) {
        this.guia = guia;
    }

    public Itinerario getItinerario() {
        return itinerario;
    }

    public void setItinerario(Itinerario itinerario) {
        this.itinerario = itinerario;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public LocalTime getHoraHasta() {
        return horaHasta;
    }

    public void setHoraHasta(LocalTime horaHasta) {
        this.horaHasta = horaHasta;
    }
}
