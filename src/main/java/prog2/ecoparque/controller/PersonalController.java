package prog2.ecoparque.controller;

import java.security.Principal;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prog2.ecoparque.dto.EmpleadoAntiguedad;
import prog2.ecoparque.dto.GuiaPublica;
import prog2.ecoparque.dto.MisDatos;
import prog2.ecoparque.service.PersonalService;

/**
 * Datos del personal que puede ver cualquier usuario con sesion.
 * Los datos personales completos de guias y cuidadores quedan solo para el administrador.
 */
@RestController
@RequestMapping("/api")
public class PersonalController {

    private final PersonalService personalService;

    public PersonalController(PersonalService personalService) {
        this.personalService = personalService;
    }

    @GetMapping("/guias/publico")
    public List<GuiaPublica> guias() {
        return personalService.guiasPublicas();
    }

    @GetMapping("/personal/yo")
    public MisDatos misDatos(Principal principal) {
        return personalService.misDatos(principal.getName());
    }

    /** Solo el administrador (lo controla SecurityConfig). */
    @GetMapping("/personal/antiguedad")
    public List<EmpleadoAntiguedad> antiguedad() {
        return personalService.listarPorAntiguedad();
    }
}
