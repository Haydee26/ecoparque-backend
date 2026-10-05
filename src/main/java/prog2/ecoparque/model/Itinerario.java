package prog2.ecoparque.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "itinerario")
public class Itinerario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String codigo;
    private Integer duracionMinutos;
    private Double longitudMetros;
    private Integer maximoVisitantes;
    private Integer cantidadEspecies;
    @ManyToMany
    @JoinTable(name = "itinerario_zona", joinColumns = @JoinColumn(name = "itinerario_id"), inverseJoinColumns = @JoinColumn(name = "zona_id"))
    private Set<Zona> zonas = new HashSet<>();

    public Itinerario() {
    }

    public Itinerario(String codigo, Integer duracionMinutos, Double longitudMetros, Integer maximoVisitantes, Integer cantidadEspecies) {
        this.codigo = codigo;
        this.duracionMinutos = duracionMinutos;
        this.longitudMetros = longitudMetros;
        this.maximoVisitantes = maximoVisitantes;
        this.cantidadEspecies = cantidadEspecies;
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(Integer duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public Double getLongitudMetros() {
        return longitudMetros;
    }

    public void setLongitudMetros(Double longitudMetros) {
        this.longitudMetros = longitudMetros;
    }

    public Integer getMaximoVisitantes() {
        return maximoVisitantes;
    }

    public void setMaximoVisitantes(Integer maximoVisitantes) {
        this.maximoVisitantes = maximoVisitantes;
    }

    public Integer getCantidadEspecies() {
        return cantidadEspecies;
    }

    public void setCantidadEspecies(Integer cantidadEspecies) {
        this.cantidadEspecies = cantidadEspecies;
    }

    public Set<Zona> getZonas() {
        return zonas;
    }

    public void setZonas(Set<Zona> zonas) {
        this.zonas = zonas;
    }
}
