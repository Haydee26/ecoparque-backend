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
import prog2.ecoparque.model.Especie;
import prog2.ecoparque.repository.EspecieRepository;

@RestController
@RequestMapping("/api/especies")
public class EspecieController {

    private final EspecieRepository especieRepository;

    public EspecieController(EspecieRepository especieRepository) {
        this.especieRepository = especieRepository;
    }

    @GetMapping
    public List<Especie> listar() {
        return especieRepository.findAll(org.springframework.data.domain.Sort.by("id"));
    }

    @PostMapping
    public Especie crear(@RequestBody Especie especie) {
        return especieRepository.save(especie);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        especieRepository.deleteById(id);
    }
    
 
    @GetMapping("/cantidad")
    public long contar(){return especieRepository.count();
    }
        
    @PutMapping("/{id}")
    public Especie actualizar(@PathVariable Long id, @RequestBody Especie especieActualizada) {
        Especie especie = especieRepository.findById(id).orElseThrow();
        especie.setNombreEspanol(especieActualizada.getNombreEspanol());
        especie.setNombreCientifico(especieActualizada.getNombreCientifico());
        especie.setDescripcion(especieActualizada.getDescripcion());
        return especieRepository.save(especie);
    }
}
