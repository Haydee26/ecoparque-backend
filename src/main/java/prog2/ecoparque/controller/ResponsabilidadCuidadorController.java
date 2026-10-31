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
import prog2.ecoparque.model.ResponsabilidadCuidador;
import prog2.ecoparque.service.ResponsabilidadCuidadorService;

@RestController
@RequestMapping("/api/responsabilidades-cuidador")
public class ResponsabilidadCuidadorController {

    private final ResponsabilidadCuidadorService servicio;

    public ResponsabilidadCuidadorController(ResponsabilidadCuidadorService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<ResponsabilidadCuidador> listar() {
        return servicio.listar();
    }

    @PostMapping
    public ResponsabilidadCuidador crear(@RequestBody ResponsabilidadCuidador responsabilidadCuidador) {
        return servicio.crear(responsabilidadCuidador);
    }

    @PutMapping("/{id}")
    public ResponsabilidadCuidador actualizar(@PathVariable Long id, @RequestBody ResponsabilidadCuidador responsabilidadCuidador) {
        return servicio.actualizar(id, responsabilidadCuidador);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
    }
}
