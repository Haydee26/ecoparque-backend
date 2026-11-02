package prog2.ecoparque.service;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prog2.ecoparque.exception.RecursoNoEncontradoException;
import prog2.ecoparque.exception.ReglaDeNegocioException;
import prog2.ecoparque.model.Parque;
import prog2.ecoparque.model.Zona;
import prog2.ecoparque.repository.ZonaRepository;

/**
 * Reglas de las zonas: nombre y extension validos, que entre todas no superen el area
 * total del parque, y que su lugar en el mapa (si lo tiene) este dentro de la cuadricula
 * y no se pise con otra zona.
 */
@Service
public class ZonaService {

    /** Tamano de la cuadricula del mapa del parque (casilleros). */
    public static final int MAPA_COLUMNAS = 10;
    public static final int MAPA_FILAS = 6;

    private final ZonaRepository zonaRepository;
    private final ParqueService parqueService;

    public ZonaService(ZonaRepository zonaRepository, ParqueService parqueService) {
        this.zonaRepository = zonaRepository;
        this.parqueService = parqueService;
    }

    @Transactional(readOnly = true)
    public List<Zona> listar() {
        return zonaRepository.findAll(Sort.by("id"));
    }

    @Transactional
    public Zona crear(Zona datos) {
        validar(datos);
        Parque parque = parqueService.obtenerBloqueado();
        controlarArea(parque, parqueService.areaUsada() + datos.getExtension());
        controlarUbicacion(datos, null);
        // Se arma una zona nueva para que nadie pueda pisar una existente mandando un id.
        Zona nueva = new Zona(datos.getNombre().trim(), datos.getExtension());
        nueva.ubicar(datos.getColumna(), datos.getFila(), datos.getAncho(), datos.getAlto());
        return zonaRepository.save(nueva);
    }

    @Transactional
    public Zona actualizar(Long id, Zona datos) {
        validar(datos);
        Parque parque = parqueService.obtenerBloqueado();
        Zona zona = buscar(id);
        double anterior = zona.getExtension() == null ? 0 : zona.getExtension();
        controlarArea(parque, parqueService.areaUsada() - anterior + datos.getExtension());
        controlarUbicacion(datos, id);
        zona.setNombre(datos.getNombre().trim());
        zona.setExtension(datos.getExtension());
        // Si no se manda ninguna ubicacion se conserva la que ya tenia.
        if (datos.ubicada()) {
            zona.ubicar(datos.getColumna(), datos.getFila(), datos.getAncho(), datos.getAlto());
        }
        return zonaRepository.save(zona);
    }

    @Transactional
    public void eliminar(Long id) {
        buscar(id);
        zonaRepository.deleteById(id);
        // flush: si la zona esta en uso, el error aparece aca y se informa con claridad.
        zonaRepository.flush();
    }

    private Zona buscar(Long id) {
        return zonaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la zona " + id + "."));
    }

    private void validar(Zona zona) {
        if (zona == null) {
            throw new ReglaDeNegocioException("Faltan los datos de la zona.");
        }
        Validaciones.textoObligatorio(zona.getNombre(), "El nombre de la zona");
        Validaciones.numeroPositivo(zona.getExtension(), "La extensión de la zona");
    }

    /** Si la zona trae ubicacion: debe estar completa, dentro de la cuadricula y libre. */
    private void controlarUbicacion(Zona datos, Long idPropio) {
        Integer[] partes = {datos.getColumna(), datos.getFila(), datos.getAncho(), datos.getAlto()};
        int cargadas = 0;
        for (Integer parte : partes) {
            if (parte != null) {
                cargadas++;
            }
        }
        if (cargadas == 0) {
            return;
        }
        if (cargadas < 4) {
            throw new ReglaDeNegocioException(
                    "La ubicación en el mapa necesita columna, fila, ancho y alto (o dejar los cuatro vacíos).");
        }
        for (Integer parte : partes) {
            if (parte < 1) {
                throw new ReglaDeNegocioException("La ubicación en el mapa debe usar números desde 1.");
            }
        }
        int ultimaColumna = datos.getColumna() + datos.getAncho() - 1;
        int ultimaFila = datos.getFila() + datos.getAlto() - 1;
        if (ultimaColumna > MAPA_COLUMNAS || ultimaFila > MAPA_FILAS) {
            throw new ReglaDeNegocioException(String.format(
                    "La zona se sale del mapa: el mapa tiene %d columnas y %d filas.", MAPA_COLUMNAS, MAPA_FILAS));
        }
        for (Zona otra : zonaRepository.findAll()) {
            if (!otra.ubicada() || otra.getId().equals(idPropio)) {
                continue;
            }
            boolean seCruzanColumnas = datos.getColumna() <= otra.getColumna() + otra.getAncho() - 1
                    && otra.getColumna() <= ultimaColumna;
            boolean seCruzanFilas = datos.getFila() <= otra.getFila() + otra.getAlto() - 1
                    && otra.getFila() <= ultimaFila;
            if (seCruzanColumnas && seCruzanFilas) {
                throw new ReglaDeNegocioException("Ese lugar del mapa ya lo ocupa la zona " + otra.getNombre() + ".");
            }
        }
    }

    private void controlarArea(Parque parque, double usadaResultante) {
        double total = parque.getAreaTotal();
        if (usadaResultante > total + ParqueService.TOLERANCIA) {
            throw new ReglaDeNegocioException(String.format(
                    "La zona supera el área disponible del parque: quedarían %.2f m² usados de %.2f m² totales.",
                    usadaResultante, total));
        }
    }
}
