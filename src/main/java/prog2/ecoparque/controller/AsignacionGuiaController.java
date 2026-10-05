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
import prog2.ecoparque.model.AsignacionGuia;
import prog2.ecoparque.repository.AsignacionGuiaRepository;

@RestController
@RequestMapping("/api/asignaciones-guia")
public class AsignacionGuiaController {

    private final AsignacionGuiaRepository asignacionGuiaRepository;

    public AsignacionGuiaController(AsignacionGuiaRepository asignacionGuiaRepository) {
        this.asignacionGuiaRepository = asignacionGuiaRepository;
    }

    @GetMapping
    public List<AsignacionGuia> listar() {
        return asignacionGuiaRepository.findAll(org.springframework.data.domain.Sort.by("id"));

    }

    @PostMapping
    public AsignacionGuia crear(@RequestBody AsignacionGuia asignacionGuia) {
        return asignacionGuiaRepository.save(asignacionGuia);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        asignacionGuiaRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    public AsignacionGuia actualizar(@PathVariable Long id, @RequestBody AsignacionGuia asignacionActualizada) {
        AsignacionGuia asignacion = asignacionGuiaRepository.findById(id).orElseThrow();
        asignacion.setGuia(asignacionActualizada.getGuia());
        asignacion.setItinerario(asignacionActualizada.getItinerario());
        asignacion.setHora(asignacionActualizada.getHora());
        return asignacionGuiaRepository.save(asignacion);
    }
}
