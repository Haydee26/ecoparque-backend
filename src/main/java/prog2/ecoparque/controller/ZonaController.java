package prog2.ecoparque.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prog2.ecoparque.model.Zona;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import prog2.ecoparque.repository.ZonaRepository;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/zonas")
public class ZonaController {

    private final ZonaRepository zonaRepository;

    public ZonaController(ZonaRepository zonaRepository) {
        this.zonaRepository = zonaRepository;
    }

    @GetMapping
    public List<Zona> listar() {
        return zonaRepository.findAll(org.springframework.data.domain.Sort.by("id"));
    }

    @PostMapping
    public Zona crear(@RequestBody Zona zona) {
        return zonaRepository.save(zona);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        zonaRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    public Zona actualizar(@PathVariable Long id, @RequestBody Zona zonaActualizada) {
        Zona zona = zonaRepository.findById(id).orElseThrow();
        zona.setNombre(zonaActualizada.getNombre());
        zona.setExtension(zonaActualizada.getExtension());
        return zonaRepository.save(zona);
    }

}
