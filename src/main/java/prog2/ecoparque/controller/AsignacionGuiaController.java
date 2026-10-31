package prog2.ecoparque.controller;

import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prog2.ecoparque.model.AsignacionGuia;
import prog2.ecoparque.service.AsignacionGuiaService;

@RestController
@RequestMapping("/api/asignaciones-guia")
public class AsignacionGuiaController {

    private final AsignacionGuiaService servicio;

    public AsignacionGuiaController(AsignacionGuiaService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<AsignacionGuia> listar() {
        return servicio.listar();
    }

    @PostMapping
    public AsignacionGuia crear(@RequestBody AsignacionGuia asignacionGuia) {
        return servicio.crear(asignacionGuia);
    }

    @PutMapping("/{id}")
    public AsignacionGuia actualizar(@PathVariable Long id, @RequestBody AsignacionGuia asignacionGuia) {
        return servicio.actualizar(id, asignacionGuia);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }
}
