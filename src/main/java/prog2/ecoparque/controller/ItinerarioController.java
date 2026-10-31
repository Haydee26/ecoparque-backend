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
import prog2.ecoparque.model.Itinerario;
import prog2.ecoparque.service.ItinerarioService;

@RestController
@RequestMapping("/api/itinerarios")
public class ItinerarioController {

    private final ItinerarioService itinerarioService;

    public ItinerarioController(ItinerarioService itinerarioService) {
        this.itinerarioService = itinerarioService;
    }

    @GetMapping
    public List<Itinerario> listar() {
        return itinerarioService.listar();
    }

    @PostMapping
    public Itinerario crear(@RequestBody Itinerario itinerario) {
        return itinerarioService.crear(itinerario);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        itinerarioService.eliminar(id);
    }

    @PutMapping("/{id}")
    public Itinerario actualizar(@PathVariable Long id, @RequestBody Itinerario itinerario) {
        return itinerarioService.actualizar(id, itinerario);
    }
}
