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
import prog2.ecoparque.model.Especimen;
import prog2.ecoparque.model.EstadoSalud;
import prog2.ecoparque.service.EspecimenService;

@RestController // esta clase atiende pedidos HTTP y devuelve JSON. 
//Es la capa controlador: no tiene reglas de negocio, solo recibe el pedido y se lo pasa al servicio.
@RequestMapping("/api/especimenes")
public class EspecimenController {

    private final EspecimenService especimenService;

    public EspecimenController(EspecimenService especimenService) {
        this.especimenService = especimenService;
    }

    @GetMapping
    public List<Especimen> listar() {
        return especimenService.listar();
    }

    /**
     * Los especimenes enfermos o en tratamiento (lo que ve el veterinario).
     */
    @GetMapping("/salud")
    public List<Especimen> listarNoSanos() {
        return especimenService.listarNoSanos();
    }

    @PostMapping
    public Especimen crear(@RequestBody Especimen especimen) {
        return especimenService.crear(especimen);
    }

    @PutMapping("/{id}")
    public Especimen actualizar(@PathVariable Long id, @RequestBody Especimen especimen) {
        return especimenService.actualizar(id, especimen);
    }

    /**
     * Lo que llega al cambiar la salud: solo el estado y la observacion.
     */

    public record CambioSalud(EstadoSalud estado, String observacion) {

    }
/*record CambioSalud: un record es una clase chiquita solo para transportar
    datos (es un DTO). Para cambiar la salud no hace falta mandar el espécimen entero, alcanza con:*/
    @PutMapping("/{id}/salud")
    public Especimen cambiarSalud(@PathVariable Long id, @RequestBody CambioSalud cambio) {
        return especimenService.cambiarSalud(id, cambio.estado(), cambio.observacion());
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        especimenService.eliminar(id);
    }

}
