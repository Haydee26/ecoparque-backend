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
import prog2.ecoparque.model.Itinerario;
import prog2.ecoparque.repository.ItinerarioRepository;

@RestController
@RequestMapping("/api/itinerarios")
public class ItinerarioController {

    private final ItinerarioRepository itinerarioRepository;

    public ItinerarioController(ItinerarioRepository itinerarioRepository) {
        this.itinerarioRepository = itinerarioRepository;
    }

    @GetMapping
    public List<Itinerario> listar() {
        return itinerarioRepository.findAll(org.springframework.data.domain.Sort.by("id"));
    }

    @PostMapping
    public Itinerario crear(@RequestBody Itinerario itinerario) {
        return itinerarioRepository.save(itinerario);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        itinerarioRepository.deleteById(id);
    }

    @PutMapping("/{id}")
    public Itinerario actualizar(@PathVariable Long id, @RequestBody Itinerario itinerarioActualizado) {
        Itinerario itinerario = itinerarioRepository.findById(id).orElseThrow();
        itinerario.setCodigo(itinerarioActualizado.getCodigo());
        itinerario.setDuracionMinutos(itinerarioActualizado.getDuracionMinutos());
        itinerario.setLongitudMetros(itinerarioActualizado.getLongitudMetros());
        itinerario.setMaximoVisitantes(itinerarioActualizado.getMaximoVisitantes());
        itinerario.setCantidadEspecies(itinerarioActualizado.getCantidadEspecies());
        itinerario.setZonas(itinerarioActualizado.getZonas());
        return itinerarioRepository.save(itinerario);
    }
}
