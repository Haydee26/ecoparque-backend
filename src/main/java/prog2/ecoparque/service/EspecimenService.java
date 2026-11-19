package prog2.ecoparque.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prog2.ecoparque.exception.RecursoNoEncontradoException;
import prog2.ecoparque.exception.ReglaDeNegocioException;
import prog2.ecoparque.model.Especie;
import prog2.ecoparque.model.Especimen;
import prog2.ecoparque.model.EstadoSalud;
import prog2.ecoparque.repository.EspecieRepository;
import prog2.ecoparque.repository.EspecimenRepository;

/* regla de los especimenes nombre, especie, fecha obligaorios, obs obligaotiro 
si no esta sano*/
@Service
public class EspecimenService {

    private final EspecimenRepository especimenRepository;
    private final EspecieRepository especieRepository;

    public EspecimenService(EspecimenRepository especimenRepository, EspecieRepository especieRepository) {
        this.especimenRepository = especimenRepository;
        this.especieRepository = especieRepository;
    }

    @Transactional(readOnly = true)
    public List<Especimen> listar() {
        return especimenRepository.findAll(Sort.by("id"));
    }

    /**
     * Lo que ve el veterinario: los que estan enfermos o en tratamiento.
     */
    @Transactional(readOnly = true)
    public List<Especimen> listarNoSanos() {
        return especimenRepository.findByEstadoSaludNot(EstadoSalud.SANO);
    }

    @Transactional 
    public Especimen crear(Especimen datos) {
        validar(datos);
        EstadoSalud estado = estadoOSano(datos.getEstadoSalud());
        validarSalud(estado, datos.getObservacionSalud());
        // Se arma uno nuevo para que nadie pueda pisar un especimen existente mandando un id.
        Especimen nuevo = new Especimen(datos.getNombre().trim(), buscarEspecie(datos.getEspecie()),
                datos.getFechaIngreso(), estado, limpiar(datos.getObservacionSalud()));
        return especimenRepository.save(nuevo);
    }

    @Transactional
    public Especimen actualizar(Long id, Especimen datos) {
        validar(datos);
        EstadoSalud estado = estadoOSano(datos.getEstadoSalud());
        validarSalud(estado, datos.getObservacionSalud());
        Especimen especimen = buscar(id);
        especimen.setNombre(datos.getNombre().trim());
        especimen.setEspecie(buscarEspecie(datos.getEspecie()));
        especimen.setFechaIngreso(datos.getFechaIngreso());
        especimen.setEstadoSalud(estado);
        especimen.setObservacionSalud(limpiar(datos.getObservacionSalud()));
        return especimenRepository.save(especimen);
    }

       /** Cambia solo la salud y registra quien hizo el cambio y cuando. */
    @Transactional
    public Especimen cambiarSalud(Long id, EstadoSalud estado, String observacion, String usuario) {
        if (estado == null) {
            throw new ReglaDeNegocioException("Hay que indicar el estado de salud.");
        }
        validarSalud(estado, observacion);
        Especimen especimen = buscar(id);
        especimen.setEstadoSalud(estado);
        especimen.setObservacionSalud(limpiar(observacion));
        especimen.setAvisadoPor(usuario);              // NUEVO: quien hizo el cambio
        especimen.setFechaAvisoSalud(LocalDate.now()); // NUEVO: la fecha de hoy
        return especimenRepository.save(especimen);
    }

    @Transactional
    public void eliminar(Long id) {
        buscar(id);
        especimenRepository.deleteById(id);
    }

    private Especimen buscar(Long id) {
        return especimenRepository.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("No existe el especimen " + id + "."));
    }

    /**
     * Del pedido llega solo el id de la especie: se busca la especie real en la
     * base.
     */
    private Especie buscarEspecie(Especie especie) {
        if (especie == null || especie.getId() == null) {
            throw new ReglaDeNegocioException("Hay que elegir la especie del especimen.");
        }
        return especieRepository.findById(especie.getId()).orElseThrow(() -> new RecursoNoEncontradoException("No existe la especie " + especie.getId() + "."));
    }

    private void validar(Especimen especimen) {

        if (especimen == null) {
            throw new ReglaDeNegocioException("Faltan los datos del especimen");
        }
        Validaciones.textoObligatorio(especimen.getNombre(), "El nombre del especimen");
        if (especimen.getFechaIngreso() == null) {
            throw new ReglaDeNegocioException("La fecha de ingreso es obligatoria.");
        }
        if (especimen.getFechaIngreso().isAfter(LocalDate.now())) {
            throw new ReglaDeNegocioException("La fecha de ingreso no puede ser futura.");
        }
    }

    private void validarSalud(EstadoSalud estado, String observacion) {

        if (estado != EstadoSalud.SANO && limpiar(observacion) == null) {
            throw new ReglaDeNegocioException("Si el especimen no esta sano, hay que escribir la observacion.");
        }
    }

    private EstadoSalud estadoOSano(EstadoSalud estado) {
        return estado == null ? EstadoSalud.SANO : estado;
    }

    /**
     * Devuelve el texto sin espacios de mas, o null si vino vacio.
     */
    private String limpiar(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }

}
