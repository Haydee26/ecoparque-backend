package prog2.ecoparque.controller;

import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import prog2.ecoparque.model.Guia;
import prog2.ecoparque.repository.GuiaRepository;

@RestController
@RequestMapping("/api/guias")
public class GuiaController {

    private final GuiaRepository guiaRepository;

    public GuiaController(GuiaRepository guiaRepository) {
        this.guiaRepository = guiaRepository;
    }

    @GetMapping
    public List<Guia> listar() {
        return guiaRepository.findAll(org.springframework.data.domain.Sort.by("id"));
       
    }

    @PostMapping
    public Guia crear(@RequestBody Guia guia) {
        return guiaRepository.save(guia);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        guiaRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    public Guia actualizar(@PathVariable Long id, @RequestBody Guia guiaActualizado) {
        Guia guia = guiaRepository.findById(id).orElseThrow();
        guia.setNombre(guiaActualizado.getNombre());
        guia.setDireccion(guiaActualizado.getDireccion());
        guia.setTelefono(guiaActualizado.getTelefono());
        guia.setFechaIngreso(guiaActualizado.getFechaIngreso());
        return guiaRepository.save(guia);
    }
}
