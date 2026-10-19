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
import prog2.ecoparque.model.Zona;
import prog2.ecoparque.service.ZonaService;

@RestController
@RequestMapping("/api/zonas")
public class ZonaController {

    private final ZonaService zonaService;

    public ZonaController(ZonaService zonaService) {
        this.zonaService = zonaService;
    }

    @GetMapping
    public List<Zona> listar() {
        return zonaService.listar();
    }

    @PostMapping
    public Zona crear(@RequestBody Zona zona) {
        return zonaService.crear(zona);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        zonaService.eliminar(id);
    }

    @PutMapping("/{id}")
    public Zona actualizar(@PathVariable Long id, @RequestBody Zona zona) {
        return zonaService.actualizar(id, zona);
    }
}
