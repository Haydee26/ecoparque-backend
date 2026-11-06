package prog2.ecoparque.model;

/**
 * Catalogo de permisos del sistema. Cada seccion tiene dos: VER (consultar) y EDITAR
 * (alta, baja y modificacion). El administrador arma cada rol tildando los que quiera.
 * Los permisos son un catalogo fijo porque el codigo los controla: un permiso solo
 * tiene sentido si alguna parte del sistema lo pregunta.
 */
public enum Permiso {
    ZONAS_VER("Zonas", "Ver"),
    ZONAS_EDITAR("Zonas", "Editar"),
    HABITATS_VER("Hábitats", "Ver"),
    HABITATS_EDITAR("Hábitats", "Editar"),
    ESPECIES_VER("Especies", "Ver"),
    ESPECIES_EDITAR("Especies", "Editar"),
    ITINERARIOS_VER("Itinerarios", "Ver"),
    ITINERARIOS_EDITAR("Itinerarios", "Editar"),
    ASIGNACIONES_VER("Asignaciones de guías", "Ver"),
    ASIGNACIONES_EDITAR("Asignaciones de guías", "Editar"),
    RESPONSABILIDADES_VER("Responsabilidades de cuidadores", "Ver"),
    RESPONSABILIDADES_EDITAR("Responsabilidades de cuidadores", "Editar"),
    EMPLEADOS_VER("Empleados", "Ver"),
    EMPLEADOS_EDITAR("Empleados", "Editar"),
    GUIAS_VER("Guías", "Ver"),
    GUIAS_EDITAR("Guías", "Editar"),
    CUIDADORES_VER("Cuidadores", "Ver"),
    CUIDADORES_EDITAR("Cuidadores", "Editar"),
    USUARIOS_VER("Usuarios", "Ver"),
    USUARIOS_EDITAR("Usuarios", "Editar"),
    ROLES_VER("Roles", "Ver"),
    ROLES_EDITAR("Roles", "Editar"),
    PARQUE_VER("Registro del parque", "Ver"),
    PARQUE_EDITAR("Registro del parque", "Editar"),
    SALUD_VER("Salud de las especies", "Ver"),
    SALUD_EDITAR("Salud de las especies", "Editar");
    ;
/* SE AGREGO SALUD PARA VER EN LA CONSOLA CON ESTO DEJA VER QUE EL ADMINISTRADOR DECIDA QUE HACER*/
    
    private final String seccion;
    private final String accion;

    Permiso(String seccion, String accion) {
        this.seccion = seccion;
        this.accion = accion;
    }

    public String getSeccion() {
        return seccion;
    }

    public String getAccion() {
        return accion;
    }

    public boolean esEditar() {
        return name().endsWith("_EDITAR");
    }

    /** El permiso VER de la misma seccion (editar siempre incluye ver). */
    public Permiso verDeLaSeccion() {
        return valueOf(name().replace("_EDITAR", "_VER"));
    }
}
