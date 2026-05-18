package com.example.Uniride.Repository;

import com.example.Uniride.Model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Query(value = "SELECT * FROM reserva WHERE id_usuario = :idUsuario", nativeQuery = true)
    List<Reserva> findByIdUsuario(Long idUsuario);

    @Query(value = "SELECT * FROM reserva WHERE id_viaje = :idViaje", nativeQuery = true)
    List<Reserva> findByIdViaje(Long idViaje);

    @Query(value = "SELECT * FROM reserva WHERE id_viaje = :idViaje AND confirmada = true", nativeQuery = true)
    List<Reserva> findConfirmadasByViaje(Long idViaje);
}