package prog2.ecoparque.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.EnumSet;
import java.util.Set;

/**
 * Rol de usuario con los permisos que el administrador le otorga.
 * Los roles base (ADMINISTRADOR, GUIA, CUIDADOR e INVITADO) vienen cargados y no se pueden
 * borrar ni renombrar; el administrador puede crear todos los roles nuevos que quiera.
 */
@Entity
@Table(name = "rol")
public class Rol {

    public static final String ADMINISTRADOR = "ADMINISTRADOR";
    public static final String GUIA = "GUIA";
    public static final String CUIDADOR = "CUIDADOR";
    public static final String INVITADO = "INVITADO";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String nombre;
    private String descripcion;
    @Column(name = "es_base")
    private boolean base;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "rol_permiso", joinColumns = @JoinColumn(name = "rol_id"))
    @Column(name = "permiso")
    @Enumerated(EnumType.STRING)
    private Set<Permiso> permisos = EnumSet.noneOf(Permiso.class);

    public Rol() {
    }

    public Rol(String nombre, String descripcion, boolean base, Set<Permiso> permisos) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.base = base;
        this.permisos = permisos;
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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isBase() {
        return base;
    }

    public Set<Permiso> getPermisos() {
        return permisos;
    }

    public void setPermisos(Set<Permiso> permisos) {
        this.permisos = permisos;
    }
}
