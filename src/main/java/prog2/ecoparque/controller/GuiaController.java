package prog2.ecoparque.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prog2.ecoparque.model.Guia;
import prog2.ecoparque.service.GuiaService;

@RestController
@RequestMapping("/api/guias")
public class GuiaController {

    private final GuiaService guiaService;

    public GuiaController(GuiaService guiaService) {
        this.guiaService = guiaService;
    }

    @GetMapping
    public List<Guia> listar() {
        return guiaService.listar();
    }
}
