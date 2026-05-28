package com.example.Uniride.Repository;

import com.example.Uniride.Model.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ViajeRepository extends JpaRepository<Viaje, Long> {

    @Query(value = "SELECT * FROM viaje WHERE id_sede = :idSede", nativeQuery = true)
    List<Viaje> findByIdSede(Long idSede);

    @Query(value = "SELECT * FROM viaje WHERE estado = :estado", nativeQuery = true)
    List<Viaje> findByEstado(String estado);

    @Query(value = "SELECT * FROM viaje WHERE id_vehiculo = :idVehiculo", nativeQuery = true)
    List<Viaje> findByIdVehiculo(Long idVehiculo);

    @Query(value = "SELECT id_viaje FROM viaje WHERE id_vehiculo = :idVehiculo", nativeQuery = true)
    List<Long> findIdsByIdVehiculo(Long idVehiculo);

    @Query(value = "SELECT id_viaje FROM viaje WHERE id_sede = :idSede", nativeQuery = true)
    List<Long> findIdsBySede(Long idSede);

    @Query(value = "SELECT * FROM viaje WHERE origen ILIKE %:ciudad%", nativeQuery = true)
    List<Viaje> findByOrigenContaining(String ciudad);

    @Query(value = "SELECT * FROM viaje WHERE destino ILIKE %:ciudad%", nativeQuery = true)
    List<Viaje> findByDestinoContaining(String ciudad);   // ← NUEVO

    @Modifying
    @Query(value = "DELETE FROM viaje WHERE id_vehiculo = :idVehiculo", nativeQuery = true)
    void deleteByIdVehiculo(Long idVehiculo);

    @Modifying
    @Query(value = "DELETE FROM viaje WHERE id_sede = :idSede", nativeQuery = true)
    void deleteBySede(Long idSede);
}