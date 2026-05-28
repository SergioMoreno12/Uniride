package com.example.Uniride.Repository;

import com.example.Uniride.Model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    @Query(value = "SELECT * FROM notificacion WHERE destinatarios = :destinatarios " +
            "ORDER BY fecha_envio DESC", nativeQuery = true)
    List<Notificacion> findByDestinatarios(String destinatarios);

    @Query(value =
            "SELECT n.* FROM notificacion n " +
                    "WHERE n.id_usuario = :idUsuario " +
                    "   OR (n.id_usuario IS NULL " +
                    "       AND (n.destinatarios = 'todos' " +
                    "            OR n.destinatarios = (SELECT rol FROM usuario WHERE id_usuario = :idUsuario))) " +
                    "ORDER BY n.fecha_envio DESC", nativeQuery = true)
    List<Notificacion> findByIdUsuario(Long idUsuario);

    @Modifying
    @Query(value = "DELETE FROM notificacion WHERE id_usuario = :idUsuario", nativeQuery = true)
    void deleteByIdUsuario(Long idUsuario);

    @Modifying
    @Query(value = "DELETE FROM notificacion WHERE id_viaje = :idViaje", nativeQuery = true)
    void deleteByIdViaje(Long idViaje);
}