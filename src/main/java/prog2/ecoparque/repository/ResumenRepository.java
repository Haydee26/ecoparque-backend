package prog2.ecoparque.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import prog2.ecoparque.model.Especie;

/**
 * Consultas de solo lectura para armar el resumen del parque.
 * Hacen las cuentas en la base de datos, sin traer todos los registros.
 */
public interface ResumenRepository extends Repository<Especie, Long> {

    @Query("select coalesce(sum(e.cantidadEjemplares), 0L) from Especie e")
    Long totalEjemplares();

    @Query("select count(e) from Especie e")
    Long totalEspecies();

    @Query("select count(g) from Guia g")
    Long totalGuias();

    @Query("select count(c) from Cuidador c")
    Long totalCuidadores();

    @Query("select e.nombreEspanol from Especie e "
            + "where e.id not in (select r.especie.id from ResponsabilidadCuidador r where r.especie is not null) "
            + "order by e.nombreEspanol")
    List<String> especiesSinCuidador();

    @Query("select i.codigo from Itinerario i "
            + "where i.id not in (select a.itinerario.id from AsignacionGuia a where a.itinerario is not null) "
            + "order by i.codigo")
    List<String> itinerariosSinGuia();
}
