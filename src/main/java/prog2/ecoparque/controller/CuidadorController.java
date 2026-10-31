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
import prog2.ecoparque.model.Cuidador;
import prog2.ecoparque.service.CuidadorService;

@RestController
@RequestMapping("/api/cuidadores")
public class CuidadorController {

    private final CuidadorService cuidadorService;

    public CuidadorController(CuidadorService cuidadorService) {
        this.cuidadorService = cuidadorService;
    }

    @GetMapping
    public List<Cuidador> listar() {
        return cuidadorService.listar();
    }

    @PostMapping
    public Cuidador crear(@RequestBody Cuidador cuidador) {
        return cuidadorService.crear(cuidador);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        cuidadorService.eliminar(id);
    }

    @PutMapping("/{id}")
    public Cuidador actualizar(@PathVariable Long id, @RequestBody Cuidador cuidador) {
        return cuidadorService.actualizar(id, cuidador);
    }
}
