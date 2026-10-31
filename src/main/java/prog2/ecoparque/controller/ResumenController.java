package prog2.ecoparque.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prog2.ecoparque.service.ResumenService;
import prog2.ecoparque.service.ResumenService.Resumen;

/**
 * Resumen numerico del parque. Solo lo puede ver el administrador
 * (la regla esta en SecurityConfig).
 */
@RestController
@RequestMapping("/api/resumen")
public class ResumenController {

    private final ResumenService resumenService;

    public ResumenController(ResumenService resumenService) {
        this.resumenService = resumenService;
    }

    @GetMapping
    public Resumen resumen() {
        return resumenService.resumen();
    }
}
