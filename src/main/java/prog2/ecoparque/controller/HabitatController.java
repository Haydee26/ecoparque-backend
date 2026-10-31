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
import prog2.ecoparque.model.Habitat;
import prog2.ecoparque.service.HabitatService;

@RestController
@RequestMapping("/api/habitats")
public class HabitatController {

    private final HabitatService habitatService;

    public HabitatController(HabitatService habitatService) {
        this.habitatService = habitatService;
    }

    @GetMapping
    public List<Habitat> listar() {
        return habitatService.listar();
    }

    @PostMapping
    public Habitat crear(@RequestBody Habitat habitat) {
        return habitatService.crear(habitat);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        habitatService.eliminar(id);
    }

    @PutMapping("/{id}")
    public Habitat actualizar(@PathVariable Long id, @RequestBody Habitat habitat) {
        return habitatService.actualizar(id, habitat);
    }
}
