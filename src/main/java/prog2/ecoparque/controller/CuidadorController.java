package prog2.ecoparque.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import prog2.ecoparque.model.Cuidador;
import prog2.ecoparque.service.CuidadorService;

@RestController
@RequestMapping("/api/cuidadores")
public class CuidadorController {

    private final CuidadorService cuidadorService;

    public CuidadorController(CuidadorService cuidadorService) {
        this.cuidadorService = cuidadorService;
    }

    @GetMapping
    public List<Cuidador> listar() {
        return cuidadorService.listar();
    }
}
