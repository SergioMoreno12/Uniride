package com.example.Uniride.Repository;

import com.example.Uniride.Model.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;
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

    @Query(value = "SELECT * FROM viaje WHERE origen ILIKE %:ciudad%", nativeQuery = true)
    List<Viaje> findByOrigenContaining(String ciudad);
}