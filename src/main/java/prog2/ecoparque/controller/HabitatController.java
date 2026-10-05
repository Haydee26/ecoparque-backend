package prog2.ecoparque.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import prog2.ecoparque.model.Habitat;
import prog2.ecoparque.repository.HabitatRepository;

@RestController
@RequestMapping("/api/habitats")
public class HabitatController {

    private final HabitatRepository habitatRepository;

    public HabitatController(HabitatRepository habitatRepository) {
        this.habitatRepository = habitatRepository;
    }
    @GetMapping
    public List<Habitat> listar() {
        return habitatRepository.findAll(org.springframework.data.domain.Sort.by("id"));
    }

    @PostMapping
    public Habitat crear(@RequestBody Habitat habitat) {
        return habitatRepository.save(habitat);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        habitatRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    public Habitat actualizar(@PathVariable Long id, @RequestBody Habitat habitatActualizado) {
        Habitat habitat = habitatRepository.findById(id).orElseThrow();
        habitat.setNombre(habitatActualizado.getNombre());
        habitat.setClima(habitatActualizado.getClima());
        habitat.setVegetacionPredominante(habitatActualizado.getVegetacionPredominante());
        return habitatRepository.save(habitat);
    }

}
