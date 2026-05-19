package com.example.Uniride.Repository;

import com.example.Uniride.Model.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {

    @Query(value = "SELECT * FROM calificacion WHERE id_conductor = :idConductor", nativeQuery = true)
    List<Calificacion> findByIdConductor(Long idConductor);

    @Query(value = "SELECT * FROM calificacion WHERE id_pasajero = :idPasajero", nativeQuery = true)
    List<Calificacion> findByIdPasajero(Long idPasajero);

    @Query(value = "SELECT * FROM calificacion WHERE id_reserva = :idReserva", nativeQuery = true)
    Optional<Calificacion> findByIdReserva(Long idReserva);

    @Query(value = "SELECT AVG(puntuacion) FROM calificacion WHERE id_conductor = :idConductor", nativeQuery = true)
    Double promedioCalificacionConductor(Long idConductor);
}