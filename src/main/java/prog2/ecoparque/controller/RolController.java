package prog2.ecoparque.controller;

import java.security.Principal;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prog2.ecoparque.dto.PermisoInfo;
import prog2.ecoparque.dto.RolDatos;
import prog2.ecoparque.dto.RolDetalle;
import prog2.ecoparque.service.RolService;

/** Roles y permisos. Quien puede usarlos lo controla SecurityConfig. */
@RestController
@RequestMapping("/api/roles")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping
    public List<RolDetalle> listar() {
        return rolService.listar();
    }

    @GetMapping("/permisos")
    public List<PermisoInfo> catalogo() {
        return rolService.catalogo();
    }

    /** Permisos de quien inició sesión. */
    @GetMapping("/mis-permisos")
    public List<String> misPermisos(Principal principal) {
        return rolService.permisosDe(principal.getName());
    }

    @PostMapping
    public RolDetalle crear(@RequestBody RolDatos datos) {
        return rolService.crear(datos);
    }

    @PutMapping("/{id}")
    public RolDetalle actualizar(@PathVariable Long id, @RequestBody RolDatos datos) {
        return rolService.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        rolService.eliminar(id);
    }
}
