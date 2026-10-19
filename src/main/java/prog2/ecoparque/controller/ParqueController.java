package prog2.ecoparque.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prog2.ecoparque.service.ParqueService;
import prog2.ecoparque.service.ParqueService.EstadoParque;

@RestController
@RequestMapping("/api/parque")
public class ParqueController {

    /** Datos que se envian para configurar el parque. */
    public record ConfiguracionParque(String nombre, Double areaTotal) {
    }

    private final ParqueService parqueService;

    public ParqueController(ParqueService parqueService) {
        this.parqueService = parqueService;
    }

    @GetMapping
    public EstadoParque estado() {
        return parqueService.estado();
    }

    @PutMapping
    public EstadoParque configurar(@RequestBody ConfiguracionParque datos) {
        return parqueService.configurar(datos.nombre(), datos.areaTotal());
    }
}
