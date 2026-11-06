package prog2.ecoparque.controller;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prog2.ecoparque.model.Especie;
import prog2.ecoparque.service.EspecieService;

@RestController
@RequestMapping("/api/especies")
public class EspecieController {

    private final EspecieService especieService;

    public EspecieController(EspecieService especieService) {
        this.especieService = especieService;
    }

    @GetMapping
    public List<Especie> listar() {
        return especieService.listar();
    }

    @GetMapping("/cantidad")
    public long contar() {
        return especieService.contar();
    }

    @PostMapping
    public Especie crear(@RequestBody Especie especie) {
        return especieService.crear(especie);
    }

    @PutMapping("/{id}")
    public Especie actualizar(@PathVariable Long id, @RequestBody Especie especie) {
        return especieService.actualizar(id, especie);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        especieService.eliminar(id);
    }

    /** Lo que llega al avisar que una especie está enferma: solo el texto. */
    public record AvisoSalud(String observacion) {
    }

    @GetMapping("/enfermas")
    public List<Especie> enfermas() {
        return especieService.listarEnfermas();
    }

    /**
     * Authentication la completa Spring Security con quien hizo el pedido:
     * así el usuario sale de la sesión y no de lo que manda la pantalla.
     */
    @PutMapping("/{id}/enferma")
    public Especie avisarEnferma(@PathVariable Long id, @RequestBody AvisoSalud aviso,
            Authentication autenticacion) {
        return especieService.avisarEnferma(id, aviso.observacion(), autenticacion.getName());
    }

    @PutMapping("/{id}/sana")
    public Especie marcaSana(@PathVariable Long id) {
        return especieService.marcarSana(id);
    }
}