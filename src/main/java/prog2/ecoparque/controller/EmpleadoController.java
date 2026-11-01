package prog2.ecoparque.controller;

import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import prog2.ecoparque.dto.EmpleadoDetalle;
import prog2.ecoparque.model.Empleado;
import prog2.ecoparque.service.EmpleadoService;

/** Base de datos de empleados. Solo el administrador (lo controla SecurityConfig). */
@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    /** Sin parametro lista a todos por antiguedad; con "q" busca por nombre, apellido o DNI. */
    @GetMapping
    public List<EmpleadoDetalle> listar(@RequestParam(required = false) String q) {
        return empleadoService.listar(q);
    }

    @PostMapping
    public Empleado crear(@RequestBody Empleado empleado) {
        return empleadoService.crear(empleado);
    }

    @PutMapping("/{id}")
    public Empleado actualizar(@PathVariable Long id, @RequestBody Empleado empleado) {
        return empleadoService.actualizar(id, empleado);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        empleadoService.eliminar(id);
    }
}
