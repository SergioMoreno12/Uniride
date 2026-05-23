package com.example.Uniride.Repository;

import com.example.Uniride.Model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    @Query(value = "SELECT * FROM vehiculo WHERE id_usuario = :idUsuario", nativeQuery = true)
    List<Vehiculo> findByIdUsuario(Long idUsuario);

    @Query(value = "SELECT id_vehiculo FROM vehiculo WHERE id_usuario = :idUsuario", nativeQuery = true)
    List<Long> findIdsByIdUsuario(Long idUsuario);

    @Modifying
    @Query(value = "DELETE FROM vehiculo WHERE id_usuario = :idUsuario", nativeQuery = true)
    void deleteByIdUsuario(Long idUsuario);
}