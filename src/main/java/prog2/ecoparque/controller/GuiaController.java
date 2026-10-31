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
import prog2.ecoparque.model.Guia;
import prog2.ecoparque.service.GuiaService;

@RestController
@RequestMapping("/api/guias")
public class GuiaController {

    private final GuiaService guiaService;

    public GuiaController(GuiaService guiaService) {
        this.guiaService = guiaService;
    }

    @GetMapping
    public List<Guia> listar() {
        return guiaService.listar();
    }

    @PostMapping
    public Guia crear(@RequestBody Guia guia) {
        return guiaService.crear(guia);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        guiaService.eliminar(id);
    }

    @PutMapping("/{id}")
    public Guia actualizar(@PathVariable Long id, @RequestBody Guia guia) {
        return guiaService.actualizar(id, guia);
    }
}
