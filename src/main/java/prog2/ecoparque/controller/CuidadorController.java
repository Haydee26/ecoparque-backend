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
import prog2.ecoparque.model.Cuidador;
import prog2.ecoparque.repository.CuidadorRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/cuidadores")
public class CuidadorController {

    private final CuidadorRepository cuidadorRepository;

    public CuidadorController(CuidadorRepository cuidadorRepository) {
        this.cuidadorRepository = cuidadorRepository;
    }

    @GetMapping
    public List<Cuidador> listar() {
        return cuidadorRepository.findAll(org.springframework.data.domain.Sort.by("id"));
    }

    @PostMapping
    public Cuidador crear(@RequestBody Cuidador cuidador) {
        return cuidadorRepository.save(cuidador);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        cuidadorRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    public Cuidador actualizar(@PathVariable Long id, @RequestBody Cuidador cuidadorActualizado) {
        Cuidador cuidador = cuidadorRepository.findById(id).orElseThrow();
        cuidador.setNombre(cuidadorActualizado.getNombre());
        cuidador.setDireccion(cuidadorActualizado.getDireccion());
        cuidador.setTelefono(cuidadorActualizado.getTelefono());
        cuidador.setFechaIngreso(cuidadorActualizado.getFechaIngreso());
        return cuidadorRepository.save(cuidador);
    }
}
