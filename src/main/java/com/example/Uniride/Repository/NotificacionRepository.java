package com.example.Uniride.Repository;

import com.example.Uniride.Model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    @Query(value = "SELECT * FROM notificacion WHERE destinatarios = :destinatarios", nativeQuery = true)
    List<Notificacion> findByDestinatarios(String destinatarios);
}