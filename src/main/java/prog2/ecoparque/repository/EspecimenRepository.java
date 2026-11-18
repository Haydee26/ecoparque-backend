
package prog2.ecoparque.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import prog2.ecoparque.model.EstadoSalud;
import prog2.ecoparque.model.Especimen;

/*¿Por qué es una interfaz y no una clase? Vos declarás qué necesitás y
Spring arma la implementación cuando arranca. Es el contrato del DAO: 
el servicio depende de esta interfaz, no de cómo se hace la consulta 
(la D de SOLID, inversión de dependencias).*/


public interface EspecimenRepository extends JpaRepository<Especimen, Long>{
    /*extends JpaRepository<Especimen, Long>: con esa sola línea Spring te da findAll, findById, save, deleteById, count y otros métodos, 
    sin escribir SQL. Especimen es la entidad y Long es el tipo de su id.*/
    /** Los especímenes que NO están en el estado indicado (se usa con SANO). */
    List<Especimen> findByEstadoSaludNot(EstadoSalud estado); //arma la lista que ve el veterinario: los enfermos y los que están en tratamiento.
    
    /** Cuántos especimenes tiene una especie. */
    
    long countByEspecieId(long especieId); //sirve para no dejar borrar una especie que todavía tiene especímenes.
}
