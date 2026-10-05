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
import prog2.ecoparque.model.ResponsabilidadCuidador;
import prog2.ecoparque.repository.ResponsabilidadCuidadorRepository;

@RestController
@RequestMapping("/api/responsabilidades-cuidador")
public class ResponsabilidadCuidadorController {

    private final ResponsabilidadCuidadorRepository responsabilidadCuidadorRepository;

    public ResponsabilidadCuidadorController(ResponsabilidadCuidadorRepository responsabilidadCuidadorRepository) {
        this.responsabilidadCuidadorRepository = responsabilidadCuidadorRepository;
    }

    @GetMapping
    public List<ResponsabilidadCuidador> listar() {
        return responsabilidadCuidadorRepository.findAll(org.springframework.data.domain.Sort.by("id"));

    }

    @PostMapping
    public ResponsabilidadCuidador crear(@RequestBody ResponsabilidadCuidador responsabilidadCuidador) {
        return responsabilidadCuidadorRepository.save(responsabilidadCuidador);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        responsabilidadCuidadorRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    public ResponsabilidadCuidador actualizar(@PathVariable Long id, @RequestBody ResponsabilidadCuidador responsabilidadActualizada) {
        ResponsabilidadCuidador responsabilidad = responsabilidadCuidadorRepository.findById(id).orElseThrow();
        responsabilidad.setCuidador(responsabilidadActualizada.getCuidador());
        responsabilidad.setEspecie(responsabilidadActualizada.getEspecie());
        responsabilidad.setFechaDesde(responsabilidadActualizada.getFechaDesde());
        return responsabilidadCuidadorRepository.save(responsabilidad);
    }
}
