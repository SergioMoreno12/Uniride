package com.example.Uniride.Repository;

import com.example.Uniride.Model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    @Query(value = "SELECT * FROM reporte WHERE estado = :estado", nativeQuery = true)
    List<Reporte> findByEstado(String estado);

    @Query(value = "SELECT * FROM reporte WHERE id_usuario = :idUsuario", nativeQuery = true)
    List<Reporte> findByIdUsuario(Long idUsuario);
}