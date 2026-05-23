package com.example.Uniride.Repository;

import com.example.Uniride.Model.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {

    @Query(value = "SELECT * FROM calificacion WHERE id_conductor = :idConductor", nativeQuery = true)
    List<Calificacion> findByIdConductor(Long idConductor);

    @Query(value = "SELECT AVG(puntuacion) FROM calificacion WHERE id_conductor = :idConductor", nativeQuery = true)
    Double promedioPorConductor(Long idConductor);

    @Query(value = "SELECT COUNT(*) FROM calificacion WHERE id_reserva = :idReserva", nativeQuery = true)
    int existsByIdReserva(Long idReserva);

    @Modifying
    @Query(value = "DELETE FROM calificacion WHERE id_pasajero = :idPasajero", nativeQuery = true)
    void deleteByIdPasajero(Long idPasajero);

    @Modifying
    @Query(value = "DELETE FROM calificacion WHERE id_conductor = :idConductor", nativeQuery = true)
    void deleteByIdConductor(Long idConductor);

    @Modifying
    @Query(value = "DELETE FROM calificacion WHERE id_reserva IN " +
            "(SELECT id_reserva FROM reserva WHERE id_viaje = :idViaje)", nativeQuery = true)
    void deleteByIdReservaViaje(Long idViaje);
}